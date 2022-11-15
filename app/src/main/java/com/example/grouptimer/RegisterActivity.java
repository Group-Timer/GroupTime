package com.example.grouptimer;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grouptimer.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private TextView wrongPasswordTextView;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        wrongPasswordTextView = (TextView) findViewById(R.id.wrongPasswordTextView);
        registerButton = (Button) findViewById(R.id.registerButton);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (emailEditText.getText().toString() == null || nameEditText.getText().toString() == null || passwordEditText.getText().toString() == null){
                    return;
                }

                if( password.equals(confirmPassword) == false ){

                    wrongPasswordTextView.setVisibility(View.VISIBLE);

                    return;
                }
                else if( password.equals(confirmPassword) == true ){

                    wrongPasswordTextView.setVisibility(View.INVISIBLE);
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(emailEditText.getText().toString(),passwordEditText.getText().toString())
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    String userName = nameEditText.getText().toString();
                                    int phoneNumber = Integer.parseInt(phoneEditText.getText().toString());
                                    String eMail = emailEditText.getText().toString();
                                    int groupNum = 0;

                                    User user = new User(eMail, groupNum, phoneNumber,userName);

                                    String uid = task.getResult().getUser().getUid();
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid).setValue(user);

                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                }
                            });


                }

            }
        });



    }
}