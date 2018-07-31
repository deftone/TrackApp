package de.deftone.trackapp.services;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import de.deftone.trackapp.activities.RoutesActivity;
import de.deftone.trackapp.database.MyLocationDB;
import de.deftone.trackapp.model.MyLocation;

import static de.deftone.trackapp.settings.Constants.EXTRA_TRACK_SET;

public class DatabaseGetTrackIdsService  extends AsyncTask {

    //todo manu: was bedeutet das?
    private Context context;

    public DatabaseGetTrackIdsService(Context context){
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

        ArrayList<MyLocation> allLocations = (ArrayList<MyLocation>) o;

        //get all different routes:
        LinkedHashSet<Integer> trackIdSet = new LinkedHashSet<>();
        for (MyLocation location : allLocations){
            trackIdSet.add(location.getTrackId());
        }

        //convert set to ArrayList, so it can be used with .get(position) in the recycler view
        ArrayList<Integer> trackIdList = new ArrayList<>(trackIdSet);

        //start new activity with all items
        Intent routesActivityIntent = new Intent(context, RoutesActivity.class);
        routesActivityIntent.putExtra(EXTRA_TRACK_SET, trackIdList);
        context.startActivity(routesActivityIntent);


    }
}
