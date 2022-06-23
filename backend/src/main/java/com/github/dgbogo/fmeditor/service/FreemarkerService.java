package com.github.dgbogo.fmeditor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dgbogo.fmeditor.dto.EditorConfiguration;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.*;
import org.apache.commons.io.FileUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

@ApplicationScoped
public class FreemarkerService {
    @Inject
    ObjectMapper objectMapper;
    
    public String render(EditorConfiguration editorConfiguration) throws IOException, TemplateException {
        final var template = new Template(
                "test",
                readFileToString(editorConfiguration.templateFilePath()),
                createConfiguration(editorConfiguration)
        );
        final var dataStr = readFileToString(editorConfiguration.dataFilePath());
        final var data = objectMapper.readValue(dataStr, HashMap.class);

        try (final var out = new StringWriter()) {
            template.process(data, out);
            return out.getBuffer().toString();
        }
    }
    
    public Configuration createConfiguration(EditorConfiguration editorConfiguration) {
        try {
            final var newConfig = new Configuration(new Version("2.3.31"));
            newConfig.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
            final var templateFolder = new File(editorConfiguration.templateFilePath()).getParentFile();
            newConfig.setDirectoryForTemplateLoading(templateFolder);
            newConfig.setTemplateLoader(new FileTemplateLoader(templateFolder));
            newConfig.setDefaultEncoding(StandardCharsets.UTF_8.name());
            newConfig.setOutputEncoding(StandardCharsets.UTF_8.name());
            newConfig.setLocale(new Locale("pt", "BR"));
            newConfig.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
            return newConfig;
        } catch (Exception exc) {
            throw new IllegalStateException("Error updating FreeMarker configuration", exc);
        }
    }
    
    private static String readFileToString(String path) throws IOException {
        return FileUtils.readFileToString(FileUtils.getFile(path), StandardCharsets.UTF_8);
    }
}
