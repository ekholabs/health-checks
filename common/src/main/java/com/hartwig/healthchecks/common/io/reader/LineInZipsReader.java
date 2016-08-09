package com.hartwig.healthchecks.common.io.reader;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
interface LineInZipsReader {

    @NotNull
    String readLines(@NotNull final String zipPath, @NotNull final String fileNameInZip, @NotNull final String filter)
            throws IOException, HealthChecksException;

    @NotNull
    static LineInZipsReader build() {
        return (zipPath, fileNameInZip, filter) -> {
            final Optional<String> lineFound = read(zipPath, fileNameInZip, filter);
            if (!lineFound.isPresent()) {
                throw new LineNotFoundException(zipPath, filter);
            }
            return lineFound.get();
        };
    }

    @NotNull
    static Optional<String> read(@NotNull final String zipPath, @NotNull final String fileNameInZip,
            @NotNull final String filter) throws IOException {
        try (final ZipFile zipFile = new ZipFile(zipPath)) {
            final List<? extends ZipEntry> fileEntryInZip = FileInZipsFinder.build().findFileInZip(zipFile,
                    fileNameInZip);
            return fileEntryInZip.stream().map(
                    zipElement -> ZipEntryReader.build().readZipElement(zipFile, zipElement).filter(
                            line -> line.contains(filter)).collect(toList())).flatMap(Collection::stream).findFirst();
        }
    }
}
