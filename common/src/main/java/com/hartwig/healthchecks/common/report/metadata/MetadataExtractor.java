package com.hartwig.healthchecks.common.report.metadata;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.PathRegexFinder;
import com.hartwig.healthchecks.common.io.reader.LineReader;

public final class MetadataExtractor {

    private static final String COLON = ":";

    private static final String REGEX_SPLIT = "\t";

    private static final int ZERO = 0;

    private static final int ONE = 1;

    private static final String LAST_LINE = "End Kinship";

    private static final String PIPELINE_VERSION = "Pipeline version:";

    private static final String REGEX = "(.*)(_)(CPCT)(\\d+)(\\.)(log)";

    private static final String PIPELINE_LOG_REGEX = "PipelineCheck.log";

    private final PathRegexFinder pathFinder;

    private final LineReader lineReader;

    public MetadataExtractor(final PathRegexFinder pathFinder, final LineReader lineReader) {
        super();
        this.pathFinder = pathFinder;
        this.lineReader = lineReader;
    }

    public ReportMetadata extractMetadata(final String runDirectory) throws IOException, HealthChecksException {
        final Path logPath = pathFinder.findPath(runDirectory, REGEX);
        final List<String> dateLines = lineReader.readLines(logPath, doesLineStartWith(LAST_LINE));
        final String date = dateLines.get(ZERO).split(REGEX_SPLIT)[ONE];
        final Path pipelineLog = pathFinder.findPath(runDirectory, PIPELINE_LOG_REGEX);
        final List<String> verionsLines = lineReader.readLines(pipelineLog, doesLineStartWith(PIPELINE_VERSION));
        final String pipelineVersion = verionsLines.get(ZERO).split(COLON)[ONE];
        return new ReportMetadata(date.trim(), pipelineVersion.trim());
    }

    private static Predicate<String> doesLineStartWith(final String prefix) {
        return line -> line.startsWith(prefix);
    }
}
