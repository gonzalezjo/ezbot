package com.github.gonzalezjo.ezbot.typeracer;

import com.github.gonzalezjo.ezbot.common.Constants;
import com.github.gonzalezjo.ezbot.common.RequestSender;
import com.github.gonzalezjo.ezbot.typeracer.interfaces.RaceTrack;
import com.github.gonzalezjo.ezbot.typeracer.interfaces.User;
import jdk.incubator.http.HttpRequest;

import java.io.IOException;
import java.lang.module.FindException;
import java.net.URI;
import java.util.Arrays;

public class GuestUser implements User {
    private final JSession jSession;
    private final URI siteAccessUri;
    private final RaceTrack raceTrack;
    private final String gwtPermutation;

    private Thread statusRequestThread;
    private Thread joinLeaveThread;
    private String nickName;
    private String spoofedGwtPermutation;

    public GuestUser() throws IOException, InterruptedException {
        this(new JSession(), Constants.DEFAULT_NICK_NAME);
    }

    public GuestUser(final JSession jSession,
                     final String nickName) {
        this.jSession = jSession;

        siteAccessUri = URI.create(String.format("%s%s;%s%s",
                Constants.BASE_URL,
                Constants.GAME_SERVER_SUFFIX,
                Constants.JSESSION_ID_SUFFIX,
                jSession.id()));

        gwtPermutation = getGwtPermutation();

        raceTrack = new UserRaceTrack(this);

        setNickName(nickName);
    }

    public GuestUser(final JSession jSession) {
        this(jSession, Constants.DEFAULT_NICK_NAME);
    }

    @Override
    public String getGwtPermutation() {
        if (spoofedGwtPermutation != null)
            if (gwtPermutation != null)
                return gwtPermutation;
            else
                return spoofedGwtPermutation;

        try {
            return testPermutations(RequestSender.makeRequest(
                    HttpRequest.newBuilder(
                            URI.create(Constants.BASE_URL + Constants.GUEST_NOCACHE_SUFFIX))
                            .build())
                    .body()
                    .split("'"));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        throw new FindException("Could not find a valid gwtPermutation");
    }

    @Override
    public URI getSiteAccessUri() {
        return siteAccessUri;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(final String nickName) {
        try {
            RequestSender.makeAndFinishRequest(
                    HttpRequest.newBuilder(getSiteAccessUri())
                            .POST(HttpRequest.BodyProcessor.fromString(
                                    String.format(
                                            "7|1|7|%s|%s|_|editUserInfo|2v|1i|%s|1|2|3|4|2|5|6|7|0|",
                                            Constants.BASE_URL,
                                            Constants.NICK_NAME_AND_RACE_CLASS_ID,
                                            nickName)
                            )),
                    this);
            this.nickName = nickName;
        } catch (final IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLobbyAccessToken() {
        return raceTrack.getLobbyAccessToken();
    }

    public String getRaceTrackId() {
        return raceTrack.getRaceTrackId();
    }

    public void idleInRoom() {
        if (statusRequestThread != null)
            statusRequestThread.interrupt();

        statusRequestThread = new Thread(() -> {
            while (true) {
                raceTrack.sendStatusRequest();
                try {
                    Thread.sleep(500, 0);
                } catch (InterruptedException ignored) {
                    System.out.println("Killing idle loop.");
                    break;
                }
            }
        });

        statusRequestThread.start();
    }

    public void sendJoinLeaves() {
        if (joinLeaveThread != null)
            joinLeaveThread.interrupt();

        joinLeaveThread = new Thread(() -> {
            while (true) {
                raceTrack.sendJoinRequest();
                try {
                    Thread.sleep(500, 0);
                } catch (InterruptedException ignored) {
                    System.out.println("Killing join/leave loop.");
                    break;
                }
                raceTrack.sendLeaveRequest();
            }
        });

        joinLeaveThread.start();
    }

    private boolean isValidGwtPermutation(final String permutation) {
        this.spoofedGwtPermutation = permutation;

        try {
            new UserRaceTrack(this).getLobbyAccessToken();
            return true;
        } catch (final Exception ignored) {
            return false;
        }

    }

    private String testPermutations(final String[] split) {
        return Arrays.stream(split)
                .sequential()
                .filter(s -> s.length() == 32)
                .filter(s -> s.replaceAll("[\\p{Upper}\\p{Digit}]", "")
                        .isEmpty())
                .filter(this::isValidGwtPermutation)
                .findFirst()
                .orElseThrow(() -> new FindException("Could not find a GWT permutation"));
    }
}
