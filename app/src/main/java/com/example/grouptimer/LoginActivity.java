package com.example.grouptimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

<<<<<<< HEAD
=======
import android.content.Intent;
>>>>>>> a5808a1c168c817965c869f85e971afae6e72c21
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

<<<<<<< HEAD
    Button loginButton;
=======
    Button loginBtn;
>>>>>>> a5808a1c168c817965c869f85e971afae6e72c21
    EditText idEditText;
    EditText passwordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
=======

        setContentView(R.layout.activity_login);


>>>>>>> a5808a1c168c817965c869f85e971afae6e72c21
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        //createAccount("rrrr@naver.com","abcdef");
<<<<<<< HEAD
        setContentView(R.layout.activity_login);
        loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
=======


        idEditText = (EditText)findViewById(R.id.idEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);


        loginBtn = (Button)findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

>>>>>>> a5808a1c168c817965c869f85e971afae6e72c21
    }

    @Override
    public void onClick(View view){
<<<<<<< HEAD
        if(view == loginButton){
=======
        if(view == loginBtn){
>>>>>>> a5808a1c168c817965c869f85e971afae6e72c21

            String id = idEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            signIn(id,password);

        }
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
<<<<<<< HEAD
=======

>>>>>>> a5808a1c168c817965c869f85e971afae6e72c21
    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
        // [END send_email_verification]
    }

    private void reload() { }

<<<<<<< HEAD
    private void updateUI(FirebaseUser user) {

=======
    private void updateUI(FirebaseUser user)
    {
        startActivity(new Intent(this, EmptyActivity.class));
>>>>>>> a5808a1c168c817965c869f85e971afae6e72c21
    }
}