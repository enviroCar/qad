package org.envirocar.qad.reanalyze;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.envirocar.qad.AlgorithmParameters;
import org.envirocar.qad.model.result.AnalysisResult;
import org.envirocar.qad.persistence.DirectoryResultPersistence;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

@Primary
@Service
@Profile(ReanalyzeApplication.REANALYZE_PROFILE)
class ReanalyzeDirectoryResultPersistence extends DirectoryResultPersistence {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", LOCALE)
                                                                        .withZone(ZONE_ID);

    ReanalyzeDirectoryResultPersistence(ObjectReader reader, ObjectWriter writer, AlgorithmParameters parameters) {
        super(reader, writer, parameters);
    }

    @Override
    protected Path getDirectory(AnalysisResult result) {
        if (result == null) {
            return getParameters().getOutputPath();
        }
        String model = result.getModel().getValue();
        String date = FORMATTER.format(result.getStart());
        return getParameters().getOutputPath().resolve(model).resolve(date);
    }
}
