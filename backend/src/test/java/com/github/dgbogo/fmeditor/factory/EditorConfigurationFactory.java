package com.github.dgbogo.fmeditor.factory;

import com.github.dgbogo.fmeditor.dto.EditorConfiguration;
import com.github.dgbogo.fmeditor.dto.TemplateRenderingMode;

import java.util.List;

public class EditorConfigurationFactory {
    public static EditorConfiguration fromTemplateAndData(String templateFile, String dataFile) {
        return EditorConfiguration.builder()
                .renderingMode(TemplateRenderingMode.HTML)
                .templateFilePath(templateFile)
                .dataFilePath(dataFile)
                .customFonts(List.of())
                .build();
    }
}
