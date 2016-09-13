package com.hartwig.healthchecks.common.io.dir;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.EmptyFolderException;
import com.hartwig.healthchecks.common.exception.FolderDoesNotExistException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.NotFolderException;

import org.junit.Test;

public class FolderCheckerTest {

    private static final String RUN_FOLDER = "run";
    private static final String NOT_A_FOLDER = "run/something";
    private static final String NON_EXISTING_FOLDER = "bla";
    private static final String EMPTY_FOLDER = "empty";

    @Test
    public void checkFolder() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(RUN_FOLDER);
        final String dirPath = testPath.getPath();
        final String path = FolderChecker.build().checkFolder(dirPath);
        assertNotNull(path);
        assertEquals(dirPath, path);
    }

    @Test
    public void checkFolderWithExtraSeparator() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(RUN_FOLDER);
        final String dirPath = testPath.getPath() + File.separator;
        final String path = FolderChecker.build().checkFolder(dirPath);
        assertNotNull(path);
        assertEquals(testPath.getPath(), path);
    }

    @Test(expected = FolderDoesNotExistException.class)
    public void checkFolderNotExist() throws IOException, HealthChecksException {
        FolderChecker.build().checkFolder(NON_EXISTING_FOLDER);
    }

    @Test(expected = EmptyFolderException.class)
    public void checkEmptyFolder() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(EMPTY_FOLDER);
        final String dirPath = testPath.getPath();
        FolderChecker.build().checkFolder(dirPath);
    }

    @Test(expected = NotFolderException.class)
    public void checkNotFolder() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(NOT_A_FOLDER);
        final String dirPath = testPath.getPath();
        FolderChecker.build().checkFolder(dirPath);
    }
}
