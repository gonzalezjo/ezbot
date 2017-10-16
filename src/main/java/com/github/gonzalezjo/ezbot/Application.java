package com.github.gonzalezjo.ezbot;

import com.github.gonzalezjo.ezbot.typeracer.GuestUser;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class Application {
    public static void main(String[] args) throws IOException {
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

        Desktop.getDesktop().browse(URI.create(
                "http://play.typeracer.com/?rt=" + user.getRaceTrackId()));
        JOptionPane.showMessageDialog(null, "Click okay to exit.");
        System.exit(-1);
    }
}
