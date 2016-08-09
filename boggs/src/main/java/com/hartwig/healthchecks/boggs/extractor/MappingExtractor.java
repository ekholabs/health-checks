package com.hartwig.healthchecks.boggs.extractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStats;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.function.DivisionOperator;
import com.hartwig.healthchecks.common.io.extractor.AbstractTotalSequenceExtractor;
import com.hartwig.healthchecks.common.io.path.SamplePathFinder;
import com.hartwig.healthchecks.common.io.reader.ZipFilesReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class MappingExtractor extends AbstractTotalSequenceExtractor {

    private static final Logger LOGGER = LogManager.getLogger(MappingExtractor.class);

    private static final long MILLIS_FACTOR = 10000L;
    private static final double HUNDRED_FACTOR = 100D;
    private static final int DOUBLE_SEQUENCE = 2;

    private static final String FLAGSTAT_FILE_FILTER = ".realign";

    @NotNull
    private final FlagStatParser flagstatParser;
    @NotNull
    private final ZipFilesReader zipFileReader;
    @NotNull
    private final SamplePathFinder samplePathFinder;

    public MappingExtractor(@NotNull final FlagStatParser flagstatParser, @NotNull final ZipFilesReader zipFileReader,
                    @NotNull final SamplePathFinder samplePathFinder) {
        super();
        this.flagstatParser = flagstatParser;
        this.zipFileReader = zipFileReader;
        this.samplePathFinder = samplePathFinder;
    }

    @Override
    @NotNull
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
                    throws IOException, HealthChecksException {
        final List<BaseDataReport> refSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, TUM_SAMPLE_SUFFIX);

        return new SampleReport(CheckType.MAPPING, refSampleData, tumorSampleData);
    }

    @NotNull
    private List<BaseDataReport> getSampleData(@NotNull final String runDirectory, @NotNull final String prefix,
                    @NotNull final String suffix) throws IOException, EmptyFileException {
        final Path sampleFile = samplePathFinder.findPath(runDirectory, prefix, suffix);

        final String sampleId = sampleFile.getFileName().toString();
        final Long totalSequences = sumOfTotalSequences(sampleFile, zipFileReader);
        final List<BaseDataReport> mappingChecks = getFlagStatsData(sampleId, sampleFile, totalSequences.toString());

        logBaseDataReports(LOGGER, mappingChecks);
        return mappingChecks;
    }

    private List<BaseDataReport> getFlagStatsData(@NotNull final String sampleId, @NotNull final Path runDirPath,
                    @NotNull final String totalSequences) throws IOException, EmptyFileException {
        final FlagStatData flagstatData = flagstatParser.parse(runDirPath + File.separator + MAPPING + File.separator,
                FLAGSTAT_FILE_FILTER);
        // KODU: Flagstat data can be null!
        if (flagstatData == null) {
            throw new EmptyFileException(FLAGSTAT_SUFFIX, runDirPath.toString());
        }
        final List<BaseDataReport> mappingDataReports = new ArrayList<>();
        final List<FlagStats> passed = flagstatData.getPassedStats();

        final BaseDataReport mappedDataReport = generateMappedDataReport(sampleId, passed);
        mappingDataReports.add(mappedDataReport);

        final BaseDataReport properDataReport = generateProperDataReport(sampleId, passed);
        mappingDataReports.add(properDataReport);

        final BaseDataReport singletonDataReport = generateSingletonDataReport(sampleId, passed);
        mappingDataReports.add(singletonDataReport);

        final BaseDataReport mateMappedDataReport = generateMateMappedDataReport(sampleId, passed);
        mappingDataReports.add(mateMappedDataReport);

        final BaseDataReport duplicateDataReport = generateDuplicateDataReport(sampleId, passed);
        mappingDataReports.add(duplicateDataReport);

        final BaseDataReport isAllReadDataReport = generateIsAllReadDataReport(sampleId, totalSequences, passed);
        mappingDataReports.add(isAllReadDataReport);

        return mappingDataReports;
    }

    @NotNull
    private static BaseDataReport generateMappedDataReport(@NotNull final String sampleId,
                    @NotNull final List<FlagStats> passed) {
        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final DivisionOperator mappedStatCalc = FlagStatsType.MAPPED_INDEX.getCalculableInstance();
        final double mappedPercentage = toPercentage(
                        mappedStatCalc.calculate(mappedStat.getValue(), totalStat.getValue()));

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PERCENTAGE_MAPPED.toString(),
                String.valueOf(mappedPercentage));
    }

    @NotNull
    private static BaseDataReport generateProperDataReport(@NotNull final String sampleId,
                    @NotNull final List<FlagStats> passed) {
        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats properPaired = passed.get(FlagStatsType.PROPERLY_PAIRED_INDEX.getIndex());
        final DivisionOperator properStatCalc = FlagStatsType.PROPERLY_PAIRED_INDEX.getCalculableInstance();
        final double properlyPairedPercentage = toPercentage(
                        properStatCalc.calculate(properPaired.getValue(), mappedStat.getValue()));

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PROPERLY_PAIRED_PROPORTION_OF_MAPPED.toString(),
                        String.valueOf(properlyPairedPercentage));
    }

    @NotNull
    private static BaseDataReport generateSingletonDataReport(@NotNull final String sampleId,
                    @NotNull final List<FlagStats> passed) {
        final FlagStats singletonStat = passed.get(FlagStatsType.SINGLETONS_INDEX.getIndex());
        final double singletonPercentage = singletonStat.getValue();

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PROPORTION_SINGLETON.toString(),
                String.valueOf(singletonPercentage));
    }

    @NotNull
    private static BaseDataReport generateMateMappedDataReport(@NotNull final String sampleId,
                    @NotNull final List<FlagStats> passed) {
        final FlagStats diffPercStat = passed.get(FlagStatsType.MATE_MAP_DIF_CHR_INDEX.getIndex());
        final double mateMappedDiffChrPerc = diffPercStat.getValue();

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PROPORTION_MAPPED_DIFFERENT_CHR.toString(),
                        String.valueOf(mateMappedDiffChrPerc));
    }

    @NotNull
    private static BaseDataReport generateDuplicateDataReport(@NotNull final String sampleId,
                    @NotNull final List<FlagStats> passed) {
        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final FlagStats duplicateStat = passed.get(FlagStatsType.DUPLICATES_INDEX.getIndex());
        final DivisionOperator duplicateStatCalc = FlagStatsType.DUPLICATES_INDEX.getCalculableInstance();
        final double proportionOfDuplicateRead = toPercentage(
                        duplicateStatCalc.calculate(duplicateStat.getValue(), totalStat.getValue()));

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_MARKDUP_PROPORTION_DUPLICATES.toString(),
                String.valueOf(proportionOfDuplicateRead));
    }

    @NotNull
    private static BaseDataReport generateIsAllReadDataReport(@NotNull final String sampleId,
                    @NotNull final String totalSequences, @NotNull final List<FlagStats> passed) {
        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final FlagStats secondaryStat = passed.get(FlagStatsType.SECONDARY_INDEX.getIndex());
        final boolean isAllReadsPresent = totalStat.getValue() == Double.parseDouble(totalSequences) * DOUBLE_SEQUENCE
                        + secondaryStat.getValue();

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_OUTPUT_COMPLETE.toString(),
                String.valueOf(isAllReadsPresent));
    }

    private static double toPercentage(final double percentage) {
        return Math.round(percentage * MILLIS_FACTOR) / HUNDRED_FACTOR;
    }
}
