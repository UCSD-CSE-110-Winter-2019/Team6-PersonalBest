package com.example.personalbest;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.google.android.gms.common.util.ArrayUtils;

import java.util.Arrays;

public class SetGoalFragment extends DialogFragment {
    SaveLocal saveLocal;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_set_goal,null);

        //spinner.setOnItemSelectedListener(this);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        saveLocal = new SaveLocal(this.getActivity());
        final String [] values = getArrayWithSteps(500, saveLocal.getGoal() * 10, 500);
        final NumberPicker np = v.findViewById(R.id.numberPicker3);
        np.setMinValue(0);
        np.setMaxValue(values.length-1);
        np.setDisplayedValues(values);
        np.setValue(Arrays.asList(values).indexOf(""+saveLocal.getGoal()));
        builder.setMessage("Enter New Goal").setView(v);

        builder.setPositiveButton("Save New Goal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveLocal.setGoal(Integer.parseInt(values[np.getValue()]));
                ((StepCountActivity) getActivity()).setGoal(saveLocal.getGoal());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Do nothing

            }
        });


        // Create the AlertDialog object and return it
        return builder.create();
    }

    public String[] getArrayWithSteps(int min, int max, int step){

        int stepArray = (max - min) / step+1;

        String [] arr = new String[stepArray];

        for (int i = 0; i < stepArray; i ++){

            arr[i] = String.valueOf(min + i*step);
        }

        return arr;
    }
}
