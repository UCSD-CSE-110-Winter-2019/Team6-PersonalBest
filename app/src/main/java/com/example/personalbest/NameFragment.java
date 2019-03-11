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
import android.text.Editable;
import android.text.TextWatcher;
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

import org.w3c.dom.Text;

import static android.content.Context.MODE_PRIVATE;

public class NameFragment extends DialogFragment {
    SaveLocal saveLocal;
    Button positiveButton;
    String valid_email;
    boolean good_email = false;
    boolean good_name = false;
    FirebaseAdapter firebaseAdapter;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        saveLocal = new SaveLocal(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        firebaseAdapter = new FirebaseAdapter(getActivity());

        //spinner.setOnItemSelectedListener(this);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout


        View v = inflater.inflate(R.layout.name_popup,null);
        final EditText nameView = v.findViewById(R.id.setName);
        final EditText emailView = v.findViewById(R.id.setPersonalEmail);
        builder.setMessage("Please enter your name and email so that your friends will know who you are.").setView(v);

        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    good_name = true;
                    if (good_email) {
                        positiveButton.setEnabled(true);
                    }
                }
                else {
                    positiveButton.setEnabled(false);
                }
            }
        });

        emailView.addTextChangedListener(new TextWatcher() {

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
                Is_Valid_Email(emailView); // pass your EditText Obj here.
            }

            public void Is_Valid_Email(EditText edt) {
                if (edt.getText().toString() == null) {
                    edt.setError("Invalid Email Address");
                    positiveButton.setEnabled(false);
                    good_email = false;
                    valid_email = null;
                } else if (isEmailValid(edt.getText().toString()) == false) {
                    edt.setError("Invalid Email Address");
                    valid_email = null;
                    good_email = false;
                    positiveButton.setEnabled(false);
                } else {
                    valid_email = edt.getText().toString();
                    good_email = true;
                    if (good_name) {
                        positiveButton.setEnabled(true);
                    }
                }
            }

            boolean isEmailValid(CharSequence email) {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches();
            } // end of TextWatcher (email)
        });


        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = nameView.getText().toString();
                String email = emailView.getText().toString();
                saveLocal.setName(name);
                saveLocal.setEmail(email);
                firebaseAdapter.addUser(name, email);
                DialogFragment addFriendFragment = new AddFriendFragment();
                addFriendFragment.show(getFragmentManager(), "Add Friend");
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



