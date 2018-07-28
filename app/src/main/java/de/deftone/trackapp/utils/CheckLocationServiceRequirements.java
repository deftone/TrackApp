package de.deftone.trackapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import de.deftone.trackapp.R;

import static de.deftone.trackapp.settings.Constants.REQUEST_PERMISSIONS_REQUEST_CODE;

public class CheckLocationServiceRequirements {

    private Context context;
    private Activity activity;

    public CheckLocationServiceRequirements(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    /**
     * Return the availability of GooglePlayServices
     */
    public boolean isGooglePlayServiceAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    /**
     * Return the current state of the permissions needed.
     */
    public boolean checkLocationPermissions() {
        int permissionStateFine = ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionStateCoarse = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionStateFine == PackageManager.PERMISSION_GRANTED
                && permissionStateCoarse == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Start permissions requests.
     * wenn man einmal ablehnt, kommt das nicht nochmal....? warum?
     */
    public void requestLocationsPermissions() {

        boolean shouldProvideRationaleFine =
                ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationaleCoarse =
                ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationaleFine || shouldProvideRationaleCoarse) {
            Toast.makeText(activity, activity.getString(R.string.permission_rationale), Toast.LENGTH_LONG).show();
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}
