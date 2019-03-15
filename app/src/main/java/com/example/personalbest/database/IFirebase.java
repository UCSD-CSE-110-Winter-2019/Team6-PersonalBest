package com.example.personalbest.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public interface IFirebase {
    void addUser(String userName, String email);

    void addFriendToFriendsList(String friendsName, String friendsEmail);

    void addFriendToPendingFriendsList(String friendsEmail);

    void addFriend(String friendsEmail);

    void getUsers();

    void getFriends(String email);

    void pushStepStats(Calendar time, int backgroundSteps, int exerciseSteps, String myEmail);

    public Task<QuerySnapshot> saveFriendStepLocal(String friendEmail);

    public Task<DocumentSnapshot> saveNewGoalsLocal(String email);

    public void pushNewGoal(Calendar time, int goal);
}
