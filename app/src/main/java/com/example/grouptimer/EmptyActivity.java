package com.example.grouptimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmptyActivity extends AppCompatActivity implements View.OnClickListener {


    Button makeGroup;
    Button personal;
    Button groupTimeTable;

    Button listAdd;


    public static int groupNumber = 0;
    int firebaseIndex;
    boolean loopCheck = false;


    Map<String, Object> taskMap = new HashMap<String, Object>();

    ArrayList<String> GroupList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);


        makeGroup = (Button) findViewById(R.id.makeGroup);
        personal = (Button) findViewById(R.id.personal);
        groupTimeTable = (Button) findViewById(R.id.groupTimeTable);
        listAdd = (Button) findViewById(R.id.add);

        makeGroup.setOnClickListener(this);
        personal.setOnClickListener(this);
        groupTimeTable.setOnClickListener(this);
        listAdd.setOnClickListener(this);






    }


    @Override
    public void onClick(View view)
    {
        if(view == makeGroup)
        {
            startActivity(new Intent(this, MakeGroupActivity.class));
        }
        else if(view == personal)
        {
            startActivity(new Intent(this, PersonalTimeTableActivity.class));
        }
        else if(view == groupTimeTable)
        {
            startActivity(new Intent(this, GroupTimeTableActivity.class));
        }


        if(view == listAdd)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();




            mDatabase.child("Users").child(user.getUid()).child("groupNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //int value = dataSnapshot.getValue(Integer.class);
                    //GroupListManager manager = dataSnapshot.getValue(GroupListManager.class);
                    String value = dataSnapshot.getValue(String.class);

                    //Log.d("GT", "dataSnapshot : " + manager.groupNumber );
                    Log.d("GT", "dataSnapshot : " + value );

                    //groupNumber = Integer.parseInt(manager.groupNumber);
                    groupNumber = Integer.parseInt(value);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });




            Log.d("GT", "Database groupNumber : " + groupNumber);


            if(groupNumber > 0)
            {
                GroupList.clear();


                for(firebaseIndex = 0; firebaseIndex < groupNumber; firebaseIndex++)
                {
                    String listID = Integer.toString(firebaseIndex);

                    mDatabase.child("Users").child(user.getUid()).child("GroupList").child(listID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //GroupListManager groupListManager = dataSnapshot.getValue(GroupListManager.class);

                            String value = dataSnapshot.getValue(String.class);


                            if(value == null)
                            {
                                return;
                            }


                            GroupList.add(value);


                            Log.d("GT", "Database Get : " + value);


                            if(firebaseIndex == (groupNumber - 1))
                            {
                                GroupList.add("Group" + firebaseIndex);

                                for(int k = 0; k < GroupList.size(); k++)
                                {
                                    Log.d("GT", "List(" + k + ") : " + GroupList.get(k));
                                }


                                taskMap.put("GroupList", GroupList);

                                mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });



                }


                taskMap.clear();
                taskMap.put("groupNumber", Integer.toString(groupNumber + 1));

                mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);
            }
            else
            {
                Log.d("GT", "groupNumber is 0");

                ArrayList<String> GroupList = new ArrayList<String>();

                GroupList.add("First Group");

                for(int k = 0; k < GroupList.size(); k++)
                {
                    Log.d("GT", "List(" + k + ") : " + GroupList.get(k));
                }

                taskMap.put("GroupList", GroupList);

                mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);


                taskMap.clear();
                taskMap.put("groupNumber", "1");

                mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);
            }
        }
    }
}