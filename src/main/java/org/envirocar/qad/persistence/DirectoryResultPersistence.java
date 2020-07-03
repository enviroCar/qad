package org.envirocar.qad.persistence;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.envirocar.qad.AlgorithmParameters;
import org.envirocar.qad.model.result.AnalysisResult;
import org.envirocar.qad.model.result.SegmentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalQueries;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class DirectoryResultPersistence implements ResultPersistence {
    private static final Logger LOG = LoggerFactory.getLogger(DirectoryResultPersistence.class);
    private static final String TIME_ZONE = "Europe/Berlin";
    private static final ZoneId ZONE_ID = ZoneId.of(TIME_ZONE);
    private static final Locale LOCALE = Locale.ROOT;
    private static final FileAttribute<?> directoryPermissions = getPermissions("rwxr-xr-x");
    private static final FileAttribute<?> filePermissions = getPermissions("rw-r--r--");
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HHmmss", LOCALE).withZone(ZONE_ID);
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd", LOCALE)
                                                                         .withZone(ZONE_ID);
    private final AlgorithmParameters parameters;
    private final ObjectWriter writer;
    private final ObjectReader reader;

    @Autowired
    public DirectoryResultPersistence(ObjectReader reader, ObjectWriter writer,
                                      AlgorithmParameters parameters) {
        this.parameters = Objects.requireNonNull(parameters);
        this.writer = Objects.requireNonNull(writer);
        this.reader = Objects.requireNonNull(reader);
    }

    @PostConstruct
    @Scheduled(cron = "0 0 0 * * *", zone = TIME_ZONE)
    public void archive() {
        LOG.debug("Archiving result entries older than {}", today());
        try {
            Files.createDirectories(getArchive(), directoryPermissions);
            Files.list(this.parameters.getOutputPath())
                 .filter(f -> f.getFileName().toString().endsWith(".json"))
                 .filter(Files::isRegularFile)
                 .filter(this::isBeforeToday)
                 .forEach(this::archiveFile);
        } catch (IOException e) {
            LOG.error("Unable to archive old results", e);
        }
    }

    private Path getArchive() {
        return this.parameters.getOutputPath().resolve("archive");
    }

    private void archiveFile(Path file) {
        String fileName = file.getFileName().toString();
        for (int i = 0; true; i++) {
            try {
                Files.move(file, getArchive().resolve(fileName));
                break;
            } catch (FileAlreadyExistsException ex) {
                fileName = String.format("%s.%d", file.getFileName(), i);
            } catch (IOException ex) {
                LOG.error(String.format("error archiving file %s", file), ex);
            }
        }
    }

    private boolean isBeforeToday(Path file) {
        if (file == null) {
            return false;
        }
        String[] s = file.getFileName().toString().split("_");
        if (s.length < 5) {
            return false;
        }
        String dateString = s[4];
        if (dateString.length() != 8) {
            return false;
        }

        try {
            return dateFormat.parse(dateString, TemporalQueries.localDate()).isBefore(today());
        } catch (DateTimeParseException ignored) {
            return false;
        }
    }

    @Override
    public void persist(AnalysisResult result) {
        try {
            persist1(result);
        } catch (IOException ex) {
            LOG.error("Error creating output file", ex);
        }
    }

    void persist1(AnalysisResult result) throws IOException {
        Path path = createFile(result);
        LOG.info("Writing output {}", path.toAbsolutePath());
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            this.writer.writeValue(w, result);
        } catch (IOException e) {
            LOG.error(String.format("Error writing %s", path), e);
        }
    }

    private boolean isDuplicate(AnalysisResult result, Path x) {
        try (Reader reader = Files.newBufferedReader(x, StandardCharsets.UTF_8)) {
            return isDuplicate(result, this.reader.readValue(reader, AnalysisResult.class));
        } catch (IOException e) {
            LOG.warn("could not read file " + x, e);
        }
        return false;
    }

    private boolean isDuplicate(AnalysisResult ar1, AnalysisResult ar2) {
        if (Objects.equals(ar1.getStart(), ar2.getStart()) &&
            Objects.equals(ar1.getEnd(), ar2.getEnd()) &&
            Objects.equals(ar1.getAxis(), ar2.getAxis()) &&
            Objects.equals(ar1.getFuelType(), ar2.getFuelType()) &&
            Objects.equals(ar1.getModel(), ar2.getModel())) {
            List<SegmentResult> s1 = ar1.getSegments();
            List<SegmentResult> s2 = ar2.getSegments();
            if (s1 == s2) {
                return true;
            }
            if (s1.size() == s2.size() && !s1.isEmpty()) {
                return Objects.equals(s1.get(0).getSegmentId(), s2.get(0).getSegmentId()) &&
                       Objects.equals(s1.get(s1.size() - 1).getSegmentId(), s2.get(s2.size() - 1).getSegmentId());
            }

        }
        return false;
    }

    private Path createFile(AnalysisResult result) throws IOException {
        String prefix = String.format("%s_%s_%s_%s_",
                                      result.getModel(),
                                      result.getAxis(),
                                      timeFormat.format(result.getStart()),
                                      dateFormat.format(result.getStart()));
        Optional<Path> duplicates = findDuplicates(result, prefix);
        if (duplicates.isPresent()) {
            throw new IOException(String.format("Ignoring result as it seems to be a duplicate of %s",
                                                duplicates.get()));
        }
        prefix += result.getTrack();
        Path path = this.parameters.getOutputPath().resolve(String.format("%s.json", prefix));

        for (int i = 0; true; i++) {
            try {
                Files.createFile(path, filePermissions);
                return path;
            } catch (FileAlreadyExistsException ignored) {
                path = this.parameters.getOutputPath().resolve(String.format("%s_%d.json", prefix, i));
            }
        }
    }

    @Nonnull
    private Optional<Path> findDuplicates(AnalysisResult result, String prefix) throws IOException {
        return Files.list(this.parameters.getOutputPath())
                    .filter(x -> x.getFileName().toString().startsWith(prefix) &&
                                 x.getFileName().toString().endsWith(".json"))
                    .filter(Files::isRegularFile)
                    .filter(x -> isDuplicate(result, x))
                    .findAny();
    }

    private static FileAttribute<Set<PosixFilePermission>> getPermissions(String s) {
        return PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(s));
    }

    private static LocalDate today() {
        return LocalDate.now(ZONE_ID);
    }

}
