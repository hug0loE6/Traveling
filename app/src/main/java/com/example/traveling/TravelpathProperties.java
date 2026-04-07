package com.example.traveling;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class TravelpathProperties extends BottomSheetDialogFragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.travelpath_properties, container, false);



            Button confirme = view.findViewById(R.id.confirm);
            ChipGroup chipAct = view.findViewById(R.id.chipAct);

            confirme.setOnClickListener(v ->{
                dismiss();
            });

            String[] activites = {
                    "Restauration", "Loisirs", "Culture"
            };


            for (String act : activites) {
                Chip chip = new Chip(requireContext());
                chip.setText(act);
                chip.setCheckable(true);
                chip.setChecked(false);
                chip.setChipBackgroundColorResource(R.color.white);
                chip.setOnClickListener(v -> {
                    chip.setSelected(!chip.isSelected());
                    if(chip.isSelected()){
                        chip.setChipBackgroundColorResource(R.color.greenSelec);
                    } else {
                        chip.setChipBackgroundColorResource(R.color.white);
                    }
                });
                chipAct.addView(chip);

            }

            return view;
        }
}
