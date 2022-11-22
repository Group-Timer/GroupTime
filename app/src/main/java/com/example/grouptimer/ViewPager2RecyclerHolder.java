package com.example.grouptimer;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewPager2RecyclerHolder extends RecyclerView.ViewHolder
{
    public CheckBox todoListCheckBox1;
    public CheckBox todoListCheckBox2;
    public CheckBox todoListCheckBox3;

    public TextView todoListText1;
    public TextView todoListText2;
    public TextView todoListText3;


    public ViewPager2RecyclerHolder(@NonNull View item)
    {
        super(item);


        todoListCheckBox1 = item.findViewById(R.id.viewpager2CheckBox1);
        todoListText1 = item.findViewById(R.id.viewpager2Text1);

        todoListCheckBox2 = item.findViewById(R.id.viewpager2CheckBox2);
        todoListText2 = item.findViewById(R.id.viewpager2Text2);

        todoListCheckBox3 = item.findViewById(R.id.viewpager2CheckBox3);
        todoListText3 = item.findViewById(R.id.viewpager2Text3);
    }


    public TextView getGroupNameText1()
    {
        return this.todoListText1;
    }

    public TextView getGroupNameText2()
    {
        return this.todoListText2;
    }

    public TextView getGroupNameText3()
    {
        return this.todoListText3;
    }
}