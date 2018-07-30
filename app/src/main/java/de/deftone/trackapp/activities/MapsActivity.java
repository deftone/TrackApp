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


        System.out.println("speed: " + getAverageSpeed());

        System.out.println("duration: " + getDuration());

        List<Double> altitudes = getAltitudes();
        System.out.println();
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
            locations.add(new Location(location));
        }

        //now calculate distance:
        System.out.println("distance mit between: " + getDistance(locations));

        //add marker to last point
        int lastPoint = myLocations.size() - 1;
        LatLng latLngFinal = new LatLng(myLocations.get(lastPoint).getLatitude(), myLocations.get(lastPoint).getLongitude());
        Marker markerFinal = googleMap.addMarker(new MarkerOptions().position(latLngFinal).title("Finish"));
        //only one marker can show info window, except you do this:
        //https://stackoverflow.com/questions/23407059/how-to-open-a-infowindow-on-every-marker-multiple-marker-in-android
        //        markerFinal.showInfoWindow();
    }


    private float getAverageSpeed() {
        Float speedSum = 0F;
        int count = 0;
        for (MyLocation location : myLocations) {
            if (location.getSpeed() > 0 && location.getSpeedAccuracy_km_h() < 0.1) {
                speedSum += location.getSpeed_km_h();
                count++;
            }
        }
        System.out.println("speed>0: " + count);
        System.out.println("speed=0: " + (myLocations.size() - count));
        return speedSum / count;
    }

    private String getDuration() {
        int last = myLocations.size() - 1;
        long diffMillisec = myLocations.get(last).getTimestamp() -
                myLocations.get(0).getTimestamp();
        long minutes = diffMillisec / 1000 / 60;
        if (minutes < 60) {
            return "0h:" + minutes + "min";
        } else
            return minutes / 60 + "h:" + minutes % 60 + "min";
    }

//    //hier kommt totaler quatsch raus!
//    private float getDistance() {
//        float length = 0;
//        for (MyLocation location : myLocations) {
//            if (location.getVerticalAccuracy() < 10) {
//                length += location.getDistance();
//            }
//        }
//        return length;
//    }

    //das hier scheint richtig zu sein
    private float getDistance(List<Location> locations) {
        float distance = 0;
        float[] result = {0};
        for (int i = 0; i < locations.size() - 1; i++) {
            Location.distanceBetween(locations.get(i).getLatitude(),
                    locations.get(i).getLongitude(),
                    locations.get(i + 1).getLatitude(),
                    locations.get(i + 1).getLongitude(),
                    result);
            distance += result[0];
        }
        return distance;
    }

    private List<Double> getAltitudes() {
        List<Double> altitudeList = new ArrayList<>();
        for (MyLocation location : myLocations) {
            if (location.getVerticalAccuracy() <= 10)
                altitudeList.add(location.getAltitude());
        }
        return altitudeList;
    }

}
