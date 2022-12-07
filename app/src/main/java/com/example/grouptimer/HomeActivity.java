package com.example.grouptimer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {


    public static Context context;


    private EditText insertCodeEdit;

    private Button insertCodeButton;
    private Button makeGroup;
    private BottomNavigationView bottom;

    private RecyclerView recyclerView;

    private TextView emptyText;

    private LinearLayoutManager linearLayoutManager;
    private GroupRecyclerViewAdapter recyclerViewAdapter;


    private ProgressBar progressBar;


    public static int groupNumber = 0;
    int firebaseIndex;


    Map<String, Object> taskMap = new HashMap<String, Object>();

    ArrayList<String> GroupList = new ArrayList<String>();
    ArrayList<String> GroupNameList = new ArrayList<String>();


    int CodeListIndex = 0;
    boolean GroupEnter;

    boolean AlreadyEntered;

    boolean RemoveGroup;

    public static ProgressDialog progressDialog = null;


    FragmentManager fragmentManager;
    FragmentTransaction ft;

    PersonalTimeTableActivity personalTimeTableActivity;
    MypageFragment mypageFragment;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d("check", "I'm here");
        context = this;


        fragmentManager = getSupportFragmentManager();
        mypageFragment = new MypageFragment();
        personalTimeTableActivity = new PersonalTimeTableActivity();

        insertCodeEdit = (EditText) findViewById(R.id.insertCodeEdit);
        insertCodeButton = (Button) findViewById(R.id.insertCodeButton);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        emptyText = (TextView) findViewById(R.id.emptyText);

        makeGroup = (Button) findViewById(R.id.makeGroup);

        bottom = findViewById(R.id.main_bottom);

        bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("check", "in function");
                switch (item.getItemId()){
                    case R.id.bottom_home:
                        Log.d("error","home");

                        startActivity(new Intent(HomeActivity.this,HomeActivity.class));

                        finish();

                        break;
                    case R.id.bottom_todo:
                        Log.d("error","personal");

                        ft = fragmentManager.beginTransaction();

                        ft.replace(R.id.home_frag, personalTimeTableActivity);
                        ft.commit();

                        break;
                    case R.id.bottom_mypage:
                        Log.d("error","my");

                        ft = fragmentManager.beginTransaction();

                        ft.replace(R.id.home_frag, mypageFragment);
                        ft.commit();

                        break;
                }
                return true;
            }
        });


        progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);


        linearLayoutManager = new LinearLayoutManager(this);
        if(linearLayoutManager == null)
        {
            Log.d("GT", "LayoutManager is null");
        }
        recyclerView.setLayoutManager(linearLayoutManager);


        insertCodeEdit.setCursorVisible(false);
        insertCodeEdit.setOnClickListener(this);

        insertCodeButton.setOnClickListener(this);
        makeGroup.setOnClickListener(this);



        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        //Load_GroupList(false, true, null, user, mDatabase);
    }


    @Override
    public void onResume() {
        super.onResume();


        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        Load_GroupList(false, true, null, user, mDatabase);

        Log.d("GT", "Activity resume");
    }


    private void Check_InsertCode(String insertCode)
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        GroupEnter = false;


        mDatabase.child("GroupInsertCode").child("codeCnt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int codeCnt;


                if(dataSnapshot.getValue() == null)
                {
                    Log.d("GT", "Empty InsertCode Database");


                    if(progressDialog != null)
                    {
                        progressDialog.dismiss();

                        progressDialog = null;
                    }


                    Toast.makeText(getApplicationContext(), "Empty InsertCode Database", Toast.LENGTH_SHORT).show();


                    return;
                }


                codeCnt = dataSnapshot.getValue(Integer.class);
                CodeListIndex = 0;


                mDatabase.child("GroupInsertCode").child("CodeList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                        {

                            if(childSnapshot != null)
                            {
                                Log.d("GT", "HashMap Key : " + childSnapshot.getKey());
                                Log.d("GT", "HashMap Value : " + childSnapshot.getValue());


                                if(childSnapshot.getKey().equals(insertCode) == true)
                                {
                                    GroupEnter = true;


                                    Enter_Group(childSnapshot.getValue().toString());


                                    return;
                                }

                                CodeListIndex++;


                                if((CodeListIndex == codeCnt) && (GroupEnter == false))
                                {
                                    Log.d("GT", "Fail Enter Group");


                                    if(progressDialog != null)
                                    {
                                        progressDialog.dismiss();

                                        progressDialog = null;
                                    }


                                    Toast.makeText(getApplicationContext(), "Fail Enter Group", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void Enter_Group(String groupID)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        AlreadyEntered = false;



        mDatabase.child("Groups").child(groupID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int getGroupNumber;
                int getMemberCnt;


                getGroupNumber = 0;
                getMemberCnt = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if(snapshot.getKey().equals("groupNumber") == true)
                    {
                        getGroupNumber = snapshot.getValue(Integer.class);

                        Log.d("GT", "groupNumber : " + getGroupNumber);
                    }

                    if(snapshot.getKey().equals("memberCnt") == true)
                    {
                        getMemberCnt = snapshot.getValue(Integer.class);

                        Log.d("GT", "memberCnt : " + getMemberCnt);
                    }
                }


                if(getGroupNumber == getMemberCnt)
                {
                    Log.d("GT", "Group member is maximum");


                    if(progressDialog != null)
                    {
                        progressDialog.dismiss();

                        progressDialog = null;
                    }


                    Toast.makeText(getApplicationContext(), "Group member is maximum", Toast.LENGTH_SHORT).show();


                    return;
                }


                Load_GroupList(true, true, groupID, user, mDatabase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void Load_GroupList(boolean listUpdate, boolean showRecyclerView, String groupID, FirebaseUser user, DatabaseReference mDatabase)
    {
        ArrayList<String> tempIDList = new ArrayList<String>();
        ArrayList<String> tempNameList = new ArrayList<String>();

        tempIDList.addAll(GroupList);
        tempNameList.addAll(GroupNameList);


        RemoveGroup = false;


        mDatabase.child("Users").child(user.getUid()).child("groupNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int value = dataSnapshot.getValue(Integer.class);

                Log.d("GT", "dataSnapshot : " + value );

                groupNumber = value;


                if(groupNumber > 0)
                {
                    GroupList.clear();
                    GroupNameList.clear();


                    for(firebaseIndex = 0; firebaseIndex < groupNumber; firebaseIndex++)
                    {
                        String listID = Integer.toString(firebaseIndex);

                        mDatabase.child("Users").child(user.getUid()).child("GroupList").child(listID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String value = dataSnapshot.getValue(String.class);


                                if(value == null)
                                {
                                    if(progressDialog != null)
                                    {
                                        progressDialog.dismiss();

                                        progressDialog = null;
                                    }


                                    return;
                                }

                                if(AlreadyEntered == true)
                                {
                                    if(progressDialog != null)
                                    {
                                        progressDialog.dismiss();

                                        progressDialog = null;
                                    }


                                    if(Integer.parseInt(listID) == groupNumber - 1)
                                    {
                                        GroupList = tempIDList;
                                        GroupNameList = tempNameList;


                                        recyclerViewAdapter = new GroupRecyclerViewAdapter(tempIDList, tempNameList);
                                        recyclerView.setAdapter(recyclerViewAdapter);


                                        recyclerView.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        //recyclerView.setNestedScrollingEnabled(false);
                                        emptyText.setVisibility(View.GONE);

                                        Log.d("GT", "Reload Group List");
                                    }


                                    return;
                                }


                                if(value.equals(groupID) == true)
                                {
                                    AlreadyEntered = true;

                                    Log.d("GT", "Already Enter Group");


                                    if(progressDialog != null)
                                    {
                                        progressDialog.dismiss();

                                        progressDialog = null;
                                    }


                                    Toast.makeText(getApplicationContext(), "Already Enter Group", Toast.LENGTH_SHORT).show();


                                    return;
                                }


                                GroupList.add(value);


                                Log.d("GT", "Database Get : " + value);


                                if(Integer.parseInt(listID) == (groupNumber - 1))
                                {
                                    Log.d("GT", "Complete loading group list");


                                    if(listUpdate == true)
                                    {
                                        GroupList.add(groupID);

                                        for(int k = 0; k < GroupList.size(); k++)
                                        {
                                            Log.d("GT", "List(" + k + ") : " + GroupList.get(k));
                                        }


                                        taskMap.put("GroupList", GroupList);

                                        mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);


                                        taskMap.clear();
                                        taskMap.put("groupNumber", groupNumber + 1);

                                        mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);


                                        Update_Group_Database(groupID);


                                        Log.d("GT", "Success Enter Group");


                                        //recyclerViewAdapter.notifyItemInserted(GroupList.size() - 1);


                                        int originSize = GroupList.size();
                                        for(int i = 0; i < originSize; i++)
                                        {
                                            int groupIndex = i;

                                            mDatabase.child("Groups").child(GroupList.get(groupIndex)).child("groupName").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if(dataSnapshot.getValue() == null)
                                                    {
                                                        GroupList.remove(groupIndex);

                                                        RemoveGroup = true;
                                                    }
                                                    else
                                                    {
                                                        String value = dataSnapshot.getValue(String.class);


                                                        GroupNameList.add(value);
                                                    }


                                                    if(groupIndex == (originSize - 1))
                                                    {
                                                        if(RemoveGroup == true)
                                                        {
                                                            taskMap.put("GroupList", GroupList);

                                                            mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);


                                                            taskMap.clear();
                                                            taskMap.put("groupNumber", GroupList.size());

                                                            mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);


                                                            recyclerViewAdapter = new GroupRecyclerViewAdapter(GroupList, GroupNameList);
                                                            recyclerView.setAdapter(recyclerViewAdapter);
                                                        }


                                                        recyclerViewAdapter = new GroupRecyclerViewAdapter(GroupList, GroupNameList);
                                                        recyclerView.setAdapter(recyclerViewAdapter);


                                                        if(progressDialog != null)
                                                        {
                                                            progressDialog.dismiss();

                                                            progressDialog = null;
                                                        }


                                                        Toast.makeText(getApplicationContext(), "Success Enter Group", Toast.LENGTH_SHORT).show();
                                                    }
                                                }


                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                        }
                                    }


                                    if(showRecyclerView == true && listUpdate == false)
                                    {
                                        Log.d("GT", "RecyclerView generating");


                                        int originSize = GroupList.size();
                                        for(int i = 0; i < originSize; i++)
                                        {
                                            int groupIndex = i;

                                            mDatabase.child("Groups").child(GroupList.get(groupIndex)).child("groupName").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if(dataSnapshot.getValue() == null)
                                                    {
                                                        GroupList.remove(groupIndex);

                                                        RemoveGroup = true;
                                                    }
                                                    else
                                                    {
                                                        String value = dataSnapshot.getValue(String.class);


                                                        GroupNameList.add(value);
                                                    }


                                                    if(groupIndex == (groupNumber - 1))
                                                    {
                                                        if(RemoveGroup == true)
                                                        {
                                                            taskMap.put("GroupList", GroupList);

                                                            mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);


                                                            taskMap.clear();
                                                            taskMap.put("groupNumber", GroupList.size());

                                                            mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);


                                                            recyclerViewAdapter = new GroupRecyclerViewAdapter(GroupList, GroupNameList);
                                                            recyclerView.setAdapter(recyclerViewAdapter);
                                                        }


                                                        recyclerViewAdapter = new GroupRecyclerViewAdapter(GroupList, GroupNameList);
                                                        recyclerView.setAdapter(recyclerViewAdapter);

                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        //recyclerView.setNestedScrollingEnabled(false);
                                                        progressBar.setVisibility(View.GONE);
                                                        emptyText.setVisibility(View.GONE);


                                                        if(progressDialog != null)
                                                        {
                                                            progressDialog.dismiss();

                                                            progressDialog = null;
                                                        }
                                                    }
                                                }


                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
                else
                {
                    Log.d("GT", "groupNumber is 0");

                    GroupList.clear();
                    GroupNameList.clear();


                    if(listUpdate == true)
                    {
                        ArrayList<String> GroupList = new ArrayList<String>();

                        GroupList.add(groupID);

                        for(int k = 0; k < GroupList.size(); k++)
                        {
                            Log.d("GT", "List(" + k + ") : " + GroupList.get(k));
                        }


                        taskMap.put("GroupList", GroupList);

                        mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);


                        taskMap.clear();
                        taskMap.put("groupNumber", 1);

                        mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);


                        Update_Group_Database(groupID);


                        int originSize = GroupList.size();
                        for(int i = 0; i < originSize; i++)
                        {
                            int groupIndex = i;

                            mDatabase.child("Groups").child(GroupList.get(groupIndex)).child("groupName").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                    if(dataSnapshot.getValue() == null)
                                    {
                                        GroupList.remove(groupIndex);

                                        RemoveGroup = true;
                                    }
                                    else
                                    {
                                        String value = dataSnapshot.getValue(String.class);


                                        GroupNameList.add(value);
                                    }


                                    if(groupIndex == (originSize - 1))
                                    {
                                        if(RemoveGroup == true)
                                        {
                                            taskMap.put("GroupList", GroupList);

                                            mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);


                                            taskMap.clear();
                                            taskMap.put("groupNumber", GroupList.size());

                                            mDatabase.child("Users").child(user.getUid()).updateChildren(taskMap);


                                            recyclerViewAdapter = new GroupRecyclerViewAdapter(GroupList, GroupNameList);
                                            recyclerView.setAdapter(recyclerViewAdapter);
                                        }


                                        Log.d("GT", "Success Enter Group");

                                        Toast.makeText(getApplicationContext(), "Success Enter Group", Toast.LENGTH_SHORT).show();

                                        recyclerView.setVisibility(View.VISIBLE);
                                        emptyText.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                    else
                    {
                        recyclerView.setVisibility(View.GONE);
                        emptyText.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }


                    if(progressDialog != null)
                    {
                        progressDialog.dismiss();

                        progressDialog = null;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void Update_Group_Database(String groupID)
    {
        Map<String, Object> taskMap = new HashMap<String, Object>();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();



        mDatabase.child("Groups").child(groupID).child("memberCnt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int memberCnt;

                ArrayList<String> memberList = new ArrayList<String>();


                if(dataSnapshot.getValue() == null)
                {
                    Log.d("GT", "dataSnapshot is null");


                    memberList.add(groupID);

                    taskMap.put("groupMember", user.getUid());

                    mDatabase.child("Groups").child(groupID).updateChildren(taskMap);


                    taskMap.clear();
                    taskMap.put("memberCnt", 1);

                    mDatabase.child("Groups").child(groupID).updateChildren(taskMap);


                    return;
                }


                memberCnt = dataSnapshot.getValue(Integer.class);


                for(int i = 0; i < memberCnt; i++)
                {
                    String listID = Integer.toString(i);


                    mDatabase.child("Groups").child(groupID).child("groupMember").child(listID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String value = dataSnapshot.getValue(String.class);


                            if(value == null)
                            {
                                return;
                            }


                            memberList.add(value);


                            if(Integer.parseInt(listID) == (memberCnt - 1))
                            {
                                memberList.add(user.getUid());

                                taskMap.put("groupMember", memberList);

                                mDatabase.child("Groups").child(groupID).updateChildren(taskMap);


                                taskMap.clear();
                                taskMap.put("memberCnt", memberCnt + 1);

                                mDatabase.child("Groups").child(groupID).updateChildren(taskMap);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    public void onClick(View view)
    {
        if(view == insertCodeEdit)
        {

        }
        else if(view == insertCodeButton)
        {
            String insertCode;


            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    progressDialog = new ProgressDialog(HomeActivity.this, R.style.ProgressDialogTheme);

                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Group matching ...");

                    progressDialog.show();
                }
            });


            insertCode = insertCodeEdit.getText().toString();

            insertCodeEdit.setText("");

            Log.d("GT", "EditText : " + insertCode);

            Check_InsertCode(insertCode);
        }
        else if(view == makeGroup)
        {
            startActivity(new Intent(this, MakeGroupActivity.class));
        }
    }
}