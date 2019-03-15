package com.example.personalbest.database;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.personalbest.FirebaseCloudMessagingAdapter;
import com.example.personalbest.NotificationService;
import com.example.personalbest.SaveLocal;
import com.example.personalbest.StepCountActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class FirebaseAdapter {
    final String PROJECT_ID="PersonalBest";
    final String TAG="FirebaseAdapter";
    FirebaseFirestore db;
    SaveLocal saveLocal;

    public FirebaseAdapter(Activity activity) {
        FirebaseApp.initializeApp(activity);
        db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        saveLocal = new SaveLocal(activity);

    }
    public void addUser(String userName, final String email){
        Map<String, Object> user = new HashMap<>();
        user.put("name", userName);
        user.put("email", email);


        // Add a new document with a generated ID
        db.collection("users")
                .document(email)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + email);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void addFriendToFriendsList(String friendsName, final String friendsEmail) {
        final String myEmail = saveLocal.getEmail();
        final String myName = saveLocal.getName();

        Map<String, Object> friend = new HashMap<>();
        friend.put("email", friendsEmail);
        friend.put("name", friendsName);

        Map<String, Object> me = new HashMap<>();
        me.put("email", myEmail);
        me.put("name", myName);

        subscribeToNotification(myEmail, friendsEmail);

        // Add a new document with a generated ID
        db.collection("users")
                .document(myEmail)
                .collection("friends")
                .document(friendsEmail)
                .set(friend)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + friendsEmail);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        db.collection("users")
                .document(friendsEmail)
                .collection("friends")
                .document(myEmail)
                .set(me)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + friendsEmail);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        db.collection("users")
                .document(friendsEmail)
                .collection("pendingFriends")
                .document(myEmail)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Removed my email: " + myEmail + " from friend: " + friendsEmail);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }

    public void subscribeToNotification(String myEmail, String friendsEmail) {
        String DOCUMENT_KEY = "";
        if(myEmail.compareTo(friendsEmail)>0){
            DOCUMENT_KEY=myEmail+friendsEmail;
        }else{
            DOCUMENT_KEY=friendsEmail+myEmail;
        }
        DOCUMENT_KEY = DOCUMENT_KEY.toString();
        String NEW_KEY="";
        String array1[] = DOCUMENT_KEY.split("@");
        for(String s : array1){
            NEW_KEY += s;
        }
        DOCUMENT_KEY = NEW_KEY;

        NotificationService notificationService = FirebaseCloudMessagingAdapter.getInstance();

        notificationService.subscribeToNotificationsTopic(DOCUMENT_KEY, task -> {
            String msg = "Subscribed to notifications";
            if (!task.isSuccessful()) {
                msg = "Subscribe to notifications failed";
            }
            Log.d(TAG, msg);
        });
    }

    public void addFriendToPendingFriendsList(final String friendsEmail){
        final String myEmail = saveLocal.getEmail();
        Map<String, Object> friend = new HashMap<>();
        friend.put("email", friendsEmail);


        // Add a new document with a generated ID
        db.collection("users")
                .document(myEmail)
                .collection("pendingFriends")
                .document(friendsEmail)
                .set(friend)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + friendsEmail);
                        createChat(myEmail, friendsEmail);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


    }

    public void addFriend(final String friendsEmail) {
        final String myEmail = saveLocal.getEmail();
        db.collection("users")
                .document(myEmail)
                .collection("friends")
                .document(friendsEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> arr = documentSnapshot.getData();
                        if (documentSnapshot.getData() == null) {
                            addFriendOfficial(friendsEmail);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to add friend");
                    }
                });

    }

    public void createChat(String myEmail, String friendsEmail) {
        String DOCUMENT_KEY = "";
        if(myEmail.compareTo(friendsEmail)>0){
            DOCUMENT_KEY=myEmail+friendsEmail;
        }else{
            DOCUMENT_KEY=friendsEmail+myEmail;
        }
        DOCUMENT_KEY = DOCUMENT_KEY.toString();
        String NEW_KEY="";
        String array1[] = DOCUMENT_KEY.split("@");
        for(String s : array1){
            NEW_KEY += s;
        }
        DOCUMENT_KEY = NEW_KEY;

        Map<String, Object> messages = new HashMap<>();

        db.collection("chats")
                .document(DOCUMENT_KEY)
                .set(messages)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Success create chat");
                        subscribeToNotification(myEmail, friendsEmail);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error set steps", e);
                    }
                });
    }

    private void addFriendOfficial(final String friendsEmail) {
        // Add a new document with a generated ID
        final String myEmail = saveLocal.getEmail();
        db.collection("users")
                .document(friendsEmail)
                .collection("pendingFriends")
                .document(myEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> arr = documentSnapshot.getData();
                        if (documentSnapshot.getData() == null) {
                            addFriendToPendingFriendsList(friendsEmail);
                        } else {
                            addFriendToFriendsList("FRIEND NAME", friendsEmail);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to add friend");
                    }
                });
    }

    public void getUsers(){

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void getFriends(String email){
        final ArrayList<String> arr = new ArrayList<>();

        db.collection("users").document(email)
                .collection("friends").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            arr.add(document.getId());
                        }

                        saveLocal.setFriends(arr);
                    }
                });
    }

    public void pushStepStats(Calendar time, int backgroundSteps, int exerciseSteps, String myEmail){

        Calendar newCal=Calendar.getInstance();
        newCal.setTimeInMillis(time.getTimeInMillis());
        newCal.set(Calendar.HOUR_OF_DAY,0);
        newCal.set(Calendar.MINUTE,0);
        newCal.set(Calendar.SECOND,0);
        newCal.set(Calendar.MINUTE,0);
        newCal.set(Calendar.SECOND,0);
        newCal.set(Calendar.MILLISECOND,0);

        String dateKey=newCal.get(Calendar.DAY_OF_MONTH)+"-"+((int)newCal.get(Calendar.MONTH)+1)+"-"+newCal.get(Calendar.YEAR);


        Map<String, Integer> steps = new HashMap<>();
        steps.put("Background", backgroundSteps);
        steps.put("Exercise", exerciseSteps);
        // Add a new document with a generated ID
        db.collection("users")
                .document(myEmail)
                .collection("steps")
                .document(""+dateKey)
                .set(steps)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Steps set for Email:" + myEmail+" Date: "+dateKey);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error set steps", e);
                    }
                });
    }

    public void saveFriendStepLocal(String friendEmail, Calendar date){

        Calendar newCal=Calendar.getInstance();
        newCal.setTimeInMillis(date.getTimeInMillis());
        newCal.set(Calendar.HOUR_OF_DAY,0);
        newCal.set(Calendar.MINUTE,0);
        newCal.set(Calendar.SECOND,0);
        newCal.set(Calendar.MINUTE,0);
        newCal.set(Calendar.SECOND,0);
        newCal.set(Calendar.MILLISECOND,0);
        String dateKey=newCal.get(Calendar.DAY_OF_MONTH)+"-"+((int)newCal.get(Calendar.MONTH)+1)+"-"+newCal.get(Calendar.YEAR);
        db.collection("users")
                .document(friendEmail)
                .collection("steps")
                .document(""+dateKey)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String,Object> steps= task.getResult().getData();
                            if(steps==null) return;
                            long backgroundSteps=((long)steps.get("Background"));
                            long exerciseSteps=(long)steps.get("Exercise");
                            saveLocal.setAccountBackgroundStep(friendEmail,(int)backgroundSteps,date);
                            saveLocal.setAccountExerciseStep(friendEmail,(int)exerciseSteps,date);
                            Log.d(TAG, "Steps saved locally for " +friendEmail+ " => Background: "
                                    +backgroundSteps+", Exercise: "+exerciseSteps+" Date: "+dateKey);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
