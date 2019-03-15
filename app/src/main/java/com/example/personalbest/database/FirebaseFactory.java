package com.example.personalbest.database;

import android.app.Activity;

public class FirebaseFactory {

    static IFirebase firebase;

    public static void createFirebase(String s, Activity a){

        if("MockFirebase".equals(s)){
            firebase = new MockFirebaseAdapter(a);
        }
        else
            firebase = new FirebaseAdapter(a);

    }

    public static IFirebase getFirebase(){
        return firebase;
    }
}
