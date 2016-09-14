package com.hartwig.healthchecks.common.io.dir;

import org.jetbrains.annotations.NotNull;

class RunContextImpl implements RunContext {

    @NotNull
    private final String runDirectory;
    @NotNull
    private final String refSample;
    @NotNull
    private final String tumorSample;
    private final boolean hasPassedTests;

    RunContextImpl(@NotNull final String runDirectory, @NotNull final String refSample,
            @NotNull final String tumorSample, final boolean hasPassedTests) {
        this.runDirectory = runDirectory;
        this.refSample = refSample;
        this.tumorSample = tumorSample;
        this.hasPassedTests = hasPassedTests;
    }

    @NotNull
    @Override
    public String runDirectory() {
        return runDirectory;
    }

    @NotNull
    @Override
    public String refSample() {
        return refSample;
    }

    @NotNull
    @Override
    public String tumorSample() {
        return tumorSample;
    }

    @Override
    public boolean hasPassedTests() {
        return hasPassedTests;
    }
}

