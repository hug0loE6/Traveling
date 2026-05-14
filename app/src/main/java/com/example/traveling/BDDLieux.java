package com.example.traveling;

import android.content.Context;

import androidx.room.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Lieux.class}, version = 1)
public abstract class BDDLieux extends RoomDatabase {
    public abstract LieuxDao getDao();
    private static BDDLieux instance;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    public static synchronized BDDLieux getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            BDDLieux.class, "Lieux-BDD").build();
        }
        return instance;
    }
}