package com.example.traveling;

import androidx.fragment.app.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DisplayItineraire extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.travelpath_itinerairedisplay, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Itineraire> listeRecue;
        if (getArguments() != null) {
            listeRecue = (ArrayList<Itineraire>) getArguments().getSerializable("ListIt"); //ya un warning mais parce que android il est zgeg et dcp c'est pg ça marche en vrai
            if (listeRecue != null) {
                for (Itineraire i : listeRecue) {
                    Log.d("TEST_BENDEL", i.lieuxIti.toString());
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

                int optlen = listeRecue.size();
                AtomicInteger selectedIt = new AtomicInteger(1);
                LinearLayout layoutOptions = view.findViewById(R.id.optionIt);

                //Reception d'une pression d'un des choix d'itineraire
                getParentFragmentManager().setFragmentResultListener("sig", getViewLifecycleOwner(), (requestKey, result) -> {
                    selectedIt.set(result.getInt("idopt"));
                    for (int index = 0; index < layoutOptions.getChildCount(); index++) {
                        View enfant = layoutOptions.getChildAt(index);
                        if (enfant instanceof Button) {
                            Button chaqueBouton = (Button) enfant;
                            int valeurBouton = Integer.parseInt(chaqueBouton.getText().toString());
                            if (valeurBouton != selectedIt.get()) {
                                chaqueBouton.setSelected(false);
                            }
                        }
                    }
                });

                //création des boutons choix options
                for (int i = 1; i <= optlen; i++) {
                    Button btn = new Button(requireContext());
                    btn.setId(View.generateViewId());
                    btn.setText(String.valueOf(i));
                    btn.setTextColor(Color.BLUE);
                    btn.setGravity(Gravity.CENTER);
                    btn.setBackgroundResource(R.drawable.circle_option);

                    int sizeInPx = (int) (50 * getResources().getDisplayMetrics().density);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sizeInPx, sizeInPx);

                    int marginInPx = (int) (8 * getResources().getDisplayMetrics().density);
                    params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);

                    btn.setLayoutParams(params);

                    int finalI = i;
                    btn.setOnClickListener(v -> {
                        v.setSelected(true);
                        selectedIt.set(finalI);
                        Bundle bundle = new Bundle();
                        bundle.putInt("idopt", finalI);
                        getParentFragmentManager().setFragmentResult("sig", bundle);
                    });

                    if (i == 1) {
                        btn.setSelected(true);
                    }
                    layoutOptions.addView(btn);
                }
            }
        }
    }
}