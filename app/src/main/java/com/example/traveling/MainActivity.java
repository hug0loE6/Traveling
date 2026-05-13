package com.example.traveling;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap lamap;
    private List<Lieux> lesLieux = new ArrayList<>();

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

                    BDDLieux bdd = BDDLieux.getInstance(MainActivity.this);
                    LieuxDao dao = bdd.getDao();
                    dao.deleteAll();
                    Log.d("DEBUG_ROOM", "Nombre récupéré : " + firebaseLieux.size());

                    for (Lieux l : firebaseLieux) {
                        dao.insert(l);
                    }

                    runOnUiThread(() -> {
                        MainActivity.this.lesLieux = firebaseLieux;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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
}