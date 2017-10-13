package com.github.gonzalezjo.ezbot.typeracer.interfaces;

public interface RaceTrack {
    String getLobbyAccessToken();

    String getRaceTrackId();

    void sendStatusRequest();

    void sendJoinRequest();

    void sendLeaveRequest();
}
