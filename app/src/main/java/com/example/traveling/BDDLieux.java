package com.example.traveling;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Lieux.class}, version = 1)
public abstract class BDDLieux extends RoomDatabase {
    public abstract LieuxDao getDao();
    private static BDDLieux instance;

    public static synchronized BDDLieux getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            BDDLieux.class, "Lieux-BDD")
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new Thread(() -> {
                LieuxDao dao = instance.getDao();

                Lieux l = new Lieux();
                l.nom = "Place de la Comédie";
                l.type = "Culture";
                l.lat = 43.60865360700228;
                l.lng = 3.8797765394776835;
                dao.insert(l);

                l = new Lieux();
                l.nom = "Zoo de Montpellier";
                l.type = "Loisirs";
                l.lat = 43.6433;
                l.lng = 3.8733;
                dao.insert(l);
            }).start();
        }
    };
}