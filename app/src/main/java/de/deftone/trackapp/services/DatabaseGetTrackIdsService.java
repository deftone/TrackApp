package de.deftone.trackapp.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.ArrayList;

import de.deftone.trackapp.activities.RoutesActivity;
import de.deftone.trackapp.database.MyLocationDB;
import de.deftone.trackapp.model.MyLocation;

import static android.content.Context.MODE_PRIVATE;
import static de.deftone.trackapp.settings.Constants.EXTRA_TRACK_ID;
import static de.deftone.trackapp.settings.Constants.EXTRA_TRACK_NAME;
import static de.deftone.trackapp.settings.Constants.EXTRA_TRACK_TIMESTAMP;
import static de.deftone.trackapp.settings.Constants.SHARED_PREF_NAME;

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

        //todo: muss man das wirklich jedes mal machen? naja, koennte sich ja was geaendert haben...
        //man koennte die ids auch in einem shared pref speichern?
        // oder einen cache?

        ArrayList<MyLocation> allLocations = (ArrayList<MyLocation>) o;

        //get all different routes:
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

        SharedPreferences namePrefs = context.getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        ArrayList<String> nameList = new ArrayList<>();
        for (Integer id: trackIdList){
            nameList.add(namePrefs.getString(String.valueOf(id), "-"));
        }

        //start new activity with all items
        Intent routesActivityIntent = new Intent(context, RoutesActivity.class);
        routesActivityIntent.putExtra(EXTRA_TRACK_ID, trackIdList);
        routesActivityIntent.putExtra(EXTRA_TRACK_TIMESTAMP, timestampList);
        routesActivityIntent.putExtra(EXTRA_TRACK_NAME, nameList);
        context.startActivity(routesActivityIntent);
    }
}
