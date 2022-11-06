package com.example.grouptimer;

import android.app.Person;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.DrawableRes;

public class TimeTableOnClickListener implements View.OnClickListener
{
    @Override
    public void onClick(View view)
    {
        int buttonPosition;
        int viewID;

        int day;
        int times;


        buttonPosition  = 0;
        viewID          = DefineValue.INVALID_VALUE;
        while(true)
        {
            if(buttonPosition >= DefineValue.TimeTable_Button_Cnt)
            {
                break;
            }


            if(view.getId() == buttonPosition)
            {
                viewID = view.getId();

                break;
            }


            buttonPosition++;
        }


        if(viewID == DefineValue.INVALID_VALUE)
        {
            return;
        }


        day     = viewID / DefineValue.Times_Of_Day;
        times   = viewID % DefineValue.Times_Of_Day;

        Log.d("GT", "Day :  " + day + "\tTimes : " + times);


        if(PersonalTimeTableActivity.ButtonClickChecker[viewID] == false)
        {
            Log.d("GT", "Button " + viewID + " first Click");


            switch(viewID / DefineValue.Times_Of_Day)
            {
                case DefineValue.Mon:
                {
                    view.setBackgroundColor(Color.RED);

                    break;
                }

                case DefineValue.Tue:
                {
                    view.setBackgroundColor(Color.MAGENTA);

                    break;
                }

                case DefineValue.Wed:
                {
                    view.setBackgroundColor(Color.YELLOW);

                    break;
                }

                case DefineValue.Thu:
                {
                    view.setBackgroundColor(Color.GREEN);

                    break;
                }

                case DefineValue.Fri:
                {
                    view.setBackgroundColor(Color.BLUE);

                    break;
                }

                case DefineValue.Sat:
                {
                    view.setBackgroundColor(Color.CYAN);

                    break;
                }

                case DefineValue.Sun:
                {
                    view.setBackgroundColor(Color.LTGRAY);

                    break;
                }
            }


            PersonalTimeTableActivity.UserTimeTable[day][times]     = 1;
            PersonalTimeTableActivity.ButtonClickChecker[viewID]    = true;
        }
        else
        {
            Log.d("GT", "Button " + viewID + " second Click");

            view.setBackground(PersonalTimeTableActivity.CustomButtonDrawable);


            PersonalTimeTableActivity.UserTimeTable[day][times]     = 0;
            PersonalTimeTableActivity.ButtonClickChecker[viewID]    = false;
        }
    }
}
