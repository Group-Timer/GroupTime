package com.example.grouptimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GroupToDoListActivity extends AppCompatActivity {

    private RecyclerView toDoListRecycleView;

    private Button toDoListPlusButton;

    private Button toDoListSaveButton;

    private RecyclerAdapter RecyclerAdapter;

    private ArrayList<ToDoListItem> toDoListItemArrayList;

    private EditText toDoListEditText;

    private ArrayList<String> toDoListArrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_to_do_list);

        toDoListRecycleView = (RecyclerView) findViewById(R.id.toDoListRecycleView);
        toDoListPlusButton = (Button) findViewById(R.id.toDoListPlusButton);
        toDoListSaveButton = (Button) findViewById(R.id.toDoListSaveButton);

        toDoListArrayList = new ArrayList<>();

        /* initiate adapter */
        RecyclerAdapter = new RecyclerAdapter();

        /* initiate recyclerview */
        toDoListItemArrayList = new ArrayList<>();

        toDoListPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                toDoListRecycleView.setLayoutManager(new LinearLayoutManager(GroupToDoListActivity.this));

                toDoListItemArrayList.add(new ToDoListItem());

                RecyclerAdapter.setToDoListItemArrayList(toDoListItemArrayList);

                toDoListRecycleView.setAdapter(RecyclerAdapter);

            }
        });



        toDoListSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(int i = toDoListItemArrayList.size() - 1 ; i < toDoListItemArrayList.size() ; i++){

                    String toDoList = toDoListItemArrayList.get(i).GetToDoListString();

                    toDoListArrayList.add(toDoList);

                    FirebaseDatabase.getInstance()
                            .getReference().child("Groups").child("-NG93VCgOSyTQPn1zTKK").child("ToDoList").setValue(toDoListArrayList);


                }



            }
        });



    }

}
