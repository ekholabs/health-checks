package com.hartwig.healthchecks.common.io.path;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SamplePathFinder {

    String FILE_S_NOT_FOUND_MSG = "File %s not Found in path %s";

    @NotNull
    Path findPath(@NotNull final String path, @NotNull final String prefix, @NotNull final String suffix)
                    throws IOException;

    @NotNull
    static SamplePathFinder build() {
        return (path, prefix, suffix) -> {
            final Optional<Path> fileFound = Files.walk(new File(path).toPath())
                            .filter(filePath -> filePath.getFileName().toString().startsWith(prefix)
                                            && filePath.getFileName().toString().endsWith(suffix)
                                            && filePath.toString().contains(path + File.separator + prefix))
                            .findFirst();
            if (!fileFound.isPresent()) {
                throw new FileNotFoundException(String.format(FILE_S_NOT_FOUND_MSG, suffix, path));
            }
            return fileFound.get();
        };
    }
}