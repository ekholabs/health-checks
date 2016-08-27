package com.hartwig.healthchecks.nesbit.extractor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.data.BaseReport;
import com.hartwig.healthchecks.common.data.PatientReport;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.path.RunContextFactory;
import com.hartwig.healthchecks.common.report.HealthCheck;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class GermlineExtractorTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();
    private static final String REF_SAMPLE = "CPCT11111111R";
    private static final String TUMOR_SAMPLE = "CPCT11111111T";

    @Test
    public void canCountSNPAndIndels() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        final GermlineExtractor extractor = new GermlineExtractor(runContext);
        final BaseReport report = extractor.extractFromRunDirectory("");

        assertEquals(CheckType.GERMLINE, report.getCheckType());
        final List<HealthCheck> refData = ((PatientReport) report).getRefSampleChecks();
        final List<HealthCheck> tumData = ((PatientReport) report).getTumorSampleChecks();

        assertSampleData(refData, 55, 4);
        assertSampleData(tumData, 74, 4);
    }

    private static void assertSampleData(@NotNull final List<HealthCheck> sampleData, final long expectedCountSNP,
            final long expectedCountIndels) {
        assertEquals(2, sampleData.size());

        final Optional<HealthCheck> snpReport = sampleData.stream().filter(
                data -> data.getCheckName().equals(GermlineCheck.VARIANTS_GERMLINE_SNP.toString())).findFirst();
        assert snpReport.isPresent();
        assertEquals(Long.toString(expectedCountSNP), snpReport.get().getValue());

        final Optional<HealthCheck> indelReport = sampleData.stream().filter(
                data -> data.getCheckName().equals(GermlineCheck.VARIANTS_GERMLINE_INDELS.toString())).findFirst();
        assert indelReport.isPresent();
        assertEquals(Long.toString(expectedCountIndels), indelReport.get().getValue());
    }
}
