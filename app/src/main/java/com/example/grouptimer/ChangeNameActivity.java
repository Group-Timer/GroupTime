package com.example.grouptimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.grouptimer.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChangeNameActivity extends AppCompatActivity {
    private String name;
    private Button check;
    private Button change;
    private EditText input;
    private boolean accept;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        DatabaseReference root;

        check = findViewById(R.id.checkButton);
        change = findViewById(R.id.changeButton);
        input = findViewById(R.id.nameEdit);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        accept = false;

        String uid = user.getUid();
        root = FirebaseDatabase.getInstance().getReference("Users");

        check.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                name = input.getText().toString();
                Log.d("name2", name);
                FirebaseDatabase.getInstance().getReference("Users").orderByChild("userName").equalTo(name)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String value="";
                        User user = snapshot.getValue(User.class);
                        Log.d("user", "user: "+ user);
                        Log.d("Uid","user"+uid);
                        if(snapshot.exists()){
                            Toast.makeText(getApplicationContext(),"이미 존재하는 닉네임입니다.",Toast.LENGTH_SHORT).show();//토스메세지 출력
                            accept = false;
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"사용가능한 닉네임입니다.",Toast.LENGTH_SHORT).show();//토스메세지 출력
                            accept = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 디비를 가져오던중 에러 발생 시
                        //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                    }
                });
            }
        });

        change.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                name = input.getText().toString();
                FirebaseDatabase.getInstance().getReference("Users").orderByChild("userName").equalTo(name)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String value="";
                                User user = snapshot.getValue(User.class);
                                Log.d("user", "user: "+ user);
                                if(snapshot.exists()){
                                    Toast.makeText(getApplicationContext(),"이미 존재하는 닉네임입니다.",Toast.LENGTH_SHORT).show();//토스메세지 출력
                                    accept = false;
                                }
                                else{
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("userName").setValue(name);
                                    Toast.makeText(getApplicationContext(),"변경이 완료되었습니다.",Toast.LENGTH_SHORT).show();//토스메세지 출력
                                    accept = true;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // 디비를 가져오던중 에러 발생 시
                                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                            }
                        });
            }
        });
    }
}