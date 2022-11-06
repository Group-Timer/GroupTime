package com.example.grouptimer;


        import androidx.annotation.NonNull;
        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.TextView;

        import com.example.grouptimer.model.Group;
        import com.example.grouptimer.model.User;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.List;

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
        purposeList.add("약속1");
        purposeList.add("약속2");
        purposeList.add("약속3");

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

                if (groupNameEditText.getText().toString() == null || howManyEditText.getText().toString() ==null ||
                        selectTextView.getText().toString() == null){
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
                                group.groupMakerUid = task.getResult().getUser().getUid();


                                FirebaseDatabase.getInstance()
                                        .getReference().child("Groups").push().setValue(group);


                                startActivity(new Intent(MakeGroupActivity.this, EmptyActivity.class));


                            }
                        });


            }
        });

    }

}