package com.github.gonzalezjo.ezbot.common;

import com.github.gonzalezjo.ezbot.typeracer.interfaces.User;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

import java.io.IOException;

public class RequestSender {
    private static HttpClient client = HttpClient.newHttpClient();

    private RequestSender() {
    }

    public static HttpResponse<String> makeRequest(
            final HttpRequest request) throws IOException, InterruptedException {

        return client.send(request, HttpResponse.BodyHandler.asString());
    }

    public static HttpResponse<String> makeAndFinishRequest(final HttpRequest.Builder post,
                                                final User user)
            throws IOException, InterruptedException {

        post.setHeader("X-GWT-Module-Base", Constants.BASE_URL);
        post.setHeader("Content-Type", Constants.GWT_RPC_CONTENT_TYPE);
        post.setHeader("X-GWT-Permutation", user.getGwtPermutation());

        return makeRequest(post.build());
    }

}
