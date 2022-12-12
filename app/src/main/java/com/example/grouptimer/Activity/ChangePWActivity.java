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

import com.example.grouptimer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePWActivity extends AppCompatActivity {

    private Button findButton;
    private EditText findPw;

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

        findButton = findViewById(R.id.findButton);
        findPw = findViewById(R.id.inputEmail);

         changeButton = findViewById(R.id.ChangeButton);
         originPw = findViewById(R.id.inputPw);
         newPw = findViewById(R.id.inputNewPw);
         confirmPw = findViewById(R.id.checkPw);

         findButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FirebaseAuth auth = FirebaseAuth.getInstance();
                 String emailAddress;

                 emailAddress = findPw.getText().toString();

                 AlertDialog.Builder builder = new AlertDialog.Builder(ChangePWActivity.this);
                 builder.setTitle("비밀번호 재설정")
                         .setMessage("재설정 메일이 전송되었습니다.");



                 auth.sendPasswordResetEmail(emailAddress)
                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if (task.isSuccessful()) {
                                     Log.d("SEND", "Email sent.");
                                     builder.create().show();
                                 }
                             }
                         });
             }
         });



         changeButton.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View view){
                 FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                 email = user.getEmail();
                 password = originPw.getText().toString();
                 AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                 String newPassword = newPw.getText().toString();
                 String confirm=confirmPw.getText().toString();

                 if(newPassword.equals(confirm)){
                     FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
                     AuthCredential credentials = EmailAuthProvider
                             .getCredential(email, password);
                     user.reauthenticate(credentials)
                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()) {
                                         user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if (task.isSuccessful()) {
                                                     Toast.makeText(getApplicationContext(),"비밀번호를 변경하였습니다.",Toast.LENGTH_SHORT).show();
                                                 } else {
                                                     Toast.makeText(getApplicationContext(),"비밀번호를 변경에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                 }
                                             }
                                         });
                                     } else {
                                         Toast.makeText(getApplicationContext(),"비밀번호를 잘못 입력하였습니다.",Toast.LENGTH_SHORT).show();
                                     }

                                 }
                             });
                 }
                 else{
                     Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                 }

             }
         });
    }
}