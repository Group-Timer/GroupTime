package com.example.grouptimer;

import static android.os.Build.USER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.grouptimer.model.User;
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

    private RecyclerView recyclerView;
    private User user;
    private AlertDialog.Builder dialog;
    private int checkNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_id);
        String id;
        String Email;

        dialog = new AlertDialog.Builder(this);
        searchButton = (Button) findViewById(R.id.search_button);
        editNumber = (EditText) findViewById(R.id.phoneEdit);
        rootRef = FirebaseDatabase.getInstance().getReference("Users");

        //clickListener override
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNumber = Integer.parseInt(editNumber.getText().toString());
                Query query =
                FirebaseDatabase.getInstance().getReference("Users");
                 //       .orderByChild("phoneNumber").equalTo(checkNumber);
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
//                User user = dataSnapshot.getValue(User.class);
//                Log.d("user", "user: "+ user);
//                int pnum = user.phoneNumber;
//                Log.d("eMail", "pnum: "+pnum);
                dialog.setTitle("Email").setMessage(UserEmail).create().show();
            }
            else{
                Toast.makeText(SearchIDActivity.this, "해당하는 이메일을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
}
//
//public class SearchIDActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private UserAdapter adapter;
//    private List<User> artistList;
//
//    DatabaseReference dbArtists;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search_id);
//
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        artistList = new ArrayList<>();
//        adapter = new UserAdapter(this, artistList);
//        recyclerView.setAdapter(adapter);
//
//        //1. SELECT * FROM Artists
//        dbArtists = FirebaseDatabase.getInstance().getReference("Artists");
//
//        //2. SELECT * FROM Artists WHERE id = "-LAJ7xKNj4UdBjaYr8Ju"
//        Query query = FirebaseDatabase.getInstance().getReference("Artists")
//                .orderByChild("id")
//                .equalTo("-LAJ7xKNj4UdBjaYr8Ju");
//        query.addListenerForSingleValueEvent(valueEventListener);
//    }
//
//
//    ValueEventListener valueEventListener = new ValueEventListener() {
//        @Override
//        public void onDataChange(DataSnapshot dataSnapshot) {
//            artistList.clear();
//            if (dataSnapshot.exists()) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    User artist = snapshot.getValue(User.class);
//                    artistList.add(artist);
//                }
//                adapter.notifyDataSetChanged();
//            }
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//
//        }
//    };
//}
