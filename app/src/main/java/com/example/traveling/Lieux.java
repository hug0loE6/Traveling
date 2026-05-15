package com.example.traveling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.*;

import java.util.Objects;

@Entity(tableName = "lieux")
public class Lieux {
    @PrimaryKey
    @NonNull
    public String nom;
    public String type;
    public double lat;
    public double lng;
    public int budget;

    public Lieux(){}

    @NonNull
    @Override
    public String toString() {
        return "Lieu{nom='" + nom + "', type='" + type + "', budget=" + budget + "', lat=" + lat + ", lng=" + lng + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || this.getClass() != obj.getClass()) return false;

        Lieux autre = (Lieux) obj;
        return Objects.equals(this.nom, autre.nom);

    }

    @Override
    public int hashCode() {
        return Objects.hash(nom);
    }
}
