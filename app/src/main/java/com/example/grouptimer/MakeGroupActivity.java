package com.example.grouptimer;


        import androidx.annotation.NonNull;
        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.grouptimer.model.Group;
        import com.example.grouptimer.model.User;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

public class MakeGroupActivity extends AppCompatActivity{

    private EditText groupNameEditText;
    private EditText howManyEditText;
    private TextView selectTextView;
    private ListView selectListView;
    private Button selectButton;
    private List<String> purposeList; //selectListView 에 들어갈 값을 넣어두는 List
    private Button makeGroupButton;

//    private int checkGroupId; // group 확인용 groupId
//    private int randomNumber; // random number

    private boolean ClickChecker = false; // selectButton 누를때 selectListView visibility 확인하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_group);

        groupNameEditText = (EditText) findViewById(R.id.groupNameEditText);
        howManyEditText = (EditText) findViewById(R.id.howManyEditText);
        selectTextView = (TextView) findViewById(R.id.selectTextView);
        selectListView = (ListView) findViewById(R.id.selectListView);
        selectButton = (Button) findViewById(R.id.selectButton);
        makeGroupButton = (Button) findViewById(R.id.makeGroupButton);


        purposeList = new ArrayList<>();
        purposeList.add("회의");
        purposeList.add("스터디");
        purposeList.add("여행");
        purposeList.add("친목");
        purposeList.add("동호회");
        purposeList.add("가족모임");

        ArrayAdapter<String> adpater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, purposeList);
        selectListView.setAdapter(adpater);

        selectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String data = (String) adapterView.getItemAtPosition(position);
                selectTextView.setText(data);
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view == selectButton) {

                    if(ClickChecker == true){
                        selectListView.setVisibility(View.INVISIBLE);

                        ClickChecker = false;
                    }
                    else
                    {
                        selectListView.setVisibility(View.VISIBLE);

                        ClickChecker = true;
                    }
                }
            }
        });

        makeGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (groupNameEditText.getText().toString().isEmpty() == true || howManyEditText.getText().toString().isEmpty() == true ||
                        selectTextView.getText().toString().isEmpty() == true){

                    Log.d("GT", "Make Group fail");


                    Toast.makeText(getApplicationContext(), "입력이 올바르지 않습니다", Toast.LENGTH_SHORT).show();


                    return;
                }


//                FirebaseDatabase.getInstance()
//                        .getReference().child("GroupNumber").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        checkGroupId = (int) dataSnapshot.getValue();
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
//                    }
//                });


                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(groupNameEditText.getText().toString(),howManyEditText.getText().toString())
                        .addOnCompleteListener(MakeGroupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                Group group = new Group();
                                group.groupName = groupNameEditText.getText().toString();
                                group.groupNumber = Integer.parseInt(howManyEditText.getText().toString());
                                group.groupMakerUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                group.groupPurpose = selectTextView.getText().toString();


                                //group.groupMakerUid = task.getResult().getUser().getUid();


                                DatabaseReference  reference = FirebaseDatabase.getInstance().getReference().child("Groups").push();


                                //FirebaseDatabase.getInstance().getReference().child("Groups").push().setValue(group);
                                reference.setValue(group);


                                ArrayList<String> memberList = new ArrayList<String>();

                                memberList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                //FirebaseDatabase.getInstance().getReference().child("Groups").push().child("groupMember").setValue(memberList);
                                reference.child("groupMember").setValue(memberList);
                                reference.child("memberCnt").setValue(1);



                                Log.d("GT", "DB Reference : " + reference.getKey());


                                ArrayList<String> groupList = new ArrayList<String>();

                                Map<String, Object> taskMap = new HashMap<String, Object>();


                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                                mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("groupNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.getValue(Integer.class) == 0)
                                        {
                                            groupList.add(reference.getKey());

                                            taskMap.put("GroupList", groupList);

                                            mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(taskMap);


                                            taskMap.clear();
                                            taskMap.put("groupNumber", 1);

                                            mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(taskMap);


                                            finish();
                                        }


                                        int groupCnt = dataSnapshot.getValue(Integer.class);

                                        for(int i = 0; i < groupCnt; i++)
                                        {
                                            String listID = Integer.toString(i);


                                            mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("GroupList").child(listID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    String value = dataSnapshot.getValue(String.class);


                                                    groupList.add(value);


                                                    if(Integer.parseInt(listID) == (groupCnt - 1))
                                                    {
                                                        groupList.add(reference.getKey());

                                                        taskMap.put("GroupList", groupList);

                                                        mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(taskMap);


                                                        taskMap.clear();
                                                        taskMap.put("groupNumber", groupCnt + 1);

                                                        mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(taskMap);


                                                        finish();
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
                        });


            }
        });

    }

}