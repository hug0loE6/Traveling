package com.example.traveling;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap lamap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    public void onMapReady(GoogleMap googleMap) {
        lamap = googleMap;

        // Coordonnées de test (ex: Paris)
        LatLng paris = new LatLng(0.0, 0.0);

        // Ajouter un marqueur
        lamap.addMarker(new MarkerOptions().position(paris).title("le 0 0"));

        // Déplacer la caméra avec un zoom (15 = niveau rue, 20 = très proche)
        lamap.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 15f));
    }
}