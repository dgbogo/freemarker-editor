package com.github.dgbogo.fmeditor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dgbogo.fmeditor.dto.EditorConfiguration;
import com.github.dgbogo.fmeditor.dto.TemplateRenderingResult;
import com.github.dgbogo.fmeditor.service.FileWatcherService;
import com.github.dgbogo.fmeditor.service.FreemarkerService;
import com.github.dgbogo.fmeditor.service.PdfRenderingService;
import com.github.dgbogo.fmeditor.dto.TemplateRenderingMode;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ServerEndpoint("/ws/{name}")
@ApplicationScoped
@Slf4j
public class StartWebSocket {
    private final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    private EditorConfiguration editorConfiguration = null;

    @Inject
    FreemarkerService freemarkerService;

    @Inject
    FileWatcherService fileWatcherService;

    @Inject
    ObjectMapper objectMapper;
    
    @Inject
    PdfRenderingService pdfRenderingService;

    @OnOpen
    public void onOpen(Session session, @PathParam("name") String name) {
        log.info("Creating session: {}", name);
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("name") String name) {
        log.info("Closing session: {}", name);
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, @PathParam("name") String name, Throwable throwable) {
        log.error("Sessione error: " + name, throwable);
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("name") String name) throws JsonProcessingException {
        this.editorConfiguration = objectMapper.readValue(message, EditorConfiguration.class);
        fileWatcherService.watchFolder(this.editorConfiguration.watchFolder(), this::renderAndBroadcast);
        freemarkerService.createConfiguration(this.editorConfiguration);
        renderAndBroadcast();
    }

    private void renderAndBroadcast() {
        log.info("Rendering template to {}", editorConfiguration.renderingMode());
        try {
            final var renderStart = LocalDateTime.now();
            final var html = freemarkerService.render(this.editorConfiguration);
            if (editorConfiguration.renderingMode() == TemplateRenderingMode.PDF) {
                final var pdf = pdfRenderingService.renderPdf(html, editorConfiguration);
                final var timeToRenderInMs = ChronoUnit.MILLIS.between(renderStart, LocalDateTime.now());
                broadcast(TemplateRenderingResult.fromPdf(pdf, timeToRenderInMs));
            } else if (editorConfiguration.renderingMode() == TemplateRenderingMode.HTML) {
                final var timeToRenderInMs = ChronoUnit.MILLIS.between(renderStart, LocalDateTime.now());
                broadcast(TemplateRenderingResult.fromHtml(html, timeToRenderInMs));
            } else if (editorConfiguration.renderingMode() == TemplateRenderingMode.TXT) {
                final var timeToRenderInMs = ChronoUnit.MILLIS.between(renderStart, LocalDateTime.now());
                broadcast(TemplateRenderingResult.fromTxt(html, timeToRenderInMs));
            }
        } catch (Exception e) {
            broadcast(TemplateRenderingResult.fromError(e));
        }
    }

    private void broadcast(TemplateRenderingResult message) {
        sessions.forEach(s -> {
            if (!s.isOpen()) {
                return;
            }
            s.getAsyncRemote().sendObject(serializeToJSON(message), result ->  {
                if (result.getException() != null) {
                    log.error("Error sending message to " + s.getId(), result.getException());
                }
            });
        });
    }

    private String serializeToJSON(TemplateRenderingResult result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.error("Error serializing to JSON", e);
            return "ERROR GENERATING RESULT";
        }
    }
}
