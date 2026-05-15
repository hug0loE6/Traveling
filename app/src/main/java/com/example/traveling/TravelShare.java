package com.example.traveling;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;

import java.util.ArrayList;

public class TravelShare extends AppCompatActivity {

    private static final int LOGIN_REQUEST_CODE = 1;

    private boolean isLoggedIn = false;
    private String currentFirstname = null;

    private User currentUser = null;

    private ImageButton loginButton;
    private RecyclerView recyclerView;
    private ArrayList<Post> posts;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_travel_share);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        // ===== BOUTON CONNEXION =====
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {

            if (!isLoggedIn) {

                Intent intent = new Intent(TravelShare.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);

            } else {

                new androidx.appcompat.app.AlertDialog.Builder(TravelShare.this)
                        .setTitle("Déconnexion")
                        .setMessage("Voulez-vous vous déconnecter ?")
                        .setPositiveButton("Oui", (dialog, which) -> {
                            isLoggedIn = false;
                            currentFirstname = null;
                            loginButton.setImageResource(android.R.drawable.ic_menu_add);
                        })
                        .setNegativeButton("Non", null)
                        .show();
            }
        });

        // ===== RECYCLERVIEW FEED =====
        recyclerView = findViewById(R.id.feedRecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        posts = new ArrayList<>();

        posts.add(new Post(
                "Alice",
                "Premier post sur TravelShare !",
                android.R.drawable.sym_def_app_icon,
                "Paris, France",
                "15/05/2026"
        ));

        posts.add(new Post(
                "Bob",
                "Découverte d’un endroit incroyable.",
                android.R.drawable.sym_def_app_icon,
                "Rome, Italie",
                "12/05/2026"
        ));

        adapter = new PostAdapter(posts);
        recyclerView.setAdapter(adapter);
    }



    // ===== RETOUR LOGIN =====
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            currentFirstname = data.getStringExtra("firstname");
            String lastname = data.getStringExtra("lastname");

            isLoggedIn = true;

            loginButton.setImageResource(android.R.drawable.sym_def_app_icon);
        }
    }
}