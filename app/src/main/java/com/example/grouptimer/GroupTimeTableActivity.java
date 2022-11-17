package com.example.grouptimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ContentFrameLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class GroupTimeTableActivity extends AppCompatActivity implements View.OnClickListener {


    LinearLayout                        RootLayout;
    ContentFrameLayout.LayoutParams     RootParams;



    String[]                    Hour                    = {"9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
    String[]                    Day                     = {"", "S", "M", "T", "W", "T", "F", "S"};

    GridLayout[]                TimeTableGridLayout     = new GridLayout[DefineValue.Day_Cnt];
    GridLayout                  HourGridLayout;

    Button[][]                  TimeTableButton         = new Button[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];

    public static int[][]       GroupTimeTable          = new int[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];

    public static int[][]        Member_1_TimeTable;
    public static int[][]        Member_2_TimeTable;
    public static int[][]        Member_3_TimeTable;
    public static int[][]        Member_4_TimeTable;
    public static int[][]        Member_5_TimeTable;

    public static Drawable      CustomButtonDrawable;


    private final int           ToDoListButtonID        = 1;
    private final int           ShareButtonID           = 2;
    private final int           ScheduleTimeButtonID    = 3;


    int GroupMemberCnt;
    int MemberCnt;
    int Member;
    int DayOfWeek;
    int LoadCnt;

    ArrayList<String> MemberIDLIst = new ArrayList<String>();

    int[][] MemberTimeTable;


    static boolean GroupMaker;

    static boolean InsertCodeOverlap;


    AlertDialog groupShareDialog;


    String GroupInsertCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Init_GroupTimeTable();


        CustomButtonDrawable = getResources().getDrawable(R.drawable.custom_button);


        InsertCodeOverlap = false;


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        mDatabase.child("Groups").child(DefineValue.Group_ID).child("groupMakerUid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);

                Log.d("GT", "groupMakerUid dataSnapshot : " + value );

                if(user.getUid().equals(value) == true)
                {
                    GroupMaker = true;
                }
                else
                {
                    GroupMaker = false;
                }


                Show_Screen();

                Load_GroupTimeTable();

                Share_Group();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void Init_GroupTimeTable()
    {
        for(int i = 0; i < DefineValue.Day_Cnt; i++)
        {
            for(int k = 0; k < DefineValue.Times_Of_Day; k++)
            {
                GroupTimeTable[i][k] = 0;
            }
        }
    }


    private void Show_Screen()
    {
        Generate_RootLayout();
        Generate_TopLayout();
        Generate_DayLayout();
        Generate_TimeTableLayout();
        Generate_BottomLayout();


        setContentView(RootLayout, RootParams);
    }


    // 최상위 Root 레이아웃 생성
    // ContentFrameLayout Params 적용
    private void Generate_RootLayout()
    {
        RootLayout = new LinearLayout(this);
        RootLayout.setOrientation(LinearLayout.VERTICAL);

        RootParams = new ContentFrameLayout.LayoutParams(ContentFrameLayout.LayoutParams.MATCH_PARENT, ContentFrameLayout.LayoutParams.MATCH_PARENT);
    }


    // 상단 레이아웃 생성
    // Time Table의 상단 부분
    private void Generate_TopLayout()
    {
        LinearLayout topLayout = new LinearLayout(this);
        topLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams topLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        Button toDoListButton = new Button(this);
        toDoListButton.setId(ToDoListButtonID);
        toDoListButton.setText("To Do List");
        toDoListButton.setGravity(Gravity.CENTER);
        toDoListButton.setOnClickListener(this);

        LinearLayout.LayoutParams toDoListParams    = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        toDoListParams.weight                       = 2;
        toDoListParams.setMargins(200, 0, 30, 15);


        Button shareButton = new Button(this);
        shareButton.setId(ShareButtonID);
        shareButton.setText("Share");
        shareButton.setGravity(Gravity.CENTER);
        shareButton.setOnClickListener(this);

        if(GroupMaker == false)
        {
            shareButton.setVisibility(View.INVISIBLE);
        }

        LinearLayout.LayoutParams shareParams   = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        shareParams.weight                      = 1;
        shareParams.setMargins(30, 0, 200, 15);


        topLayout.addView(toDoListButton, toDoListParams);
        topLayout.addView(shareButton, shareParams);

        RootLayout.addView(topLayout, topLayoutParams);
    }


    // 하단 레이아웃 생성
    // Time Table의 하단 부분
    private void Generate_BottomLayout()
    {
        LinearLayout bottomLayout = new LinearLayout(this);

        LinearLayout.LayoutParams bottomLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        Button scheduleTimeButton = new Button(this);
        scheduleTimeButton.setId(ScheduleTimeButtonID);
        scheduleTimeButton.setText("Schedule Time");
        scheduleTimeButton.setGravity(Gravity.CENTER);
        scheduleTimeButton.setOnClickListener(this);

        if(GroupMaker == false)
        {
            scheduleTimeButton.setVisibility(View.INVISIBLE);
        }

        LinearLayout.LayoutParams scheduleParams    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        scheduleParams.setMargins(300, 15, 300, 15);


        bottomLayout.addView(scheduleTimeButton, scheduleParams);

        RootLayout.addView(bottomLayout, bottomLayoutParams);
    }


    // 요일 레이아웃 생성
    private void Generate_DayLayout()
    {
        LinearLayout dayLinearLayout = new LinearLayout(this);
        dayLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        dayLinearLayout.setWeightSum(8);

        LinearLayout.LayoutParams dayLinearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dayLinearParams.setMargins(0, 30, 20, 0);

        for(int i = 0; i < DefineValue.TimeTable_GridLayout_Cnt; i++)
        {
            TextView dayText = new TextView(this);

            dayText.setText(Day[i]);
            dayText.setGravity(Gravity.CENTER_HORIZONTAL);

            LinearLayout.LayoutParams dayParams     = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            dayParams.weight                        = 1;

            dayLinearLayout.addView(dayText, dayParams);
        }

        RootLayout.addView(dayLinearLayout, dayLinearParams);
    }


    // 시간대, Time Table 레이아웃 생성
    private void Generate_TimeTableLayout()
    {
        int buttonID;


        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setWeightSum(8);

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearParams.setMarginEnd(20);


        HourGridLayout = new GridLayout(this);
        HourGridLayout.setOrientation(GridLayout.VERTICAL);
        HourGridLayout.setRowCount(12);

        LinearLayout.LayoutParams hourGridParams    = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        hourGridParams.weight                       = 1;

        for(int i = 0; i < DefineValue.Times_Of_Day; i++)
        {
            TextView hourText = new TextView(this);

            hourText.setText(Hour[i]);
            hourText.setGravity(Gravity.CENTER_HORIZONTAL);

            LinearLayout.LayoutParams hourParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 130);

            HourGridLayout.addView(hourText, hourParams);
        }

        linearLayout.addView(HourGridLayout, hourGridParams);


        // 요일별 세부 레이아웃 생성
        buttonID = 0;
        for( int i = 0; i < DefineValue.Day_Cnt; i++)
        {
            //GridLayout gridLayout = new GridLayout(this);
            TimeTableGridLayout[i] = new GridLayout(this);

            TimeTableGridLayout[i].setOrientation(GridLayout.VERTICAL);
            TimeTableGridLayout[i].setRowCount(12);

            LinearLayout.LayoutParams gridParams    = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            gridParams.weight                       = 1;
            //gridParams.setMarginStart(5);
            //gridParams.setMarginEnd(5);


            // Time Table Button 생성
            for(int k = 0; k < DefineValue.Times_Of_Day; k++)
            {
                //Button button = new Button(this);
                TimeTableButton[i][k] = new Button(this);
                TimeTableButton[i][k].setOnClickListener(new GroupTimeTableOnClickListener());
                TimeTableButton[i][k].setBackground(CustomButtonDrawable);
                //TimeTableButton[i][k].setBackgroundColor(Color.GRAY);
                TimeTableButton[i][k].setId(buttonID);

                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 130);

                TimeTableGridLayout[i].addView(TimeTableButton[i][k], buttonParams);


                buttonID++;
            }


            linearLayout.addView(TimeTableGridLayout[i], gridParams);
        }


        RootLayout.addView(linearLayout, linearParams);
    }


    private void Load_GroupTimeTable()
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        GroupMemberCnt = 0;

        LoadCnt = 0;


        mDatabase.child("Groups").child(DefineValue.Group_ID).child("memberCnt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if( dataSnapshot.getValue(Integer.class) == null)
                {
                    return;
                }


                int value = dataSnapshot.getValue(Integer.class);

                Log.d("GT", "memberCnt dataSnapshot : " + value );


                //groupNumber = Integer.parseInt(manager.groupNumber);
                GroupMemberCnt = value;


                for(MemberCnt = 0; MemberCnt < GroupMemberCnt; MemberCnt++)
                {
                    String listID = Integer.toString(MemberCnt);



                    mDatabase.child("Groups").child(DefineValue.Group_ID).child("groupMember").child(listID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String value = dataSnapshot.getValue(String.class);


                            Log.d("GT", "Member List dataSnapshot : " + value );


                            if(value == null)
                            {
                                return;
                            }


                            MemberIDLIst.add(value);


                            //if(MemberCnt == (GroupMemberCnt - 1))
                            if(MemberIDLIst.size() == GroupMemberCnt)
                            {
                                MemberTimeTable = new int[GroupMemberCnt][DefineValue.Day_Cnt];

                                for(Member = 0; Member < MemberIDLIst.size(); Member++)
                                {
                                    for(DayOfWeek = 0; DayOfWeek < DefineValue.Day_Cnt; DayOfWeek++)
                                    {
                                        int memberIndex = Member;
                                        int dayIndex = DayOfWeek;

                                        String listID;


                                        listID = Integer.toString(dayIndex);


                                        mDatabase.child("PersonalTimeTable").child(MemberIDLIst.get(memberIndex)).child("TimeTable").child(listID).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                int value = dataSnapshot.getValue(Integer.class);


                                                Log.d("GT", "dataSnapshot : " + value);


                                                MemberTimeTable[memberIndex][dayIndex] = value;


                                                LoadCnt++;


                                                if(LoadCnt == (DefineValue.Day_Cnt * GroupMemberCnt))
                                                {
                                                    Show_GroupTimeTable();
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void Show_GroupTimeTable()
    {
        Log.d("GT", "Showing GroupTimeTable");


        for(int i = 0; i < GroupMemberCnt; i++)
        {
            Convert_Time_IntegerToBit(MemberTimeTable[i]);
        }



        for(int buttonID = 0; buttonID < (DefineValue.Day_Cnt * DefineValue.Times_Of_Day); buttonID++)
        {
            int overlapPeople;
            int overlapLevel;
            int day;
            int times;


            day     = buttonID / DefineValue.Times_Of_Day;
            times   = buttonID % DefineValue.Times_Of_Day;

            overlapPeople = GroupTimeTable[day][times];


            ///////
            // 비정상적인 종료 발생함 !!!!!!
            ///////
            overlapLevel = DefineValue.OverlapLevel[overlapPeople][GroupMemberCnt];

            switch(overlapLevel)
            {
                case 0:
                {
                    break;
                }

                case 1:
                {
                    TimeTableButton[day][times].setBackgroundColor(Color.parseColor("#fff0f3"));

                    break;
                }

                case 2:
                {
                    TimeTableButton[day][times].setBackgroundColor(Color.parseColor("#ffccd5"));

                    break;
                }

                case 3:
                {
                    TimeTableButton[day][times].setBackgroundColor(Color.parseColor("#ffb3c1"));

                    break;
                }

                case 4:
                {
                    TimeTableButton[day][times].setBackgroundColor(Color.parseColor("#ff8fa3"));

                    break;
                }

                case 5:
                {
                    TimeTableButton[day][times].setBackgroundColor(Color.parseColor("#ff4d6d"));

                    break;
                }
            }
        }
    }


    private void Convert_Time_IntegerToBit(int[] timeTable)
    {
        //     TimeTable을 int형에서 bit 단위로 변환하는 과정

        //int[][] getTimeTable = new int[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];
        for(int day = 0; day < DefineValue.Day_Cnt; day++)
        {
            int loadTime = timeTable[day];

            //Log.d("GT", "Time : " + loadTime);

            for( int i = DefineValue.Max_Bit_Size - 1; i >= 0; i-- )
            {
                GroupTimeTable[ day ][ i ] += (loadTime & DefineValue.Time_Convert_Key);

                loadTime >>= 1;
            }


            String message = "";
            for( int k = 0; k < DefineValue.Max_Bit_Size; k++ )
            {
                message += GroupTimeTable[day][k];

                if(k == 3)
                {
                    message += " ";
                }
                if(k == 7)
                {
                    message += " ";
                }
            }
            Log.d("GT", "Bit : " + message);
            Log.d("GT", "------------------------------");
        }
    }


    private void Share_Group()
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        mDatabase.child("Groups").child(DefineValue.Group_ID).child("InsertCode").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() == null)
                {
                    String randomCode;


                    while(true)
                    {
                        randomCode = Generate_GroupInsertCode();

                        Check_GroupInsertCode(randomCode);

                        if(InsertCodeOverlap == false)
                        {
                            break;
                        }
                    }


                    GroupInsertCode = randomCode;


                    return;
                }


                String value = dataSnapshot.getValue(String.class);

                GroupInsertCode = value;

                Log.d("GT", "Group Insert Code : " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private String Generate_GroupInsertCode()
    {
        int integerCnt;
        int charCnt;
        int randomCodeCnt;

        String randomCode = "";


        Random typeRandom;
        Random integerRandom;
        Random charRandom;


        typeRandom = new Random();
        typeRandom.setSeed(System.currentTimeMillis());

        integerRandom = new Random();
        integerRandom.setSeed(System.currentTimeMillis());

        charRandom = new Random();
        charRandom.setSeed(System.currentTimeMillis());


        integerCnt      = 0;
        charCnt         = 0;
        randomCodeCnt   = 0;
        while(true)
        {
            int type;


            if(randomCodeCnt == 6)
            {
                break;
            }


            type = typeRandom.nextInt(2);

            if(type == 0)
            {
                // 랜덤 숫자 생성
                // 범위 : 1 ~ 9

                if(integerCnt != 3)
                {
                    int randomValue;


                    randomValue = integerRandom.nextInt(9) + 1;

                    randomCode += Integer.toString(randomValue);


                    randomCodeCnt++;
                    integerCnt++;
                }
            }
            else if(type == 1)
            {
                // 랜덤 알파벳 문자 생성
                // 대문자 범위 : A ~ Z
                // 소문자 범위 : a ~ z

                if(charCnt != 3)
                {
                    int englishType;

                    char randomValue;


                    englishType = typeRandom.nextInt(2);

                    if(englishType == 0)
                    {
                        // 대문자

                        randomValue = (char)(charRandom.nextInt(25) + 65);
                    }
                    else
                    {
                        // 소문자

                        randomValue = (char)(charRandom.nextInt(25) + 97);
                    }


                    randomCode += Character.toString(randomValue);


                    randomCodeCnt++;
                    charCnt++;
                }
            }
        }


        Log.d("GT", "Random Code : " + randomCode);


        return randomCode;
    }


    private void Check_GroupInsertCode(String randomCode)
    {
        Map<String, Object> taskMap = new HashMap<String, Object>();


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        InsertCodeOverlap = false;


        mDatabase.child("GroupInsertCode").child("codeCnt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int codeCnt;

                HashMap<String, String> insertCodeMap = new HashMap<String, String>();


                if(dataSnapshot.getValue() == null)
                {
                    Log.d("GT", "dataSnapshot is null");


                    insertCodeMap.put(randomCode, DefineValue.Group_ID);

                    taskMap.put("CodeList", insertCodeMap);

                    mDatabase.child("GroupInsertCode").updateChildren(taskMap);


                    taskMap.clear();
                    taskMap.put("codeCnt", 1);

                    mDatabase.child("GroupInsertCode").updateChildren(taskMap);


                    taskMap.clear();
                    taskMap.put("InsertCode", randomCode);

                    mDatabase.child("Groups").child(DefineValue.Group_ID).updateChildren(taskMap);


                    return;
                }


                codeCnt = dataSnapshot.getValue(Integer.class);


                mDatabase.child("GroupInsertCode").child("CodeList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        HashMap<String, String> groupMap = new HashMap<String, String>();


                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                        {
                            if(childSnapshot != null)
                            {
                                Log.d("GT", "HashMap Key : " + childSnapshot.getKey());
                                Log.d("GT", "HashMap Value : " + childSnapshot.getValue());


                                groupMap.put(childSnapshot.getKey(), childSnapshot.getValue().toString());
                            }
                        }





                        groupMap.put(randomCode, DefineValue.Group_ID);


                        taskMap.put("CodeList", groupMap);

                        mDatabase.child("GroupInsertCode").updateChildren(taskMap);


                        taskMap.clear();
                        taskMap.put("codeCnt", codeCnt + 1);

                        mDatabase.child("GroupInsertCode").updateChildren(taskMap);


                        taskMap.clear();
                        taskMap.put("InsertCode", randomCode);

                        mDatabase.child("Groups").child(DefineValue.Group_ID).updateChildren(taskMap);
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


    private void Generate_InsertCode_Dialog()
    {
        AlertDialog.Builder builder;

        LayoutInflater inflater;

        View customDialogView;

        DialogInterface.OnClickListener dialogClickListener;


        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(dialogInterface == groupShareDialog && i == DialogInterface.BUTTON_POSITIVE)
                {

                }
                else if(dialogInterface == groupShareDialog && i == DialogInterface.BUTTON_NEGATIVE)
                {

                }
            }
        };


        builder = new AlertDialog.Builder(this);

        //inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        //customDialogView = inflater.inflate(R.layout.custom_dialog_insertcode_share_layout, null);

        //TextView textView = (TextView) findViewById(R.id.insertCode);
        //textView.setText(GroupInsertCode);


        //builder.setView(customDialogView);
        builder.setMessage(GroupInsertCode);
        builder.setPositiveButton("공유", dialogClickListener);
        builder.setNegativeButton("확인", dialogClickListener);

        groupShareDialog = builder.create();
        groupShareDialog.show();
    }


    @Override
    public void onClick(View view)
    {
        if(view.getId() == ToDoListButtonID)
        {
            startActivity(new Intent(this, GroupToDoListActivity.class));
        }
        else if(view.getId() == ShareButtonID)
        {
            //Share_Group();

            if(GroupInsertCode == null)
            {
                return;
            }

            Generate_InsertCode_Dialog();
        }
        else if(view.getId() == ScheduleTimeButtonID)
        {

        }
    }
}