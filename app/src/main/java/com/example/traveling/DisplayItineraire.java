package com.example.traveling;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class DisplayItineraire extends Fragment {

    public interface onValidateItineraire {
        void onValidation(Itineraire it);
    }

    private onValidateItineraire callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (DisplayItineraire.onValidateItineraire) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " doit implémenter l'interface");
        }
    }

    private LinearLayout litineraire;

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
                litineraire = view.findViewById(R.id.affichageIt);
                Button validation = view.findViewById(R.id.validate);


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
                        afficheItineraire(listeRecue.get(finalI-1), view);
                    });

                    if (i == 1) {
                        btn.setSelected(true);
                        afficheItineraire(listeRecue.get(0), view);
                    }
                    layoutOptions.addView(btn);
                }
                //Validation d'un itineraire
                validation.setOnClickListener(v ->{
                    Context c = getContext();
                    if(c != null) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Valider l'itineraire ?")
                                .setMessage("Si vous confirmez votre choix, les autres options disparaîtront.")
                                .setPositiveButton("Oui", (dialog, which) -> {
                                    callback.onValidation(listeRecue.get(selectedIt.get()-1));
                                    if (getActivity() instanceof VueMap) {
                                        ((VueMap) getActivity()).closeDisplay();
                                    }
                                })
                                .setNegativeButton("Non", (dialog, which) -> {
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
    }

    private void afficheItineraire(Itineraire it, View laview){
        litineraire.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        TextView bud = laview.findViewById(R.id.totalbud);
        TextView tmp = laview.findViewById(R.id.totaltmp);
        String budget = "Coût moyen de l'itineraire : " + it.totalBud + "€";
        String temps = "Durée moyen de l'itineraire : " + it.timetraj + " minutes";
        bud.setText(budget);
        tmp.setText(temps);
        int nbLieux = it.lieuxIti.size();
        for(int i = 0; i < nbLieux; i++) {
            Lieux lieuActuel = it.lieuxIti.get(i);
            View etapeView = inflater.inflate(R.layout.etape, litineraire, false);
            TextView tvNomLieu = etapeView.findViewById(R.id.tvNomLieu);
            LinearLayout layoutTrajet = etapeView.findViewById(R.id.layoutTrajet);
            TextView tvInfosTrajet = etapeView.findViewById(R.id.tvInfosTrajet);
            tvNomLieu.setText(String.format(Locale.FRANCE,"%s (%s - %d€)", lieuActuel.nom, lieuActuel.type, lieuActuel.budget));
            if (i < nbLieux - 1) {
                Distance trajetSuivant = it.distancesIti.get(i);
                if(trajetSuivant.distance > 1.1) {
                    String infos = String.format(Locale.FRANCE, "↓ %.2f km (%d min)",
                            trajetSuivant.distance,
                            (int) trajetSuivant.temps);
                    tvInfosTrajet.setText(infos);
                }
                else {
                    int metres = (int) (trajetSuivant.distance * 1000);
                    String infos = String.format(Locale.FRANCE, "↓ %d m (%d min)",
                            metres, (int) trajetSuivant.temps);
                    tvInfosTrajet.setText(infos);
                }
                layoutTrajet.setVisibility(View.VISIBLE);
            } else {
                layoutTrajet.setVisibility(View.GONE);
            }
            etapeView.setClickable(true);
            etapeView.setFocusable(true);
            etapeView.setOnClickListener(v-> {
                if(getActivity() instanceof  VueMap){
                    VueMap lemain = (VueMap) getActivity();
                    lemain.showInfowindow(lieuActuel.nom);
                    FrameLayout sheet = lemain.findViewById(R.id.displayIt);
                    BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(sheet);
                    behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                }
            });
            litineraire.addView(etapeView);
        }
    }
}