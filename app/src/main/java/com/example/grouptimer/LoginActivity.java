package com.example.grouptimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

    Button loginButton;
    Button findIdButton;
    Button findPasswordButton;

    EditText idEditText;
    EditText passwordEditText;


    boolean LoginButtonCheck;

    ProgressDialog progressDialog = null;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        LoginButtonCheck = false;


        mAuth = FirebaseAuth.getInstance();

        loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);


        idEditText = (EditText)findViewById(R.id.idEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);

        findIdButton = (Button)findViewById(R.id.findIdButton);
        findIdButton.setOnClickListener(this);

        findPasswordButton = (Button) findViewById(R.id.findPasswordButton);
        findPasswordButton.setOnClickListener(this);
    }




    @Override
    public void onClick(View view){

        if(view == loginButton){

            if(LoginButtonCheck == false)
            {
                LoginButtonCheck = true;


                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        progressDialog = new ProgressDialog(LoginActivity.this, R.style.ProgressDialogTheme);

                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Sign in ...");

                        progressDialog.show();
                    }
                });


                String id = idEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if(id.isEmpty() == true || password.isEmpty() == true)
                {
                    if(progressDialog != null)
                    {
                        progressDialog.dismiss();

                        progressDialog = null;
                    }

                    LoginButtonCheck = false;

                    Toast.makeText(this, "입력이 올바르지 않습니다", Toast.LENGTH_SHORT).show();

                    return;
                }

                signIn(id,password);
            }
        }

        else if(view == findIdButton){
            startActivity(new Intent(this, SearchIDActivity.class));
        }

        else if(view == findPasswordButton){
            startActivity(new Intent(this, ChangePWActivity.class));
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

                            Toast.makeText(LoginActivity.this, "일치하는 회원정보가 없습니다",
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

                            if(progressDialog != null)
                            {
                                progressDialog.dismiss();

                                progressDialog = null;

                                LoginButtonCheck = false;
                            }

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            if(progressDialog != null)
                            {
                                progressDialog.dismiss();

                                progressDialog = null;

                                LoginButtonCheck = false;
                            }

                            Toast.makeText(LoginActivity.this, "일치하는 회원정보가 없습니다",
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

    private void updateUI(FirebaseUser user)
    {
        if(user == null)
            return ;

        startActivity(new Intent(this, HomeActivity.class));

        finish();
    }
}