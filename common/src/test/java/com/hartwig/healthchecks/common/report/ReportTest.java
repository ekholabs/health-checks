package com.hartwig.healthchecks.common.report;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.GenerateReportException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.PathRegexFinder;
import com.hartwig.healthchecks.common.io.reader.LineReader;
import com.hartwig.healthchecks.common.report.metadata.MetadataExtractor;
import com.hartwig.healthchecks.common.report.metadata.ReportMetadata;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.util.PropertiesUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import mockit.Mocked;
import mockit.NonStrictExpectations;

public class ReportTest {

    private static final String ZERO = "0";
    private static final String SOME_VERSION = "v1.7";
    private static final String SOME_DATE = "2016-Jul-09T15.41.42";
    private static final String TMP_DIR = "/tmp";
    private static final String ONE = "1";
    private static final String RUN_DIR = "runDir";
    private static final String REPORT_DIR = "report.dir";

    @Test
    public void generateStOutReport(@Mocked final MetadataExtractor metadataExtractor,
            @Mocked final PropertiesUtil propertiesUtil) throws IOException, HealthChecksException {
        new NonStrictExpectations() {
            {
                new MetadataExtractor((PathRegexFinder) any, (LineReader) any);
                result = metadataExtractor;
                times = 1;

                propertiesUtil.getProperty(REPORT_DIR);
                returns(TMP_DIR);

                metadataExtractor.extractMetadata(RUN_DIR);
                returns(new ReportMetadata(SOME_DATE, SOME_VERSION));
            }
        };

        final Report report = StandardOutputReport.getInstance();

        final BaseResult baseConfig1 = new TestResult(CheckType.MAPPING);
        report.addResult(baseConfig1);

        final BaseResult baseConfig2 = new TestResult(CheckType.PRESTATS);
        report.addResult(baseConfig2);

        final Optional<String> jsonOptional = report.generateReport(RUN_DIR);
        assertNotNull(jsonOptional);
        assertTrue(jsonOptional.isPresent());
        final String json = jsonOptional.get();
        assertTrue(json.contains(SOME_DATE));
        assertTrue(json.contains(SOME_VERSION));
    }

    @Test
    public void generateReportMetadataIOException(@Mocked final MetadataExtractor metadataExtractor,
            @Mocked final PropertiesUtil propertiesUtil) throws IOException, HealthChecksException {
        new NonStrictExpectations() {
            {
                new MetadataExtractor((PathRegexFinder) any, (LineReader) any);
                result = metadataExtractor;
                times = 1;

                propertiesUtil.getProperty(REPORT_DIR);
                returns(TMP_DIR);

                metadataExtractor.extractMetadata(RUN_DIR);
                result = new IOException();
            }
        };

        final Report report = StandardOutputReport.getInstance();

        final BaseResult baseConfig1 = new TestResult(CheckType.MAPPING);
        report.addResult(baseConfig1);

        final BaseResult baseConfig2 = new TestResult(CheckType.PRESTATS);
        report.addResult(baseConfig2);

        final Optional<String> jsonOptional = report.generateReport(RUN_DIR);
        assertNotNull(jsonOptional);
        assertTrue(jsonOptional.isPresent());
        final String json = jsonOptional.get();
        assertFalse(json.contains(SOME_DATE));
        assertFalse(json.contains(SOME_VERSION));
    }

    @Test
    public void generateReportMetadataHealthCheckException(@Mocked final MetadataExtractor metadataExtractor,
            @Mocked final PropertiesUtil propertiesUtil) throws IOException, HealthChecksException {
        new NonStrictExpectations() {
            {
                new MetadataExtractor((PathRegexFinder) any, (LineReader) any);
                result = metadataExtractor;
                times = 1;

                propertiesUtil.getProperty(REPORT_DIR);
                returns(TMP_DIR);

                metadataExtractor.extractMetadata(RUN_DIR);
                result = new HealthChecksException("");
            }
        };

        final Report report = StandardOutputReport.getInstance();

        final BaseResult baseConfig1 = new TestResult(CheckType.MAPPING);
        report.addResult(baseConfig1);

        final BaseResult baseConfig2 = new TestResult(CheckType.PRESTATS);
        report.addResult(baseConfig2);

        final Optional<String> jsonOptional = report.generateReport(RUN_DIR);
        assertNotNull(jsonOptional);
        assertTrue(jsonOptional.isPresent());
        final String json = jsonOptional.get();
        assertFalse(json.contains(SOME_DATE));
        assertFalse(json.contains(SOME_VERSION));
    }

    @Test(expected = GenerateReportException.class)
    public void generateReportException(@Mocked final MetadataExtractor metadataExtractor,
            @Mocked final PropertiesUtil propertiesUtil, @Mocked final FileWriter fileWriter)
            throws IOException, HealthChecksException {
        new NonStrictExpectations() {
            {
                PropertiesUtil.getInstance();
                result = propertiesUtil;
                times = 1;

                new MetadataExtractor((PathRegexFinder) any, (LineReader) any);
                result = metadataExtractor;
                times = 1;

                propertiesUtil.getProperty(REPORT_DIR);
                returns(TMP_DIR);

                metadataExtractor.extractMetadata(RUN_DIR);
                returns(new ReportMetadata(SOME_DATE, SOME_VERSION));

                new FileWriter(new File(anyString));
                result = fileWriter;
                times = 1;

                fileWriter.write(anyString);
                result = new IOException("");

            }
        };
        final Report report = JsonReport.getInstance();

        final BaseResult baseConfig1 = new TestResult(CheckType.MAPPING);
        report.addResult(baseConfig1);

        final Optional<String> location = report.generateReport(RUN_DIR);

        assertNotNull(location);
        assertFalse(location.isPresent());
    }

    private static class TestResult extends BaseResult {
        TestResult(@NotNull final CheckType checkType) {
            super(checkType);
        }
    }
}
