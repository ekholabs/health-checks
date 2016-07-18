package com.hartwig.healthchecks.smitty.adapter;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.reader.Reader;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.smitty.extractor.KinshipExtractor;

@ResourceWrapper(type = CheckCategory.SMITTY)
public class SmittyAdapter implements HealthCheckAdapter {

    private final Report report = JsonReport.getInstance();

    @Override
    public void runCheck(@NotNull final String runDirectory) {
        final Reader kinshipReader = Reader.build();
        final DataExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
        final HealthChecker kinshipChecker = new HealthCheckerImpl(CheckType.KINSHIP, runDirectory, kinshipExtractor);
        final BaseReport kinshipReport = kinshipChecker.runCheck();
        report.addReportData(kinshipReport);
    }
}
