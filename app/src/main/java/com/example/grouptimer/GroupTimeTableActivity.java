package com.example.grouptimer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ContentFrameLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class GroupTimeTableActivity extends AppCompatActivity implements View.OnClickListener {


    public static Context context;


    LinearLayout                        RootLayout;
    ContentFrameLayout.LayoutParams     RootParams;


    boolean scheduleChecked;
    boolean endTimeChecked;

    AlertDialog customPickerDialog;

    private TextView StartDateTextView;
    private TextView StartTimeTextView;
    private TextView EndDateTextView;
    private TextView EndTimeTextView;

    int StartDateValue;
    int StartTimeValue;
    int EndDateValue;
    int EndTimeValue;

    boolean StartDateChecked;
    boolean StartTimeChecked;
    boolean EndDateChecked;
    boolean EndTimeChecked;


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
    private final int           ChattingButtonID        = 4;


    static int GroupMemberCnt;
    int MemberCnt;
    int Member;
    int DayOfWeek;
    int LoadCnt;

    static ArrayList<String> MemberIDList = new ArrayList<String>();
    static ArrayList<String> MemberNameList = new ArrayList<String>();

    static Map<String, String> MemberInfo = new HashMap<String, String>();

    int[][] MemberTimeTable;


    static boolean GroupMaker;

    static boolean InsertCodeOverlap;


    AlertDialog groupShareDialog;


    String GroupInsertCode;


    ActivityResultLauncher<Intent> requestLauncher;


    //public final Cursor query(Uri url, String[] projection, String selection, String[] selectionArgs, String sortOrder)


    DatePickerDialog datePickerDialog = null;
    TimePickerDialog timePickerDialog = null;

    private String dateText = null;
    private String timeText = null;
    private String ScheduleTimeText = null;

    static LayoutInflater inflater;


    ProgressDialog progressDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        context = this;


        CustomButtonDrawable = getResources().getDrawable(R.drawable.custom_button);

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        InsertCodeOverlap = false;


        StartDateValue = 0;
        StartTimeValue = 0;
        EndDateValue = 0;
        EndTimeValue = 0;


        requestLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
        {
            @Override
            public void onActivityResult(ActivityResult result) {

                if(result == null)
                {
                    return;
                }

                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();

                    String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
                    Cursor cursor = getContentResolver().query(result.getData().getData(), projection, null, null, null);

                    if(cursor.moveToFirst() == true)
                    {
                        String name = cursor.getString(0);
                        String phone = cursor.getString(1);

                        Log.d("GT", "Name, Phone : " + name + ", " + phone);


                        Send_SMS(phone);
                    }
                }
            }
        });
    }


    @Override
    public void onResume()
    {
        super.onResume();


        progressDialog = new ProgressDialog(GroupTimeTableActivity.this, R.style.ProgressDialogTheme);

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Loading ...");
                progressDialog.setCancelable(false);

                progressDialog.show();
            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        Init_GroupTimeTable();


        mDatabase.child("Groups").child(DefineValue.Group_ID).child("groupMakerUid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot == null)
                {
                    return;
                }

                if(dataSnapshot.getValue(String.class) == null)
                {
                    return;
                }

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
        RootParams.setMargins(0,20,20,20);
    }


    // 상단 레이아웃 생성
    // Time Table의 상단 부분
    private void Generate_TopLayout()
    {
        LinearLayout topLayout = new LinearLayout(this);
        topLayout.setOrientation(LinearLayout.HORIZONTAL);
        topLayout.setWeightSum(5);
        topLayout.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams topLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        topLayoutParams.setMargins(20, 40, 20, 20);
        topLayoutParams.gravity = Gravity.CENTER;


        Button scheduleStartTimeButton = new Button(this);
        scheduleStartTimeButton.setId(ScheduleTimeButtonID);
        scheduleStartTimeButton.setText("Schedule Time");
        scheduleStartTimeButton.setGravity(Gravity.CENTER);
        scheduleStartTimeButton.setBackgroundResource(R.drawable.small_button);
        scheduleStartTimeButton.setOnClickListener(this);


        LinearLayout.LayoutParams scheduleStartParams       = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        scheduleStartParams.weight                          = 2;
        scheduleStartParams.setMargins(0, 5, 0, 5);


        TextView tempTextView = new Button(this);
        tempTextView.setVisibility(View.INVISIBLE);

        LinearLayout.LayoutParams tempTextParams    = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        tempTextParams.weight                       = 0.5f;


        Button shareButton = new Button(this);
        shareButton.setId(ShareButtonID);
        shareButton.setText("Share");
        shareButton.setGravity(Gravity.CENTER);
        shareButton.setBackgroundResource(R.drawable.small_button);
        shareButton.setOnClickListener(this);


        LinearLayout.LayoutParams shareParams   = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        shareParams.weight                      = 1.5f;
        shareParams.setMargins(0, 5, 0, 5);
        //shareParams.setMargins(30, 15, 20, 15);


        if(GroupMaker == false)
        {
            shareButton.setVisibility(View.INVISIBLE);
        }

        if(GroupMaker == false)
        {
            scheduleStartTimeButton.setVisibility(View.INVISIBLE);
        }


        topLayout.addView(scheduleStartTimeButton, scheduleStartParams);
        topLayout.addView(tempTextView, tempTextParams);
        topLayout.addView(shareButton, shareParams);

        RootLayout.addView(topLayout, topLayoutParams);
    }


    // 하단 레이아웃 생성
    // Time Table의 하단 부분
    private void Generate_BottomLayout()
    {
        LinearLayout bottomLayout = new LinearLayout(this);

        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);


        LinearLayout.LayoutParams bottomLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        Button toDoListButton = new Button(this);
        toDoListButton.setId(ToDoListButtonID);
        toDoListButton.setText("To Do List");
        toDoListButton.setGravity(Gravity.CENTER);
        toDoListButton.setOnClickListener(this);

        LinearLayout.LayoutParams toDoListParams    = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        toDoListParams.weight                       = 2;
        toDoListParams.setMargins(200, 0, 30, 15);


        Button chattingButton = new Button(this);
        chattingButton.setId(ChattingButtonID);
        chattingButton.setText("Chatting");
        chattingButton.setGravity(Gravity.CENTER);
        chattingButton.setOnClickListener(this);


        LinearLayout.LayoutParams chattingParams    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //scheduleParams.setMargins(300, 15, 300, 15);


        bottomLayout.addView(toDoListButton, toDoListParams);
        bottomLayout.addView(chattingButton, chattingParams);


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


        MemberIDList.clear();

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


                for(int i = 0; i < GroupMemberCnt; i++)
                {
                    if(i == 0)
                        Member_1_TimeTable = new int[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];
                    else if(i == 1)
                        Member_2_TimeTable = new int[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];
                    else if(i == 2)
                        Member_3_TimeTable = new int[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];
                    else if(i == 3)
                        Member_4_TimeTable = new int[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];
                    else if(i == 4)
                        Member_5_TimeTable = new int[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];
                }


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


                            MemberIDList.add(value);


                            //if(MemberCnt == (GroupMemberCnt - 1))
                            if(MemberIDList.size() == GroupMemberCnt)
                            {
                                MemberTimeTable = new int[GroupMemberCnt][DefineValue.Day_Cnt];

                                for(Member = 0; Member < MemberIDList.size(); Member++)
                                {
                                    for(DayOfWeek = 0; DayOfWeek < DefineValue.Day_Cnt; DayOfWeek++)
                                    {
                                        int memberIndex = Member;
                                        int dayIndex = DayOfWeek;

                                        String listID;


                                        listID = Integer.toString(dayIndex);


                                        mDatabase.child("PersonalTimeTable").child(MemberIDList.get(memberIndex)).child("TimeTable").child(listID).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if(dataSnapshot == null)
                                                {
                                                    MemberTimeTable[memberIndex][dayIndex] = 0;
                                                }
                                                else if(dataSnapshot.getValue(Integer.class) == null)
                                                {
                                                    MemberTimeTable[memberIndex][dayIndex] = 0;
                                                }
                                                else
                                                {
                                                    int value = dataSnapshot.getValue(Integer.class);


                                                    Log.d("GT", "dataSnapshot : " + value);


                                                    MemberTimeTable[memberIndex][dayIndex] = value;
                                                }


                                                LoadCnt++;

                                                if(LoadCnt == (DefineValue.Day_Cnt * GroupMemberCnt))
                                                {
                                                    Show_GroupTimeTable();


                                                    if(progressDialog != null)
                                                    {
                                                        progressDialog.dismiss();
                                                        progressDialog = null;
                                                    }
                                                }


                                                if((memberIndex == MemberIDList.size() - 1) && (dayIndex == DefineValue.Day_Cnt -1) )
                                                {
                                                    for(int i = 0; i < MemberIDList.size(); i++)
                                                    {
                                                        int index = i;

                                                        mDatabase.child("Users").child(MemberIDList.get(index)).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                if(dataSnapshot.getValue() == null)
                                                                {
                                                                    return;
                                                                }

                                                                String value = dataSnapshot.getValue(String.class);

                                                                MemberNameList.add(value);


                                                                if(MemberNameList.size() == MemberIDList.size())
                                                                {
                                                                    for(int k = 0; k < MemberNameList.size(); k++)
                                                                    {
                                                                        MemberInfo.put(MemberIDList.get(k), MemberNameList.get(k));
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
            Convert_Time_IntegerToBit(MemberTimeTable[i], i);
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


    private void Convert_Time_IntegerToBit(int[] timeTable, int memberIndex)
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

                if(memberIndex == 0)
                    Member_1_TimeTable[day][i] = (loadTime & DefineValue.Time_Convert_Key);
                else if(memberIndex == 1)
                    Member_2_TimeTable[day][i] = (loadTime & DefineValue.Time_Convert_Key);
                else if(memberIndex == 2)
                    Member_3_TimeTable[day][i] = (loadTime & DefineValue.Time_Convert_Key);
                else if(memberIndex == 3)
                    Member_4_TimeTable[day][i] = (loadTime & DefineValue.Time_Convert_Key);
                else if(memberIndex == 4)
                    Member_5_TimeTable[day][i] = (loadTime & DefineValue.Time_Convert_Key);


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


    private void Send_SMS(String number){
        try{
            Uri smsUri = Uri.parse("sms:"+number);
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, smsUri);
            sendIntent.putExtra("sms_body", "<Group Timer>\n\n그룹 입장 코드 : " + GroupInsertCode);
            startActivity(sendIntent);

        }catch (Exception e){
            e.printStackTrace();
        }
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

        View customDialogView;

        DialogInterface.OnClickListener dialogClickListener;

        TextView groupNameText;
        TextView insertCodeText;


        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(dialogInterface == groupShareDialog && i == DialogInterface.BUTTON_POSITIVE)
                {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);

                    requestLauncher.launch(intent);
                }
                else if(dialogInterface == groupShareDialog && i == DialogInterface.BUTTON_NEGATIVE)
                {

                }
            }
        };


        builder = new AlertDialog.Builder(this);

        customDialogView = inflater.inflate(R.layout.custom_dialog_insertcode_share_layout, null);

        builder.setView(customDialogView);
        //builder.setMessage(GroupInsertCode);
        builder.setPositiveButton("공유", dialogClickListener);
        builder.setNegativeButton("확인", dialogClickListener);

        groupShareDialog = builder.create();

        groupNameText = (TextView) customDialogView.findViewById(R.id.groupName);
        insertCodeText= (TextView) customDialogView.findViewById(R.id.insertCode);

        groupNameText.setText(DefineValue.Group_Name);
        groupNameText.setTextColor(Color.parseColor("#FBAEAE"));
        insertCodeText.setText(GroupInsertCode);


        groupShareDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                groupShareDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                groupShareDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });


        groupShareDialog.show();
    }
    
    
    private void Set_ScheduleTime()
    {
        //Show_DatePicker();

        Show_CustomPickerDialog();
    }


    private void Show_CustomPickerDialog()
    {
        CheckBox checkBox;
        LinearLayout endDateLayout;
        LinearLayout endTimeLayout;


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View customDialogView = inflater.inflate(R.layout.custom_schedule_time_dialog, null);


        endDateLayout = (LinearLayout) customDialogView.findViewById(R.id.endDateLayout);
        endTimeLayout = (LinearLayout) customDialogView.findViewById(R.id.endTimeLayout);

        endDateLayout.setVisibility(View.GONE);
        endTimeLayout.setVisibility(View.GONE);


        StartDateTextView = (TextView) customDialogView.findViewById(R.id.startDate);
        StartTimeTextView = (TextView) customDialogView.findViewById(R.id.startTime);
        EndDateTextView = (TextView) customDialogView.findViewById(R.id.endDate);
        EndTimeTextView = (TextView) customDialogView.findViewById(R.id.endTime);

        StartDateTextView.setOnClickListener(this);
        StartTimeTextView.setOnClickListener(this);
        EndDateTextView.setOnClickListener(this);
        EndTimeTextView.setOnClickListener(this);


        checkBox = (CheckBox) customDialogView.findViewById(R.id.endTimeCheck);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scheduleChecked = checkBox.isChecked();

                Log.d("GT", "Dialog check box : " + scheduleChecked);

                if(scheduleChecked == true)
                {
                    endDateLayout.setVisibility(View.VISIBLE);
                    endTimeLayout.setVisibility(View.VISIBLE);

                    if (customDialogView.getParent() != null) {
                        ((ViewGroup) customDialogView.getParent()).removeView(customDialogView);
                    }

                    builder.setView(customDialogView);

                    if(customPickerDialog != null)
                    {
                        customPickerDialog.dismiss();
                        customPickerDialog = null;

                        customPickerDialog = builder.create();


                        customPickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {

                                customPickerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                customPickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                            }
                        });


                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                customPickerDialog.show();
                            }
                        });
                    }
                }
                else
                {
                    endDateLayout.setVisibility(View.GONE);
                    endTimeLayout.setVisibility(View.GONE);

                    if (customDialogView.getParent() != null)
                    {
                        ((ViewGroup) customDialogView.getParent()).removeView(customDialogView);
                    }

                    builder.setView(customDialogView);

                    if(customPickerDialog != null)
                    {
                        customPickerDialog.dismiss();
                        customPickerDialog = null;

                        customPickerDialog = builder.create();


                        customPickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {

                                customPickerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                customPickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                            }
                        });


                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                customPickerDialog.show();
                            }
                        });
                    }
                }
            }
        });



        builder.setView(customDialogView);
        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(scheduleChecked == true)
                {
                    if(StartDateValue == 0 || StartTimeValue == 0 || EndDateValue == 0 || EndTimeValue == 0)
                    {
                        Toast.makeText(getApplicationContext(), "입력이 올바르지 않습니다", Toast.LENGTH_SHORT).show();

                        if(customPickerDialog != null)
                        {
                            customPickerDialog.dismiss();

                            customPickerDialog = null;

                            if (customDialogView.getParent() != null)
                            {
                                ((ViewGroup) customDialogView.getParent()).removeView(customDialogView);
                            }

                            checkBox.setChecked(scheduleChecked);

                            customPickerDialog = builder.create();


                            customPickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {

                                    customPickerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                    customPickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                }
                            });


                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    customPickerDialog.show();
                                }
                            });
                        }


                        return;
                    }


                    if(StartDateValue == EndDateValue)
                    {
                        if(StartTimeValue == EndTimeValue)
                        {
                            Toast.makeText(getApplicationContext(), "시작, 종료 시간이 동일합니다", Toast.LENGTH_SHORT).show();

                            if(customPickerDialog != null)
                            {
                                customPickerDialog.dismiss();

                                customPickerDialog = null;

                                if (customDialogView.getParent() != null)
                                {
                                    ((ViewGroup) customDialogView.getParent()).removeView(customDialogView);
                                }

                                checkBox.setChecked(scheduleChecked);

                                customPickerDialog = builder.create();


                                customPickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialogInterface) {

                                        customPickerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                        customPickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                    }
                                });


                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        customPickerDialog.show();
                                    }
                                });
                            }


                            return;
                        }
                        else if(StartTimeValue > EndTimeValue)
                        {
                            Toast.makeText(getApplicationContext(), "시작, 종료 시간이 올바르지 않습니다", Toast.LENGTH_SHORT).show();

                            if(customPickerDialog != null)
                            {
                                customPickerDialog.dismiss();

                                customPickerDialog = null;

                                if (customDialogView.getParent() != null)
                                {
                                    ((ViewGroup) customDialogView.getParent()).removeView(customDialogView);
                                }

                                checkBox.setChecked(scheduleChecked);

                                customPickerDialog = builder.create();


                                customPickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialogInterface) {

                                        customPickerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                        customPickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                    }
                                });


                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        customPickerDialog.show();
                                    }
                                });
                            }


                            return;
                        }
                    }
                }
                else
                {
                    if(StartDateValue == 0 || StartTimeValue == 0)
                    {
                        Toast.makeText(getApplicationContext(), "입력이 올바르지 않습니다", Toast.LENGTH_SHORT).show();

                        if(customPickerDialog != null)
                        {
                            customPickerDialog.dismiss();

                            customPickerDialog = null;

                            if (customDialogView.getParent() != null)
                            {
                                ((ViewGroup) customDialogView.getParent()).removeView(customDialogView);
                            }

                            checkBox.setChecked(scheduleChecked);

                            customPickerDialog = builder.create();


                            customPickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {

                                    customPickerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                    customPickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                }
                            });


                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    customPickerDialog.show();
                                }
                            });
                        }


                        return;
                    }
                }



                if(StartDateValue != 0)
                {
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(DefineValue.Group_ID).child("ScheduleStartDate").setValue(StartDateValue);
                }

                if(StartTimeValue != 0)
                {
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(DefineValue.Group_ID).child("ScheduleStartTime").setValue(StartTimeValue);
                }

                if(EndDateValue != 0)
                {
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(DefineValue.Group_ID).child("ScheduleEndDate").setValue(EndDateValue);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(DefineValue.Group_ID).child("ScheduleEndDate").setValue(0);
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(DefineValue.Group_ID).child("ScheduleEndTime").setValue(0);
                }

                if(EndTimeValue != 0)
                {
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(DefineValue.Group_ID).child("ScheduleEndTime").setValue(EndTimeValue);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(DefineValue.Group_ID).child("ScheduleEndDate").setValue(0);
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(DefineValue.Group_ID).child("ScheduleEndTime").setValue(0);
                }
            }
        });

        builder.setNegativeButton("취소", null);


        customPickerDialog = builder.create();


        customPickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                customPickerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                customPickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });


        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                customPickerDialog.show();
            }
        });
    }


    private void Show_CustomDialog_DatePicker()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        dateText = null;


        datePickerDialog = new DatePickerDialog(this, R.style.PickerDialogTheme, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                dateText = Integer.toString(year);

                if(month + 1 < 10)
                {
                    dateText += "0" + Integer.toString(month + 1);
                }
                else
                {
                    dateText += Integer.toString(month + 1);
                }

                if(day < 10)
                {
                    dateText += "0" + Integer.toString(day);
                }
                else
                {
                    dateText += Integer.toString(day);
                }


                if(datePickerDialog != null)
                {
                    String buffer;
                    String yearText;
                    String monthText;
                    String dayText;


                    yearText = dateText.substring(0, 4);
                    monthText = dateText.substring(4, 6);
                    dayText = dateText.substring(6, 8);


                    buffer = yearText + " - " + monthText + " - " + dayText;


                    if(StartDateChecked == true)
                    {
                        StartDateValue = Integer.parseInt(dateText);

                        StartDateTextView.setText(buffer);

                        Log.d("GT", "Custom Picker Dialog Value : " + StartDateValue);
                    }

                    if(EndDateChecked == true)
                    {
                        EndDateValue = Integer.parseInt(dateText);

                        EndDateTextView.setText(buffer);

                        Log.d("GT", "Custom Picker Dialog Value : " + EndDateValue);
                    }

                    datePickerDialog.dismiss();

                    datePickerDialog = null;
                }
            }
        }, year, month, day);


        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.setCanceledOnTouchOutside(false);


        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                datePickerDialog.show();
            }
        });
    }


    private void Show_DatePicker()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        datePickerDialog = new DatePickerDialog(this, R.style.PickerDialogTheme, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                dateText = Integer.toString(year);

                if(month + 1 < 10)
                {
                    dateText += "0" + Integer.toString(month + 1);
                }
                else
                {
                    dateText += Integer.toString(month + 1);
                }

                if(day < 10)
                {
                    dateText += "0" + Integer.toString(day);
                }
                else
                {
                    dateText += Integer.toString(day);
                }


                if(datePickerDialog != null)
                {
                    datePickerDialog.dismiss();

                    datePickerDialog = null;
                }

                if(dateText.isEmpty() == false)
                {
                    Show_TimePicker();
                }
            }
        }, year, month, day);


        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.custom_picker_dialog, null);

        TextView textView = customView.findViewById(R.id.pickerTitle);

        if(endTimeChecked == false)
        {
            textView.setText("시작 시간");
        }
        else
        {
            textView.setText("종료 시간");
        }


        datePickerDialog.setCustomTitle(customView);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                if(scheduleChecked == true && endTimeChecked == true)
                {
                    endTimeChecked = false;

                    if(datePickerDialog != null)
                    {
                        datePickerDialog.dismiss();

                        datePickerDialog = null;
                    }


                    Show_TimePicker();
                }
            }
        });

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                datePickerDialog.show();
            }
        });
    }


    private void Show_CustomDialog_TimePicker()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        timeText = null;


        timePickerDialog = new TimePickerDialog(this, R.style.PickerDialogTheme, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute)
            {
                if(hour < 10)
                {
                    timeText = "0" + Integer.toString(hour);
                }
                else
                {
                    timeText = Integer.toString(hour);
                }

                if(minute < 10)
                {
                    timeText += "0" + Integer.toString(minute);
                }
                else
                {
                    timeText += Integer.toString(minute);
                }


                if(timePickerDialog != null)
                {
                    String buffer;
                    String hourText;
                    String minuteText;


                    if(timeText.length() < 4)
                    {
                        hourText = timeText.substring(0, 1);
                        minuteText = timeText.substring(1, 3);
                    }
                    else
                    {
                        hourText = timeText.substring(0, 2);
                        minuteText = timeText.substring(2, 4);
                    }

                    buffer = hourText + " : " + minuteText;


                    if(StartTimeChecked == true)
                    {
                        StartTimeValue = Integer.parseInt(timeText);

                        StartTimeTextView.setText(buffer);


                        Log.d("GT", "Custom Picker Dialog Value : " + StartTimeValue);
                    }

                    if(EndTimeChecked == true)
                    {
                        EndTimeValue = Integer.parseInt(timeText);

                        EndTimeTextView.setText(buffer);

                        Log.d("GT", "Custom Picker Dialog Value : " + EndTimeValue);
                    }


                    timePickerDialog.dismiss();

                    timePickerDialog = null;
                }
            }
        }, hour, minute,false);


        timePickerDialog.setCanceledOnTouchOutside(false);


        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                timePickerDialog.show();
            }
        });
    }


    private void Show_TimePicker()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        timePickerDialog = new TimePickerDialog(this, R.style.PickerDialogTheme, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute)
            {
                if(hour < 10)
                {
                    timeText = "0" + Integer.toString(hour);
                }
                else
                {
                    timeText = Integer.toString(hour);
                }

                if(minute < 10)
                {
                    timeText += "0" + Integer.toString(minute);
                }
                else
                {
                    timeText += Integer.toString(minute);
                }


                if(timePickerDialog != null)
                {
                    timePickerDialog.dismiss();

                    timePickerDialog = null;
                }

                if(timeText.isEmpty() == false)
                {
                    ScheduleTimeText = dateText + timeText;


                    Log.d("GT", "Schedule Time : " + ScheduleTimeText);


                    if(scheduleChecked == false)
                    {
                        return;
                    }


                    if(endTimeChecked == false)
                    {
                        endTimeChecked = true;

                        Show_DatePicker();
                    }
                }
            }
        }, hour, minute,false);


        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.custom_picker_dialog, null);

        TextView textView = customView.findViewById(R.id.pickerTitle);

        if(endTimeChecked == false)
        {
            textView.setText("시작 시간");
        }
        else
        {
            textView.setText("종료 시간");
        }


        timePickerDialog.setCustomTitle(customView);
        //timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                Log.d("GT", "Timepicker Dialog : Dismiss");
            }
        });
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                Log.d("GT", "Timepicker Dialog : Cancel");


                if(timePickerDialog != null)
                {
                    timePickerDialog.dismiss();

                    timePickerDialog = null;
                }

                Show_DatePicker();
            }
        });


        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                timePickerDialog.show();
            }
        });
    }


    public void Reload_GroupMemberList(String groupID, int reloadMemberCnt, String message, String senderUID, long sendTime)
    {
        DatabaseReference reloadDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupID).child("groupMember");


        MemberIDList.clear();


        for(int i = 0; i < reloadMemberCnt; i++)
        {
            String position = Integer.toString(i);


            reloadDatabaseReference.child(position).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String value = dataSnapshot.getValue(String.class);


                    MemberIDList.add(value);

                    if(Integer.parseInt(position) == (reloadMemberCnt - 1))
                    {
                        ((GroupChattingActivity)GroupChattingActivity.GroupChattingContext).function(message, senderUID, sendTime);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
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
                Toast.makeText(this, "그룹 입장 코드가 없습니다.", Toast.LENGTH_SHORT);

                return;
            }

            Generate_InsertCode_Dialog();
        }
        else if(view.getId() == ScheduleTimeButtonID)
        {
            endTimeChecked = false;

            Set_ScheduleTime();
        }
        else if(view.getId() == ChattingButtonID)
        {
            startActivity(new Intent(this, GroupChattingActivity.class));
        }
        else if(view == StartDateTextView)
        {
            StartDateChecked = true;
            StartTimeChecked = false;
            EndDateChecked = false;
            EndTimeChecked = false;

            Show_CustomDialog_DatePicker();
        }
        else if(view == StartTimeTextView)
        {
            StartDateChecked = false;
            StartTimeChecked = true;
            EndDateChecked = false;
            EndTimeChecked = false;

            Show_CustomDialog_TimePicker();
        }
        else if(view == EndDateTextView)
        {
            StartDateChecked = false;
            StartTimeChecked = false;
            EndDateChecked = true;
            EndTimeChecked = false;

            Show_CustomDialog_DatePicker();
        }
        else if(view == EndTimeTextView)
        {
            StartDateChecked = false;
            StartTimeChecked = false;
            EndDateChecked = false;
            EndTimeChecked = true;

            Show_CustomDialog_TimePicker();
        }
    }
}