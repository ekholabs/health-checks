package com.hartwig.healthchecks.flint.adapter;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckCategory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.ErrorHandlingChecker;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.flint.check.InsertSizeMetricsChecker;
import com.hartwig.healthchecks.flint.check.SummaryMetricsChecker;
import com.hartwig.healthchecks.flint.check.WGSMetricsChecker;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ResourceWrapper(type = CheckCategory.FLINT)
public class FlintAdapter extends AbstractHealthCheckAdapter {

    @Override
    public void runCheck(@NotNull final RunContext runContext, @NotNull final String reportType) {
        final HealthCheckReportFactory healthCheckReportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
        final Report report = healthCheckReportFactory.create();

        final HealthChecker insertSizeExtractor = new InsertSizeMetricsChecker(runContext);
        final ErrorHandlingChecker insertSizeChecker = new ErrorHandlingChecker(CheckType.INSERT_SIZE,
                insertSizeExtractor);
        final BaseResult insertSizeReport = insertSizeChecker.checkedRun();
        report.addResult(insertSizeReport);

        final HealthChecker summaryExtractor = new SummaryMetricsChecker(runContext);
        final ErrorHandlingChecker summaryChecker = new ErrorHandlingChecker(CheckType.SUMMARY_METRICS,
                summaryExtractor);
        final BaseResult summaryReport = summaryChecker.checkedRun();
        report.addResult(summaryReport);

        final HealthChecker wgsExtractor = new WGSMetricsChecker(runContext);
        final ErrorHandlingChecker coverageChecker = new ErrorHandlingChecker(CheckType.COVERAGE, wgsExtractor);
        final BaseResult coverageReport = coverageChecker.checkedRun();
        report.addResult(coverageReport);
    }
}
