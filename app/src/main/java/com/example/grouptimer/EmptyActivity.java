package com.example.grouptimer;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmptyActivity extends AppCompatActivity implements View.OnClickListener {


    Button button1;

    public static FragmentManager manager;

    PersonalTimeTableActivity personalTimeTableActivity;

    GroupToDoListActivity groupToDoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);


        button1 = (Button) findViewById(R.id.button1);

        button1.setOnClickListener(this);

        manager = getSupportFragmentManager();

        personalTimeTableActivity = new PersonalTimeTableActivity();
    }

    @Override
    public void onClick(View view)
    {
        if(view == button1)
        {
            FragmentTransaction tf = manager.beginTransaction();
            tf.replace(R.id.fragmentContainer, personalTimeTableActivity);
            tf.commit();
        }
    }
}