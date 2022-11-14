package com.example.grouptimer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewHolder>
{
    private ArrayList<String> AdapterIDList;
    private ArrayList<String> AdapterNameList;

    private ViewGroup parent;


    public GroupRecyclerViewAdapter(ArrayList<String> idList, ArrayList<String> nameList)
    {
        Log.d("GT", "id ataList size : " + idList.size());
        Log.d("GT", "name dataList size : " + nameList.size());

        this.AdapterIDList = idList;
        this.AdapterNameList = nameList;
    }

    @NonNull
    @Override
    public GroupRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        this.parent = parent;


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_recyclerview_item, parent, false);

        GroupRecyclerViewHolder viewHolder = new GroupRecyclerViewHolder(view);


        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull GroupRecyclerViewHolder viewHolder, int position)
    {
        String name = this.AdapterNameList.get(position);
        String id = this.AdapterIDList.get(position);


        viewHolder.GroupNameText.setText(name);
        viewHolder.GroupIDText = id;


        viewHolder.GroupNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //DefineValue.Group_ID = viewHolder.GroupNameText.getText().toString();
                //DefineValue.Group_ID = AdapterIDList.get(viewHolder.getAdapterPosition());
                DefineValue.Group_ID = viewHolder.GroupIDText;

                Log.d("GT", "Select Group ID : " + DefineValue.Group_ID);

                parent.getContext().startActivity(new Intent(parent.getContext(), GroupTimeTableActivity.class));
            }
        });

        viewHolder.ExpansionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("GT", viewHolder.GroupNameText.getText() + " : Expansion button pressed");
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return this.AdapterIDList.size();
    }
}
