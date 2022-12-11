package com.example.grouptimer;

import androidx.annotation.NonNull;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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

public class PersonalTimeTableActivity extends Fragment implements View.OnClickListener {


    LinearLayout                        RootLayout;
    ContentFrameLayout.LayoutParams     RootParams;


    private Button editButton;
    private Button saveButton;


    String[]                    Hour                    = {"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"};
    String[]                    Day                     = {"", "S", "M", "T", "W", "T", "F", "S"};

    GridLayout[]                TimeTableGridLayout     = new GridLayout[DefineValue.Day_Cnt];
    GridLayout                  HourGridLayout;

    Button[][]                  TimeTableButton         = new Button[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];

    public static int[][]       UserTimeTable           = new int[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];

    public static boolean[]     ButtonClickChecker      = new boolean[DefineValue.TimeTable_Button_Cnt];

    public static Drawable      CustomButtonDrawable;

    public static boolean       TimeTableEditable;


    private final int           EditButtonID            = 1;
    private final int           SaveButtonID            = 2;

    private final int           TimeButtonHeightSize    = 120;


    int DayOfWeek;
    int LoadCnt;

    int[][] TimeTableBit;


    ProgressDialog progressDialog = null;

    ProgressDialog saveProgressDialog = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        CustomButtonDrawable = getResources().getDrawable(R.drawable.custom_button);


        progressDialog = new ProgressDialog(getContext(), R.style.ProgressDialogTheme);

        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("로딩중 ...");
                progressDialog.setCancelable(false);

