package de.deftone.trackapp.services;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import de.deftone.trackapp.MainActivity;
import de.deftone.trackapp.database.MyLocationDB;
import de.deftone.trackapp.model.MyLocation;

public class DatabaseSaveRouteService extends AsyncTask {

    private Context context;

    public DatabaseSaveRouteService(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        //object[0] is actually an ArrayList containing MyLocation objects
        for (MyLocation location : (ArrayList<MyLocation>) objects[0]) {
            MyLocationDB.getInstance(context).getLocationRepo().insert(location);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        //reset myLocationList here or in MainAcitivy??
        //if this is done here, only a few points might be lost, in Main Activiy all might be lost...
        MainActivity.clearSavingList();
    }
}
