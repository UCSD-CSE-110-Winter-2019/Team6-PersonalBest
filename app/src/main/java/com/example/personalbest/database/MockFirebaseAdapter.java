package com.example.personalbest.database;

import android.app.Activity;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.util.Log;

import com.example.personalbest.SaveLocal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MockFirebaseAdapter implements IFirebase {
    SaveLocal saveLocal;
    final String FRIENDS_LIST = "friendsList";
    final String PENDING_LIST = "pendingList";

    public HashMap<String, HashMap<String, ArrayList<String>>> db;
    MockFirebaseAdapter(Activity a){
        saveLocal = new SaveLocal(a);
        db = new HashMap<>();
    }
    @Override
    public void addUser(String userName, String email) {
        HashMap<String, ArrayList<String>> hash = new HashMap<>();

        db.put(email, hash);
        hash.put(FRIENDS_LIST, new ArrayList<String>());
        hash.put(PENDING_LIST, new ArrayList<String>());
    }

    @Override
    public void addFriendToFriendsList(String friendsName, String friendsEmail) {

    }

    @Override
    public void addFriendToPendingFriendsList(String friendsEmail) {

    }

    @Override
    public void addFriend(String friendsEmail) {
        String myEmail = saveLocal.getEmail();
        if (!db.get(myEmail).get(FRIENDS_LIST).contains(friendsEmail)) {
            if (db.get(friendsEmail).get(PENDING_LIST).contains(myEmail)) {
                db.get(myEmail).get(FRIENDS_LIST).add(friendsEmail);
                db.get(friendsEmail).get(FRIENDS_LIST).add(myEmail);
                db.get(friendsEmail).get(PENDING_LIST).remove(myEmail);
            } else {
                db.get(myEmail).get(PENDING_LIST).add(friendsEmail);
            }
        }
    }

    @Override
    public void getUsers() {
    }

    @Override
    public void getFriends(String email) {
        ArrayList<String> myFriends = db.get(email).get(FRIENDS_LIST);
        saveLocal.setFriends(myFriends);
    }

    @Override
    public void pushStepStats(Calendar time, int backgroundSteps, int exerciseSteps, String myEmail) {

    }

    @Override
    public void saveFriendStepLocal(String friendEmail, Calendar date) {

    }
}
