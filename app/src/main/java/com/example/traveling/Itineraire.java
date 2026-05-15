package com.example.traveling;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class Itineraire implements Serializable {
    public ArrayList<Lieux> lieuxIti;
    public ArrayList<Distance> distancesIti = new ArrayList<>();
    public int totalBud;
    public int timetraj;

    public Itineraire(int b, int t, ArrayList<Lieux> l){
        totalBud = b;
        timetraj = t;
        lieuxIti = new ArrayList<>(l);
        for(int i = 1; i<l.size(); i++){
            distancesIti.add(new Distance(l.get(i-1), l.get(i)));
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;

        if (obj == null || this.getClass() != obj.getClass()) return false;

        Itineraire autre = (Itineraire) obj;

        if(lieuxIti.size() != autre.lieuxIti.size()) return false;

        for(int i=0; i<lieuxIti.size();i++){
            if(!lieuxIti.get(i).equals(autre.lieuxIti.get(i))) return false;
        }
        return true;
    }
}
