package com.example.traveling;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DisplayItineraire extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.travelpath_itinerairedisplay, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton close = view.findViewById(R.id.btn_close);
        close.setOnClickListener(v -> {
            //TODO : mettre une confirmation parce que ça stop nette l'itineraire sans rien save
            if (getActivity() instanceof VueMap) {
                ((VueMap) getActivity()).closeDisplay();
            }
        });
    }
}