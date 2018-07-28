package de.deftone.trackapp.services;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import de.deftone.trackapp.database.MyLocationDB;
import de.deftone.trackapp.model.MyLocation;

import static de.deftone.trackapp.settings.Constants.FORMATTER;

public class DatabaseGetRouteService extends AsyncTask {

    private Context context;
    private TextView textView;

    public DatabaseGetRouteService(Context context, TextView textView) {
        this.context = context;
        this.textView = textView;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        if (objects.length == 0) {
            return MyLocationDB
                    .getInstance(context)
                    .getLocationRepo()
                    .getAllLocations();
        } else {
            return MyLocationDB
                    .getInstance(context)
                    .getLocationRepo()
                    .getAllLocations((int) objects[0]);
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        List<MyLocation> allLocations = (ArrayList<MyLocation>) o;

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
        textView.setText(locationString.toString());
    }
}
