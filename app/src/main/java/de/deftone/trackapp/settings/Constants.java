package de.deftone.trackapp.settings;

import java.time.format.DateTimeFormatter;

import de.deftone.trackapp.services.LocationMonitoringService;

public interface Constants {

    //set the interval in which you want to get locations
    public static final int LOCATION_INTERVAL = 10000;//120000;  //every two minutes
    //if a location is available sooner you can get it (i.e. another app is using the location services).
    public static final int FASTEST_LOCATION_INTERVAL = 10000;//60000;  //every minute
    //how many meters diference to get a new location
    public static final float LOCATION_DISTANCE_INTERVAL = 10.0f; // every 10 meters

    public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LOCATION = "extra_location";
    public static final String EXTRA_LOCATION_LIST = "extra_location_list";
    public static final String EXTRA_TRACK_SET = "extra_all_track_ids";


    public static final int PERMISSIONS_REQUEST_LOCATION = 99;
    /**
     * Code used in requesting runtime permissions.
     */
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss");

    public static final String SHARED_PREF_TRACK_ID = "shared_pref_file_for_track_id";
    public static final String SHARED_PREF_TRACK_ID_KEY = "shared_pref_file_for_track_id_key";

    // Notification Unique id to identify created Notification from //service
    public static final int NOTIFICATION = 1001;

}
