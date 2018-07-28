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

public class DatabaseDeletingService extends AsyncTask {

    private Context context;

    public DatabaseDeletingService(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        if (objects.length > 0) {
            List<MyLocation> myLocations = (ArrayList<MyLocation>) objects[0];
            MyLocationDB.getInstance(context).getLocationRepo().delete(myLocations);
        }
        return null;
    }
}
