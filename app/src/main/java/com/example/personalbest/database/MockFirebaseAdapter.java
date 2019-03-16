package com.example.personalbest.database;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.util.Log;

import com.example.personalbest.ChatMessage;
import com.example.personalbest.SaveLocal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static android.content.ContentValues.TAG;

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
    public Task<QuerySnapshot> saveFriendStepLocal(String friendEmail) {
        return new Task<QuerySnapshot>() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public boolean isSuccessful() {
                return true;
            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Nullable
            @Override
            public QuerySnapshot getResult() {
                return null;
            }

            @Nullable
            @Override
            public <X extends Throwable> QuerySnapshot getResult(@NonNull Class<X> aClass) throws X {
                return null;
            }

            @Nullable
            @Override
            public Exception getException() {
                return null;
            }

            @NonNull
            @Override
            public Task<QuerySnapshot> addOnSuccessListener(@NonNull OnSuccessListener<? super QuerySnapshot> onSuccessListener) {
                return this;
            }

            @NonNull
            @Override
            public Task<QuerySnapshot> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super QuerySnapshot> onSuccessListener) {
                return this;
            }

            @NonNull
            @Override
            public Task<QuerySnapshot> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super QuerySnapshot> onSuccessListener) {
                return this;
            }

            @NonNull
            @Override
            public Task<QuerySnapshot> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<QuerySnapshot> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<QuerySnapshot> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                return null;
            }
        };
    }

    @Override
    public Task<DocumentSnapshot> saveNewGoalsLocal(String email) {
        return new Task<DocumentSnapshot>() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public boolean isSuccessful() {
                return true;
            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Nullable
            @Override
            public DocumentSnapshot getResult() {
                return null;
            }

            @Nullable
            @Override
            public <X extends Throwable> DocumentSnapshot getResult(@NonNull Class<X> aClass) throws X {
                return null;
            }

            @Nullable
            @Override
            public Exception getException() {
                return null;
            }

            @NonNull
            @Override
            public Task<DocumentSnapshot> addOnSuccessListener(@NonNull OnSuccessListener<? super DocumentSnapshot> onSuccessListener) {
                return this;
            }

            @NonNull
            @Override
            public Task<DocumentSnapshot> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super DocumentSnapshot> onSuccessListener) {
                return this;
            }

            @NonNull
            @Override
            public Task<DocumentSnapshot> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super DocumentSnapshot> onSuccessListener) {
                return this;
            }

            @NonNull
            @Override
            public Task<DocumentSnapshot> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<DocumentSnapshot> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<DocumentSnapshot> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                return null;
            }
        };
    }

    @Override
    public void pushNewGoal(Calendar time, int goal) {

    }
}
