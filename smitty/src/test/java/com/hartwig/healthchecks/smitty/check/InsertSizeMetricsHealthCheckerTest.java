package com.hartwig.healthchecks.smitty.check;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.common.util.ErrorReport;
import com.hartwig.healthchecks.smitty.extractor.InsertSizeMetricsExtractor;
import com.hartwig.healthchecks.smitty.report.InsertSizeMetricsReport;

import mockit.Expectations;
import mockit.Mocked;

public class InsertSizeMetricsHealthCheckerTest {

    private static final String DUMMY_CHECK = "DUMMY_CHECK";

    private static final String REF_VALUE = "409";

    private static final String TUM_VALUE = "309";

    private static final String WRONG_ERROR_MESSAGE = "Wrong Error Message";

    private static final String WRONG_ERROR = "Wrong Error";

    private static final String DUMMY_ERROR = "DUMMY_ERROR";

    private static final String WRONG_CHECK_NAME = "Wrong Check Name";

    private static final String WRONG_CHECK_STATUS = "Wrong Check status";

    private static final String WRONG_PATIENT_ID_MSG = "Wrong Patient ID";

    private static final String WRONG_TYPE_MSG = "Report with wrong type";

    private static final String DUMMY_ID = "DUMMY_ID";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Mocked
    private InsertSizeMetricsExtractor dataExtractor;

    @Test
    public void verifyHealthChecker() throws IOException, HealthChecksException {
        final BaseReport testData = createTestData();
        final HealthChecker checker = new InsertSizeMetricsHealthChecker(DUMMY_RUN_DIR, dataExtractor);
        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                returns(testData);
            }
        };

        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.INSERT_SIZE, report.getCheckType());
        final List<BaseDataReport> refReports = ((InsertSizeMetricsReport) report).getReferenceSample();
        final List<BaseDataReport> tumReports = ((InsertSizeMetricsReport) report).getTumorSample();
        assertBaseData(refReports, DUMMY_ID, DUMMY_CHECK, REF_VALUE);
        assertBaseData(tumReports, DUMMY_ID, DUMMY_CHECK, TUM_VALUE);

    }

    @Test
    public void verifyHealthCheckerIOException() throws IOException, HealthChecksException {
        final HealthChecker checker = new InsertSizeMetricsHealthChecker(DUMMY_RUN_DIR, dataExtractor);
        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                result = new IOException(DUMMY_ERROR);
            }
        };
        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.INSERT_SIZE, report.getCheckType());
        final String error = ((ErrorReport) report).getError();
        final String errorMessage = ((ErrorReport) report).getMessage();

        assertEquals(WRONG_ERROR, IOException.class.getName(), error);
        assertEquals(WRONG_ERROR_MESSAGE, DUMMY_ERROR, errorMessage);
    }

    @Test
    public void verifyHealthCheckerEmptyFileException() throws IOException, HealthChecksException {
        final HealthChecker checker = new InsertSizeMetricsHealthChecker(DUMMY_RUN_DIR, dataExtractor);
        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                result = new EmptyFileException(DUMMY_ERROR);
            }
        };
        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.INSERT_SIZE, report.getCheckType());
        final String error = ((ErrorReport) report).getError();
        final String errorMessage = ((ErrorReport) report).getMessage();

        assertEquals(WRONG_ERROR, EmptyFileException.class.getName(), error);
        assertEquals(WRONG_ERROR_MESSAGE, DUMMY_ERROR, errorMessage);
    }

    @Test
    public void verifyHealthCheckerLineNotFoundException() throws IOException, HealthChecksException {
        final HealthChecker checker = new InsertSizeMetricsHealthChecker(DUMMY_RUN_DIR, dataExtractor);
        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                result = new LineNotFoundException(DUMMY_ERROR);
            }
        };
        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.INSERT_SIZE, report.getCheckType());
        final String error = ((ErrorReport) report).getError();
        final String errorMessage = ((ErrorReport) report).getMessage();

        assertEquals(WRONG_ERROR, LineNotFoundException.class.getName(), error);
        assertEquals(WRONG_ERROR_MESSAGE, DUMMY_ERROR, errorMessage);
    }

    private BaseReport createTestData() {
        final BaseDataReport testDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, REF_VALUE);
        final BaseDataReport secTestDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, TUM_VALUE);

        return new InsertSizeMetricsReport(CheckType.INSERT_SIZE, Arrays.asList(testDataReport),
                        Arrays.asList(secTestDataReport));
    }

    private void assertBaseData(final List<BaseDataReport> reports, final String patientId, final String check,
                    final String expectedValue) {
        final BaseDataReport value = reports.stream().filter(p -> p.getCheckName().equals(check)).findFirst().get();
        assertEquals(WRONG_CHECK_NAME, check, value.getCheckName());
        assertEquals(WRONG_CHECK_STATUS, expectedValue, value.getValue());
        assertEquals(WRONG_PATIENT_ID_MSG, patientId, value.getPatientId());
    }
}
