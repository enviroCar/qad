package org.envirocar.qad.persistence;

import com.fasterxml.jackson.databind.ObjectWriter;
import org.envirocar.qad.AlgorithmParameters;
import org.envirocar.qad.model.result.AnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Component
public class DirectoryResultPersistence implements ResultPersistence {
    private static final Logger LOG = LoggerFactory.getLogger(DirectoryResultPersistence.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss")
                                                                             .withLocale(Locale.ROOT)
                                                                             .withZone(ZoneId.of("Europe/Berlin"));
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")
                                                                             .withLocale(Locale.ROOT)
                                                                             .withZone(ZoneId.of("Europe/Berlin"));
    private static final FileAttribute<?> filePermissions = getPermissions("rw-r--r--");
    private final AlgorithmParameters parameters;
    private final ObjectWriter writer;

    @Autowired
    public DirectoryResultPersistence(ObjectWriter writer, AlgorithmParameters parameters) {
        this.parameters = Objects.requireNonNull(parameters);
        this.writer = Objects.requireNonNull(writer);
    }

    @Override
    public void persist(AnalysisResult result) {
        try {

            Path path = createFile(result);

            LOG.info("Writing output {}", path.toAbsolutePath());
            try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                writer.writeValue(w, result);
            } catch (IOException e) {
                LOG.error("Error writing " + path, e);
            }
        } catch (IOException ex) {
            LOG.error("Error creating output file", ex);
        }
    }

    private Path createFile(AnalysisResult result) throws IOException {

        Path path;
        String prefix = String.format("%s_%02d_%d_%s_%s_%s",
                                      result.getModel(),
                                      result.getAxis().getId(),
                                      result.getAxis().getDirection(),
                                      TIME_FORMATTER.format(result.getEnd()),
                                      DATE_FORMATTER.format(result.getEnd()),
                                      result.getTrack());

        for (int i = 0; true; i++) {
            try {
                path = Files.createFile(parameters.getOutputPath().resolve(String.format("%s_%d.json", prefix, i)),
                                        filePermissions);
                break;
            } catch (FileAlreadyExistsException ignored) {
            }
        }
        return path;
    }

    private static FileAttribute<Set<PosixFilePermission>> getPermissions(String s) {
        return PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(s));
    }

}
