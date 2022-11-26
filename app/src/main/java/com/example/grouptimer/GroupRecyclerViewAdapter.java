package com.example.grouptimer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import androidx.viewpager2.widget.ViewPager2;

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

    //private boolean ExpansionButtonClick;


    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private GroupToDoListRecyclerViewAdapter recyclerViewAdapter;

    private ArrayList<String> toDoListTextList;
    private ArrayList<Boolean> toDoListCheckBoxList;


    //ViewPager2 viewPager2;
    //ViewPager2RecyclerAdapter pagerAdapter;


    int startDateValue;
    int startTimeValue;
    int endDateValue;
    int endTimeTavlue;


    public GroupRecyclerViewAdapter(ArrayList<String> idList, ArrayList<String> nameList)
    {
        Log.d("GT", "id dataList size : " + idList.size());
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
    public void onBindViewHolder(@NonNull GroupRecyclerViewHolder viewHolder, @SuppressLint("RecyclerView") int position)
    {
        String name = this.AdapterNameList.get(position);
        String id = this.AdapterIDList.get(position);


        viewHolder.ExpansionButtonClick = false;


        if(position < AdapterNameList.size() - 1)
        {
            viewHolder.GroupNameText.setBackgroundResource(R.drawable.group_list_recycler_item);
        }

        viewHolder.GroupNameText.setText(name);
        viewHolder.GroupIDText = id;
        //viewHolder.ToDoListRecyclerView.setVisibility(View.GONE);

        //viewPager2 = parent.findViewById(R.id.viewPager);
        //viewHolder.ToDoListViewPagers.setVisibility(View.GONE);
        //viewPager2.setVisibility(View.GONE);


        viewHolder.GroupNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //DefineValue.Group_ID = viewHolder.GroupNameText.getText().toString();
                //DefineValue.Group_ID = AdapterIDList.get(viewHolder.getAdapterPosition());
                DefineValue.Group_ID = viewHolder.GroupIDText;

                Log.d("GT", "Select Group ID : " + DefineValue.Group_ID);

                DefineValue.Group_Name = viewHolder.GroupNameText.getText().toString();

                parent.getContext().startActivity(new Intent(parent.getContext(), GroupTimeTableActivity.class));
            }
        });


        viewHolder.ExpansionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("GT", viewHolder.GroupNameText.getText() + " : Expansion button pressed");


                if(viewHolder.ExpansionButtonClick == true)
                {
                    viewHolder.ExpansionButtonClick = false;


                    viewHolder.ExpansionButton.setText("+");
                    //viewHolder.ToDoListRecyclerView.setVisibility(View.GONE);

                    viewHolder.ToDoListProgressBar.setBackground(null);
                    viewHolder.ToDoListViewPagers.setBackground(null);
                    viewHolder.EmptyText.setBackground(null);

                    viewHolder.ToDoListProgressBar.setVisibility(View.GONE);
                    viewHolder.GroupScheduleTimeText.setVisibility(View.GONE);
                    viewHolder.ToDoListViewPagers.setVisibility(View.GONE);
                    viewHolder.EmptyText.setVisibility(View.GONE);

                    if(position < AdapterNameList.size() - 1)
                    {
                        viewHolder.GroupNameText.setBackgroundResource(R.drawable.group_list_recycler_item);
                    }
                }
                else
                {
                    viewHolder.ExpansionButtonClick = true;

                    viewHolder.ExpansionButton.setText("-");


                    if(position < AdapterNameList.size() - 1)
                    {
                        viewHolder.ToDoListProgressBar.setBackgroundResource(R.drawable.group_list_recycler_item);
                    }
                    viewHolder.GroupNameText.setBackground(null);
                    viewHolder.ToDoListProgressBar.setVisibility(View.VISIBLE);
                    viewHolder.GroupScheduleTimeText.setVisibility(View.GONE);
                    viewHolder.ToDoListViewPagers.setVisibility(View.GONE);
                    viewHolder.EmptyText.setVisibility(View.GONE);


                    toDoListTextList.clear();
                    toDoListCheckBoxList.clear();


                    FirebaseDatabase.getInstance().getReference().child("Groups").child(id).child("ScheduleStartDate").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            startDateValue = 0;

                            if(snapshot.getValue() == null || snapshot.getValue(Integer.class) == 0)
                            {
                                viewHolder.GroupScheduleTimeText.setVisibility(View.GONE);


                                return;
                            }

                            startDateValue = snapshot.getValue(Integer.class);


                            FirebaseDatabase.getInstance().getReference().child("Groups").child(id).child("ScheduleStartTime").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    startTimeValue = 0;

                                    if(snapshot.getValue() == null || snapshot.getValue(Integer.class) == 0)
                                    {
                                        viewHolder.GroupScheduleTimeText.setVisibility(View.GONE);


                                        return;
                                    }

                                    startTimeValue = snapshot.getValue(Integer.class);


                                    FirebaseDatabase.getInstance().getReference().child("Groups").child(id).child("ScheduleEndDate").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            endDateValue = 0;

                                            /*
                                            if(snapshot.getValue() == null || snapshot.getValue(Integer.class) == 0)
                                            {
                                                viewHolder.GroupScheduleTimeText.setVisibility(View.GONE);


                                                return;
                                            }

                                             */


                                            endDateValue = snapshot.getValue(Integer.class);


                                            FirebaseDatabase.getInstance().getReference().child("Groups").child(id).child("ScheduleEndTime").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String scheduleText;


                                                    endTimeTavlue = 0;

                                                    /*
                                                    if(snapshot.getValue() == null || snapshot.getValue(Integer.class) == 0)
                                                    {
                                                        viewHolder.GroupScheduleTimeText.setVisibility(View.GONE);


                                                        return;
                                                    }

                                                     */

                                                    endTimeTavlue = snapshot.getValue(Integer.class);


                                                    scheduleText = Integer.toString(startDateValue).substring(0, 4) + " / ";
                                                    scheduleText += Integer.toString(startDateValue).substring(4, 6) + " / ";
                                                    scheduleText += Integer.toString(startDateValue).substring(6, 8) + "  -  ";

                                                    if(Integer.toString(startTimeValue).length() < 2)
                                                    {
                                                        scheduleText += "0" + " : ";
                                                        scheduleText += "0" + Integer.toString(startTimeValue).substring(0, 1);
                                                    }
                                                    else if(Integer.toString(startTimeValue).length() < 3)
                                                    {
                                                        scheduleText += "0" + " : ";
                                                        scheduleText += Integer.toString(startTimeValue).substring(0, 2);
                                                    }
                                                    else if(Integer.toString(startTimeValue).length() < 4)
                                                    {
                                                        scheduleText += Integer.toString(startTimeValue).substring(0, 1) + " : ";
                                                        scheduleText += Integer.toString(startTimeValue).substring(1, 3);
                                                    }
                                                    else
                                                    {
                                                        scheduleText += Integer.toString(startTimeValue).substring(0, 2) + " : ";
                                                        scheduleText += Integer.toString(startTimeValue).substring(2, 4);
                                                    }

                                                    if(endDateValue != 0)
                                                    {
                                                        scheduleText += "\n\n";

                                                        scheduleText += Integer.toString(endDateValue).substring(0, 4) + " / ";
                                                        scheduleText += Integer.toString(endDateValue).substring(4, 6) + " / ";
                                                        scheduleText += Integer.toString(endDateValue).substring(6, 8) + "  -  ";

                                                        if(Integer.toString(endTimeTavlue).length() < 4)
                                                        {
                                                            scheduleText += Integer.toString(endTimeTavlue).substring(0, 1) + " : ";
                                                            scheduleText += Integer.toString(endTimeTavlue).substring(1, 3);
                                                        }
                                                        else
                                                        {
                                                            scheduleText += Integer.toString(endTimeTavlue).substring(0, 2) + " : ";
                                                            scheduleText += Integer.toString(endTimeTavlue).substring(2, 4);
                                                        }
                                                    }


                                                    viewHolder.GroupScheduleTimeText.setText(scheduleText);
                                                    viewHolder.GroupScheduleTimeText.setVisibility(View.VISIBLE);
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

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


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

                                                        // RecyclerView
                                                        /*
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

                                                         */



                                                        // ViewPager2
                                                        ViewPager2RecyclerAdapter pagerAdapter = new ViewPager2RecyclerAdapter(toDoListTextList, toDoListCheckBoxList);

                                                        //viewPager2.setAdapter(pagerAdapter);
                                                        //viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
                                                        viewHolder.ToDoListViewPagers.setAdapter(pagerAdapter);
                                                        viewHolder.ToDoListViewPagers.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

                                                        //viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                                        viewHolder.ToDoListViewPagers.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                                            @Override
                                                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                                                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                                                                if (positionOffsetPixels == 0) {
                                                                    viewHolder.ToDoListViewPagers.setCurrentItem(position);
                                                                    //viewPager2.setCurrentItem(position);
                                                                }
                                                            }

                                                            @Override
                                                            public void onPageSelected(int position) {
                                                                super.onPageSelected(position);
                                                            }
                                                        });

                                                        //viewPager2.setVisibility(View.VISIBLE);
                                                        if(position < AdapterNameList.size() - 1)
                                                        {
                                                            viewHolder.ToDoListViewPagers.setBackgroundResource(R.drawable.group_list_recycler_item);
                                                        }

                                                        viewHolder.ToDoListProgressBar.setBackground(null);
                                                        viewHolder.ToDoListProgressBar.setVisibility(View.GONE);
                                                        viewHolder.ToDoListViewPagers.setVisibility(View.VISIBLE);
                                                        viewHolder.EmptyText.setVisibility(View.GONE);


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
                                if(position < AdapterNameList.size() - 1)
                                {
                                    viewHolder.EmptyText.setBackgroundResource(R.drawable.group_list_recycler_item);
                                }

                                viewHolder.ToDoListProgressBar.setBackground(null);
                                viewHolder.ToDoListProgressBar.setVisibility(View.GONE);
                                viewHolder.ToDoListViewPagers.setVisibility(View.GONE);
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
