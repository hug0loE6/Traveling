package com.example.traveling;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Distance {

    public Lieux from;
    public Lieux to;
    public double distance; // en km

    public Distance(Lieux from, Lieux to) {
        this.from = from;
        this.to = to;
    }

    @NonNull
    @Override
    public String toString() {
        return "(" + from.nom + " -> " + to.nom + ") : " + distance;
    }

    public void calculerDistance() {
        double lat1 = from.lat;
        double lon1 = from.lng;
        double lat2 = to.lat;
        double lon2 = to.lng;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        distance = 6371 * c; // en kilomètres
    }

    public static List<Distance> genToutDistance(List<Lieux> mesLieux, Lieux from){
        List<Distance> resultats = new ArrayList<>();

        for (int i = 0; i < mesLieux.size(); i++) {
            Lieux to = mesLieux.get(i);
            if (to == from) continue;

            Distance thedist = new Distance(from, to);

            thedist.calculerDistance();

            resultats.add(thedist);
        }
        return resultats;
    }
}
