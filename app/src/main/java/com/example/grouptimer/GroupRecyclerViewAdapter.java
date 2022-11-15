package com.example.grouptimer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewHolder>
{
    private ArrayList<String> AdapterIDList;
    private ArrayList<String> AdapterNameList;

    private ViewGroup parent;

    private boolean ExpansionButtonClick;


    private GridLayoutManager gridLayoutManager;
    private GroupToDoListRecyclerViewAdapter recyclerViewAdapter;

    private ArrayList<String> toDoListTextList;
    private ArrayList<Boolean> toDoListCheckBoxList;


    public GroupRecyclerViewAdapter(ArrayList<String> idList, ArrayList<String> nameList)
    {
        Log.d("GT", "id ataList size : " + idList.size());
        Log.d("GT", "name dataList size : " + nameList.size());

        this.AdapterIDList = idList;
        this.AdapterNameList = nameList;
    }

    @NonNull
    @Override
    public GroupRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        this.parent = parent;


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_recyclerview_item, parent, false);

        GroupRecyclerViewHolder viewHolder = new GroupRecyclerViewHolder(view);


        toDoListTextList = new ArrayList<String>();
        toDoListCheckBoxList = new ArrayList<Boolean>();


        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull GroupRecyclerViewHolder viewHolder, int position)
    {
        String name = this.AdapterNameList.get(position);
        String id = this.AdapterIDList.get(position);


        ExpansionButtonClick = false;


        viewHolder.GroupNameText.setText(name);
        viewHolder.GroupIDText = id;
        viewHolder.ToDoListRecyclerView.setVisibility(View.GONE);


        viewHolder.GroupNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //DefineValue.Group_ID = viewHolder.GroupNameText.getText().toString();
                //DefineValue.Group_ID = AdapterIDList.get(viewHolder.getAdapterPosition());
                DefineValue.Group_ID = viewHolder.GroupIDText;

                Log.d("GT", "Select Group ID : " + DefineValue.Group_ID);

                parent.getContext().startActivity(new Intent(parent.getContext(), GroupTimeTableActivity.class));
            }
        });


        viewHolder.ExpansionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("GT", viewHolder.GroupNameText.getText() + " : Expansion button pressed");


                if(ExpansionButtonClick == true)
                {
                    ExpansionButtonClick = false;


                    viewHolder.ExpansionButton.setText("+");
                    viewHolder.ToDoListRecyclerView.setVisibility(View.GONE);
                    viewHolder.EmptyText.setVisibility(View.GONE);
                }
                else
                {
                    ExpansionButtonClick = true;

                    viewHolder.ExpansionButton.setText("-");


                    toDoListTextList.clear();
                    toDoListCheckBoxList.clear();


                    FirebaseDatabase.getInstance().getReference().child("Groups").child(id).child("ToDoListCnt").addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if(dataSnapshot.getValue(Integer.class) != null)
                            {
                                int toDoListCnt = dataSnapshot.getValue(Integer.class);


                                for( int i = 0 ; i < toDoListCnt ; i++)
                                {
                                    int listIndex = i ;
                                    FirebaseDatabase.getInstance().getReference().child("Groups").child(id).child("ToDoList").child(String.valueOf(listIndex)).addListenerForSingleValueEvent(new ValueEventListener()
                                    {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {

                                            String toDoList = dataSnapshot.getValue(String.class);

                                            toDoListTextList.add(toDoList);


                                            FirebaseDatabase.getInstance().getReference().child("Groups").child(id).child("CheckBox").child(String.valueOf(listIndex)).addListenerForSingleValueEvent(new ValueEventListener()
                                            {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot)
                                                {

                                                    Boolean checkbox = dataSnapshot.getValue(Boolean.class);

                                                    toDoListCheckBoxList.add(checkbox);


                                                    if(listIndex == (toDoListCnt - 1))
                                                    {
                                                        gridLayoutManager = new GridLayoutManager(parent.getContext(), 3, GridLayoutManager.HORIZONTAL, false);
                                                        if(gridLayoutManager == null)
                                                        {
                                                            Log.d("GT", "LayoutManager is null");
                                                        }
                                                        viewHolder.ToDoListRecyclerView.setLayoutManager(gridLayoutManager);

                                                        recyclerViewAdapter = new GroupToDoListRecyclerViewAdapter(toDoListTextList, toDoListCheckBoxList);
                                                        viewHolder.ToDoListRecyclerView.setAdapter(recyclerViewAdapter);

                                                        //viewHolder.ExpansionButton.setText("-");
                                                        viewHolder.ToDoListRecyclerView.setVisibility(View.VISIBLE);
                                                        //viewHolder.ToDoListRecyclerView.setNestedScrollingEnabled(false);



                                                        for(int i = 0; i < toDoListTextList.size(); i++)
                                                        {
                                                            Log.d("GT", "Text List " + i + " : " + toDoListTextList.get(i));
                                                            Log.d("GT", "Check List " + i + " : " + toDoListCheckBoxList.get(i));
                                                        }
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
                            else
                            {
                                viewHolder.EmptyText.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return this.AdapterIDList.size();
    }
}
