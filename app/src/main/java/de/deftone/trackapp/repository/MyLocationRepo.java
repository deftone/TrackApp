package de.deftone.trackapp.repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.deftone.trackapp.model.MyLocation;

@Dao
public interface MyLocationRepo {

    @Query("SELECT * FROM mylocation")
    List<MyLocation> getAllLocations();

    @Query("SELECT * FROM mylocation WHERE trackId=:id")
    List<MyLocation> getAllLocations(int id );

    @Query("SELECT * FROM mylocation WHERE trackId BETWEEN :minId AND :maxId")
    List<MyLocation> getAllLocations(int minId, int maxId);

    @Insert
    void insert(MyLocation myLocation);

    //oder am Ende beim speichern?
    @Insert
    void insert(List<MyLocation> myLocations);

    @Delete
    void delete(MyLocation myLocation);

    @Delete
    void delete(List<MyLocation> myLocations);

}
