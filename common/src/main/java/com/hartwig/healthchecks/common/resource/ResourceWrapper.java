package com.hartwig.healthchecks.common.resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hartwig.healthchecks.common.checks.CheckType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceWrapper {

    CheckType type();
}
