package com.hartwig.healthchecks.roz.check;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.dir.TestRunContextFactory;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.SingleValueResult;

import org.junit.Test;

public class SlicedCheckerTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();
    private static final String REF_SAMPLE = "sample1";
    private static final String TUMOR_SAMPLE = "sample2";

    @Test
    public void canAnalyseTypicalSlicedVCF() throws IOException, HealthChecksException {
        RunContext runContext = TestRunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        final SlicedChecker checker = new SlicedChecker();

        final BaseResult result = checker.run(runContext);
        assertEquals(CheckType.SLICED, result.getCheckType());
        final HealthCheck sampleData = ((SingleValueResult) result).getCheck();
        assertEquals(SlicedCheck.SLICED_NUMBER_OF_VARIANTS.toString(), sampleData.getCheckName());
        assertEquals(REF_SAMPLE, sampleData.getSampleId());
        assertEquals("4", sampleData.getValue());
    }

    @Test(expected = IOException.class)
    public void readingNonExistingFileYieldsIOException() throws IOException, HealthChecksException {
        RunContext runContext = TestRunContextFactory.testContext("DoesNotExist", REF_SAMPLE, TUMOR_SAMPLE);

        final SlicedChecker checker = new SlicedChecker();
        checker.run(runContext);
    }
}
