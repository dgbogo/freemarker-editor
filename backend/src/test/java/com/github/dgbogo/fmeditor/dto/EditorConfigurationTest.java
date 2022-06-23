package com.github.dgbogo.fmeditor.dto;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static com.github.dgbogo.fmeditor.factory.EditorConfigurationFactory.fromTemplateAndData;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class EditorConfigurationTest {
    @Test
    void watchFolder_DefaultOnWindows() {
        final var separator = "\\\\";
        testDefaultWatchFolder(separator, "C:\\Documents\\fmeditor\\test.ftl", "C:\\Documents\\fmeditor\\test.json", "C:\\Documents\\fmeditor\\");
        testDefaultWatchFolder(separator, "C:\\Documents\\fmeditor\\test.ftl", "C:\\Documents\\fmeditor\\data\\test.json", "C:\\Documents\\fmeditor\\");
        testDefaultWatchFolder(separator, "C:\\Documents\\fmeditor2\\test.ftl", "C:\\Documents\\fmeditor\\data\\files\\test.json", "C:\\Documents\\");
        testDefaultWatchFolder(separator, "C:\\Documents\\fmeditor2\\test.ftl", "C:\\Temp\\fmeditor2\\test.json", "C:\\");
    }

    @Test
    void watchFolder_DefaultOnUnix() {
        final var separator = "/";
        testDefaultWatchFolder(separator, "/var/fmeditor/test.ftl", "/var/fmeditor/test.json", "/var/fmeditor/");
        testDefaultWatchFolder(separator, "/var/fmeditor/test.ftl", "/var/fmeditor/data/files/test.json", "/var/fmeditor/");
        testDefaultWatchFolder(separator, "/var/fmeditor2/test.ftl", "/var/fmeditor/data/files/test.json", "/var/");
        testDefaultWatchFolder(separator, "/var/fmeditor2/test.ftl", "/tmp/fmeditor2/test.json", "/");
    }

    private void testDefaultWatchFolder(String fileSeparator, String templateFile, String dataFile, String expectedResult) {
        final var config = fromTemplateAndData(templateFile, dataFile);
        assertEquals(expectedResult, config.watchFolder(fileSeparator));
    }
}
