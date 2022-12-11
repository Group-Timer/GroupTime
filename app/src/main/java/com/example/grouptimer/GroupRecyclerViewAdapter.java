package com.example.grouptimer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewHolder>
{
    FirebaseUser user;
    DatabaseReference databaseReference;


    private ArrayList<String> AdapterIDList;
    private ArrayList<String> AdapterNameList;

    private ViewGroup parent;


    private ArrayList<String> toDoListTextList;
    private ArrayList<Boolean> toDoListCheckBoxList;


    int startDateValue;
    int startTimeValue;
    int endDateValue;
    int endTimeTavlue;


    private ArrayList<String> GroupMemberList;
    private int MemberCnt;
    private int GroupNumber;


    AlertDialog dialog = null;
    ProgressDialog progressDialog = null;

    boolean GroupMakerCheck;


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

        viewHolder.GroupName = name;
        viewHolder.GroupNameText.setText(name);
        viewHolder.GroupIDText = id;


        viewHolder.GroupNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference().child("Groups").child(viewHolder.GroupIDText).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue() == null)
                        {
                            AlertDialog noticeDialog;
                            AlertDialog.Builder builder;


                            builder = new AlertDialog.Builder(parent.getContext());

                            builder.setMessage("그룹 생성자가 그룹을 삭제했습니다");
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Load_User_GroupNumber(viewHolder.GroupIDText, true);
                                }
                            });


                            noticeDialog = builder.create();
                            noticeDialog.setCancelable(false);
                            noticeDialog.setCanceledOnTouchOutside(false);

                            noticeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {

                                    noticeDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                }
                            });

                            noticeDialog.show();


                            return;
                        }


                        DefineValue.Group_ID = viewHolder.GroupIDText;

                        Log.d("GT", "Select Group ID : " + DefineValue.Group_ID);

                        DefineValue.Group_Name = viewHolder.GroupNameText.getText().toString();

                        parent.getContext().startActivity(new Intent(parent.getContext(), GroupTimeTableActivity.class));
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });


        viewHolder.GroupNameText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Group_LongClick(viewHolder.GroupIDText, viewHolder.GroupName);


                return false;
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


                                            endDateValue = snapshot.getValue(Integer.class);


                                            FirebaseDatabase.getInstance().getReference().child("Groups").child(id).child("ScheduleEndTime").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String scheduleText;


                                                    endTimeTavlue = 0;


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

                                                        if(Integer.toString(endTimeTavlue).length() < 2)
                                                        {
                                                            scheduleText += "0" + " : ";
                                                            scheduleText += "0" + Integer.toString(endTimeTavlue).substring(0, 1);
                                                        }
                                                        else if(Integer.toString(endTimeTavlue).length() < 3)
                                                        {
                                                            scheduleText += "0" + " : ";
                                                            scheduleText += Integer.toString(endTimeTavlue).substring(0, 2);
                                                        }
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
                                                        ViewPager2RecyclerAdapter pagerAdapter = new ViewPager2RecyclerAdapter(toDoListTextList, toDoListCheckBoxList);

                                                        viewHolder.ToDoListViewPagers.setAdapter(pagerAdapter);
                                                        viewHolder.ToDoListViewPagers.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);


                                                        viewHolder.ToDoListViewPagers.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                                            @Override
                                                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                                                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                                                                if (positionOffsetPixels == 0) {
                                                                    viewHolder.ToDoListViewPagers.setCurrentItem(position);
                                                                }
                                                            }

                                                            @Override
                                                            public void onPageSelected(int position) {
                                                                super.onPageSelected(position);
                                                            }
                                                        });


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


    private void Group_LongClick(String groupID, String groupName)
    {
        user = FirebaseAuth.getInstance().getCurrentUser();



        Show_LongClickDialog(groupID, groupName);
    }


    private void Show_LongClickDialog(String groupID, String groupName)
    {
        AlertDialog.Builder builder;


        builder = new AlertDialog.Builder(parent.getContext());

        builder.setTitle(groupName);
        builder.setMessage("\n그룹을 나가시겠습니까?\n\n그룹 생성자일 경우 다른 그룹 멤버들도 그룹을 나가게 됩니다\n\n또한, 채팅 기록도 사라지게 됩니다\n");
        builder.setPositiveButton("나가기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                progressDialog = new ProgressDialog(parent.getContext(), R.style.ProgressDialogTheme);

                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("그룹 나가는중 ...");


                if(dialog != null)
                {
                    dialog.dismiss();

                    dialog = null;
                }


                progressDialog.show();


                Load_GroupMaker(groupID);
            }
        });

        builder.setNegativeButton("취소", null);


        dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });


        dialog.show();
    }


    private void Load_GroupMaker(String groupID)
    {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupID).child("groupMakerUid");

        GroupMakerCheck = false;


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);


                if(user.getUid().equals(value) == true)
                {
                    GroupMakerCheck = true;
                }


                Load_MemberCnt(groupID);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void Load_MemberCnt(String groupID)
    {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupID).child("memberCnt");

        MemberCnt = 0;


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int value = dataSnapshot.getValue(Integer.class);


                MemberCnt = value;

                Load_GroupMemberList(groupID);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void Load_GroupMemberList(String groupID)
    {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupID).child("groupMember");

        GroupMemberList = new ArrayList<String>();


        for(int i = 0; i < MemberCnt; i++)
        {
            String position = Integer.toString(i);


            databaseReference.child(position).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String value = dataSnapshot.getValue(String.class);


                    GroupMemberList.add(value);

                    if(Integer.parseInt(position) == (MemberCnt - 1))
                    {
                        Load_User_GroupNumber(groupID, false);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }


    private void Load_User_GroupNumber(String groupID, boolean reload)
    {
        if(reload == true)
        {
            user = FirebaseAuth.getInstance().getCurrentUser();
        }


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("groupNumber");

        GroupNumber = 0;


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int value = dataSnapshot.getValue(Integer.class);


                GroupNumber = value;


                if(reload == true)
                {
                    Map<String, Object> taskMap = new HashMap<String, Object>();


                    String userID = user.getUid();

                    databaseReference = FirebaseDatabase.getInstance().getReference();


                    for(int i = 0; i < AdapterIDList.size(); i++)
                    {
                        if(AdapterIDList.get(i).equals(groupID) == true)
                        {
                            AdapterIDList.remove(i);
                            AdapterNameList.remove(i);

                            break;
                        }
                    }

                    GroupNumber--;


                    taskMap.put("groupNumber", GroupNumber);
                    databaseReference.child("Users").child(userID).updateChildren(taskMap);
                    taskMap.clear();


                    taskMap.put("GroupList", AdapterIDList);
                    databaseReference.child("Users").child(userID).updateChildren(taskMap);
                    taskMap.clear();


                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


                    ((HomeActivity)HomeActivity.context).Load_GroupList(false, true, null, user, mDatabase);
                }
                else
                {
                    Exit_Group(groupID);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void Exit_Group(String groupID)
    {
        Map<String, Object> taskMap = new HashMap<String, Object>();


        String userID = user.getUid();


        databaseReference = FirebaseDatabase.getInstance().getReference();


        for(int i = 0; i < AdapterIDList.size(); i++)
        {
            if(AdapterIDList.get(i).equals(groupID) == true)
            {
                AdapterIDList.remove(i);
                AdapterNameList.remove(i);

                break;
            }
        }

        for(int i = 0; i < MemberCnt; i++)
        {
            if(GroupMemberList.get(i).equals(userID) == true)
            {
                GroupMemberList.remove(i);

                break;
            }
        }

        MemberCnt--;
        GroupNumber--;


        taskMap.put("memberCnt", MemberCnt);
        databaseReference.child("Groups").child(groupID).updateChildren(taskMap);
        taskMap.clear();


        taskMap.put("groupMember", GroupMemberList);
        databaseReference.child("Groups").child(groupID).updateChildren(taskMap);
        taskMap.clear();


        taskMap.put("groupNumber", GroupNumber);
        databaseReference.child("Users").child(userID).updateChildren(taskMap);
        taskMap.clear();


        taskMap.put("GroupList", AdapterIDList);
        databaseReference.child("Users").child(userID).updateChildren(taskMap);
        taskMap.clear();


        if(GroupMakerCheck == true)
        {
            databaseReference.child("Groups").child(groupID).removeValue();
            databaseReference.child("Chat").child("Messages").child(groupID).removeValue();


            Log.d("GT", "Group : " + groupID);
            Log.d("GT", "Group Maker : Delete group");
        }


        if(progressDialog != null)
        {
            progressDialog.dismiss();

            progressDialog = null;
        }


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        ((HomeActivity)HomeActivity.context).Load_GroupList(false, true, null, user, mDatabase);
    }
}
