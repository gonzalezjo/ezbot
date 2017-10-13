package com.github.gonzalezjo.ezbot.typeracer;

import com.github.gonzalezjo.ezbot.common.Constants;
import com.github.gonzalezjo.ezbot.common.RequestSender;
import com.github.gonzalezjo.ezbot.typeracer.interfaces.RaceTrack;
import com.github.gonzalezjo.ezbot.typeracer.interfaces.User;
import jdk.incubator.http.HttpRequest;

import java.io.IOException;
import java.lang.module.FindException;
import java.util.StringTokenizer;

public class UserRaceTrack implements RaceTrack {
    private final String lobbyAccessToken;
    private final User user;

    public UserRaceTrack(final User user) {
        this.user = user;
        this.lobbyAccessToken = getLobbyAccessToken();
    }

    @Override
    public String getLobbyAccessToken() {
        if (lobbyAccessToken != null)
            return lobbyAccessToken;

        try {
            final String response = RequestSender.makeAndFinishRequest(
                    HttpRequest.newBuilder(user.getSiteAccessUri())
                            .POST(HttpRequest.BodyProcessor.fromString(
                                    String.format(
                                            "7|1|5|%s|%s|_|getInitialStatus|11|1|2|3|4|1|5|5|0|0|0|",
                                            Constants.BASE_URL,
                                            Constants.NICK_NAME_AND_RACE_CLASS_ID
                                    )
                            )), user)
                    .body();

            if (response.length() < 800)
                throw new FindException("Invalid lobby access token.");

            StringTokenizer responseReader = new StringTokenizer(response, "'");
            responseReader.nextToken();

            return responseReader.nextToken();
        } catch (final IOException | InterruptedException e) {
            e.printStackTrace();
        }

        throw new FindException("Could not find the lobby access token.");
    }

    @Override
    public String getRaceTrackId() {
        try {
            final String response = RequestSender.makeAndFinishRequest(
                    HttpRequest.newBuilder(user.getSiteAccessUri())
                            .POST(HttpRequest.BodyProcessor.fromString(
                                    String.format(
                                            "7|1|6|%s|%s|_|createAndJoinCustomRoom|11|1z|1|2|3|4|1|5|5|0|1|6|%s|",
                                            Constants.BASE_URL,
                                            Constants.NICK_NAME_AND_RACE_CLASS_ID,
                                            lobbyAccessToken
                                    )
                            )), user
            ).body();

            final String[] substrings = response.replaceFirst(".*\\[", "") // first level
                    .replaceFirst("Welcome.*", "") // second level
                    .split("\"");

            for (int i = substrings.length - 1; i >= 0; i--) {
                if (substrings[i].length() > 3)
                    return substrings[i];

                if (i == 0)
                    throw new FindException("Could not detect race track ID in response data.");
            }
        } catch (final IOException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new UnknownError("Unknown error when attempting to find race track ID.");
    }

    @Override
    public void sendStatusRequest() {
        try {
            RequestSender.makeAndFinishRequest(HttpRequest.newBuilder(
                    user.getSiteAccessUri())
                            .POST(HttpRequest.BodyProcessor.fromString(
                                    String.format(
                                            "7|1|6|%s|%s|_|getStatus|11|1z|1|2|3|4|1|5|5|580163|1|6|%s|",
                                            Constants.BASE_URL,
                                            Constants.NICK_NAME_AND_RACE_CLASS_ID,
                                            lobbyAccessToken)
                            )),
                    user);
        } catch (IOException | InterruptedException e) {
            System.out.println("Could not send a status request.");
        }
    }

    @Override
    public void sendJoinRequest() {
        try {
            RequestSender.makeAndFinishRequest(HttpRequest.newBuilder(
                    user.getSiteAccessUri())
                            .POST(HttpRequest.BodyProcessor.fromString(
                                    String.format(
                                            "7|1|7|%s|%s|_|joinGameInRoom|11|Z|1z|1|2|3|4|2|5|6|5|552929|1|7|%s|0|",
                                            Constants.BASE_URL,
                                            Constants.NICK_NAME_AND_RACE_CLASS_ID,
                                            lobbyAccessToken)
                            )),
                    user);
        } catch (IOException | InterruptedException e) {
            System.out.print("Could not send a join request.");
        }
    }

    @Override
    public void sendLeaveRequest() {
        try {
            RequestSender.makeAndFinishRequest(HttpRequest.newBuilder(
                    user.getSiteAccessUri())
                            .POST(HttpRequest.BodyProcessor.fromString(
                                    String.format(
                                            "7|1|6|%s|%s|_|leaveGame|11|1z|1|2|3|4|1|5|5|552929|1|6|%s|",
                                            Constants.BASE_URL,
                                            Constants.NICK_NAME_AND_RACE_CLASS_ID,
                                            lobbyAccessToken)
                            )),
                    user);
        } catch (IOException | InterruptedException e) {
            System.out.print("Could not send a join request.");
        }
    }
}
