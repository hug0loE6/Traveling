package com.example.traveling;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import java.util.Collections;
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
    private Uri selectedImageUri;
    private Uri imageUri;

    private ImageButton discoverBtn;

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
        discoverBtn = findViewById(R.id.discoverBtn);

        discoverBtn.setOnClickListener(v -> {
            shuffleFeed();
        });

        // ===== RECYCLERVIEW FEED =====
        recyclerView = findViewById(R.id.feedRecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        posts = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("images");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                new Thread(() -> {
                    ArrayList<Post> allposts = new ArrayList<>();
                    for (DataSnapshot unpost : snapshot.getChildren()){
                        Post thepost = unpost.getValue(Post.class);
                        allposts.add(thepost);
                    }
                    runOnUiThread(() -> {
                        posts.clear();
                        posts.addAll(allposts);
                        adapter = new PostAdapter(posts);
                        recyclerView.setAdapter(adapter);
                    });
                }).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erreur de sync", error.toException());
            }
        });
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

    private void shuffleFeed() {

        if(posts == null || posts.isEmpty()) return;

        Collections.shuffle(posts);

        adapter.notifyDataSetChanged();

        Toast.makeText(this,
                "Flux découverte activé 🎲",
                Toast.LENGTH_SHORT).show();
    }

    private void showPostDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_post, null);

        EditText descriptionInput = view.findViewById(R.id.descriptionInput);
        EditText locationInput = view.findViewById(R.id.locationInput);
        EditText dateInput = view.findViewById(R.id.dateInput);

        new AlertDialog.Builder(this)
                .setTitle("Créer un post")
                .setView(view)
                .setPositiveButton("Publier", (dialog, which) -> {

                    uploadPost(
                            descriptionInput.getText().toString(),
                            locationInput.getText().toString(),
                            dateInput.getText().toString()
                    );
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void uploadPost(String description, String location, String date) {

        MediaManager.get().upload(selectedImageUri)
                .unsigned(UPLOAD_PRESET)
                .callback(new UploadCallback() {

                    @Override
                    public void onSuccess(String requestId, Map resultData) {

                        String imageUrl = (String) resultData.get("secure_url");
                        Log.d("Cloudinary", "Succès ! URL de l'image : " + imageUrl);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("images");
                        long timestampActuel = System.currentTimeMillis();
                        long timestampInverse = Long.MAX_VALUE - timestampActuel;
                        String idInverse = String.valueOf(timestampInverse);
                        DatabaseReference nouvelleImageRef = ref.child(idInverse);
                        Post nvpost = new Post(
                                currentFirstname,
                                description,
                                location,
                                date,
                                imageUrl,
                                android.R.drawable.sym_def_app_icon
                        );
                        nouvelleImageRef.setValue(nvpost);
                        runOnUiThread(() -> {
                            Toast.makeText(TravelShare.this,
                                    "Post créé !",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override public void onError(String requestId, ErrorInfo error) {}
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
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

            selectedImageUri = data.getData();
            showPostDialog();
            imageUri = selectedImageUri;
        }
    }
}