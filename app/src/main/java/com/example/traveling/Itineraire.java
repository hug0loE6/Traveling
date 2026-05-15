package com.example.traveling;

import java.util.ArrayList;
import java.util.List;

public class Itineraire {
    public List<Lieux> lieuxIti;
    public List<Distance> distancesIti = new ArrayList<>();
    public int totalBud;
    public int timetraj;

    public Itineraire(int b, int t, List<Lieux> l){
        totalBud = b;
        timetraj = t;
        lieuxIti = new ArrayList<>(l);
        for(int i = 1; i<l.size(); i++){
            distancesIti.add(new Distance(l.get(i-1), l.get(i)));
        }
    }
}