                progressDialog.show();
            }
        });


        Show_Screen();

        View view = inflater.inflate(R.layout.activity_personal_time_table, container, false);


        LinearLayout linearLayout;

        linearLayout = (LinearLayout) view.findViewById(R.id.linear);

        linearLayout.addView(RootLayout, RootParams);


        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        Init_Button_Checker();


        TimeTableEditable = false;


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ArrayList<Integer> timeTable = new ArrayList<Integer>();


        LoadCnt = 0;
        for(DayOfWeek = 0; DayOfWeek < DefineValue.Day_Cnt; DayOfWeek++)
        {
            String listID = Integer.toString(DayOfWeek);


            mDatabase.child("PersonalTimeTable").child(user.getUid()).child("TimeTable").child(listID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getValue() == null)
                    {
                        Log.d("GT", "is null");


                        if(progressDialog != null)
                        {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }


                        return;
                    }

                    int value = dataSnapshot.getValue(Integer.class);


                    Log.d("GT", "dataSnapshot : " + value);


                    timeTable.add(value);


                    if(timeTable.size() == DefineValue.Day_Cnt)
                    {
                        Show_TimeTable(timeTable);


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


    @Override
    public void onResume()
    {
        super.onResume();
    }


    // 액티비티 화면 출력
    private void Show_Screen()
    {
        Generate_RootLayout();


        Generate_TopLayout();
        Generate_DayLayout();
        Generate_TimeTableLayout();
    }


    private void Init_Button_Checker()
    {
        for(int i = 0; i < DefineValue.TimeTable_Button_Cnt; i++)
        {
            ButtonClickChecker[i] = false;
        }
    }


    // 최상위 Root 레이아웃 생성
    // ContentFrameLayout Params 적용
    private void Generate_RootLayout()
    {
        RootLayout = new LinearLayout(getContext());
        RootLayout.setOrientation(LinearLayout.VERTICAL);

        RootParams = new ContentFrameLayout.LayoutParams(ContentFrameLayout.LayoutParams.MATCH_PARENT, ContentFrameLayout.LayoutParams.WRAP_CONTENT);
        RootParams.setMargins(0,20,20,20);
    }


    // 상단 레이아웃 생성
    // Time Table의 상단 부분
    private void Generate_TopLayout()
    {
        LinearLayout topLayout = new LinearLayout(getContext());
        topLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams topLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        topLayoutParams.topMargin = 40;

        TextView textView = new TextView(getContext());
        textView.setText("Time Table");
        textView.setTextSize(30);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextColor(Color.parseColor("#F08080"));
        textView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams textParams    = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        textParams.weight                       = 1;
        textParams.setMarginStart(10);
        textParams.setMarginEnd(10);


        editButton = new Button(getContext());
        editButton.setId(EditButtonID);
        editButton.setText("Edit");
        editButton.setBackgroundResource(R.drawable.small_button_outline);
        editButton.setOnClickListener(this);

        LinearLayout.LayoutParams editParams    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        editParams.gravity                      = Gravity.CENTER_VERTICAL;
        editParams.setMarginEnd(20);


        saveButton = new Button(getContext());
        saveButton.setId(SaveButtonID);
        saveButton.setText("Save");
        saveButton.setBackgroundResource(R.drawable.small_button);
        saveButton.setOnClickListener(this);

        LinearLayout.LayoutParams saveParams    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        saveParams.gravity                      = Gravity.CENTER_VERTICAL;
        saveParams.setMarginEnd(20);


        topLayout.addView(textView, textParams);
        topLayout.addView(editButton, editParams);
        topLayout.addView(saveButton, saveParams);

        RootLayout.addView(topLayout, topLayoutParams);
    }


    // 요일 레이아웃 생성
    private void Generate_DayLayout()
    {
        LinearLayout dayLinearLayout = new LinearLayout(getContext());
        dayLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        dayLinearLayout.setWeightSum(8);

        LinearLayout.LayoutParams dayLinearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dayLinearParams.setMargins(0, 30, 20, 0);

        for(int i = 0; i < DefineValue.TimeTable_GridLayout_Cnt; i++)
        {
            TextView dayText = new TextView(getContext());

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


        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setWeightSum(8);

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearParams.setMarginEnd(20);


        HourGridLayout = new GridLayout(getContext());
        HourGridLayout.setOrientation(GridLayout.VERTICAL);
        HourGridLayout.setRowCount(12);

        LinearLayout.LayoutParams hourGridParams    = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        hourGridParams.weight                       = 1;
        hourGridParams.setMargins(0,90,0,0);

        for(int i = 0; i < DefineValue.Times_Of_Day; i++)
        {
            TextView hourText = new TextView(getContext());

            hourText.setText(Hour[i]);
            hourText.setGravity(Gravity.CENTER_HORIZONTAL);

            LinearLayout.LayoutParams hourParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TimeButtonHeightSize);

            HourGridLayout.addView(hourText, hourParams);
        }

        linearLayout.addView(HourGridLayout, hourGridParams);


        // 요일별 세부 레이아웃 생성
        buttonID = 0;
        for( int i = 0; i < DefineValue.Day_Cnt; i++)
        {
            //GridLayout gridLayout = new GridLayout(this);
            TimeTableGridLayout[i] = new GridLayout(getContext());

            TimeTableGridLayout[i].setOrientation(GridLayout.VERTICAL);
            TimeTableGridLayout[i].setRowCount(12);

            LinearLayout.LayoutParams gridParams    = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            gridParams.weight                       = 1;


            // Time Table Button 생성
            for(int k = 0; k < DefineValue.Times_Of_Day; k++)
            {
                TimeTableButton[i][k] = new Button(getContext());
                TimeTableButton[i][k].setOnClickListener(new TimeTableOnClickListener());
                TimeTableButton[i][k].setBackground(CustomButtonDrawable);
                TimeTableButton[i][k].setId(buttonID);

                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TimeButtonHeightSize);

                TimeTableGridLayout[i].addView(TimeTableButton[i][k], buttonParams);


                buttonID++;
            }


            linearLayout.addView(TimeTableGridLayout[i], gridParams);
        }


        RootLayout.addView(linearLayout, linearParams);
    }


    private int Convert_Time_BitToInteger(int day)
    {
        int     time            = 0;
        int     shiftPosition   = DefineValue.Max_Bit_Size - 1;


        for( int i = 0; i < DefineValue.Max_Bit_Size; i++ )
        {
            int value;


            value = UserTimeTable[ day ][ i ] & DefineValue.Time_Convert_Key;

            value <<= shiftPosition;

            time += value;
            shiftPosition--;
        }


        return time;
    }


    private void Convert_Time_IntegerToBit(int[] timeTable)
    {
        // TimeTable을 int형에서 bit 단위로 변환하는 과정
        TimeTableBit = new int[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];
        for(int day = 0; day < DefineValue.Day_Cnt; day++)
        {
            int loadTime = timeTable[day];

            Log.d("GT", "Time : " + loadTime);

            for( int times = DefineValue.Max_Bit_Size - 1; times >= 0; times-- )
            {
                int value;
                int buttonID;


                value = loadTime & DefineValue.Time_Convert_Key;

                TimeTableBit[ day ][ times ]    = value;
                UserTimeTable[day][times]       = value;

                buttonID                        = (DefineValue.Times_Of_Day * day) + times;
                if(value == 1)
                {
                    ButtonClickChecker[buttonID]    = true;
                }
                else
                {
                    ButtonClickChecker[buttonID]    = false;
                }


                loadTime >>= 1;
            }


            String message = "";
            for( int k = 0; k < DefineValue.Max_Bit_Size; k++ )
            {
                message += TimeTableBit[day][k];

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


    private void Save_Firebase(int[] timeTable)
    {
        ArrayList<Integer> timeTableList = new ArrayList<Integer>();

        Map<String, Object> taskMap = new HashMap<String, Object>();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        if (user != null) {
            Log.d("GT", user.getUid());
        } else {
            Log.d("GT", "Non user");
        }


        for(int i = 0; i < DefineValue.Day_Cnt; i++)
        {
            timeTableList.add(timeTable[i]);
        }


        taskMap.put("TimeTable", timeTableList);

        mDatabase.child("PersonalTimeTable").child(user.getUid()).updateChildren(taskMap);


        if(saveProgressDialog != null)
        {
            Log.d("GT", "Dismiss Dialog");


            saveProgressDialog.dismiss();

            saveProgressDialog = null;
        }
    }


    private void Show_TimeTable(ArrayList<Integer> timeTable)
    {
        int[] personalTime;


        personalTime = new int[DefineValue.Day_Cnt];

        for(int i = 0; i < timeTable.size(); i++)
        {
            personalTime[i] = timeTable.get(i);
        }


        Convert_Time_IntegerToBit(personalTime);


        for(int day = 0; day < DefineValue.Day_Cnt; day++)
        {
            for(int times = 0; times < DefineValue.Times_Of_Day; times++)
            {
                if(TimeTableBit[day][times] == 1)
                {
                    switch(day)
                    {
                        case DefineValue.Mon:
                        {
                            TimeTableButton[day][times].setBackgroundColor(Color.parseColor("#FFD6A5"));

                            break;
                        }

                        case DefineValue.Tue:
                        {
                            TimeTableButton[day][times].setBackgroundColor(Color.parseColor("#FDFFB6"));

                            break;
                        }

                        case DefineValue.Wed:
                        {
                            TimeTableButton[day][times].setBackgroundColor(Color.parseColor("#CAFFBF"));

                            break;
                        }

                        case DefineValue.Thu:
                        {
                            TimeTableButton[day][times].setBackgroundColor(Color.parseColor("#9BF6FF"));

                            break;
                        }

                        case DefineValue.Fri:
                        {
                            TimeTableButton[day][times].setBackgroundColor(Color.parseColor("#BDB2FF"));

                            break;
                        }

                        case DefineValue.Sat:
                        {
                            TimeTableButton[day][times].setBackgroundColor(Color.parseColor("#FFC6FF"));

                            break;
                        }

                        case DefineValue.Sun:
                        {
                            TimeTableButton[day][times].setBackgroundColor(Color.parseColor("#FFADAD"));

                            break;
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onClick(View view)
    {
        int[]   timeTable   = new int[DefineValue.Day_Cnt];


        if(view.getId() == EditButtonID)
        {
            if(TimeTableEditable == true)
            {
                TimeTableEditable = false;


                editButton.setText("Edit");
                editButton.setBackgroundResource(R.drawable.small_button_outline);
            }
            else
            {
                TimeTableEditable = true;


                editButton.setText("Editing");
                editButton.setBackgroundResource(R.drawable.small_button);
            }
        }
        else if(view.getId() == SaveButtonID)
        {
            Log.d("GT", "Save Button Click");


            TimeTableEditable = false;


            editButton.setText("Edit");
            editButton.setBackgroundResource(R.drawable.small_button_outline);


            saveProgressDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);

            saveProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            saveProgressDialog.setCanceledOnTouchOutside(false);
            saveProgressDialog.setCancelable(false);
            saveProgressDialog.setMessage("저장중 ...");

            saveProgressDialog.show();


            Handler handler = new Handler();

            handler.postDelayed(new Runnable()
            {
                public void run()
                {
                    int time;

                    for(int day = 0; day < DefineValue.Day_Cnt; day++)
                    {
                        time = Convert_Time_BitToInteger(day);

                        timeTable[day] = time;
                    }


                    Save_Firebase(timeTable);
                }
            }, 500);
        }
    }
}