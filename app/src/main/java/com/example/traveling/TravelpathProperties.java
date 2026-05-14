package com.example.traveling;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class TravelpathProperties extends BottomSheetDialogFragment {

        public interface OnConfimProp {
            void onDataSent(PropertiesIt data);
        }

        private OnConfimProp callback;

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            try {
                callback = (OnConfimProp) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context + " doit implémenter l'interface");
            }
        }

        private void addlieuxtoobligatoire(String text, ChipGroup grp) {
            for (int i = 0; i < grp.getChildCount(); i++) {
                Chip existingChip = (Chip) grp.getChildAt(i);
                if (existingChip.getText().toString().equals(text)) {
                    return;
                }
            }
            Chip chip = new Chip(requireContext());
            chip.setText(text);
            chip.setCloseIconVisible(true);
            chip.setCheckable(false);

            chip.setOnCloseIconClickListener(v -> {
                grp.removeView(chip);
            });
            grp.addView(chip);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.travelpath_properties, container, false);
            Button confirme = view.findViewById(R.id.confirm);
            EditText duration = view.findViewById(R.id.duration);
            EditText budge = view.findViewById(R.id.budget);
            ChipGroup chipAct = view.findViewById(R.id.chipAct);
            ChipGroup chipLieu = view.findViewById(R.id.chipLieux);
            Spinner spinn = view.findViewById(R.id.spinnerLieux);

            //Fermer la fenêtre prop et enregistre les info et déclencher algo de recherche de chemin
            //TODO : remplir propit des lieux obligatoires (et aussi update propit)
            confirme.setOnClickListener(v -> {
                if (callback != null) {
                    List<String> t = new ArrayList<>();
                    for (int i = 0; i < chipAct.getChildCount(); i++) {
                        View caac = chipAct.getChildAt(i);
                        if (caac instanceof Chip && caac.isSelected()) {
                            t.add(((Chip) caac).getText().toString());
                        }
                    }
                    try {
                        PropertiesIt propchoosen = new PropertiesIt(
                                Integer.parseInt(duration.getText().toString()),
                                Integer.parseInt(budge.getText().toString()),
                                t);
                        callback.onDataSent(propchoosen);
                        dismiss();
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(),"Veuillez bien renseigner le budget et la durée.",Toast.LENGTH_SHORT).show();
                    }
                }

            });

            String[] activites = {
                    "Restauration", "Loisirs", "Culture"
            };

            //Setup les bouton options des type activités obligatoire
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

            //Setup les options des lieux obligatoires
            List<String> test = new ArrayList<>();
            test.add("Selectionner...");
            BDDLieux bdd = BDDLieux.getInstance(requireContext());
            LieuxDao dao = bdd.getDao();
            BDDLieux.databaseWriteExecutor.execute(() -> {
                List<Lieux> mesLieux = dao.getAll();
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        for (Lieux l : mesLieux) {
                            test.add(l.nom);
                        }
                    });
                }
            });
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, test);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinn.setAdapter(adapter);

            spinn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        addlieuxtoobligatoire(test.get(position), chipLieu);
                        spinn.setSelection(0);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            return view;
        }
}
