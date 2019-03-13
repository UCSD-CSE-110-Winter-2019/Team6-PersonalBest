package com.example.personalbest;

import android.app.Dialog;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalbest.MainActivity;
import com.example.personalbest.R;
import com.example.personalbest.SaveLocal;
import com.example.personalbest.StepCountActivity;
import com.example.personalbest.database.FirebaseAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import static android.content.Context.MODE_PRIVATE;

public class AddFriendFragment extends DialogFragment {
    SaveLocal saveLocal;
    private String valid_email;
    Button positiveButton;
    FirebaseAdapter firebaseAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //spinner.setOnItemSelectedListener(this);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        firebaseAdapter = new FirebaseAdapter(getActivity());
        View v = inflater.inflate(R.layout.add_friend_popup,null);
        final EditText email = v.findViewById(R.id.emailView2);

        email.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

                // TODO Auto-generated method stub
                Is_Valid_Email(email); // pass your EditText Obj here.
            }

            public void Is_Valid_Email(EditText edt) {
                if (edt.getText().toString() == null) {
                    edt.setError("Invalid Email Address");
                    positiveButton.setEnabled(false);
                    valid_email = null;
                } else if (isEmailValid(edt.getText().toString()) == false) {
                    edt.setError("Invalid Email Address");
                    valid_email = null;
                    positiveButton.setEnabled(false);
                } else {
                    valid_email = edt.getText().toString();
                    positiveButton.setEnabled(true);
                }
            }

            boolean isEmailValid(CharSequence email) {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches();
            } // end of TextWatcher (email)
        });

        builder.setMessage("Please add your friend's email").setView(v);
        builder.setPositiveButton("Add Friend", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firebaseAdapter.addFriend(valid_email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setEnabled(false);
        }

    }
}