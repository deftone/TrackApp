package de.deftone.trackapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class MyLocation implements Serializable {

    //todo: distance rausnehmen!

    @PrimaryKey
    private long timestamp;
    private int trackId;
    private double latitude;
    private double longitude;
    private double altitude; // in meters above the WGS 84 reference ellipsoid.
    private float speed;   // in meters/second over ground
    private float accuracy; // estimated horizontal accuracy of this location, radial, in meters
    private float verticalAccuracy; //vertical accuracy of this location, in meters
    private float speedAccuracy; //estimated speed accuracy of this location, in meters per second.
    private float distance; //unit: m

    public MyLocation(long timestamp, int trackId, double latitude, double longitude, double altitude,
                      float speed, float accuracy, float verticalAccuracy, float speedAccuracy,
                      float distance) {
        this.timestamp = timestamp;
        this.trackId = trackId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.accuracy = accuracy;
        this.verticalAccuracy = verticalAccuracy;
        this.speedAccuracy = speedAccuracy;
        this.distance = distance;
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

    public float getSpeed_km_h() {
        return speed * 3600 / 1000;
    }

    public double getAltitude() {
        return altitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getVerticalAccuracy() {
        return verticalAccuracy;
    }

    public float getSpeedAccuracy() {
        return speedAccuracy;
    }

    public float getSpeedAccuracy_km_h() {
        return speedAccuracy * 3600 / 1000;
    }

    public float getDistance() {
        return distance;
    }
}

