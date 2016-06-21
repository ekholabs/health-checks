package com.hartwig.healthchecks.common.exception;

import org.jetbrains.annotations.NotNull;

public class NotFoundException extends Exception {

    public NotFoundException(@NotNull final String message) {
        super(message);
    }
}
