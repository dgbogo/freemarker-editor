package com.github.dgbogo.fmeditor.dto;

import io.quarkus.runtime.util.ExceptionUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Data
@Accessors(chain = true)
public class TemplateRenderingResult {
    private TemplateRenderingMode renderingMode = TemplateRenderingMode.HTML;
    private String html;
    private String error;
    private String stackTrace;
    private Long timeToRenderMs;

    public static TemplateRenderingResult fromPdf(ByteArrayOutputStream pdf, long timeToRender) {
        return new TemplateRenderingResult()
                .setHtml(Base64.getEncoder().encodeToString(pdf.toByteArray()))
                .setRenderingMode(TemplateRenderingMode.PDF)
                .setTimeToRenderMs(timeToRender);
    }

    public static TemplateRenderingResult fromHtml(String html, long timeToRender) {
        return new TemplateRenderingResult()
                .setHtml(html)
                .setTimeToRenderMs(timeToRender);
    }

    public static TemplateRenderingResult fromTxt(String txt, long timeToRender) {
        return new TemplateRenderingResult()
                .setHtml(txt)
                .setRenderingMode(TemplateRenderingMode.TXT)
                .setTimeToRenderMs(timeToRender);
    }

    public static TemplateRenderingResult fromError(Throwable error) {
        return new TemplateRenderingResult()
                .setError(error.getMessage())
                .setStackTrace(ExceptionUtil.generateStackTrace(error));
    }

    private TemplateRenderingResult() {
    }
}
