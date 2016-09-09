package com.hartwig.healthchecks.nesbit.check;

import org.jetbrains.annotations.NotNull;

enum SomaticCheck {
    COUNT("VARIANTS_SOMATIC_%s"),
    AF_MEDIAN("SOMATIC_AF_%s_MEDIAN"),
    AF_LOWER_SD("SOMATIC_AF_%s_LOWER_SD"),
    AF_UPPER_SD("SOMATIC_AF_%s_UPPER_SD"),
    PROPORTION_CHECK("SOMATIC_%s_PROPORTION_VARIANTS_%s_CALLERS"),
    PRECISION_CHECK("SOMATIC_%s_PRECISION_%s_2+_CALLERS"),
    SENSITIVITY_CHECK("SOMATIC_%s_SENSITIVITY_%s_VARIANTS_2+_CALLERS");

    @NotNull
    private final String checkPattern;

    SomaticCheck(@NotNull final String label) {
        this.checkPattern = label;
    }

    @NotNull
    public String checkName(@NotNull final String input1) {
        return String.format(checkPattern, input1.toUpperCase());
    }

    @NotNull
    public String checkName(@NotNull final String input1, @NotNull final String input2) {
        return String.format(checkPattern, input1.toUpperCase(), input2.toUpperCase());
    }
}
