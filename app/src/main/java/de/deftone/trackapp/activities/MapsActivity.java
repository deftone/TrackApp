package de.deftone.trackapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.deftone.trackapp.R;
import de.deftone.trackapp.model.MyLocation;
import de.deftone.trackapp.services.DatabaseDeleteRouteService;
import de.deftone.trackapp.services.DatabaseGetTrackIdsService;
import de.deftone.trackapp.utils.TrackingUtils;

import static de.deftone.trackapp.settings.Constants.EXTRA_LOCATION_LIST;
import static de.deftone.trackapp.settings.Constants.SHARED_PREF_NAME;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Context context = this;
    private ArrayList<MyLocation> myLocations;
    private int trackId;
    private String routeName;
    private SharedPreferences pref;
    @BindView(R.id.distanceView)
    TextView distanceView;
    @BindView(R.id.speedView)
    TextView speedView;
    @BindView(R.id.altitudeView)
    TextView altitudeView;
    @BindView(R.id.durationView)
    TextView durationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        //get locations
        Intent intent = getIntent();
        myLocations = (ArrayList<MyLocation>) intent.getSerializableExtra(EXTRA_LOCATION_LIST);

        // get route infos
        trackId = myLocations.get(0).getTrackId();
        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        routeName = pref.getString(String.valueOf(trackId), "-");
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(myLocations.get(0).getTimestamp()/1000,
                0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String date = formatter.format(localDateTime);
        //adjust title of activity
        setTitle(routeName + " (" + date + ")");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_routes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.delete_route:
                deleteRoute(trackId, routeName);
                return true;

            case R.id.save_route_name:
                saveNewNameForRoute(pref, trackId, routeName);
                return true;

            case R.id.show_profile:
                Intent intent = new Intent(this, AltitudeActivity.class);
                intent.putExtra(EXTRA_LOCATION_LIST, myLocations);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteRoute(int trackId, String routeName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = "Delete route ";
        if (routeName.equals("-"))
            title += "#" + trackId;
        else
            title += "'" + routeName + "'";
        builder.setTitle(title)
                .setMessage(R.string.delete_message)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        DatabaseDeleteRouteService databaseDeleteRouteService = new DatabaseDeleteRouteService(context);
                        databaseDeleteRouteService.execute(myLocations);
                        DatabaseGetTrackIdsService service = new DatabaseGetTrackIdsService(context);
                        service.execute();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void saveNewNameForRoute(final SharedPreferences pref, final int trackId, final String oldName) {

        String message = "Enter name for this route:";
        if (!oldName.equals("-"))
            message = "(and overwrite existing name '" + oldName + "')";

        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Save route #" + trackId)
                .setMessage(message)
                .setView(taskEditText)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = String.valueOf(taskEditText.getText());
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(String.valueOf(trackId), newName);
                        editor.apply();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //add marker with title to first point
        LatLng latLngFirst = new LatLng(myLocations.get(0).getLatitude(), myLocations.get(0).getLongitude());
        Marker markerStart = googleMap.addMarker(new MarkerOptions().position(latLngFirst).title("Start"));
        markerStart.showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngFirst, 13));

        List<Location> locations = new ArrayList<>();

        //put small icon for all points
        for (MyLocation myLocation : myLocations) {
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.point));
            Location location = new Location("own location");
            location.setLatitude(myLocation.getLatitude());
            location.setLongitude(myLocation.getLongitude());
//            location.setSpeed(myLocation.getSpeed());
            location.setAccuracy(myLocation.getAccuracy());
            locations.add(new Location(location));
        }

        //now calculate distance:

        //add marker to last point
        int lastPoint = myLocations.size() - 1;
        LatLng latLngFinal = new LatLng(myLocations.get(lastPoint).getLatitude(), myLocations.get(lastPoint).getLongitude());
        Marker markerFinal = googleMap.addMarker(new MarkerOptions().position(latLngFinal).title("Finish"));
        //only one marker can show info window, except you do this:
        //https://stackoverflow.com/questions/23407059/how-to-open-a-infowindow-on-every-marker-multiple-marker-in-android
        //        markerFinal.showInfoWindow();

        //update textviews:
        float distanceInKm = TrackingUtils.getAndSetDistanceInKm(locations, myLocations);
        float durationInH = TrackingUtils.getDurationInH(myLocations);

        durationView.setText(TrackingUtils.getDuration(myLocations));
        speedView.setText(String.format("%.2f km/h", distanceInKm / durationInH));
        distanceView.setText(TrackingUtils.getDistanceInKm(distanceInKm));
        altitudeView.setText(TrackingUtils.getDifferenceAltitude(myLocations));
    }

    @Override
    public void onBackPressed() {
        DatabaseGetTrackIdsService service = new DatabaseGetTrackIdsService(context);
        service.execute();
    }
}