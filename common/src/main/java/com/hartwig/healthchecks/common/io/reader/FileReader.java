package com.hartwig.healthchecks.common.io.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.EmptyFileException;

@FunctionalInterface
public interface FileReader {

    @NotNull
    List<String> readLines(@NotNull Path fileToRead) throws IOException, EmptyFileException;

    @NotNull
    static FileReader build() {
        return (fileToRead) -> {
            final List<String> lines = read(fileToRead);
            if (lines.isEmpty()) {
                throw new EmptyFileException(fileToRead.getFileName().toString(), fileToRead.toString());
            }
            return lines;
        };
    }

    @NotNull
    static List<String> read(@NotNull final Path fileToRead) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(fileToRead.toString()))) {
            return lines.collect(Collectors.toList());
        }
    }
}
