//
//
//
//package de.deftone.trackapp;
//
//        import android.Manifest;
//        import android.content.DialogInterface;
//        import android.content.IntentSender;
//        import android.content.pm.PackageManager;
//        import android.location.Location;
//        import android.os.Build;
//        import android.os.Looper;
//        import android.support.annotation.NonNull;
//        import android.support.annotation.Nullable;
//        import android.support.v4.app.ActivityCompat;
//        import android.support.v4.app.FragmentActivity;
//        import android.os.Bundle;
//        import android.support.v4.content.ContextCompat;
//        import android.support.v7.app.AlertDialog;
//        import android.util.Log;
//        import android.widget.Toast;
//
//        import com.google.android.gms.common.ConnectionResult;
//        import com.google.android.gms.common.api.GoogleApiClient;
//        import com.google.android.gms.location.FusedLocationProviderClient;
//        import com.google.android.gms.location.LocationCallback;
//        import com.google.android.gms.location.LocationListener;
//        import com.google.android.gms.location.LocationRequest;
//        import com.google.android.gms.location.LocationResult;
//        import com.google.android.gms.location.LocationServices;
//        import com.google.android.gms.maps.CameraUpdateFactory;
//        import com.google.android.gms.maps.GoogleMap;
//        import com.google.android.gms.maps.OnMapReadyCallback;
//        import com.google.android.gms.maps.SupportMapFragment;
//        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//        import com.google.android.gms.maps.model.LatLng;
//        import com.google.android.gms.maps.model.Marker;
//        import com.google.android.gms.maps.model.MarkerOptions;
//
//        import java.util.List;
//
//public class testcode extends FragmentActivity implements OnMapReadyCallback,
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        LocationListener {
//
//    /*
//     * Define a request code to send to Google Play services
//     * This code is returned in Activity.onActivityResult
//     */
//    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
//    public static final String TAG = MapsActivity.class.getSimpleName();
//
//    private GoogleMap mMap;
//    private GoogleApiClient mGoogleApiClient;
//    private LocationRequest mLocationRequest;
//    private FusedLocationProviderClient mFusedLocationClient;
//    private SupportMapFragment mapFrag;
//    private GoogleMap mGoogleMap;
//
//    private Location mLastLocation;
//    private Marker mCurrLocationMarker;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//
//        //first check permissions:
////        checkLocationPermission();
//
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFrag.getMapAsync(this);
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//
//        // Create the LocationRequest object
//        mLocationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
//                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
//    }
//
////    @Override
////    protected void onResume() {
////        super.onResume();
//////        setUpMapIfNeeded();
////        mGoogleApiClient.connect();
////    }
//
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
////        mMap = googleMap;
////
////        // Add a marker in Sydney and move the camera
////        LatLng sydney = new LatLng(-34, 151);
////        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
////        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//        mGoogleMap = googleMap;
////        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(5000); // 5 sec interval
//        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            //Location Permission already granted
//            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//            mGoogleMap.setMyLocationEnabled(true);
//        }
//    }
//
//    LocationCallback mLocationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            List<Location> locationList = locationResult.getLocations();
//            if (locationList.size() > 0) {
//                //The last location in the list is the newest
//                Location location = locationList.get(locationList.size() - 1);
//                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
//                mLastLocation = location;
//                if (mCurrLocationMarker != null) {
//                    mCurrLocationMarker.remove();
//                }
//
//                //Place current location marker
//                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//                markerOptions.title("Current Position");
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
//
//                //move map camera
//                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
//            }
//        }
//    };
//
//    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
//
////    private void checkLocationPermission() {
////        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
////                != PackageManager.PERMISSION_GRANTED) {
////
////            // Should we show an explanation?
////            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
////                    Manifest.permission.ACCESS_FINE_LOCATION)) {
////
////                // Show an explanation to the user *asynchronously* -- don't block
////                // this thread waiting for the user's response! After the user
////                // sees the explanation, try again to request the permission.
////                new AlertDialog.Builder(this)
////                        .setTitle("Location Permission Needed")
////                        .setMessage("This app needs the Location permission, please accept to use location functionality")
////                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialogInterface, int i) {
////                                //Prompt the user once explanation has been shown
////                                ActivityCompat.requestPermissions(MapsActivity.this,
////                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
////                                        MY_PERMISSIONS_REQUEST_LOCATION);
////                            }
////                        })
////                        .create()
////                        .show();
////
////
////            } else {
////                // No explanation needed, we can request the permission.
////                ActivityCompat.requestPermissions(this,
////                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
////                        MY_PERMISSIONS_REQUEST_LOCATION);
////            }
////        }
////    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // location-related task you need to do.
//                    if (ContextCompat.checkSelfPermission(this,
//                            Manifest.permission.ACCESS_FINE_LOCATION)
//                            == PackageManager.PERMISSION_GRANTED) {
//
//                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//                        mGoogleMap.setMyLocationEnabled(true);
//                    }
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }
//
//
//    /**
//     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
//     * just add a marker near Africa.
//     * <p/>
//     * This should only be called once and when we are sure that {@link #mMap} is not null.
//     */
//    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
//    }
//
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        if (connectionResult.hasResolution()) {
//            try {
//                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
//            } catch (IntentSender.SendIntentException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
//        }
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        Log.i(TAG, "Location services connected.");
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (location == null) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        } else {
//            handleNewLocation(location);
//        }
//    }
//
//    private void handleNewLocation(Location location) {
//        Log.d(TAG, location.toString());
//
//        double currentLatitude = location.getLatitude();
//        double currentLongitude = location.getLongitude();
//
//        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
//
//        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
//        MarkerOptions options = new MarkerOptions()
//                .position(latLng)
//                .title("I am here!");
//        mMap.addMarker(options);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//    }
//
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.i(TAG, "Location services suspended. Please reconnect.");
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        handleNewLocation(location);
//    }
//}
//
