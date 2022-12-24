package com.example.grouptimer.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.grouptimer.Common.DefineValue;
import com.example.grouptimer.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GroupToDoListFragment extends Fragment {

    Fragment mainFragment;
    EditText inputToDo;

    public static ArrayList<String> toDoListArrayList ;

    public static ArrayList<Boolean> checkArrayList ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_group_to_do_list, container, false);

        mainFragment = new MainFragment();
        toDoListArrayList = new ArrayList<String>();
        checkArrayList = new ArrayList<Boolean>();

        inputToDo = rootView.findViewById(R.id.inputToDo);

        //getSupportFragmentManager 을 이용하여 이전에 만들었던 **FrameLayout**에 `fragment_main.xml`이 추가
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();

        Button saveButton = rootView.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                saveToDo();

                Toast.makeText(getActivity().getApplicationContext(),"추가되었습니다.",Toast.LENGTH_SHORT).show();

            }
        });


        return rootView;

    }


    private void saveToDo(){

        //EditText에 적힌 글을 가져오기
        String todo = inputToDo.getText().toString();

        for(int i = 0 ; i < toDoListArrayList.size() ; i++ ){

            if( todo.equals( toDoListArrayList.get(i) ) ){

                Toast.makeText(getActivity().getApplicationContext(),"이미 존재하는 할 일 입니다.",Toast.LENGTH_SHORT).show();

                inputToDo.setText("");

                return;

            }
        }

        toDoListArrayList.add(todo);

        checkArrayList.add(false);

        FirebaseDatabase.getInstance()
                .getReference().child("Groups").child(DefineValue.Group_ID).child("ToDoList").setValue(toDoListArrayList);

        FirebaseDatabase.getInstance()
                .getReference().child("Groups").child(DefineValue.Group_ID).child("CheckBox").setValue(checkArrayList);

        FirebaseDatabase.getInstance()
                .getReference().child("Groups").child(DefineValue.Group_ID).child("ToDoListCnt").setValue(toDoListArrayList.size());


        //저장과 동시에 EditText 안의 글 초기화
        inputToDo.setText("");
    }
}