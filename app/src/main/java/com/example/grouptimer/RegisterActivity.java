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

    private String password;
    private String confirmPassword;

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

                password = passwordEditText.getText().toString();
                confirmPassword = confirmPasswordEditText.getText().toString();

                if (emailEditText.getText().toString() == null || nameEditText.getText().toString() == null ||
                        passwordEditText.getText().toString() == null){
                    return;
                }

                if( password != confirmPassword ){

                    wrongPasswordTextView.setVisibility(View.VISIBLE);
                    passwordEditText.setText(null);
                    confirmPasswordEditText.setText(null);
                    confirmPassword = null;
                    return;
                }


               else if( password == confirmPassword || wrongPasswordTextView.getVisibility() == View.VISIBLE){

                    wrongPasswordTextView.setVisibility(View.INVISIBLE);

                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(emailEditText.getText().toString(),passwordEditText.getText().toString())
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    String userName = nameEditText.getText().toString();
                                    int phoneNumber = Integer.parseInt(phoneEditText.getText().toString());

                                    User user = new User(userName, phoneNumber);


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