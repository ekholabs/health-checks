package com.hartwig.healthchecks;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.exception.NotFoundException;
import com.hartwig.healthchecks.common.util.Report;
import com.hartwig.healthchecks.util.adapter.HealthChecksFlyweight;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.Collection;

public class HealthChecksApplication {

    private static final String RUN_DIRECTORY = "rundir";
    private static final String CHECK_TYPE = "checktype";
    private static final String ALL_CHECKS = "all";
    private static Logger LOGGER = LogManager.getLogger(HealthChecksApplication.class);
    private String runDirectory;
    private String checkType;

    public HealthChecksApplication(String runDirectory, String checkType) {
        this.runDirectory = runDirectory;
        this.checkType = checkType;
    }

    public static void main(String[] args) throws ParseException, IOException {
        Options options = createOptions();
        CommandLine cmd = createCommandLine(args, options);

        String runDirectory = cmd.getOptionValue(RUN_DIRECTORY);
        String checkType = cmd.getOptionValue(CHECK_TYPE);

        if (runDirectory == null || checkType == null) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Health-Checks", options);
        }

        HealthChecksApplication healthChecksApplication = new HealthChecksApplication(runDirectory, checkType);
        healthChecksApplication.processHealthChecks();
    }

    @NotNull
    private static CommandLine createCommandLine(@NotNull String[] args, @NotNull Options options)
            throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    @NotNull
    private static Options createOptions() {
        Options options = new Options();
        options.addOption(RUN_DIRECTORY, true, "The path containing the data for a single run");
        options.addOption(CHECK_TYPE, true, "The type of check to b executed for a single run");
        return options;
    }

    public void processHealthChecks() {
        if (checkType.equals(ALL_CHECKS)) {
            executeAllcheck(runDirectory);
        } else {
            HealthChecksFlyweight flyweight = HealthChecksFlyweight.getInstance();
            try {
                HealthCheckAdapter healthCheckAdapter = flyweight.getAdapter(checkType);
                healthCheckAdapter.runCheck(runDirectory);
            } catch (NotFoundException e) {
                LOGGER.error(e.getMessage());
            }
        }
        Report.getInstance().generateReport();
    }

    protected void executeAllcheck(@NotNull String runDirectory) {
        HealthChecksFlyweight flyweight = HealthChecksFlyweight.getInstance();
        Collection<HealthCheckAdapter> adapters = flyweight.getAllAdapters();

        Observable.from(adapters)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        (h) -> h.runCheck(runDirectory),
                        (t) -> t.printStackTrace(),
                        () -> {
                            Report.getInstance().generateReport();
                        }
                );

    }
}