package com.example.traveling;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TravelpathProperties extends BottomSheetDialogFragment {


        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.travelpath_properties, container, false);

            Button confirme = view.findViewById(R.id.confirm);
            confirme.setOnClickListener(v ->{
                dismiss();
            });

            return view;
        }
}
