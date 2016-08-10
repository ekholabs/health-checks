package com.hartwig.healthchecks.flint.extractor;

import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.REF_SAMPLE_SUFFIX;
import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.SAMPLE_PREFIX;
import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.TUM_SAMPLE_SUFFIX;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.path.SamplePathData;
import com.hartwig.healthchecks.common.io.reader.SampleFinderAndReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class InsertSizeMetricsExtractor extends AbstractFlintExtractor {

    private static final Logger LOGGER = LogManager.getLogger(InsertSizeMetricsExtractor.class);
    private static final String INSERT_SIZE_METRICS_EXTENSION = ".insert_size_metrics";
    private static final String UNDERSCORE = "_";
    private static final String QC_STATS = "QCStats";
    private static final String DEDUP_SAMPLE_SUFFIX = "dedup";

    @NotNull
    private final SampleFinderAndReader reader;

    public InsertSizeMetricsExtractor(@NotNull final SampleFinderAndReader reader) {
        super();
        this.reader = reader;
    }

    @NotNull
    @Override
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<BaseDataReport> referenceSample = getSampleData(runDirectory, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSample = getSampleData(runDirectory, TUM_SAMPLE_SUFFIX);

        return new SampleReport(CheckType.INSERT_SIZE, referenceSample, tumorSample);
    }

    @NotNull
    private List<BaseDataReport> getSampleData(@NotNull final String runDirectory, @NotNull final String sampleType)
            throws IOException, HealthChecksException {
        final String suffix = sampleType + UNDERSCORE + DEDUP_SAMPLE_SUFFIX;
        final String path = runDirectory + File.separator + QC_STATS;
        final SamplePathData samplePath = new SamplePathData(path, SAMPLE_PREFIX, suffix,
                INSERT_SIZE_METRICS_EXTENSION);
        final List<String> lines = reader.readLines(samplePath);
        final String sampleId = getSampleId(suffix, lines, PICARD_SAMPLE_IDENTIFIER);

        final BaseDataReport medianReport = getValue(lines, suffix, sampleId,
                InsertSizeMetricsCheck.MAPPING_MEDIAN_INSERT_SIZE);
        final BaseDataReport width70PerReport = getValue(lines, suffix, sampleId,
                InsertSizeMetricsCheck.MAPPING_WIDTH_OF_70_PERCENT);
        return Arrays.asList(medianReport, width70PerReport);
    }

    @NotNull
    private static BaseDataReport getValue(@NotNull final List<String> lines, @NotNull final String suffix,
            @NotNull final String sampleId, @NotNull final InsertSizeMetricsCheck check) throws LineNotFoundException {
        final String value = getValueFromLine(lines, suffix, check.getFieldName(), check.getColumnIndex());
        final BaseDataReport baseDataReport = new BaseDataReport(sampleId, check.toString(), value);
        baseDataReport.log(LOGGER);
        return baseDataReport;
    }
}
