package de.deftone.trackapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.deftone.trackapp.model.MyLocation;
import de.deftone.trackapp.services.DatabaseGetTrackIdsService;
import de.deftone.trackapp.services.DatabaseSaveRouteService;
import de.deftone.trackapp.services.LocationMonitoringService;
import de.deftone.trackapp.utils.CheckLocationServiceRequirements;
import de.deftone.trackapp.utils.TrackingUtils;

import static de.deftone.trackapp.settings.Constants.ACTION_LOCATION_BROADCAST;
import static de.deftone.trackapp.settings.Constants.EXTRA_LOCATION;
import static de.deftone.trackapp.settings.Constants.REQUEST_PERMISSIONS_REQUEST_CODE;
import static de.deftone.trackapp.settings.Constants.SHARED_PREF_TRACK_ID;
import static de.deftone.trackapp.settings.Constants.SHARED_PREF_TRACK_ID_KEY;

//todo: 1) abourt button, 2) pause tracking button
//todo: 3) besseres layout der aktuellen anzeige und lat/long mit accuracy, current speed, diff altitude hinzufuegen
//todo: 4) save automatically after 100 points - bei abort muss dann aber das auch geloescht werden!

public class MainActivity extends AppCompatActivity {

    private boolean locatingServiceStarted = false;
    private boolean trackingActive = false;
    private List<MyLocation> myLocationList = new ArrayList<>();
    private List<Location> locationList = new ArrayList<>();
    @BindView(R.id.button_start)
    Button buttonStart;
    @BindView(R.id.button_save)
    Button buttonSave;
    @BindView(R.id.trackIdView)
    TextView trackIdView;
    @BindView(R.id.accuracyView)
    TextView accuracyView;
    @BindView(R.id.distanceView)
    TextView distanceView;
    @BindView(R.id.speedView)
    TextView speedView;
    @BindView(R.id.speedAvgView)
    TextView speedAvgView;
    @BindView(R.id.altitudeView)
    TextView altitudeView;
    @BindView(R.id.altitudeDiffView)
    TextView altitudeDiffView;
    @BindView(R.id.durationView)
    TextView durationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        updateLayout(View.GONE, View.GONE);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (trackingActive) {
                            MyLocation myLocation = (MyLocation) intent.getSerializableExtra(EXTRA_LOCATION);
                            if (myLocation != null) {
                                myLocationList.add(myLocation);
//                                showLocationsInTextView(myLocationList);
                                //also "real" Location for distance
                                Location location = new Location("own location");
                                location.setLatitude(myLocation.getLatitude());
                                location.setLongitude(myLocation.getLongitude());
                                locationList.add(new Location(location));
                                //update info text views
                                updateInfoBoxes();
                            }
                        }
                    }
                }, new IntentFilter(ACTION_LOCATION_BROADCAST)
        );

        Toast.makeText(this, R.string.activate_gps, Toast.LENGTH_LONG).show();

        //check requirements and start service
        CheckLocationServiceRequirements checkLocationServiceRequirements = new CheckLocationServiceRequirements(this, this);
        if (checkLocationServiceRequirements.isGooglePlayServiceAvailable()) {
            if (checkLocationServiceRequirements.checkLocationPermissions()) {
                //everything ok, show start button and start service
                updateLayout(View.VISIBLE, View.GONE);
                startLocationMonitorService();
            } else {
                checkLocationServiceRequirements.requestLocationsPermissions();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_google_playservice_available, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Callback received when a permissions request has been completed. - can't be outsourced :(
     * //todo manu: kann ich das irgendwie in die util klasse auslagern?
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //everything okay - show start button and start service
                updateLayout(View.VISIBLE, View.GONE);
                startLocationMonitorService();
            } else {
                // Permission denied.
                Toast.makeText(this, getString(R.string.permission_denied_explanation), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        stopLocationMonitorService();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.show_routes_list:
                DatabaseGetTrackIdsService databaseGetTrackIdsService = new DatabaseGetTrackIdsService(this);
                databaseGetTrackIdsService.execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateLayout(int startVisibility, int saveVisibility) {
        buttonStart.setVisibility(startVisibility);
        buttonSave.setVisibility(saveVisibility);
        durationView.setVisibility(saveVisibility);
        speedView.setVisibility(saveVisibility);
        distanceView.setVisibility(saveVisibility);
        altitudeView.setVisibility(saveVisibility);
    }

    private void updateInfoBoxes() {
        trackIdView.setText(String.valueOf(getTrackId()));
        accuracyView.setText(TrackingUtils.getAccuracy(myLocationList));
        durationView.setText(TrackingUtils.getDuration(myLocationList));
        distanceView.setText(TrackingUtils.getDistanceInKm(locationList));
        speedView.setText(TrackingUtils.getCurrentSpeed(myLocationList));
        speedAvgView.setText(TrackingUtils.getAverageSpeedInMotion(myLocationList));
        altitudeView.setText(TrackingUtils.getLastAltitude(myLocationList));
        altitudeDiffView.setText("");
    }

    private void startLocationMonitorService() {
        //And it will be keep running until you close the entire application from task manager
        //no -> that does not work: it will be stopped when saving the route
        if (!locatingServiceStarted) {
            //Start location sharing service to app server.........
            Intent locationServiceIntent = new Intent(this, LocationMonitoringService.class);
            startService(locationServiceIntent);
            locatingServiceStarted = true;
        }
    }

    private void stopLocationMonitorService() {
        //Stop location sharing service to app server  -das hier funktioniert nicht richtig.... workaround!
        stopService(new Intent(this, LocationMonitoringService.class));
        locatingServiceStarted = false;
    }


    @OnClick(R.id.button_start)
    void startTracking() {
        trackingActive = true;
        //get and set track id
        setTrackId(getTrackId() + 1);
        //reset location list
        myLocationList.clear();
        locationList.clear();
        //update layout
        Toast.makeText(this, R.string.msg_location_service_started, Toast.LENGTH_LONG).show();
        updateLayout(View.GONE, View.VISIBLE);
    }

    @OnClick(R.id.button_save)
    void saveRoute() {
        trackingActive = false;
        //add location to database
        DatabaseSaveRouteService databaseSaveRouteService = new DatabaseSaveRouteService(this);
        databaseSaveRouteService.execute(myLocationList);
        //update layout
        updateLayout(View.VISIBLE, View.GONE);
        Toast.makeText(this, R.string.route_saved, Toast.LENGTH_LONG).show();
    }

    private int getTrackId() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_TRACK_ID, MODE_PRIVATE);
        return sharedPref.getInt(SHARED_PREF_TRACK_ID_KEY, 0);
    }

    private void setTrackId(int id) {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_TRACK_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(SHARED_PREF_TRACK_ID_KEY, id).apply();
    }
}
