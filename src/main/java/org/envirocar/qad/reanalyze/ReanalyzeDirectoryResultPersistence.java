package org.envirocar.qad.reanalyze;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.envirocar.qad.QADParameters;
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
public class ReanalyzeDirectoryResultPersistence extends DirectoryResultPersistence {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", LOCALE)
                                                                        .withZone(ZONE_ID);

    public ReanalyzeDirectoryResultPersistence(ObjectReader reader, ObjectWriter writer, QADParameters parameters) {
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
