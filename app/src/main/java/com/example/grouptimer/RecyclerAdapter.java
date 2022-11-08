package com.example.grouptimer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<ToDoListItem> toDoListItemArrayList;

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(toDoListItemArrayList.get(position));
    }

    public void setToDoListItemArrayList(ArrayList<ToDoListItem> list){
        this.toDoListItemArrayList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return toDoListItemArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public static CheckBox toDoListCheckBox;
        public static EditText toDoListEditText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            toDoListCheckBox = (CheckBox) itemView.findViewById(R.id.toDoListCheckBox);
            toDoListEditText = (EditText) itemView.findViewById(R.id.toDoListEditText);

        }

        void onBind(ToDoListItem item){

            toDoListEditText.setText(item.toDoList);
            toDoListCheckBox.isChecked();

        }

    }
}