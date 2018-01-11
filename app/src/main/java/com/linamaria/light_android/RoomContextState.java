package com.linamaria.light_android;

/**
 * Created by LinaMaria on 12/13/2017.
 */

public class RoomContextState {

    private String room;
    private Status lightStatus;
    private Status ringerStatus;
    private int lightLevel;
    private int noiseLevel;

    public RoomContextState(String room, Status lightStatus, Status ringerStatus, int lightLevel, int noiseLevel) {
        super();
        this.room = room;
        this.lightStatus = lightStatus;
        this.ringerStatus = ringerStatus;
        this.lightLevel = lightLevel;
        this.noiseLevel = noiseLevel;
    }

    public String getRoom() {
        return this.room;
    }

    public Status getLightStatus() {
        return this.lightStatus;
    }

    public Status getRingerStatus() {
        return ringerStatus;
    }

    public int getLightLevel() {
        return this.lightLevel;
    }

    public int getNoiseLevel() {
        return this.noiseLevel;
    }

    public static enum Status{
        ON, OFF;
    }

}
