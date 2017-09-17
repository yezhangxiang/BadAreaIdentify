package com.company;

/**
 * Created by kele on 2017/9/13.
 */
public class Position {
    private int longitude;
    private int latitude;

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public int getLatitude() {
        return latitude;
    }

    public Position(int longitude, int latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }
}
