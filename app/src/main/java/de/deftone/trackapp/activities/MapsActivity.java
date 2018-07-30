package de.deftone.trackapp.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import de.deftone.trackapp.R;
import de.deftone.trackapp.model.MyLocation;
import de.deftone.trackapp.utils.TrackingUtils;

import static de.deftone.trackapp.settings.Constants.EXTRA_LOCATION_LIST;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    ArrayList<MyLocation> myLocations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //get locations
        Intent intent = getIntent();
        myLocations = (ArrayList<MyLocation>) intent.getSerializableExtra(EXTRA_LOCATION_LIST);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        System.out.println("speed: " + TrackingUtils.getAverageSpeedInMotion(myLocations));

        System.out.println("duration: " + TrackingUtils.getDuration(myLocations));

        System.out.println("last altitude: " + TrackingUtils.getLastAltitude(myLocations));

//        List<Double> altitudes = TrackingUtils.getAltitudesInM(myLocations);
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
        System.out.println("distance mit between: " + TrackingUtils.getDistanceInKm(locations));

        //add marker to last point
        int lastPoint = myLocations.size() - 1;
        LatLng latLngFinal = new LatLng(myLocations.get(lastPoint).getLatitude(), myLocations.get(lastPoint).getLongitude());
        Marker markerFinal = googleMap.addMarker(new MarkerOptions().position(latLngFinal).title("Finish"));
        //only one marker can show info window, except you do this:
        //https://stackoverflow.com/questions/23407059/how-to-open-a-infowindow-on-every-marker-multiple-marker-in-android
        //        markerFinal.showInfoWindow();
    }

}
