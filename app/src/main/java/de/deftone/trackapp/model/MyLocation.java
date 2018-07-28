package de.deftone.trackapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class MyLocation implements Serializable{

    @PrimaryKey
    private long timestamp;
    private int trackId;
    private double latitude;
    private double longitude;
    private float speed;   //converted to km/h
    private double altitude;

    public MyLocation(long timestamp, int trackId, double latitude, double longitude, float speed,
                      double altitude){
        this.timestamp = timestamp;
        this.trackId = trackId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed*3600/1000;
        this.altitude = altitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getTrackId() {
        return trackId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public double getAltitude() {
        return altitude;
    }
}
