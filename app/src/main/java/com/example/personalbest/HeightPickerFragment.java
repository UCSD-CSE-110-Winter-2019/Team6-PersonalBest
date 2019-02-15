package com.example.personalbest;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.personalbest.MainActivity;
import com.example.personalbest.R;
import com.example.personalbest.SaveLocal;
import com.example.personalbest.StepCountActivity;

import static android.content.Context.MODE_PRIVATE;

public class HeightPickerFragment extends DialogFragment {
    SaveLocal saveLocal;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //spinner.setOnItemSelectedListener(this);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        saveLocal = new SaveLocal(this.getActivity());

        View v = inflater.inflate(R.layout.height_popup,null);
        builder.setMessage("Please enter your height").setView(v);
        final NumberPicker np = v.findViewById(R.id.numberPicker);
        final NumberPicker np2 = v.findViewById(R.id.numberPicker2);
        np.setMinValue(1);
        np.setMaxValue(8);

        np2.setMinValue(0);
        np2.setMaxValue(11);
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                int height_feet =  np.getValue();
                int height_inches = np2.getValue();

                saveLocal.saveHeight(height_feet, height_inches);
                Toast.makeText(getContext(), "Saved Height", Toast.LENGTH_SHORT).show();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}