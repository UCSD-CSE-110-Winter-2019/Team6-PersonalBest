package com.example.personalbest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.personalbest.database.FirebaseAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class FriendsListActivity extends AppCompatActivity {
    ArrayList <String> arrayList;
    SaveLocal saveLocal;
    FirebaseAdapter firebaseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseAdapter=new FirebaseAdapter(this);
        saveLocal = new SaveLocal(this);
        firebaseAdapter.getFriends(saveLocal.getEmail());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);
        ListView listView = findViewById(R.id.listView);
        arrayList = saveLocal.getFriends();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(FriendsListActivity.this, "you clicked: " + arrayList.get(i).toString(), Toast.LENGTH_SHORT).show();
                launchFriendGraph(arrayList.get(i));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DialogFragment optionsFrag = new OptionsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", arrayList.get(i));
                optionsFrag.setArguments(bundle);
                optionsFrag.show(getSupportFragmentManager(), "Long Press");

                Toast.makeText(FriendsListActivity.this, "you long clicked: " + arrayList.get(i), Toast.LENGTH_SHORT).show();
                saveLocal.setLastClickedFriend(arrayList.get(i));

                return false;
            }
        });/*

        for (String friend : arrayList) {

            firebaseAdapter.saveNewGoalsLocal(friend);
            int i = 0;
            //Calendar needs to be mocked
            Calendar currDay=Calendar.getInstance();
            while (i < 28) {
                firebaseAdapter.saveFriendStepLocal(friend, currDay);
                currDay.add(Calendar.DAY_OF_YEAR, -1);
                String dateKey=currDay.get(Calendar.DAY_OF_MONTH)+"-"+((int)currDay.get(Calendar.MONTH)+1)+"-"+currDay.get(Calendar.YEAR);

                Log.d("Friend Data","Saved "+friend+" data for day "+dateKey);
                i++;
            }
        }*/
    }

    void fillArray(ArrayList <String> arr){
        //Put values into the arraylist

        arr.add("Hello");
        arr.add("World");
    }

    public void addFriend(View v){
        DialogFragment addFriendFragment = new AddFriendFragment();
        addFriendFragment.show(getSupportFragmentManager(), "Add Friend");

    }

    public void launchChatView(View view) {
        Intent intent = new Intent(this, MessageActivity.class);
        //int dailySteps=(int)fitnessService.getDailyStepCount(Calendar.getInstance());
        //intent.putExtra("numSteps", dailySteps);
        startActivity(intent);
    }

    public void launchFriendGraph(String email){
        Intent intent = new Intent(this, MonthGraph.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }
}
