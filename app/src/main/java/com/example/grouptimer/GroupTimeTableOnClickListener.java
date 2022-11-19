package com.example.grouptimer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import java.util.ArrayList;

public class GroupTimeTableOnClickListener implements View.OnClickListener
{
    AlertDialog dialog;


    @Override
    public void onClick(View view)
    {
        int buttonID;

        int day;
        int times;

        int overlapCnt;

        boolean timeOverlap;

        String dialogMessage;


        ArrayList<String> overlapMemberList = new ArrayList<String>();


        buttonID = view.getId();

        day     = buttonID / DefineValue.Times_Of_Day;
        times   = buttonID % DefineValue.Times_Of_Day;


        overlapCnt = 0;
        for(int i = 0; i < GroupTimeTableActivity.GroupMemberCnt; i++)
        {
            timeOverlap = false;


            if(i == 0)
            {
                if(GroupTimeTableActivity.Member_1_TimeTable[day][times] == 1)
                {
                    timeOverlap = true;;
                }
            }
            else if(i == 1)
            {
                if(GroupTimeTableActivity.Member_2_TimeTable[day][times] == 1)
                {
                    timeOverlap = true;;
                }
            }
            else if(i == 2)
            {
                if(GroupTimeTableActivity.Member_3_TimeTable[day][times] == 1)
                {
                    timeOverlap = true;;
                }
            }
            else if(i == 3)
            {
                if(GroupTimeTableActivity.Member_4_TimeTable[day][times] == 1)
                {
                    timeOverlap = true;;
                }
            }
            else if(i == 4)
            {
                if(GroupTimeTableActivity.Member_5_TimeTable[day][times] == 1)
                {
                    timeOverlap = true;
                }
            }


            if(timeOverlap == true)
            {
                overlapMemberList.add(GroupTimeTableActivity.MemberNameList.get(i));

                overlapCnt++;
            }
        }


        if(overlapCnt == 0)
        {
            return;
        }



        AlertDialog.Builder builder;


        builder = new AlertDialog.Builder(view.getContext());

        DialogInterface.OnClickListener dialogClickListener;
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(dialogInterface == dialog && i == DialogInterface.BUTTON_POSITIVE)
                {

                }
            }
        };


        dialogMessage = Generate_Dialog_Message(day, times, overlapMemberList, overlapCnt);

        builder.setMessage(dialogMessage);
        builder.setPositiveButton("확인", dialogClickListener);
        //builder.setNegativeButton("취소", dialogClickListener);

        dialog = builder.create();
        dialog.show();


        return;
    }


    private String Generate_Dialog_Message(int day, int time, ArrayList<String> overlapMemberList, int overlapCnt)
    {
        String message;
        String dayText;
        String timeText;


        dayText = DefineValue.Days[day];
        timeText = Integer.toString(time + 9);

        message = dayText + "요일" + " " + timeText + "시" + "\n\n";

        for(int i = 0; i < overlapCnt; i++)
        {
            message += overlapMemberList.get(i) + "\n";
        }


        return message;
    }
}
