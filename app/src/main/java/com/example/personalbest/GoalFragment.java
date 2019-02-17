package com.example.personalbest;

import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalbest.MainActivity;
import com.example.personalbest.R;
import com.example.personalbest.SaveLocal;
import com.example.personalbest.StepCountActivity;

import org.w3c.dom.Text;

import static android.content.Context.MODE_PRIVATE;

public class GoalFragment extends DialogFragment {
    SaveLocal saveLocal;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //spinner.setOnItemSelectedListener(this);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        saveLocal = new SaveLocal(this.getActivity());

        View v = inflater.inflate(R.layout.fragment_goal,null);
        builder.setMessage("Congratulations!").setView(v);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                DialogFragment goalFrag = new SetGoalFragment();
                goalFrag.show(getActivity().getSupportFragmentManager(), "Set Goal");

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Do nothing

            }
        });

        builder.setNeutralButton("Add 500 steps", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveLocal.setGoal(saveLocal.getGoal() + 500);
                ((StepCountActivity) getActivity()).setGoal(saveLocal.getGoal());
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}