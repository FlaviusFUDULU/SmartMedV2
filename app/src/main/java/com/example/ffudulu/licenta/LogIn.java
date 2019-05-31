package com.example.ffudulu.licenta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;

public class LogIn extends Activity {

    private Button mBtnLogin;
    private TextView mTxtSignUp;
    private EditText mEmailField;
    private EditText mPasswordField;
    private ProgressBar mProgressBarLogin;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final FileCacher<String> userCacherType = new FileCacher<>(LogIn.this, "type");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();

        mBtnLogin = (Button) findViewById(R.id.btnLogIn);
        mTxtSignUp = (TextView) findViewById(R.id.txtSignUp);

        mEmailField = (EditText) findViewById(R.id.txtUsername);
        mPasswordField = (EditText) findViewById(R.id.txtPassword);
        mProgressBarLogin = (ProgressBar) findViewById( R.id.progressBarLogIn);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
//                    Toast.makeText(LogIn.this, "You are allready logged in",
//                            Toast.LENGTH_LONG).show();
                    try {
                        if (userCacherType.readCache().contains("Pacient")) {

//                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                            if (firebaseUser.getPhotoUrl() == null) {
//                                Toast.makeText(LogIn.this, "Nu s-a facut inca internarea inca!",
//                                        Toast.LENGTH_LONG).show();
//                                mProgressBarLogin.setVisibility(View.GONE);
//                                FirebaseAuth.getInstance().signOut();
//                                enableAll();
//                            } else {
                                Intent mInitialization = new Intent(LogIn.this, Initialization.class);
                                startActivity(mInitialization);
//                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        mTxtSignUp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                goToRegister();
            }

        });

        mBtnLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                trySignIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        Intent mType = new Intent(LogIn.this, ModeSelect.class);
        startActivity(mType);
    }


    private void trySignIn(){
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();


        mProgressBarLogin.setVisibility( View.VISIBLE);
        disableAll();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            mProgressBarLogin.setVisibility(View.GONE);
            Toast.makeText(LogIn.this, "Unul sau mai multe câmpuri sunt goale", Toast.LENGTH_LONG).show();
            enableAll();

        }
        else{
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                mProgressBarLogin.setVisibility(View.GONE);
                                Toast.makeText(LogIn.this, "Email sau parolă incorectă!", Toast.LENGTH_LONG).show();
                                enableAll();
                            }
                            else{
                                enableAll();
                                mProgressBarLogin.setVisibility(View.GONE);
                                Intent mInitialize = new Intent(LogIn.this, Initialization.class);
                                startActivity(mInitialize);
                            }
                        }
                    });
        }
//        return status;
//        SignIn signedIn = new SignIn(mAuth, email, password);
//        if (signedIn.signIntoAccount() == 0){
//            mProgressBarLogin.setVisibility(View.GONE);
//            Toast.makeText(LogIn.this, "One or both field is empty", Toast.LENGTH_LONG).show();
//            enableAll();
//        }
//        if (signedIn.signIntoAccount() == -1){
//            mProgressBarLogin.setVisibility(View.GONE);
//            Toast.makeText(LogIn.this, "Username or password incorrect!", Toast.LENGTH_LONG).show();
//            enableAll();
//        }
//        if (signedIn.signIntoAccount() == 2){
//            //cacheData();
//            enableAll();
//            mProgressBarLogin.setVisibility(View.GONE);
//            Intent mInitialize = new Intent(LogIn.this, Initialization.class);
//            startActivity(mInitialize);
//        }

            mEmailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    mEmailField.setHint("");
                }
            });
    }

    private void goToRegister(){
        Intent mRegister = new Intent(LogIn.this, Register.class);
        //Intent mRegister = new Intent(LogIn.this, QRActivity.class);
        startActivity(mRegister);
    }

    private void enableAll(){
        mEmailField.setEnabled(true);
        mPasswordField.setEnabled(true);
        mBtnLogin.setEnabled(true);
    }

    private void disableAll(){
        mEmailField.setEnabled(false);
        mPasswordField.setEnabled(false);
        mBtnLogin.setEnabled(false);
    }

}
