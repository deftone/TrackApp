package de.deftone.trackapp.services;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.ArrayList;

import de.deftone.trackapp.activities.RoutesActivity;
import de.deftone.trackapp.database.MyLocationDB;
import de.deftone.trackapp.model.MyLocation;

import static de.deftone.trackapp.settings.Constants.EXTRA_TRACK_ID;
import static de.deftone.trackapp.settings.Constants.EXTRA_TRACK_TIMESTAMP;

public class DatabaseGetTrackIdsService extends AsyncTask {

    private Context context;

    public DatabaseGetTrackIdsService(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return MyLocationDB
                .getInstance(context)
                .getLocationRepo()
                .getAllLocations();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        //todo: muss man das wirklich jedes mal machen?
        //man koennte die ids auch in einem shared pref speichern?

        ArrayList<MyLocation> allLocations = (ArrayList<MyLocation>) o;

        //get all different routes:
        //todo: store all this info in shared prefs??
        ArrayList<Integer> trackIdList = new ArrayList<>();
        ArrayList<Long> timestampList = new ArrayList<>();
        int oldTrackId = 0;
        for (MyLocation location : allLocations) {
            if (location.getTrackId() != oldTrackId) {
                trackIdList.add(location.getTrackId());
                timestampList.add(location.getTimestamp());
                oldTrackId = location.getTrackId();
            }
        }

        //start new activity with all items
        Intent routesActivityIntent = new Intent(context, RoutesActivity.class);
        routesActivityIntent.putExtra(EXTRA_TRACK_ID, trackIdList);
        routesActivityIntent.putExtra(EXTRA_TRACK_TIMESTAMP, timestampList);
        context.startActivity(routesActivityIntent);
    }
}
