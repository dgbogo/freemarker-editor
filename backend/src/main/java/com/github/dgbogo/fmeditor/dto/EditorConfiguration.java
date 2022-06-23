package com.github.dgbogo.fmeditor.dto;

import lombok.Builder;

import java.io.File;
import java.util.List;

@Builder
public record EditorConfiguration(
        String templateFilePath,
        String dataFilePath,
        String watchFolder,
        TemplateRenderingMode renderingMode,
        List<CustomFont> customFonts
) {
    public TemplateRenderingMode renderingMode() {
        return renderingMode != null ? renderingMode : TemplateRenderingMode.HTML;
    }

    public String watchFolder() {
        return watchFolder(File.separator);
    }

    String watchFolder(final String fileSeparator) {
        if (watchFolder != null) {
            return watchFolder;
        }
        final var templateFileFolder =  templateFilePath.split(fileSeparator);
        final var dataFileFolder = dataFilePath.split(fileSeparator);
        final var commonFolder = new StringBuilder();
        for (int i = 0; i < templateFileFolder.length && i < dataFileFolder.length; i++) {
            if (templateFileFolder[i].equals(dataFileFolder[i])) {
                commonFolder.append(templateFileFolder[i]);
                commonFolder.append(fileSeparator.charAt(0));
            } else {
                break;
            }
        }
        return commonFolder.toString();
    }
}
