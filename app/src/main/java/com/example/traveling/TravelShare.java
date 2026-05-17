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
    private ImageButton pathbtn;
    private RecyclerView recyclerView;
    private ArrayList<Post> posts;
    private PostAdapter adapter;
    private static final int PICK_IMAGE_REQUEST = 2;
    private ImageButton addPostButton;

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
        //btnMap
        pathbtn = findViewById(R.id.btnMap);

        pathbtn.setOnClickListener(v -> {

                Intent intent = new Intent(TravelShare.this, VueMap.class);
                startActivity(intent);

        });

        addPostButton = findViewById(R.id.addPostButton);

        addPostButton.setOnClickListener(v -> {

            if(!isLoggedIn) {
                showAnonymousPopup();
                return;
            }

            openGallery();
        });


        // ===== RECYCLERVIEW FEED =====
        recyclerView = findViewById(R.id.feedRecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        posts = new ArrayList<>();

        posts.add(new Post(
                "Alice",
                "Vue incroyable après 3h de randonnée.",
                "Lac Blanc, Chamonix",
                "Août 2024",
                null,
                android.R.drawable.sym_def_app_icon
        ));

        posts.add(new Post(
                "Bob",
                "Petit café caché dans une rue magnifique.",
                "Rome, Italie",
                "Mai 2023",
                null,
                android.R.drawable.sym_def_app_icon
        ));

        adapter = new PostAdapter(posts);
        recyclerView.setAdapter(adapter);
    }

    private void openGallery() {

        if(!isLoggedIn) return;

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void showAnonymousPopup() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Mode anonyme")
                .setMessage("Fonctionnalité indisponible en mode anonyme.")
                .setPositiveButton("OK", null)
                .show();
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
        if(requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null) {

            android.net.Uri imageUri = data.getData();

            posts.add(0, new Post(
                    currentFirstname,
                    "Nouvelle aventure ✈️",
                    "Lieu inconnu",
                    "Aujourd'hui",
                    imageUri.toString(),
                    android.R.drawable.sym_def_app_icon
            ));

            adapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
        }
    }
}