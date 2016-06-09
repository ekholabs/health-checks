package com.hartwig.healthchecks.boggs.adapter;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hartwig.healthchecks.boggs.flagstatreader.SambambaFlagStatParser;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.PatientExtractor;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestastHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsExtractor;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckCategory;

@ResourceWrapper(type = CheckCategory.BOGGS)
public class BoggsAdapter implements HealthCheckAdapter {

	private static Logger LOGGER = LogManager.getLogger(BoggsAdapter.class);

	private static final String IO_ERROR_MSG = "Got IO Exception with message: %s";
	private Report report = JsonReport.getInstance();

	public void runCheck(String runDirectory) {
		try {
			PatientExtractor dataExtractor = new PatientExtractor(new SambambaFlagStatParser());
			HealthChecker checker = new MappingHealthChecker(runDirectory, dataExtractor);
			BaseReport mapping=  checker.runCheck() ;
			report.addReportData(mapping);

			PrestatsExtractor prestatsExtractor = new PrestatsExtractor();
			HealthChecker prestastHealthChecker = new PrestastHealthChecker(runDirectory, prestatsExtractor);
			BaseReport prestatsErrors= prestastHealthChecker.runCheck();
			report.addReportData(prestatsErrors);
		} catch (IOException e) {
			LOGGER.error(String.format(IO_ERROR_MSG, e.getMessage()));
		}
	}
}
