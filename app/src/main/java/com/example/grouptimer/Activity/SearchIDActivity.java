package com.example.grouptimer.Activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.grouptimer.Object.User;
import com.example.grouptimer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class SearchIDActivity extends AppCompatActivity {
    private Button searchButton;
    private EditText editNumber;

    private DatabaseReference rootRef;

    private AlertDialog.Builder dialog;
    private int checkNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_id);


        dialog = new AlertDialog.Builder(this);
        searchButton = (Button) findViewById(R.id.search_button);
        editNumber = (EditText) findViewById(R.id.phoneEdit);
        rootRef = FirebaseDatabase.getInstance().getReference("Users");

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNumber = Integer.parseInt(editNumber.getText().toString());
                Query query = FirebaseDatabase.getInstance().getReference("Users");
                query.addListenerForSingleValueEvent(valueEventListener);
            }
        });
    }


    ValueEventListener valueEventListener = new ValueEventListener(){
        @Override
        public void onDataChange(DataSnapshot dataSnapshot){
            String UserEmail = "";
            if(dataSnapshot.exists()){
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    Log.d("result", "User name: " + user.getUserName() + ", phonenum " + user.getPhoneNumber());
                    if (user.getPhoneNumber() == checkNumber){
                        UserEmail = user.geteMail();
                        break;
                    }
                }

                dialog.setTitle("Email").setMessage(UserEmail).create().show();
            }
            if(UserEmail==""){
                Toast.makeText(SearchIDActivity.this, "해당하는 이메일을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
}