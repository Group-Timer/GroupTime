package com.example.grouptimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        FirebaseUser user = mAuth.getCurrentUser();
        uid = user.getUid();
        storage = FirebaseStorage.getInstance();
        userEmail = findViewById(R.id.UserID);
        nickName = findViewById(R.id.nickname);
        phoneNumber = findViewById(R.id.phoneNumber);
        photo = findViewById(R.id.profileIMG);

        changeImage = findViewById(R.id.changeImage);
        changeProfile = findViewById(R.id.changeProfile);
        changePW = findViewById(R.id.changePW);

        FirebaseUser userInfo = FirebaseAuth.getInstance().getCurrentUser();

        userEmail.setText(userInfo.getEmail());
        nickName.setText(userInfo.getDisplayName());
        phoneNumber.setText(userInfo.getPhoneNumber());

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