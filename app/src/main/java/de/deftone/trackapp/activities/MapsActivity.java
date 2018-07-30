package de.deftone.trackapp.activities;

import android.content.Intent;
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

import de.deftone.trackapp.R;
import de.deftone.trackapp.model.MyLocation;

import static de.deftone.trackapp.settings.Constants.EXTRA_LOCATION_LIST;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    ArrayList<MyLocation> locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //get locations
        Intent intent = getIntent();
        locationList = (ArrayList<MyLocation>) intent.getSerializableExtra(EXTRA_LOCATION_LIST);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        System.out.println("speed: " + getAverageSpeed());

        System.out.println("duration: " + getDuration());
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
        LatLng latLngFirst = new LatLng(locationList.get(0).getLatitude(), locationList.get(0).getLongitude());
        Marker markerStart = googleMap.addMarker(new MarkerOptions().position(latLngFirst).title("Start"));
        markerStart.showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngFirst, 13));

        //put small icon for all points
        for (MyLocation location : locationList) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.point));
        }

        //add marker to last point
        int lastPoint = locationList.size() - 1;
        LatLng latLngFinal = new LatLng(locationList.get(lastPoint).getLatitude(), locationList.get(lastPoint).getLongitude());
        Marker markerFinal = googleMap.addMarker(new MarkerOptions().position(latLngFinal).title("Finish"));
        //only one marker can show info window, except you do this:
        //https://stackoverflow.com/questions/23407059/how-to-open-a-infowindow-on-every-marker-multiple-marker-in-android
        //        markerFinal.showInfoWindow();
    }

    //todo: use getSpeed_km_h
    private float getAverageSpeed() {
        Float speedSum = 0F;
        int count = 0;
        for (MyLocation location : locationList) {
            if (location.getSpeed() > 0) {
            speedSum += location.getSpeed();
                count++;
            }
        }
        return speedSum / count;
    }

    private String getDuration() {
        int last = locationList.size() - 1;
        long diffMillisec = locationList.get(last).getTimestamp() -
                locationList.get(0).getTimestamp();
        long minutes = diffMillisec / 1000 / 60;
        if (minutes < 60) {
            return "0h:" + minutes + "min";
        } else
            return minutes / 60 + "h:" + minutes % 60 + "min";
    }


}
