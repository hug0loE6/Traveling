package com.example.traveling;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class DisplayItineraire extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.travelpath_itinerairedisplay, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            @SuppressWarnings("unchecked")
            ArrayList<Itineraire> listeRecue = (ArrayList<Itineraire>) getArguments().getSerializable("ListIt");
            if (listeRecue != null) {
                for (Itineraire i : listeRecue) {
                    Log.d("TEST_BENDEL", i.lieuxIti.toString());
                }
            }
        }


        ImageButton close = view.findViewById(R.id.btn_close);
        close.setOnClickListener(v -> {
            // Création d'une boîte de dialogue de confirmation
            Context c = getContext();
            if(c != null) {
                new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setTitle("Quitter l'itinéraire ?")
                        .setMessage("Si vous fermez ce volet, l'itineraire sera abandonné.")
                        .setPositiveButton("Quitter", (dialog, which) -> {
                            if (getActivity() instanceof VueMap) {
                                ((VueMap) getActivity()).closeDisplay();
                            }
                        })
                        .setNegativeButton("Annuler", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setCancelable(true)
                        .show();
            } else {
                throw new IllegalStateException("Context null.");
            }
        });
    }
}