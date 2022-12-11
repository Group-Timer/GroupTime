package com.example.grouptimer;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

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

        String dialogTitle;


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

        TextView dateText;
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        TextView textView5;



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


        dialogTitle = Generate_Dialog_Message(day, times);


        View customDialogView = GroupTimeTableActivity.inflater.inflate(R.layout.custom_dialog_overlap_member_layout, null);

        builder.setView(customDialogView);
        builder.setPositiveButton("확인", dialogClickListener);

        dialog = builder.create();

        dateText = (TextView) customDialogView.findViewById(R.id.date);
        textView1 = (TextView) customDialogView.findViewById(R.id.member1);
        textView2 = (TextView) customDialogView.findViewById(R.id.member2);
        textView3 = (TextView) customDialogView.findViewById(R.id.member3);
        textView4 = (TextView) customDialogView.findViewById(R.id.member4);
        textView5 = (TextView) customDialogView.findViewById(R.id.member5);


        dateText.setText(dialogTitle);
        dateText.setTextColor(Color.parseColor("#FBAEAE"));

        for(int i = 0; i < overlapMemberList.size(); i++)
        {
            String value = overlapMemberList.get(i);


            if(i == 0)
            {
                textView1.setText(value);
                textView1.setVisibility(View.VISIBLE);
            }
            else if(i == 1)
            {
                textView2.setText(value);
                textView2.setVisibility(View.VISIBLE);
            }
            else if(i == 2)
            {
                textView3.setText(value);
                textView3.setVisibility(View.VISIBLE);
            }
            else if(i == 3)
            {
                textView4.setText(value);
                textView4.setVisibility(View.VISIBLE);
            }
            else if(i == 4)
            {
                textView5.setText(value);
                textView5.setVisibility(View.VISIBLE);
            }
        }


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });


        dialog.show();


        return;
    }


    private String Generate_Dialog_Message(int day, int time)
    {
        String message;
        String dayText;
        String timeText;


        dayText = DefineValue.Days[day];
        timeText = Integer.toString(time + 9);

        message = dayText + "요일" + " " + timeText + "시";


        return message;
    }
}
