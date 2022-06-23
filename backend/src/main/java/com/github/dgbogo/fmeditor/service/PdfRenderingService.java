package com.github.dgbogo.fmeditor.service;

import com.github.dgbogo.fmeditor.dto.CustomFont;
import com.github.dgbogo.fmeditor.dto.EditorConfiguration;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.util.XRRuntimeException;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@ApplicationScoped
@Slf4j
public class PdfRenderingService {
    public ByteArrayOutputStream renderPdf(String html, EditorConfiguration editorConfiguration) {
        return renderPdf(html, editorConfiguration, false);
    }
    
    private ByteArrayOutputStream renderPdf(String html, EditorConfiguration editorConfiguration, boolean alreadyStrippedInvalidXML) {
        final var outputStream = new ByteArrayOutputStream();
        final var builder = new PdfRendererBuilder();

        builder.withHtmlContent(html, editorConfiguration.templateFilePath());
        builder.useFastMode();
        builder.toStream(outputStream);
        loadFonts(builder, editorConfiguration.customFonts());

        try {
            builder.run();
        } catch (Exception ex) {
            if (ex instanceof XRRuntimeException && ex.getMessage().contains("SAXParseException") && !alreadyStrippedInvalidXML) {
                return renderPdf(stripNonValidXMLCharacters(html), editorConfiguration, true);
            }
            throw new IllegalStateException(ex);
        }
        return outputStream;
    }
    
    private void loadFonts(PdfRendererBuilder builder, List<CustomFont> fonts) {
        if (fonts == null || fonts.isEmpty()) {
            return;
        }
        fonts.forEach(font -> builder.useFont(() -> {
            try {
                return new FileInputStream(font.path());
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(String.format("Error loading font '%s' from path '%s'", font.name(), font.path()), e);
            }
        }, font.name()));
    }

    private String stripNonValidXMLCharacters(String in) {
        log.info("Removing invalid XML characters");
        if (in == null || ("".equals(in))) return null;
        final var out = new StringBuffer(in);
        for (int i = 0; i < out.length(); i++) {
            if(out.charAt(i) == 0x1a) {
                out.setCharAt(i, ' ');
            }
        }
        return out.toString();
    }
}
