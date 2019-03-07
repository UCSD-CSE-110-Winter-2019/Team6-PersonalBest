package com.example.personalbest.database;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.personalbest.StepCountActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class FirebaseAdapter {
    final String PROJECT_ID="PersonalBest";
    final String TAG="FirebaseAdapter";
    FirebaseFirestore db;
    public FirebaseAdapter(Context context) {
//        FirebaseApp.initializeApp(context);
//         db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name

    }
    public void addUser(String userName){
        Map<String, Object> user = new HashMap<>();
        user.put("name", userName);


        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}