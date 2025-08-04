package org.com.domain.services;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.com.domain.model.HealthResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executor;

@ApplicationScoped
public class ProcessorService {

    @ConfigProperty(name = "processor.default.host")
    String defaultHost;

    @ConfigProperty(name = "processor.fallback.host")
    String fallbackHost;

    private HttpClient client;

    @PostConstruct
    void init() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .executor((Executor) Thread.ofVirtual().factory())
                .build();
    }

    public String fetchPaymentProcessor(String correlationId, BigDecimal amount, String requestAt, boolean isDefault) throws IOException, InterruptedException {
        String base = (isDefault ? defaultHost : fallbackHost);

        String url = base + "/payments";

        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("correlationId", correlationId)
                .add("amount", amount)
                .add("requestAt", requestAt);

        JsonObject jsonObject = builder.build();
        String jsonString = jsonObject.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        int code = response.statusCode();
        if (code == 200) {
            return response.body();
        } else {
            throw new IOException("Processor return " + code);
        }
    }

    public HealthResponse fetchCheckHealth(boolean isDefault) {
        String base = isDefault ? defaultHost : fallbackHost;
        String url = base + "/payments/service-health";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(2))
                .GET().build();

        try {
            long start = System.nanoTime();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            long durationMs = (System.nanoTime() - start) / 1_000_000;

            if (response.statusCode() != 200) {
                return new HealthResponse(true, durationMs);
            }

            try (var reader = Json.createReader(new StringReader(response.body()))) {
                JsonObject object = reader.readObject();
                boolean failing = object.getBoolean("failing", true);
                long minResponseTime = object.getJsonNumber("minResponseTime").longValue();
                return new HealthResponse(failing, minResponseTime);
            }
        } catch (Exception e) {
            return new HealthResponse(true, -1);
        }
    }

}
