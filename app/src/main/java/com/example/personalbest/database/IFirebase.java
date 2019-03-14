package com.example.personalbest.database;

import java.util.Calendar;

public interface IFirebase {
    void addUser(String userName, String email);

    void addFriendToFriendsList(String friendsName, String friendsEmail);

    void addFriendToPendingFriendsList(String friendsEmail);

    void addFriend(String friendsEmail);

    void getUsers();

    void getFriends(String email);

    void pushStepStats(Calendar time, int backgroundSteps, int exerciseSteps, String myEmail);

    void saveFriendStepLocal(String friendEmail, Calendar date);
}
