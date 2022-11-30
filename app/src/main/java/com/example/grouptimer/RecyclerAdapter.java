package com.example.grouptimer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<ToDoListItem> toDoListItemArrayList;
    private Context context ;
    private Boolean[] checkBoxArrayList = new Boolean[100];
    private ArrayList<String> toDoListArrayList;

    public RecyclerAdapter(Context context , ArrayList<String> toDoListArrayList){
        this.context = context;
        this.toDoListArrayList = toDoListArrayList;
    }

    public RecyclerAdapter(View.OnClickListener onClickListener, ArrayList<String> toDoListArrayList) {
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public  CheckBox toDoListCheckBox;
        public  EditText toDoListEditText;
        private String toDoList;
        private Boolean check;
        private EditTextListener editTextListener;


        public ViewHolder(@NonNull View itemView, EditTextListener editTextListener) {
            super(itemView);

            toDoListCheckBox = (CheckBox) itemView.findViewById(R.id.toDoListCheckBox);

//            toDoList = toDoListEditText.getText().toString();
//            check = toDoListCheckBox.isChecked();

            this.editTextListener = editTextListener;
            toDoListEditText = (EditText) itemView.findViewById(R.id.toDoListEditText);
            toDoListEditText.addTextChangedListener(editTextListener);

        }



        void onBind(ToDoListItem item){

            toDoListEditText.setText(item.toDoList);
            //toDoListCheckBox.isChecked();

            toDoListCheckBox.setChecked(item.check);

        }

        public String GetToDoList(){
            return toDoList = toDoListEditText.getText().toString();
        }


    }

//    @NonNull
//    @Override
//    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_list_layout, parent, false);
//        return new ViewHolder(view);
//    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.to_do_list_layout, parent, false),
                new EditTextListener());
    }


    private class EditTextListener implements  TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            toDoListArrayList.set(position, charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

//    private class CheckBoxListener implements



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {

        holder.onBind(toDoListItemArrayList.get(position));

        final int pos = position ;

        holder.toDoListEditText.setText(toDoListItemArrayList.get(position).TodoList());
        holder.toDoListCheckBox.setChecked(toDoListItemArrayList.get(position).Check());


//        holder.toDoListEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EditText toDoListEditText = (EditText) view ;
//                toDoListEditText.getText().toString();
//            }
//        });
//
        holder.toDoListCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckBox toDoListCheckBox = (CheckBox) view ;

                checkBoxArrayList[pos] =toDoListCheckBox.isChecked();

            }
        });




    }

    public void setToDoListItemArrayList(ArrayList<ToDoListItem> list){
        this.toDoListItemArrayList = list;
        notifyDataSetChanged();
    }

    public ToDoListItem getItem(int position) {
        return toDoListItemArrayList.get(position); // 아이템 가져오기
    }

    public EditText getToDoList(int position) {
        return getItem(position).GetToDoListEditText();
    }


    @Override
    public int getItemCount() {
        return toDoListItemArrayList.size();
    }


}