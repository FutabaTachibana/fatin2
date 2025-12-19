package org.f14a.fatin2.event.response;

import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.type.Response;

import java.util.Map;
import java.util.concurrent.*;

public class ResponseManager {
    // echo -> CompletableFuture<Response>
    private final Map<String, CompletableFuture<Response>> waitingFuture = new ConcurrentHashMap<>();

    private final ScheduledExecutorService cleanupExecutor;

    public ResponseManager() {
        this.cleanupExecutor = Executors.newScheduledThreadPool(1, r -> {
            Thread thread = new Thread(r, "Response-Cleanup-Thread");
            thread.setDaemon(true);
            return thread;
        });
        this.cleanupExecutor.scheduleAtFixedRate(
                this::cleanupDoneResponses, 30, 30, java.util.concurrent.TimeUnit.SECONDS
        );
    }
    // Called by MessageEvent
    public CompletableFuture<Response> registerFuture(String echo, int timeoutSeconds) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        waitingFuture.put(echo, future);
        // Schedule a timeout task
        future.orTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    if (ex instanceof TimeoutException) {
                        EventBus.LOGGER.warn("Response timeout for echo: {}", echo);
                        waitingFuture.remove(echo);
                    }
                    return null;
                });
        return future;
    }
    // Called by ResponseListener (integrated in ResponseEvent)
    public void receiveResponse(Response response) {
        String echo = response.echo();
        if (echo == null || echo.isEmpty()) {
            EventBus.LOGGER.warn("Received response without echo: {}", response);
            return;
        }
        // Get the corresponding CompletableFuture and complete it
        CompletableFuture<Response> future = waitingFuture.remove(echo);
        if (future == null) {
            EventBus.LOGGER.debug("No waiting response found for echo: {}", echo);
            return;
        } else {
            future.complete(response);
            EventBus.LOGGER.debug("Completed response for echo: {}", echo);
        }
    }
    public void cleanupDoneResponses() {
        waitingFuture.entrySet().removeIf(entry -> entry.getValue().isDone());
    }
    public void shutdown() {
        EventBus.LOGGER.info("Shutting down ResponseManager...");
        cleanupExecutor.shutdown();
        waitingFuture.values().forEach(f -> f.cancel(true));
        waitingFuture.clear();
        EventBus.LOGGER.debug("ResponseManager shut down complete.");
    }
}
