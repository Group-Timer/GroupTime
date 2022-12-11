package com.example.grouptimer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewPager2RecyclerAdapter extends RecyclerView.Adapter<ViewPager2RecyclerHolder>
{
    private ArrayList<String> AdapterTodoListText;
    private ArrayList<Boolean> AdapterTodoListCheckBox;


    public ViewPager2RecyclerAdapter(ArrayList<String> todoListText, ArrayList<Boolean> todoListCheckBox)
    {
        this.AdapterTodoListText = todoListText;
        this.AdapterTodoListCheckBox = todoListCheckBox;
    }


    @Override
    public ViewPager2RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_todolist_viewpager2_item, parent, false);

        ViewPager2RecyclerHolder holder = new ViewPager2RecyclerHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewPager2RecyclerHolder holder, int position)
    {
        String text;
        boolean checked;

        int listIndex;


        listIndex = position * 3;


        if(listIndex < this.AdapterTodoListText.size())
        {
            text = this.AdapterTodoListText.get(listIndex);
            checked = this.AdapterTodoListCheckBox.get(listIndex);

            holder.todoListText1.setText(text);
            holder.todoListCheckBox1.setChecked(checked);
            holder.todoListCheckBox1.setEnabled(false);

            holder.todoListText1.setVisibility(View.VISIBLE);
            holder.todoListCheckBox1.setVisibility(View.VISIBLE);

            listIndex++;
        }

        if(listIndex < this.AdapterTodoListText.size())
        {
            text = this.AdapterTodoListText.get(listIndex);
            checked = this.AdapterTodoListCheckBox.get(listIndex);

            holder.todoListText2.setText(text);
            holder.todoListCheckBox2.setChecked(checked);
            holder.todoListCheckBox2.setEnabled(false);

            holder.todoListText2.setVisibility(View.VISIBLE);
            holder.todoListCheckBox2.setVisibility(View.VISIBLE);

            listIndex++;
        }

        if(listIndex < this.AdapterTodoListText.size())
        {
            text = this.AdapterTodoListText.get(listIndex);
            checked = this.AdapterTodoListCheckBox.get(listIndex);

            holder.todoListText3.setText(text);
            holder.todoListCheckBox3.setChecked(checked);
            holder.todoListCheckBox3.setEnabled(false);

            holder.todoListText3.setVisibility(View.VISIBLE);
            holder.todoListCheckBox3.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount()
    {
        if((this.AdapterTodoListText.size() % 3) == 0)
        {
            return this.AdapterTodoListText.size() / 3;
        }
        else
        {
            return (this.AdapterTodoListText.size() / 3) + 1;
        }
    }
}
