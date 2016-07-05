package com.hartwig.healthchecks.flint.extractor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.flint.reader.InsertSizeMetricsReader;
import com.hartwig.healthchecks.flint.report.InsertSizeMetricsReport;

public class InsertSizeMetricsExtractor extends AbstractFlintExtractor {

    private final InsertSizeMetricsReader reader;

    public InsertSizeMetricsExtractor(final InsertSizeMetricsReader reader) {
        super();
        this.reader = reader;
    }

    @Override
    public BaseReport extractFromRunDirectory(final String runDirectory) throws IOException, HealthChecksException {
        final List<BaseDataReport> referenceSample = getSampleData(runDirectory, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSample = getSampleData(runDirectory, TUM_SAMPLE_SUFFIX);

        return new InsertSizeMetricsReport(CheckType.INSERT_SIZE, referenceSample, tumorSample);
    }

    private List<BaseDataReport> getSampleData(final String runDirectory, final String sampleType)
                    throws IOException, HealthChecksException {
        final String suffix = sampleType + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final String path = runDirectory + File.separator + QC_STATS;
        final List<String> lines = reader.readLines(path, SAMPLE_PREFIX, suffix);
        if (lines.isEmpty()) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, suffix, runDirectory));
        }

        final String patientId = getPatientId(suffix, lines, INPUT);

        final BaseDataReport medianReport = getValue(lines, suffix, patientId,
                        InsertSizeMetricsCheck.MAPPING_MEDIAN_INSERT_SIZE);
        final BaseDataReport width70PerReport = getValue(lines, suffix, patientId,
                        InsertSizeMetricsCheck.MAPPING_WIDTH_OF_70_PERCENT);
        return Arrays.asList(medianReport, width70PerReport);
    }

    private BaseDataReport getValue(final List<String> lines, final String suffix, final String patientId,
                    final InsertSizeMetricsCheck check) throws LineNotFoundException {
        final String value = getValueFromLine(lines, suffix, check.getFieldName(), check.getIndex());
        final BaseDataReport baseDataReport = new BaseDataReport(patientId, check.toString(), value);
        logBaseDataReport(baseDataReport);
        return baseDataReport;
    }

    private String getValueFromLine(final List<String> lines, final String suffix, final String filter,
                    final int fieldIndex) throws LineNotFoundException {
        final Integer index = findLineIndex(suffix, lines, filter);
        final String line = lines.get(index + ONE);
        final String[] lineValues = line.split(SEPERATOR_REGEX);
        return lineValues[fieldIndex];
    }
}