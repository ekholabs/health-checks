package com.hartwig.healthchecks.smitty.extractor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.data.BaseReport;
import com.hartwig.healthchecks.common.data.SingleValueReport;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.reader.FileFinderAndReader;
import com.hartwig.healthchecks.common.report.HealthCheck;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class KinshipExtractor implements DataExtractor {

    private static final Logger LOGGER = LogManager.getLogger(KinshipExtractor.class);

    private static final String MALFORMED_FILE_MSG = "Malformed %s file is path %s -> %s lines found was expecting %s";

    private static final String KINSHIP_EXTENSION = ".kinship";
    private static final int EXPECTED_NUM_LINES = 2;
    private static final String COLUMN_SEPARATOR = "\t";
    private static final int KINSHIP_COLUMN = 7;

    @NotNull
    private final FileFinderAndReader kinshipReader = FileFinderAndReader.build();
    @NotNull
    private final RunContext runContext;

    public KinshipExtractor(@NotNull final RunContext runContext) {
        this.runContext = runContext;
    }

    @Override
    @NotNull
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<String> kinshipLines = kinshipReader.readLines(runContext.runDirectory(), KINSHIP_EXTENSION);
        if (kinshipLines.size() != EXPECTED_NUM_LINES) {
            throw new MalformedFileException(
                    String.format(MALFORMED_FILE_MSG, KinshipCheck.KINSHIP_TEST.toString(), runDirectory,
                            kinshipLines.size(), EXPECTED_NUM_LINES));
        }
        final Optional<HealthCheck> optBaseDataReport = kinshipLines.stream().skip(1).map(line -> {
            final String[] values = line.split(COLUMN_SEPARATOR);
            return new HealthCheck(runContext.tumorSample(), KinshipCheck.KINSHIP_TEST.toString(),
                    values[KINSHIP_COLUMN]);
        }).findFirst();

        assert optBaseDataReport.isPresent();

        optBaseDataReport.get().log(LOGGER);
        return new SingleValueReport(CheckType.KINSHIP, optBaseDataReport.get());
    }
}
