package com.hartwig.healthchecks.common.io.path;

import static org.junit.Assert.assertEquals;

import com.hartwig.healthchecks.common.exception.MalformedRunDirException;

import org.junit.Test;

public class CPCTRunContextFactoryTest {

    private static final String VALID_PATIENT = "CPCT12345678";
    private static final String VALID_RUNDIR = "160101_HMFregCPCT_FR10002000_FR20003000_" + VALID_PATIENT;
    private static final String INVALID_RUNDIR = "RandomRunDir";
    private static final String INVALID_PATIENT_NAME = "RunDir_CPCT123456";

    @Test
    public void resolveCorrectPaths() throws MalformedRunDirException {
        RunContext runContext = CPCTRunContextFactory.fromRunDirectory(VALID_RUNDIR);
        assertEquals(VALID_PATIENT + CPCTRunContextFactory.REF_SAMPLE_SUFFIX, runContext.refSample());
        assertEquals(VALID_PATIENT + CPCTRunContextFactory.TUMOR_SAMPLE_SUFFIX, runContext.tumorSample());
    }

    @Test(expected = MalformedRunDirException.class)
    public void exceptionOnRandomRunDir() throws MalformedRunDirException {
        CPCTRunContextFactory.fromRunDirectory(INVALID_RUNDIR);
    }

    @Test(expected = MalformedRunDirException.class)
    public void exceptionOnRunDirWithTooShortPatientName() throws MalformedRunDirException {
        CPCTRunContextFactory.fromRunDirectory(INVALID_PATIENT_NAME);
    }
}