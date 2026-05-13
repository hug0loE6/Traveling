package com.example.traveling;

import android.content.Context;

import androidx.room.*;

@Database(entities = {Lieux.class}, version = 1)
public abstract class BDDLieux extends RoomDatabase {
    public abstract LieuxDao getDao();
    private static BDDLieux instance;

    public static synchronized BDDLieux getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            BDDLieux.class, "Lieux-BDD").build();
        }
        return instance;
    }
}