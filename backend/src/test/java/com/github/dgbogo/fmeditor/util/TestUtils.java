package com.github.dgbogo.fmeditor.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class TestUtils {
    public static String getResourcePath(String resource) {
        return TestUtils.class.getClassLoader().getResource(resource).getFile();
    }

    public static String getResourceContent(String resource) {
        try (final var is = TestUtils.class.getClassLoader().getResourceAsStream(resource)) {
            if (is == null) {
                throw new IllegalStateException("Resource not found: " + resource);
            }
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
