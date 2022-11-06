package com.example.grouptimer;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ContentFrameLayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PersonalTimeTableActivity extends AppCompatActivity implements View.OnClickListener {


    LinearLayout                        RootLayout;
    ContentFrameLayout.LayoutParams     RootParams;



    String[]                    Hour                    = {"9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
    String[]                    Day                     = {"", "S", "M", "T", "W", "T", "F", "S"};

    GridLayout[]                TimeTableGridLayout     = new GridLayout[DefineValue.Day_Cnt];
    GridLayout                  HourGridLayout;

    Button[][]                  TimeTableButton         = new Button[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];

    public static int[][]       UserTimeTable           = new int[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];

    public static boolean[]     ButtonClickChecker      = new boolean[DefineValue.TimeTable_Button_Cnt];

    public static Drawable      CustomButtonDrawable;


    private final int           EditButtonID            = 1;
    private final int           SaveButtonID            = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        CustomButtonDrawable = getResources().getDrawable(R.drawable.custom_button);


        Show_Screen();

        Init_Button_Checker();
    }


    // 액티비티 화면 출력
    private void Show_Screen()
    {
        Generate_RootLayout();
        Generate_TopLayout();
        Generate_DayLayout();
        Generate_TimeTableLayout();


        setContentView(RootLayout, RootParams);
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


        TextView textView = new TextView(this);
        textView.setText("Time Table");
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams textParams    = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        textParams.weight                       = 1;
        textParams.setMarginStart(10);
        textParams.setMarginEnd(10);


        Button editButton = new Button(this);
        editButton.setId(EditButtonID);
        editButton.setText("Edit");
        editButton.setOnClickListener(this);

        LinearLayout.LayoutParams editParams    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        editParams.gravity                      = Gravity.CENTER_VERTICAL;
        editParams.setMarginEnd(10);


        Button saveButton = new Button(this);
        saveButton.setId(SaveButtonID);
        saveButton.setText("Save");
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

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
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
                TimeTableButton[i][k].setOnClickListener(new TimeTableOnClickListener());
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


    private int Convert_Time(int day)
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


    private void Save_Firebase(int[] timeTable)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d("GT", user.getUid());
        } else {
            // No user is signed in
            Log.d("GT", "Non");
        }









        int[][] getTimeTable = new int[DefineValue.Day_Cnt][DefineValue.Times_Of_Day];
        for(int day = 0; day < DefineValue.Day_Cnt; day++)
        {
            int loadTime = timeTable[day];

            Log.d("GT", "Time : " + loadTime);

            for( int i = DefineValue.Max_Bit_Size - 1; i >= 0; i-- )
            {
                getTimeTable[ day ][ i ] = loadTime & DefineValue.Time_Convert_Key;

                loadTime >>= 1;
            }


            String message = "";
            for( int k = 0; k < DefineValue.Max_Bit_Size; k++ )
            {
                message += getTimeTable[day][k];

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


    @Override
    public void onClick(View view)
    {
        int[]   timeTable   = new int[DefineValue.Day_Cnt];


        if(view.getId() == EditButtonID)
        {

        }
        else if(view.getId() == SaveButtonID)
        {
            int time;


            for(int day = 0; day < DefineValue.Day_Cnt; day++)
            {
                time = Convert_Time(day);

                timeTable[day] = time;
            }


            Save_Firebase(timeTable);
        }
    }
}