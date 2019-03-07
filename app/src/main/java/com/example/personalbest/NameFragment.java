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

public class NameFragment extends DialogFragment {
    SaveLocal saveLocal;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //spinner.setOnItemSelectedListener(this);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout


        String name = getArguments().getString("name");
        View v = inflater.inflate(R.layout.name_popup,null);
        builder.setMessage(name).setView(v);


        // Create the AlertDialog object and return it
        return builder.create();
    }
}