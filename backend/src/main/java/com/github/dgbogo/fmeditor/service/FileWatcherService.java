package com.github.dgbogo.fmeditor.service;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Timer;
import java.util.TimerTask;

import static java.nio.file.StandardWatchEventKinds.*;

@ApplicationScoped
@Slf4j
public class FileWatcherService {
    private static final long DEBOUNCE_DELAY_MS = 30;

    private Timer debouncedRenderer = new Timer();

    private Thread watcherThread;

    public void watchFolder(String folderPath, Runnable callback) {
        stopWatching();
        watcherThread = new Thread(() -> {
            try {
                watchFolderInternal(folderPath, callback);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        watcherThread.start();
    }

    private void watchFolderInternal(String folderPath, Runnable callback) throws IOException {
        try (final var watchService = FileSystems.getDefault().newWatchService()) {
            log.info("Initializing watcher on folder: {}", folderPath);
            final var dir = Path.of(folderPath);
            watchPathRecursively(watchService, dir);
            while (true) {
                final WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    final var changed = (Path) event.context();
                    log.info("File changed: {}", changed);
                }
                debounceRendering(callback);
                boolean valid = wk.reset();
                if (!valid) {
                    log.info("Key has been unregistered");
                }
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
    
    private void stopWatching() {
        if (watcherThread != null && watcherThread.isAlive()) {
            watcherThread.interrupt();
        }
    }

    private void debounceRendering(Runnable callback) {
        debouncedRenderer.cancel();
        debouncedRenderer.purge();
        debouncedRenderer = new Timer();
        debouncedRenderer.schedule(new TimerTask() {
            @Override
            public void run() {
                callback.run();
            }
        }, DEBOUNCE_DELAY_MS);
    }

    private static void watchPathRecursively(WatchService watchService, Path root) {
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception exc) {
            throw new IllegalStateException(String.format("Error while watching folder '%s' recursively", root.toString()));
        }
    }
}
