package com.github.gonzalezjo.ezbot;

import com.github.gonzalezjo.ezbot.typeracer.GuestUser;

import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        GuestUser user;

        try {
            user = new GuestUser();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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
