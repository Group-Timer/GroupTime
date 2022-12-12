package com.example.grouptimer.Listener;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.example.grouptimer.Common.DefineValue;
import com.example.grouptimer.Fragment.PersonalTimeTableFragment;


public class TimeTableOnClickListener implements View.OnClickListener
{
    @Override
    public void onClick(View view)
    {
        int buttonPosition;
        int viewID;

        int day;
        int times;


        if(PersonalTimeTableFragment.TimeTableEditable == false)
        {
            return;
        }


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


        if(PersonalTimeTableFragment.ButtonClickChecker[viewID] == false)
        {
            Log.d("GT", "Button " + viewID + " first Click");


            switch(viewID / DefineValue.Times_Of_Day)
            {
                case DefineValue.Mon:
                {
                    view.setBackgroundColor(Color.parseColor("#FFD6A5"));

                    break;
                }

                case DefineValue.Tue:
                {
                    view.setBackgroundColor(Color.parseColor("#FDFFB6"));

                    break;
                }

                case DefineValue.Wed:
                {
                    view.setBackgroundColor(Color.parseColor("#CAFFBF"));

                    break;
                }

                case DefineValue.Thu:
                {
                    view.setBackgroundColor(Color.parseColor("#9BF6FF"));

                    break;
                }

                case DefineValue.Fri:
                {
                    view.setBackgroundColor(Color.parseColor("#BDB2FF"));

                    break;
                }

                case DefineValue.Sat:
                {
                    view.setBackgroundColor(Color.parseColor("#FFC6FF"));

                    break;
                }

                case DefineValue.Sun:
                {
                    view.setBackgroundColor(Color.parseColor("#FFADAD"));

                    break;
                }
            }


            PersonalTimeTableFragment.UserTimeTable[day][times]     = 1;
            PersonalTimeTableFragment.ButtonClickChecker[viewID]    = true;
        }
        else
        {
            Log.d("GT", "Button " + viewID + " second Click");

            view.setBackground(PersonalTimeTableFragment.CustomButtonDrawable);


            PersonalTimeTableFragment.UserTimeTable[day][times]     = 0;
            PersonalTimeTableFragment.ButtonClickChecker[viewID]    = false;
        }
    }
}
