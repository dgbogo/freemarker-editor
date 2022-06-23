package com.github.dgbogo.fmeditor.service;

import com.github.dgbogo.fmeditor.factory.EditorConfigurationFactory;
import com.github.dgbogo.fmeditor.service.FreemarkerService;
import com.github.dgbogo.fmeditor.util.TestUtils;
import freemarker.template.TemplateException;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class FreemarkerServiceTest {
    @Inject
    FreemarkerService freemarkerService;

    @Test
    void render_Html() throws TemplateException, IOException {
        final var config = EditorConfigurationFactory.fromTemplateAndData(
                TestUtils.getResourcePath("templates/test/test.ftl"),
                TestUtils.getResourcePath("templates/test/test.json")
        );

        final var html = freemarkerService.render(config);

        assertEquals(TestUtils.getResourceContent("templates/test/expected.html"), html);
    }
}
