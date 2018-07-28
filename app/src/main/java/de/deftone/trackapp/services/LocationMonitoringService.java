package de.deftone.trackapp.services;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import de.deftone.trackapp.model.MyLocation;
import de.deftone.trackapp.settings.Constants;

import static de.deftone.trackapp.settings.Constants.ACTION_LOCATION_BROADCAST;
import static de.deftone.trackapp.settings.Constants.EXTRA_LOCATION;
import static de.deftone.trackapp.settings.Constants.LOCATION_DISTANCE_INTERVAL;
import static de.deftone.trackapp.settings.Constants.SHARED_PREF_TRACK_ID;
import static de.deftone.trackapp.settings.Constants.SHARED_PREF_TRACK_ID_KEY;


public class LocationMonitoringService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LocationRequest mLocationRequest = new LocationRequest();
    // Notification Unique id to identify created Notification from //service
    private static final int NOTIFICATION = 1001;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GoogleApiClient mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //use time diff for location update
        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_LOCATION_INTERVAL);
//        //todo: test this!!
//        //use distance diff for location update
//        mLocationRequest.setSmallestDisplacement(LOCATION_DISTANCE_INTERVAL);

        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes
        mLocationRequest.setPriority(priority);

        mLocationClient.connect();

        //todo: einkommentieren wenn nicht alleine unterwegs
//        //make it a forground service so it will update the location even when app in background
//        Notification notification = new Notification();
//        startForeground(NOTIFICATION, notification);
        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //only if permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    List<Location> locationList = locationResult.getLocations();
                    if (locationList.size() > 0) {

                        //The last location in the list is the newest
                        Location location = locationList.get(locationList.size() - 1);
                        MyLocation myLocation = new MyLocation(System.currentTimeMillis(), getTrackId(),
                                location.getLatitude(), location.getLongitude(),
                                location.getSpeed(), location.getAltitude());
                        //Send result to activities
                        sendMessageToUI(myLocation);
                    }
                }
            };

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } else {
            //no permission
            Toast.makeText(this, "Connection impossible! Permission denied :(", Toast.LENGTH_LONG).show();
        }
    }

    private void sendMessageToUI(MyLocation myLocation) {
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, myLocation);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    private int getTrackId(){
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_TRACK_ID, MODE_PRIVATE);
        return sharedPref.getInt(SHARED_PREF_TRACK_ID_KEY, 0);
    }

}
