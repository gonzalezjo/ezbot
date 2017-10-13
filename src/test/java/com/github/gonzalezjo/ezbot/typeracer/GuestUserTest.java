package com.github.gonzalezjo.ezbot.typeracer;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class GuestUserTest {

    @Test
    public void getNickName() {
    }

    @Test
    public void setNickName() {
    }

    @Test
    public void getRaceTrackId() {
    }

    @Test
    public void createDefaultEzBot() {
        GuestUser user;

        try {
            user = new GuestUser();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Assert.fail("Error while creating user.");
            return;
        }

        System.out.println(String.format("Username: %s", user.getNickName()));
        System.out.println(String.format("Access URI: %s", user.getSiteAccessUri()));
        System.out.println(String.format("Lobby access token: %s", user.getLobbyAccessToken()));
        System.out.println(String.format("Lobby ID: %s", user.getRaceTrackId()));
        System.out.println(String.format(
                "Lobby join URL: http://play.typeracer.com/?rt=%s",
                user.getRaceTrackId()));

        user.idleInRoom();
        user.sendJoinLeaves();
    }
}