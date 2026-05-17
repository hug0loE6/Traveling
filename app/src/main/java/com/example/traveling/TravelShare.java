package com.example.traveling;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.AlgorithmParameterGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TravelShare extends AppCompatActivity {

    private static final String CLOUD_NAME = "dfx1vuhqf";
    private static final String UPLOAD_PRESET = "testla";

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

        //log cloudinary
        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", CLOUD_NAME);
            config.put("secure", "true");
            MediaManager.init(this, config);
        } catch (IllegalStateException ignored) {
        }

        // ===== BOUTON CONNEXION =====
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {

            if (!isLoggedIn) {

                Intent intent = new Intent(TravelShare.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);

            } else {

                new AlertDialog.Builder(TravelShare.this)
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
        new AlertDialog.Builder(this)
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

            Uri imageUri = data.getData();

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

            //upload image to database cloudinary
            try {
                // 2. Ouvrir un InputStream à partir de l'Uri
                InputStream inputStream = getContentResolver().openInputStream(imageUri);

                // 3. Lancer l'upload via le MediaManager
                MediaManager.get().upload(imageUri)
                        .unsigned(UPLOAD_PRESET) // Utilisation du preset non signé
                        .callback(new UploadCallback() {
                            @Override
                            public void onStart(String requestId) {
                                Log.d("Cloudinary", "Début de l'upload...");
                            }

                            @Override
                            public void onProgress(String requestId, long bytes, long totalBytes) {
                                // Optionnel : Calculer la progression
                            }

                            @Override
                            public void onSuccess(String requestId, Map resultData) {
                                // 4. L'upload a réussi ! On récupère l'URL ici
                                String imageUrl = (String) resultData.get("secure_url");
                                Log.d("Cloudinary", "Succès ! URL de l'image : " + imageUrl);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("images");
                                DatabaseReference nouvelleImageRef = ref.push();
                                nouvelleImageRef.setValue(imageUrl)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Firebase", "URL enregistrée avec succès !");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Firebase", "Erreur d'enregistrement : " + e.getMessage());
                                        });

                                // Vous pouvez maintenant utiliser cette URL (ex: l'enregistrer en BDD)
                                runOnUiThread(() -> {
                                    Toast.makeText(TravelShare.this, "Image uploadée avec succès !", Toast.LENGTH_SHORT).show();
                                });
                            }

                            @Override
                            public void onError(String requestId, ErrorInfo error) {
                                Log.e("Cloudinary", "Erreur d'upload : " + error.getDescription());
                            }

                            @Override
                            public void onReschedule(String requestId, ErrorInfo error) {
                                // En cas de coupure réseau, Cloudinary replanifiera l'upload
                            }
                        })
                        .dispatch(); // Lance l'envoi en arrière-plan
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Impossible de lire le fichier de l'image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}