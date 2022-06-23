package com.github.dgbogo.fmeditor.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum TemplateRenderingMode {
    HTML(0),
    PDF(1);
    
    @JsonValue
    private final int value;
    
    TemplateRenderingMode(int value) {
        this.value = value;
    }
    
    @JsonCreator
    public static TemplateRenderingMode of(int value) {
        return Arrays.stream(values())
                .filter(m -> m.value == value)
                .findAny()
                .orElse(null);
    }
}
