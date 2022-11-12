package com.example.grouptimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePWActivity extends AppCompatActivity {

    private Button changeButton;
    private EditText originPw;
    private EditText newPw;
    private EditText confirmPw;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);
        String newPassword = newPw.getText().toString();
        String confirm = confirmPw.getText().toString();

         changeButton = findViewById(R.id.changeButton);
         originPw = findViewById(R.id.inputPw);
         newPw = findViewById(R.id.inputNewPw);
         confirmPw = findViewById(R.id.confirmPasswordEditText);

         changeButton.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View view){
                 FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                 email = user.getEmail();
                 password = originPw.getText().toString();
                 AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                 if(credential!=null){
                    if(newPassword == confirm){
                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(),"비밀번호가 변경되었습니다.",Toast.LENGTH_SHORT);
                                        }
                                    }
                                });
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT);
                    }
                 }
                 else {
                     Toast.makeText(getApplicationContext(),"비밀번호를 잘못 입력하였습니다.",Toast.LENGTH_SHORT);
                 }
             }
         });

    }
}