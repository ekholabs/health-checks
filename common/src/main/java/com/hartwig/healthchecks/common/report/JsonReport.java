package com.hartwig.healthchecks.common.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.common.util.PropertiesUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class JsonReport implements Report {

    private static final String REPORT_NAME = "health-checks_%s.json";
    private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger(Report.class);
    private static final JsonReport instance = new JsonReport();

    private final Map<CheckType, BaseReport> healthChecks = new ConcurrentHashMap<>();

    private JsonReport() {
    }

    public static JsonReport getInstance() {
        return instance;
    }

    @Override
    public void addReportData(@NotNull final BaseReport reportData) {
        healthChecks.putIfAbsent(reportData.getCheckType(), reportData);
    }

    @NotNull
    @Override
    public Optional<String> generateReport() {
        final JsonArray reportArray = new JsonArray();

        healthChecks.forEach((k, v) -> {
            final JsonElement configJson = GSON.toJsonTree(v);

            final JsonObject element = new JsonObject();
            element.add(k.toString(), configJson);

            reportArray.add(element);
        });

        final PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();

        final String reportDir = propertiesUtil.getProperty("report.dir");
        final String fileName = String.format("%s/%s", reportDir,
                String.format(REPORT_NAME, System.currentTimeMillis()));

        try (FileWriter fileWriter = new FileWriter(new File(fileName))) {
            final JsonObject reportJson = new JsonObject();
            reportJson.add("health_checks", reportArray);
            fileWriter.write(GSON.toJson(reportJson));
            fileWriter.flush();
        } catch (IOException e) {
            LOGGER.error(String.format("Error occurred whilst generating reports. Error -> %s", e.getMessage()));

            return Optional.empty();
        }

        return Optional.of(fileName);
    }
}
