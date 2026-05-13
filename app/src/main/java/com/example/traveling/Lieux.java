package com.example.traveling;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "lieux")
public class Lieux {
    @PrimaryKey
    @NonNull
    public String nom;
    public String type;
    public double lat;
    public double lng;

    public Lieux(){}

    @NonNull
    @Override
    public String toString() {
        return "Lieu{nom='" + nom + "', type='" + type + "', lat=" + lat + ", lng=" + lng + "}";
    }
}
