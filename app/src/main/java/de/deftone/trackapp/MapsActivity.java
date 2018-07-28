package de.deftone.trackapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.deftone.trackapp.model.MyLocation;
import de.deftone.trackapp.services.DatabaseDeleteRouteService;
import de.deftone.trackapp.services.DatabaseGetRouteService;
import de.deftone.trackapp.services.DatabaseSaveRouteService;
import de.deftone.trackapp.services.LocationMonitoringService;
import de.deftone.trackapp.utils.CheckLocationServiceRequirements;

import static de.deftone.trackapp.settings.Constants.ACTION_LOCATION_BROADCAST;
import static de.deftone.trackapp.settings.Constants.EXTRA_LOCATION;
import static de.deftone.trackapp.settings.Constants.FORMATTER;
import static de.deftone.trackapp.settings.Constants.REQUEST_PERMISSIONS_REQUEST_CODE;
import static de.deftone.trackapp.settings.Constants.SHARED_PREF_TRACK_ID;
import static de.deftone.trackapp.settings.Constants.SHARED_PREF_TRACK_ID_KEY;

public class MapsActivity extends AppCompatActivity {

    private boolean locatingServiceStarted = false;
    private boolean trackingActive = false;
    private List<MyLocation> allLocations = new ArrayList<>();
    private Context context = this;
    private Intent locationServiceIntent;
    @BindView(R.id.button_start)
    Button buttonStart;
    @BindView(R.id.button_save)
    Button buttonSave;
    @BindView(R.id.msgView)
    TextView messageTextView;

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
                                allLocations.add(myLocation);
                                showLocationsInTextView(allLocations);
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

    //todo manu: kann ich das irgendwie in die util klasse auslagern?

    /**
     * Callback received when a permissions request has been completed. - can't be outsourced :(
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

    private void updateLayout(int startVisibility, int saveVisibility) {
        buttonStart.setVisibility(startVisibility);
        buttonSave.setVisibility(saveVisibility);
    }

    private void showLocationsInTextView(List<MyLocation> allLocations) {
        //now update textview
        StringBuilder locationString = new StringBuilder();
        for (MyLocation location : allLocations) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(location.getTimestamp()),
                    TimeZone.getDefault().toZoneId());
            locationString.append(location.getTrackId()).append(", ")
                    .append(dateTime.format(FORMATTER)).append(", ")
                    .append(location.getLatitude()).append(", ")
                    .append(location.getLongitude()).append(", ")
                    .append(location.getSpeed()).append(", ")
                    .append(location.getAltitude()).append(System.lineSeparator());
        }
        messageTextView.setText(locationString.toString());
    }

    private void startLocationMonitorService() {
        //And it will be keep running until you close the entire application from task manager
        //no -> that does not work: it will be stopped when saving the route
        if (!locatingServiceStarted) {
            //Start location sharing service to app server.........
             locationServiceIntent = new Intent(this, LocationMonitoringService.class);
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
        allLocations.clear();
        //update layout
        messageTextView.setText(R.string.msg_location_service_started);
        updateLayout(View.GONE, View.VISIBLE);
    }

    @OnClick(R.id.button_save)
    void saveRoute() {
        trackingActive = false;
        //add location to database
        DatabaseSaveRouteService databaseSaveRouteService = new DatabaseSaveRouteService(this);
        databaseSaveRouteService.execute(allLocations);
        //update layout
        updateLayout(View.VISIBLE, View.GONE);
        messageTextView.setText(R.string.route_saved);
    }

    @OnClick(R.id.button_show)
    void showAllRoutes() {
        DatabaseGetRouteService databaseGetRouteService = new DatabaseGetRouteService(this, messageTextView);
//        //todo: auswaehlbar machen!
//        int trackId = 7;
//        databaseGetRouteService.execute(trackId);
        databaseGetRouteService.execute();
    }

    //todo katrin!
    @OnClick(R.id.button_delete)
    void deleteAllRoutes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(R.string.delete_title)
                .setMessage(R.string.delete_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        DatabaseDeleteRouteService databaseDeleteRouteService = new DatabaseDeleteRouteService(context);
                        //todo: hier muss natuerlich eine List mit MyLocations uebergeben werden! sonst geht das nicth!!
                        databaseDeleteRouteService.execute();
                        messageTextView.setText("");
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
