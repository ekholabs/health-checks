package com.hartwig.healthchecks.nesbit.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class VCFSomaticDataFactory {

    private static final String VCF_COLUMN_SEPARATOR = "\t";
    private static final Logger LOGGER = LogManager.getLogger(VCFSomaticDataFactory.class);

    private static final int INFO_COLUMN = 7;
    private static final String INFO_FIELD_SEPARATOR = ";";
    private static final String CALLER_ALGO_IDENTIFIER = "set=";
    private static final String CALLER_ALGO_START = "=";
    private static final String CALLER_ALGO_SEPARATOR = "-";
    private static final String CALLER_FILTERED_IDENTIFIER = "filterIn";
    private static final String CALLER_INTERSECTION_IDENTIFIER = "Intersection";

    private static final int SAMPLE_COLUMN = 9;
    private static final String SAMPLE_FIELD_SEPARATOR = ":";
    private static final int AF_COLUMN_INDEX = 1;
    private static final String AF_FIELD_SEPARATOR = ",";

    private VCFSomaticDataFactory() {
    }

    @NotNull
    public static VCFSomaticData fromVCFLine(@NotNull final String line) {
        final String[] values = line.split(VCF_COLUMN_SEPARATOR);

        final VCFType type = VCFExtractorFunctions.extractVCFType(values);
        final List<String> callers = extractCallers(values);
        final double alleleFrequency = calcAlleleFrequency(values);
        return new VCFSomaticData(type, callers, alleleFrequency);
    }

    @NotNull
    private static List<String> extractCallers(@NotNull final String[] values) {
        final String info = values[INFO_COLUMN];

        final Optional<String> setValue = Arrays.stream(info.split(INFO_FIELD_SEPARATOR)).filter(
                infoLine -> infoLine.contains(CALLER_ALGO_IDENTIFIER)).map(
                infoLine -> infoLine.substring(infoLine.indexOf(CALLER_ALGO_START) + 1,
                        infoLine.length())).findFirst();
        assert setValue.isPresent();

        final String[] allCallers = setValue.get().split(CALLER_ALGO_SEPARATOR);
        List<String> finalCallers = Lists.newArrayList();
        if (allCallers.length > 0 && allCallers[0].equals(CALLER_INTERSECTION_IDENTIFIER)) {
            finalCallers.addAll(VCFConstants.ALL_CALLERS);
        } else {
            finalCallers.addAll(
                    Arrays.stream(allCallers).filter(caller -> !caller.startsWith(CALLER_FILTERED_IDENTIFIER)).collect(
                            Collectors.toList()));
        }
        return finalCallers;
    }

    private static double calcAlleleFrequency(@NotNull final String[] values) {
        final String[] sampleFields = values[SAMPLE_COLUMN].split(SAMPLE_FIELD_SEPARATOR);
        final String[] afFields = sampleFields[AF_COLUMN_INDEX].split(AF_FIELD_SEPARATOR);

        if (afFields.length < 2) {
            LOGGER.warn("Incorrectly formatted AF field found: " + sampleFields[AF_COLUMN_INDEX]);
            return Double.NaN;
        }

        final int readCount = Integer.valueOf(afFields[1]);
        int totalReadCount = 0;
        for (String afField : afFields) {
            totalReadCount += Integer.valueOf(afField);
        }

        return (double) readCount / totalReadCount;
    }
}
