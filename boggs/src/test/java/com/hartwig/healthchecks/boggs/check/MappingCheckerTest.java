package com.hartwig.healthchecks.boggs.check;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.dir.TestRunContextFactory;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.PatientResult;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class MappingCheckerTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();

    private static final String REF_SAMPLE = "sample1";
    private static final String TUMOR_SAMPLE = "sample2";
    private static final String EMPTY_FLAGSTAT_SAMPLE = "sample3";
    private static final String EMPTY_FASTQC_SAMPLE = "sample4";
    private static final String NON_EXISTING_SAMPLE = "sample5";

    private final MappingChecker checker = new MappingChecker();

    @Test
    public void correctInputYieldsCorrectOutput() throws IOException, HealthChecksException {
        final RunContext runContext = TestRunContextFactory.forTest(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);
        final BaseResult result = checker.tryRun(runContext);
        assertResult(result);
    }

    @Test
    public void errorRunYieldsCorrectNumberOfChecks() {
        final RunContext runContext = TestRunContextFactory.forTest(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);
        final PatientResult result = (PatientResult) checker.errorRun(runContext);
        assertEquals(6, result.getRefSampleChecks().size());
        assertEquals(6, result.getTumorSampleChecks().size());
    }

    @Test(expected = EmptyFileException.class)
    public void emptyFlagStatYieldsEmptyFileException() throws IOException, HealthChecksException {
        final RunContext runContext = TestRunContextFactory.forTest(RUN_DIRECTORY, EMPTY_FLAGSTAT_SAMPLE,
                EMPTY_FLAGSTAT_SAMPLE);
        checker.tryRun(runContext);
    }

    @Test(expected = EmptyFileException.class)
    public void emptyTotalSequenceFileYieldsEmptyFileException() throws IOException, HealthChecksException {
        final RunContext runContext = TestRunContextFactory.forTest(RUN_DIRECTORY, EMPTY_FASTQC_SAMPLE,
                EMPTY_FASTQC_SAMPLE);
        checker.tryRun(runContext);
    }

    @Test(expected = IOException.class)
    public void nonExistingFileYieldsIOException() throws IOException, HealthChecksException {
        final RunContext runContext = TestRunContextFactory.forTest(RUN_DIRECTORY, NON_EXISTING_SAMPLE,
                NON_EXISTING_SAMPLE);
        checker.tryRun(runContext);
    }

    private static void assertResult(@NotNull final BaseResult result) {
        assertEquals(CheckType.MAPPING, result.getCheckType());
        assertRefSampleData(((PatientResult) result).getRefSampleChecks());
        assertTumorSampleData(((PatientResult) result).getTumorSampleChecks());
    }

    private static void assertRefSampleData(@NotNull final List<HealthCheck> checks) {
        final HealthCheck mappedCheck = extractHealthCheck(checks, MappingCheck.MAPPING_PERCENTAGE_MAPPED);
        assertEquals("0.9693877551020408", mappedCheck.getValue());
        assertEquals(REF_SAMPLE, mappedCheck.getSampleId());

        final HealthCheck mateCheck = extractHealthCheck(checks, MappingCheck.MAPPING_PROPORTION_MAPPED_DIFFERENT_CHR);
        assertEquals("0.010526315789473684", mateCheck.getValue());

        final HealthCheck properCheck = extractHealthCheck(checks,
                MappingCheck.MAPPING_PROPERLY_PAIRED_PROPORTION_OF_MAPPED);
        assertEquals("0.9473684210526315", properCheck.getValue());

        final HealthCheck singletonCheck = extractHealthCheck(checks, MappingCheck.MAPPING_PROPORTION_SINGLETON);
        assertEquals("0.010526315789473684", singletonCheck.getValue());

        final HealthCheck duplicateCheck = extractHealthCheck(checks,
                MappingCheck.MAPPING_MARKDUP_PROPORTION_DUPLICATES);
        assertEquals("0.10204081632653061", duplicateCheck.getValue());

        final HealthCheck proportionCheck = extractHealthCheck(checks,
                MappingCheck.MAPPING_PROPORTION_READ_VS_TOTAL_SEQUENCES);
        assertEquals("0.97", proportionCheck.getValue());
    }

    private static void assertTumorSampleData(@NotNull final List<HealthCheck> checks) {
        final HealthCheck mappedCheck = extractHealthCheck(checks, MappingCheck.MAPPING_PERCENTAGE_MAPPED);
        assertEquals("0.875", mappedCheck.getValue());
        assertEquals(TUMOR_SAMPLE, mappedCheck.getSampleId());

        final HealthCheck mateCheck = extractHealthCheck(checks, MappingCheck.MAPPING_PROPORTION_MAPPED_DIFFERENT_CHR);
        assertEquals("0.02857142857142857", mateCheck.getValue());

        final HealthCheck properCheck = extractHealthCheck(checks,
                MappingCheck.MAPPING_PROPERLY_PAIRED_PROPORTION_OF_MAPPED);
        assertEquals("0.7142857142857143", properCheck.getValue());

        final HealthCheck singletonCheck = extractHealthCheck(checks, MappingCheck.MAPPING_PROPORTION_SINGLETON);
        assertEquals("0.07142857142857142", singletonCheck.getValue());

        final HealthCheck duplicateCheck = extractHealthCheck(checks,
                MappingCheck.MAPPING_MARKDUP_PROPORTION_DUPLICATES);
        assertEquals("0.125", duplicateCheck.getValue());

        final HealthCheck proportionCheck = extractHealthCheck(checks,
                MappingCheck.MAPPING_PROPORTION_READ_VS_TOTAL_SEQUENCES);
        assertEquals("0.7", proportionCheck.getValue());
    }

    @NotNull
    private static HealthCheck extractHealthCheck(@NotNull final List<HealthCheck> checks,
            @NotNull final MappingCheck checkName) {
        final Optional<HealthCheck> optCheck = checks.stream().filter(
                check -> check.getCheckName().equals(checkName.toString())).findFirst();

        assert optCheck.isPresent();
        return optCheck.get();
    }
}
