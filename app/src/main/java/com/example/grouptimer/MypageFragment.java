package com.example.grouptimer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.grouptimer.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class MypageFragment extends Fragment  {

    private TextView userEmail;
    private TextView nickName;
    private TextView phoneNumber;
    private Button changeImage;
    private Button changeProfile;
    private Button changePW;
    private String photoName;
    private FirebaseAuth mAuth;
    private String uid;
    private BottomNavigationView bottom;

    private final int GALLERY_CODE = 10;
    ImageView photo;
    private FirebaseStorage storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        return inflater.inflate(R.layout.activity_my_page, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle saveInstanceState){

        storage = FirebaseStorage.getInstance();
        userEmail = view.findViewById(R.id.UserID);
        nickName = view.findViewById(R.id.nickname);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        photo = view.findViewById(R.id.profileIMG);

        changeImage = view.findViewById(R.id.changeImage);
        changeProfile = view.findViewById(R.id.changeProfile);
        changePW = view.findViewById(R.id.changePW);

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
                Glide.with(MypageFragment.this).load(uri).into(photo);
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
                Intent intent = new Intent(getActivity(), ChangeNameActivity.class);
                startActivity(intent);
            }
        });

        changePW.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), ChangePWActivity.class);
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
    @Override
    public void onResume() {super.onResume();}
}
