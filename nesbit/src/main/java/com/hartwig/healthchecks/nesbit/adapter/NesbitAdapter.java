package com.hartwig.healthchecks.nesbit.adapter;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.reader.FilteredReader;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckCategory;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.nesbit.extractor.SomaticExtractor;
import com.hartwig.healthchecks.nesbit.extractor.GermlineExtractor;

@ResourceWrapper(type = CheckCategory.NESBIT)
public class NesbitAdapter implements HealthCheckAdapter {

    private final Report report = JsonReport.getInstance();

    @Override
    public void runCheck(@NotNull final String runDirectory) {
        final FilteredReader germlineReader = FilteredReader.build();
        final DataExtractor germlineExtractor = new GermlineExtractor(germlineReader);
        final HealthChecker germline = new HealthCheckerImpl(CheckType.GERMLINE, runDirectory, germlineExtractor);
        final BaseReport germlineReport = germline.runCheck();
        report.addReportData(germlineReport);

        final FilteredReader somaticReader = FilteredReader.build();
        final DataExtractor somaticExtractor = new SomaticExtractor(somaticReader);
        final HealthChecker somatic = new HealthCheckerImpl(CheckType.SOMATIC, runDirectory, somaticExtractor);
        final BaseReport somaticReport = somatic.runCheck();
        report.addReportData(somaticReport);
    }
}