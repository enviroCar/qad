package org.envirocar.qad.persistence;

import com.fasterxml.jackson.databind.ObjectWriter;
import org.envirocar.qad.AlgorithmParameters;
import org.envirocar.qad.model.result.AnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.IOException;
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
import java.util.Locale;
import java.util.Objects;
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
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd", LOCALE).withZone(ZONE_ID);
    //private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss", LOCALE).withZone(ZONE_ID);
    //private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", LOCALE).withZone(ZONE_ID);

    private final AlgorithmParameters parameters;
    private final ObjectWriter writer;

    @Autowired
    public DirectoryResultPersistence(ObjectWriter writer, AlgorithmParameters parameters) {
        this.parameters = Objects.requireNonNull(parameters);
        this.writer = Objects.requireNonNull(writer);
    }

    @PostConstruct
    @Scheduled(cron = "0 0 0 * * *", zone = TIME_ZONE)
    public void archive() {
        LOG.debug("Archiving result entries older than {}", today());
        try {
            Files.createDirectories(getArchive(), directoryPermissions);
            Files.list(parameters.getOutputPath())
                 .filter(f -> f.getFileName().toString().endsWith(".json"))
                 .filter(Files::isRegularFile)
                 .filter(this::isBeforeToday)
                 .forEach(this::archiveFile);
        } catch (IOException e) {
            LOG.error("Unable to archive old results", e);
        }
    }

    private Path getArchive() {
        return parameters.getOutputPath().resolve("archive");
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
            Path path = createFile(result);
            LOG.info("Writing output {}", path.toAbsolutePath());
            try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                writer.writeValue(w, result);
            } catch (IOException e) {
                LOG.error(String.format("Error writing %s", path), e);
            }
        } catch (IOException ex) {
            LOG.error("Error creating output file", ex);
        }
    }

    private Path createFile(AnalysisResult result) throws IOException {
        String prefix = String.format("%s_%s_%s_%s_%s",
                                      result.getModel(),
                                      result.getAxis(),
                                      timeFormat.format(result.getStart()),
                                      dateFormat.format(result.getStart()),
                                      result.getTrack());
//        String prefix = String.format("%s_%s_%s_%s_%s",
//                                      result.getModel(),
//                                      dateFormat.format(result.getStart()),
//                                      timeFormat.format(result.getStart()),
//                                      result.getAxis(),
//                                      result.getTrack());
        Path path = parameters.getOutputPath().resolve(String.format("%s.json", prefix));

        for (int i = 0; true; i++) {
            try {
                Files.createFile(path, filePermissions);
                break;
            } catch (FileAlreadyExistsException ignored) {
                path = parameters.getOutputPath().resolve(String.format("%s_%d.json", prefix, i));
            }
        }
        return path;
    }

    private static FileAttribute<Set<PosixFilePermission>> getPermissions(String s) {
        return PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(s));
    }

    private static LocalDate today() {
        return LocalDate.now(ZONE_ID);
    }

}
