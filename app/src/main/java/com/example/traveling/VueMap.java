package com.example.traveling;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VueMap extends FragmentActivity implements OnMapReadyCallback, TravelpathProperties.OnConfimProp {

    private GoogleMap lamap;
    private List<Lieux> lesLieux = new ArrayList<>();
    private BottomSheetBehavior<FrameLayout> behavior;

    private void createMarker(){
        if (lamap == null || lesLieux.isEmpty()){
            Log.d("SYNCMARKER", "Status map : " + (lamap != null) + ", status liste Lieux : " + !lesLieux.isEmpty());
            return;
        }
        for (Lieux l: lesLieux) {
            Log.d("AffichageLieux", l.toString());
            LatLng cord = new LatLng(l.lat, l.lng);
            lamap.addMarker(new MarkerOptions().position(cord).title(l.nom));
        }
    }

    private void syncDataFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lieux");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                new Thread(() -> {
                    List<Lieux> firebaseLieux = new ArrayList<>();
                    for (DataSnapshot unLieu : snapshot.getChildren()) {
                        Lieux lieu = unLieu.getValue(Lieux.class);
                        firebaseLieux.add(lieu);
                        String output = "Element lieux ajouté dans la liste firebase, value : " + lieu;
                        Log.d("Firebase", output);
                    }

                    BDDLieux bdd = BDDLieux.getInstance(VueMap.this);
                    LieuxDao dao = bdd.getDao();
                    dao.deleteAll();
                    Log.d("DEBUG_ROOM", "Nombre récupéré : " + firebaseLieux.size());

                    for (Lieux l : firebaseLieux) {
                        dao.insert(l);
                    }

                    runOnUiThread(() -> {
                        VueMap.this.lesLieux = firebaseLieux;
                        createMarker();
                    });
                }).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erreur de sync", error.toException());
            }
        });
    }

    public void openDisplay() {
        FrameLayout container = findViewById(R.id.displayIt);
        container.setVisibility(View.VISIBLE);
        behavior.setHideable(false);
        DisplayItineraire fragment = new DisplayItineraire();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.displayIt, fragment)
                .commit();
        behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
    }

    public void closeDisplay() {
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.displayIt);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
        FrameLayout container = findViewById(R.id.displayIt);
        container.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vuemap);

        //Chargement des lieux de la BDD (et init si premier lancement) dans la variable utilisé plus tard pour maps
        syncDataFirebase();

        //Permet au bouton d'ouvrir la fenêtre de proprieté de la création du voyage
        ImageButton prop = findViewById(R.id.btnPropPath);
        prop.setOnClickListener(v ->{
            TravelpathProperties propriete = new TravelpathProperties();
            propriete.show(getSupportFragmentManager(), "propriete");
        });

        //Création de la Google Map sur un Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //Création comportement du display d'itineraire
        FrameLayout display = findViewById(R.id.displayIt);
        behavior = BottomSheetBehavior.from(display);
        behavior.setHideable(false);
        behavior.setFitToContents(false);
        behavior.setHalfExpandedRatio(0.35f);
        float density = getResources().getDisplayMetrics().density;
        int peekHeightInPixels = (int) (40 * density);
        behavior.setPeekHeight(peekHeightInPixels);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        lamap = googleMap;

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));
            if (!success) {
                Log.e("MapError", "Échec de l'analyse du style.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapError", "Impossible de trouver le fichier de style.", e);
        }
        createMarker();
        lamap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.61093, 3.87635), 15f));
    }

    private void genItineraire(PropertiesIt data) {
        int nbofgenItineraire = 0;
        List<Itineraire> optIti = new ArrayList<>();
        int breakloop = 0; //var de débug pour casser la boucle parce que j'ai 0 lieux de test vue que je suis un zgeg et que j'ai la giga flemme de mettre d'autre lieux de montpellier pour l'application traveling de l'UE Programmation mobile.
        while (nbofgenItineraire < 3){
            List<Lieux> itineraire = new ArrayList<>();
            List<String> obligatoirtodo = new ArrayList<>(data.lieux);
            Random r = new Random();
            List<Lieux> filtered = new ArrayList<>();
            for(Lieux l : lesLieux){
                if (data.type.contains(l.type)) filtered.add(l);
            }
            Lieux depart = filtered.get(r.nextInt(filtered.size()));
            List<Lieux> explorable = new ArrayList<>(filtered);
            explorable.remove(depart);
            itineraire.add(depart);
            obligatoirtodo.remove(depart.nom);
            int timetraj = 0;
            int rembud = data.budget - depart.budget;

            while (!explorable.isEmpty() && timetraj<data.duree && rembud>0) {
                List<Distance> check = Distance.genToutDistance(itineraire.get(itineraire.size() - 1), explorable);
                Log.d("DEBUT_DIST", check.toString());
                double mindist = Double.POSITIVE_INFINITY;
                for (Distance dist : check) {
                    if (dist.distance < mindist) {
                        mindist = dist.distance;
                    }
                }
                List<Distance> potentialnextstep = new ArrayList<>();
                Distance nextstep = new Distance();
                for (Distance d : check) {
                    if (mindist * 1.25 > d.distance) {
                        potentialnextstep.add(d);
                        if(obligatoirtodo.contains(d.to.nom)){
                            obligatoirtodo.remove(d.to.nom);
                            potentialnextstep.clear();
                            nextstep = d;
                            break;
                        }
                    }
                }
                if(!potentialnextstep.isEmpty()) nextstep = potentialnextstep.get(r.nextInt(potentialnextstep.size()));
                explorable.remove(nextstep.to);
                itineraire.add(nextstep.to);
                timetraj = (int) Math.round(timetraj + nextstep.temps);
                rembud -= nextstep.to.budget;
                if(timetraj > data.duree*0.67) break;
            }
            Log.d("ITINERAIRE", itineraire.toString());
            breakloop++;
            if (breakloop >= 67) {
                break;
            }
            //check correspondance prop
            Log.d("DEBUG_IT", "timetraj= " + timetraj + ", rembud= " + rembud);
            if(!(data.duree*0.67 <= timetraj) || !(timetraj <= data.duree*1.33)) continue;
            if(rembud < 0) continue;
            if(!obligatoirtodo.isEmpty()) continue;

            //Save itineraire
            //TODO : faire un check si j'ai pas gen le même chemin
            optIti.add(new Itineraire(data.budget-rembud,timetraj,itineraire));
            nbofgenItineraire++;
        }
        if (nbofgenItineraire == 0){
            Toast.makeText(this,"Aucun trajet trouvé",Toast.LENGTH_SHORT).show();
        } else {
            openDisplay();
            /*DisplayItineraire display = new DisplayItineraire();
            display.show(getSupportFragmentManager(), "displayitineraire");*/
        }

    }


    @Override
    public void onDataSent(PropertiesIt data) {
        Toast.makeText(this,"send",Toast.LENGTH_SHORT).show();
        Log.d("TESTDATA", "d="+data.duree+",b="+data.budget+",t="+data.type+",l="+data.lieux);
        genItineraire(data);
    }
}