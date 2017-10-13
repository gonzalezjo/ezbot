package com.github.gonzalezjo.ezbot.typeracer;

import com.github.gonzalezjo.ezbot.common.Constants;
import com.github.gonzalezjo.ezbot.common.RequestSender;
import jdk.incubator.http.HttpRequest;

import java.io.IOException;
import java.net.URI;

public class JSession {
    private final String id;

    public JSession() throws IOException, InterruptedException {
        this(RequestSender.makeRequest(
                HttpRequest.newBuilder(
                        URI.create(Constants.BASE_URL)
                ).build())
                .body()
                .replaceAll("\\s", "")
                .replaceAll(".*jsessionid=", "")
                .replaceAll("\".*+", ""));
    }

    public JSession(final String gameSessionId) throws IOException, InterruptedException {
        this.id = gameSessionId;
    }

    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return this.id();
    }
}
