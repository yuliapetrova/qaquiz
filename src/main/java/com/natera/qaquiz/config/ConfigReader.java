package com.natera.qaquiz.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class ConfigReader {

    private static final String FILENAME = "application.yml";
    private static TestConfig config;

    @SneakyThrows
    public static TestConfig getTestConfig() {

        if (config != null) {
            return config;
        }

        val mapper = new ObjectMapper(new YAMLFactory());
        config = new TestConfig();

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            config = mapper.readValue(classLoader.getResource(FILENAME), TestConfig.class);
        } catch (IOException exception) {
            throw new IOException("Test config file not found exception.", exception);
        }
        return config;
    }
}
