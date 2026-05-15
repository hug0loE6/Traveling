package com.example.traveling;

import java.util.ArrayList;
import java.util.List;

public class PropertiesIt {
    public int duree;
    public int budget;
    public List<String> type;
    public List<String> lieux;

    public PropertiesIt(int d, int b, List<String> t, List<String> l){
        duree = d;
        budget = b;
        type = t;
        lieux = l;
    }
}
