package com.hartwig.healthchecks.nesbit.check;

import com.hartwig.healthchecks.nesbit.model.VCFType;

import org.jetbrains.annotations.NotNull;

final class VCFExtractorFunctions {

    private static final int ALT_INDEX = 4;
    private static final int REF_INDEX = 3;
    private static final String MULTIPLE_ALTS_IDENTIFIER = ",";

    private VCFExtractorFunctions() {
    }

    @NotNull
    static VCFType getVCFType(@NotNull final String[] values) {
        final String refValue = values[REF_INDEX];
        final String altValue = values[ALT_INDEX];

        final String[] allAlts = altValue.split(MULTIPLE_ALTS_IDENTIFIER);

        VCFType type = VCFType.SNP;

        for (String alt : allAlts) {
            if (refValue.length() != alt.length()) {
                type = VCFType.INDELS;
            }
        }
        return type;
    }
}
