package de.deftone.trackapp.database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import de.deftone.trackapp.model.MyLocation;
import de.deftone.trackapp.repository.MyLocationRepo;

@Database(entities = {MyLocation.class}, version = 1, exportSchema = false)
public abstract class MyLocationDB extends RoomDatabase {

    private static final String DB_NAME = "locationDatabase.db";
    private static volatile MyLocationDB instance;

    public static synchronized MyLocationDB getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static MyLocationDB create(final Context context) {
        return Room.databaseBuilder(
                context,
                MyLocationDB.class,
                DB_NAME).build();
    }

    public abstract MyLocationRepo getLocationRepo();

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
