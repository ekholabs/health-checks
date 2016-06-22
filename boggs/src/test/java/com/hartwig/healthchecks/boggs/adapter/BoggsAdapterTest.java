package com.hartwig.healthchecks.boggs.adapter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingExtractor;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsCheck;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsExtractor;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsHealthChecker;
import com.hartwig.healthchecks.boggs.model.report.MappingDataReport;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsDataReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import mockit.Mock;
import mockit.MockUp;

public class BoggsAdapterTest {

    private static final String DUMMY_ID = "DUMMY_ID";
    private static final String DUMMY_STATUS = "DummyStatus";
    private static final String _123 = "123";
    private static final String SOME_ID = "SomeId";
    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Test
    public void verifyAdapterRunning() throws IOException, EmptyFileException {
        final MappingReport dummyMappingReport = getDummyMappingReport();
        final PrestatsReport dummyPrestatsReport = getDummyPrestatsReport();

        new MockUp<MappingHealthChecker>() {
            @Mock
            void $init(final String runDir, final MappingExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() {
                return dummyMappingReport;
            }
        };
        new MockUp<PrestatsHealthChecker>() {
            @Mock
            void $init(final String runDir, final PrestatsExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() {
                return dummyPrestatsReport;
            }
        };

        final BoggsAdapter adapter = new BoggsAdapter();
        adapter.runCheck(DUMMY_RUN_DIR);
    }

    private MappingReport getDummyMappingReport() {
        final MappingDataReport mappingDataReport = new MappingDataReport(1.0d, 2.0d, 2.0d, 1.0d, 0.2d, true);
        return new MappingReport(CheckType.MAPPING, SOME_ID, _123, mappingDataReport);
    }

    private PrestatsReport getDummyPrestatsReport() {
        final PrestatsDataReport prestatsTestDataReport = new PrestatsDataReport(DUMMY_ID, DUMMY_STATUS,
                        PrestatsCheck.DUMMY);
        final PrestatsReport testData = new PrestatsReport(CheckType.PRESTATS);
        testData.addReferenceData(Arrays.asList(prestatsTestDataReport));
        testData.addTumorData(Arrays.asList(prestatsTestDataReport));
        return testData;
    }
}
