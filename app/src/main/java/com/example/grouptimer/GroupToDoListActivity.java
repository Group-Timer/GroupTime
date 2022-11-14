package com.example.grouptimer;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupToDoListActivity extends AppCompatActivity {

    private RecyclerView toDoListRecycleView;

    private Button toDoListPlusButton;

    private Button toDoListSaveButton;

    private RecyclerAdapter RecyclerAdapter;

    private ArrayList<ToDoListItem> toDoListItemArrayList;

    private EditText toDoListEditText;

    private ArrayList<String> toDoListArrayList;

    private ArrayList<Boolean> toDoListCheckBoxArrayList;

    //firebase 에 저장되어있는 총 toDoList 개수
    private int toDoListCnt = 0;

    //plus 버튼 누르기 전의 총 toDoListItemArrayList 의 크기
    private int toDoListPlusButtonBefore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_to_do_list);

        toDoListRecycleView = (RecyclerView) findViewById(R.id.toDoListRecycleView);
        toDoListPlusButton = (Button) findViewById(R.id.toDoListPlusButton);
        toDoListSaveButton = (Button) findViewById(R.id.toDoListSaveButton);

        toDoListArrayList = new ArrayList<>();
        toDoListCheckBoxArrayList =new ArrayList<>();

        /* initiate adapter */
        RecyclerAdapter = new RecyclerAdapter();

        /* initiate recyclerview */
        toDoListItemArrayList = new ArrayList<>();


        FirebaseDatabase.getInstance().getReference()
                .child("Groups").child(DefineValue.Group_ID).child("ToDoListCnt")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue(Integer.class) != null){
                            toDoListCnt = dataSnapshot.getValue(Integer.class);

                            toDoListPlusButtonBefore = toDoListCnt;


                            //firebase 에서 ToDoList 에 있는 값을 가져와서 toDoListArrayList 에 저장
                            for( int i = 0 ; i < toDoListCnt ; i++){

                                int outerIndex = i ;
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Groups").child(DefineValue.Group_ID).child("ToDoList").child(String.valueOf(i))
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                String toDoList = dataSnapshot.getValue(String.class);

                                                toDoListArrayList.add(toDoList);


                                                //firebase 에서 CheckBox 에 있는 값을 가져와서 toDoListCheckBoxArrayList 에 저장
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("Groups").child(DefineValue.Group_ID).child("CheckBox").child(String.valueOf(outerIndex))
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                Boolean checkbox = dataSnapshot.getValue(Boolean.class);

                                                                toDoListCheckBoxArrayList.add(checkbox);


                                                                if(outerIndex == (toDoListCnt - 1))
                                                                {

                                                                    for(int j = 0 ; j < toDoListCnt ;j ++){

                                                                        String toDoList = toDoListArrayList.get(j);
                                                                        Boolean checkBox = toDoListCheckBoxArrayList.get(j);
                                                                        toDoListItemArrayList.add(new ToDoListItem(GroupToDoListActivity.this, toDoList , checkBox));

                                                                    }


                                                                    toDoListRecycleView.setLayoutManager(new LinearLayoutManager(GroupToDoListActivity.this));
                                                                    RecyclerAdapter.setToDoListItemArrayList(toDoListItemArrayList);
                                                                    toDoListRecycleView.setAdapter(RecyclerAdapter);
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }

                                        });

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





        toDoListPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toDoListRecycleView.setLayoutManager(new LinearLayoutManager(GroupToDoListActivity.this));

                toDoListItemArrayList.add(new ToDoListItem(GroupToDoListActivity.this, null , false));

                RecyclerAdapter.setToDoListItemArrayList(toDoListItemArrayList);

                toDoListRecycleView.setAdapter(RecyclerAdapter);

                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                System.out.println(toDoListItemArrayList.size());
                System.out.println(toDoListItemArrayList.get(toDoListItemArrayList.size() - 1 ).GetToDoListString());

            }
        });



        toDoListSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(int i = toDoListPlusButtonBefore ; i < toDoListItemArrayList.size() ; i++){

                    String toDoList = toDoListItemArrayList.get(i).GetToDoListString();

                    System.out.println("********************************");
                    System.out.println(i);
                    System.out.println(toDoList);

                    boolean checkbox = toDoListItemArrayList.get(i).GetToDoListCheckBoxBoolean();

                    toDoListArrayList.add(toDoList);

                    toDoListCheckBoxArrayList.add(checkbox);

                }

                FirebaseDatabase.getInstance()
                        .getReference().child("Groups").child(DefineValue.Group_ID).child("ToDoList").setValue(toDoListArrayList);

                FirebaseDatabase.getInstance()
                        .getReference().child("Groups").child(DefineValue.Group_ID).child("CheckBox").setValue(toDoListCheckBoxArrayList);

                FirebaseDatabase.getInstance()
                        .getReference().child("Groups").child(DefineValue.Group_ID).child("ToDoListCnt").setValue(toDoListItemArrayList.size());





            }
        });



    }
}
