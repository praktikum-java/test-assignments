package ru.practicum.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.exception.StatisticSendingClientException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@Component
public class StatisticClient {
    @Autowired
    public StatisticClient(@Value("${stats-server.url}") String statServerUrl, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.statServerUrl = statServerUrl;
    }

    private final ObjectMapper objectMapper;
    private final String statServerUrl;

    public List<ViewStat> getStatistic(String start, String end, String[] uris, Boolean unique) {
        List<ViewStat> result;
        String encodeStart = URLEncoder.encode(start, StandardCharsets.UTF_8);
        String encodeEnd = URLEncoder.encode(end, StandardCharsets.UTF_8);
        String encodeUris = URLEncoder.encode(convertArrayToStringForUrl(uris), StandardCharsets.UTF_8);
        String encodeUnique = URLEncoder.encode(String.valueOf(unique), StandardCharsets.UTF_8);
        URI uri = URI.create(statServerUrl + "/stats?start=" + encodeStart + "&end=" + encodeEnd +
                "&uris=" + encodeUris + "&unique=" + encodeUnique);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .timeout(Duration.of(10, SECONDS))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response;
        try {
            response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            result = objectMapper.readValue(response.body(), objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, ViewStat.class));
        } catch (IOException | InterruptedException e) {
            throw new StatisticSendingClientException("An error occurred when sending a request from a client");
        }
        log.info("Send request GET uri = {}. \n And get response: {}", uri, response);
        return result;
    }

    private String convertArrayToStringForUrl(String[] uris) {
        List<String> fields = new ArrayList<>(List.of(uris));
        return fields.stream().map(String::valueOf).collect(Collectors.joining(",", "", ""));
    }

    public Long getViewsByUri(Long eventId) {
        Long result;
        URI uri = URI.create(statServerUrl + "/events/" + eventId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response;
        try {
            response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            result = objectMapper.readValue(response.body(), Long.class);
        } catch (IOException | InterruptedException e) {
            log.warn("Error: " + e + " \n Cause = " + e.getCause() + " \n Message = " + e.getMessage());
            throw new StatisticSendingClientException("An error occurred when sending a request from a client 'GetViewsByUri'");
        }
        log.info("Send request GET uri = {}. \n And get response: {}", uri, response);
        return result;
    }

    public void saveCall(Map<String, String> endpointHit) {
        URI uri = URI.create(statServerUrl + "/hit");
        String body = getFormDataAsString(endpointHit);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.of(10, SECONDS))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .header("Accept", "application/json")
                .build();
        HttpClient.newBuilder().build()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());
        log.info("Send request POST uri = {} to save statistic with body: {}", uri, body);
    }

    private static String getFormDataAsString(Map<String, String> formData) {
        StringBuilder formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
            if (formBodyBuilder.length() > 0) {
                formBodyBuilder.append("&");
            }
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
            formBodyBuilder.append("=");
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
        }
        return formBodyBuilder.toString();
    }
}