package com.hartwig.healthchecks.common.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

    private static Logger LOGGER = LogManager.getLogger(PropertiesUtil.class);

    private static PropertiesUtil instance = new PropertiesUtil();

    private static Properties properties = new Properties();

    static {
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("config.properties"));
        } catch (IOException e) {
            LOGGER.error(String.format("Error loading properties. Error -> %s", e.getMessage()));
        }
    }

    private PropertiesUtil(){}

    public static PropertiesUtil getInstance() {
        return instance;
    }

    @NotNull
    public String getProperty(@NotNull  String key) {
        return properties.getProperty(key);
    }
}