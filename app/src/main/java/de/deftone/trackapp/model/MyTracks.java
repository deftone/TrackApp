package de.deftone.trackapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class MyTracks {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;

    //do we want an init constructor only?
    public MyTracks() {
    }

    public void setId(@NonNull int id) {
        if (id < 0) {
            //throw exceptoin?
        } else {
            //hier muss geprueft werden, dass es die naechste hoehere zahl ist und es diese id noch nciht gibt
            this.id = id;
        }
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setName(String name) {
        //use default?
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
