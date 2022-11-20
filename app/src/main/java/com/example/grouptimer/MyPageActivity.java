package com.example.grouptimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.grouptimer.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class MyPageActivity extends AppCompatActivity {

    private TextView userEmail;
    private TextView nickName;
    private TextView phoneNumber;
    private Button changeImage;
    private Button changeProfile;
    private Button changePW;
    private String photoName;
    private FirebaseAuth mAuth;
    private String uid;

    private final int GALLERY_CODE = 10;
    ImageView photo;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        storage = FirebaseStorage.getInstance();
        userEmail = findViewById(R.id.UserID);
        nickName = findViewById(R.id.nickname);
        phoneNumber = findViewById(R.id.phoneNumber);
        photo = findViewById(R.id.profileIMG);

        changeImage = findViewById(R.id.changeImage);
        changeProfile = findViewById(R.id.changeProfile);
        changePW = findViewById(R.id.changePW);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser userInfo = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        uid = user.getUid();
        userEmail.setText(userInfo.getEmail());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        storageReference.child(uid+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(MyPageActivity.this).load(uri).into(photo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        Query query =
                FirebaseDatabase.getInstance().getReference("Users");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    User user = data.getValue(User.class);
                    int num = user.getPhoneNumber();
                    if(!(user.geteMail()==null)) {
                        if (user.geteMail().equals(userInfo.getEmail())) {
                            nickName.setText(user.getUserName());
                            phoneNumber.setText(Integer.toString(num));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        changeProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MyPageActivity.this, ChangeNameActivity.class);
                startActivity(intent);
            }
        });

        changePW.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MyPageActivity.this, ChangePWActivity.class);
                startActivity(intent);
            }
        });

        changeImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, GALLERY_CODE);
            }
        });


    }

    protected void onActivityResult(int requestCode, final int resultCode, final Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==GALLERY_CODE){
            Uri file = data.getData();
            StorageReference storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child(uid+".png");
            UploadTask uploadTask = riversRef.putFile(file);

            try{
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                photo.setImageBitmap(img);
            }catch(Exception e){
                e.printStackTrace();
            }

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MyPageActivity.this, "사진이 정상적으로 업로드 되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}