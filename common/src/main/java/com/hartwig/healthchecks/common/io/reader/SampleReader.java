package com.hartwig.healthchecks.common.io.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SampleReader {

    String FILE_S_NOT_FOUND_MSG = "File %s not Found in path %s";

    @NotNull
    List<String> readLines(@NotNull final String path, final String extension) throws IOException;

    @NotNull
    static SampleReader build(@NotNull final String prefix, @NotNull final String suffix) {
        return (path, extension) -> {
            final Path filePath = findFilePath(path, prefix, suffix);
            final Path fileToRead = findFileInPath(filePath.toString(), extension);
            return Files.lines(Paths.get(fileToRead.toString())).collect(Collectors.toList());
        };
    }

    @NotNull
    static Path findFileInPath(@NotNull final String searchPath, @NotNull final String suffix) throws IOException {
        final Optional<Path> searchedFile = Files.walk(new File(searchPath).toPath())
                        .filter(path -> path.getFileName().toString().endsWith(suffix)).findFirst();
        if (!searchedFile.isPresent()) {
            throw new FileNotFoundException(String.format(FILE_S_NOT_FOUND_MSG, suffix, searchPath));
        }
        return searchedFile.get();
    }

    @NotNull
    static Path findFilePath(@NotNull final String path, @NotNull final String prefix, @NotNull final String suffix)
                    throws IOException {
        final Optional<Path> fileFound = Files.walk(new File(path).toPath())
                        .filter(filePath -> filePath.getFileName().toString().startsWith(prefix)
                                        && filePath.getFileName().toString().endsWith(suffix)
                                        && filePath.toString().contains(path + File.separator + prefix))
                        .findFirst();
        if (!fileFound.isPresent()) {
            throw new FileNotFoundException(String.format(FILE_S_NOT_FOUND_MSG, suffix, path));
        }
        return fileFound.get();
    }
}