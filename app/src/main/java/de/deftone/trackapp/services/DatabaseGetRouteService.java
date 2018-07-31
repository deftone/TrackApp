package de.deftone.trackapp.services;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.ArrayList;

import de.deftone.trackapp.activities.MapsActivity;
import de.deftone.trackapp.database.MyLocationDB;
import de.deftone.trackapp.model.MyLocation;

import static de.deftone.trackapp.settings.Constants.EXTRA_LOCATION_LIST;

public class DatabaseGetRouteService extends AsyncTask {

    private Context context;

    public DatabaseGetRouteService(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        return MyLocationDB
                .getInstance(context)
                .getLocationRepo()
                .getAllLocations((int) objects[0]);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        ArrayList<MyLocation> allLocations = (ArrayList<MyLocation>) o;

        //start MapActivity with this locations
        Intent mapsActivityIntent = new Intent(context, MapsActivity.class);
        mapsActivityIntent.putExtra(EXTRA_LOCATION_LIST, allLocations);
        context.startActivity(mapsActivityIntent);
    }
}
