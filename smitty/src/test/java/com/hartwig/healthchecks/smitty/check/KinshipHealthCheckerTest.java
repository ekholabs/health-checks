package com.hartwig.healthchecks.smitty.check;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.common.util.ErrorReport;
import com.hartwig.healthchecks.smitty.extractor.KinshipExtractor;
import com.hartwig.healthchecks.smitty.report.KinshipReport;

import mockit.Expectations;
import mockit.Mocked;

public class KinshipHealthCheckerTest {

    private static final String DUMMY_CHECK = "DUMMY_CHECK";

    private static final String EXPECTED_VALUE = "0.04";

    private static final String WRONG_ERROR_MESSAGE = "Wrong Error Message";

    private static final String WRONG_ERROR = "Wrong Error";

    private static final String DUMMY_ERROR = "DUMMY_ERROR";

    private static final String WRONG_CHECK_NAME = "Wrong Check Name";

    private static final String WRONG_CHECK_STATUS = "Wrong Check status";

    private static final String WRONG_PATIENT_ID_MSG = "Wrong Patient ID";

    private static final String WRONG_NUMBER_OF_CHECKS_MSG = "Wrong Number of checks";

    private static final String WRONG_TYPE_MSG = "Report with wrong type";

    private static final String DUMMY_ID = "DUMMY_ID";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Mocked
    private KinshipExtractor dataExtractor;

    @Test
    public void verifyPrestatsHealthChecker() throws IOException, HealthChecksException {
        final BaseDataReport testDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, EXPECTED_VALUE);

        final KinshipReport testData = new KinshipReport(CheckType.KINSHIP, Arrays.asList(testDataReport));

        final HealthChecker checker = new KinshipHealthChecker(DUMMY_RUN_DIR, dataExtractor);

        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                returns(testData);
            }
        };

        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.KINSHIP, report.getCheckType());
        final List<BaseDataReport> baseDataReports = ((KinshipReport) report).getPatientData();
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, 1, baseDataReports.size());
        assertEquals(WRONG_CHECK_NAME, DUMMY_CHECK, baseDataReports.get(0).getCheckName());
        assertEquals(WRONG_CHECK_STATUS, EXPECTED_VALUE, baseDataReports.get(0).getValue());
        assertEquals(WRONG_PATIENT_ID_MSG, DUMMY_ID, baseDataReports.get(0).getPatientId());
    }

    @Test
    public void verifyPrestatsHealthCheckerIOException() throws IOException, HealthChecksException {
        final HealthChecker checker = new KinshipHealthChecker(DUMMY_RUN_DIR, dataExtractor);
        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                result = new IOException(DUMMY_ERROR);
            }
        };
        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.KINSHIP, report.getCheckType());
        final String error = ((ErrorReport) report).getError();
        final String errorMessage = ((ErrorReport) report).getMessage();

        assertEquals(WRONG_ERROR, IOException.class.getName(), error);
        assertEquals(WRONG_ERROR_MESSAGE, DUMMY_ERROR, errorMessage);
    }

    @Test
    public void verifyPrestatsHealthCheckerEmptyFileException() throws IOException, HealthChecksException {
        final HealthChecker checker = new KinshipHealthChecker(DUMMY_RUN_DIR, dataExtractor);
        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                result = new EmptyFileException(DUMMY_ERROR);
            }
        };
        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.KINSHIP, report.getCheckType());
        final String error = ((ErrorReport) report).getError();
        final String errorMessage = ((ErrorReport) report).getMessage();

        assertEquals(WRONG_ERROR, EmptyFileException.class.getName(), error);
        assertEquals(WRONG_ERROR_MESSAGE, DUMMY_ERROR, errorMessage);
    }

    @Test
    public void verifyPrestatsHealthCheckerMalformedFileException() throws IOException, HealthChecksException {
        final HealthChecker checker = new KinshipHealthChecker(DUMMY_RUN_DIR, dataExtractor);
        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                result = new MalformedFileException(DUMMY_ERROR);
            }
        };
        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.KINSHIP, report.getCheckType());
        final String error = ((ErrorReport) report).getError();
        final String errorMessage = ((ErrorReport) report).getMessage();

        assertEquals(WRONG_ERROR, MalformedFileException.class.getName(), error);
        assertEquals(WRONG_ERROR_MESSAGE, DUMMY_ERROR, errorMessage);
    }
}