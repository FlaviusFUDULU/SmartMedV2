package com.example.ffudulu.licenta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;

import fragments.MainDrawer;
import users.UserFamily;
import users.UserMedic;
import users.UserPersonalData;

public class Initialization extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseRef;
    private FileCacher<users.UserMedic> userCacher;
    private FileCacher<users.UserPersonalData> userPacientCacher;
    private FileCacher<users.UserFamily> userFamilyCacher;
    private final FileCacher<String> userCacherType = new FileCacher<>(Initialization.this, "type");

    private UserMedic userMedic = null;
    private UserPersonalData userPacient = null;
    private UserFamily userFamily = null;

    private ProgressBar mProgressBarLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_initialization);


        mProgressBarLogin = (ProgressBar) findViewById(R.id.initialization_progressBarLogIn);
        mProgressBarLogin.setVisibility(View.VISIBLE);

        try {
            userCacherType.readCache();
        } catch (IOException e) {
            e.printStackTrace();
            Intent mModeSelect = new Intent(Initialization.this, ModeSelect.class);
            startActivity(mModeSelect);
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
//                    Toast.makeText(LogIn.this, "You are allready logged in",
//                            Toast.LENGTH_LONG).show();
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    databaseRef = FirebaseDatabase.getInstance().getReference();

//                    Picasso.with(Initialization.this).load(firebaseUser.getPhotoUrl())
//                            .into(new SaveImage().picassoImageTarget(getApplicationContext()
//                                    , "SmartMedProfile", firebaseUser.getUid()));
                    databaseRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                for (DataSnapshot dss : ds.getChildren()) {
                                    if (dss.child(firebaseUser.getUid()).getValue() != null) {
                                        try {
                                            if (userCacherType.readCache().contains("Staff")) {
                                                userMedic = new UserMedic(
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserMedic.class).getuId(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserMedic.class).getEmail(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserMedic.class).getFirstName(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserMedic.class).getHospitalName(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserMedic.class).getLastName(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserMedic.class).getRank(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserMedic.class).getSectionName(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserMedic.class).getCnp(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserMedic.class).getId(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserMedic.class).getAddress(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserMedic.class).getPhoneNumber(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserMedic.class).getDateOfBirth(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserMedic.class).getPhotoUrl()
                                                );
                                                cacheData();
                                                mProgressBarLogin.setVisibility(View.GONE);
                                                Intent mDrawer = new Intent(Initialization.this, MainDrawer.class);
                                                startActivity(mDrawer);
                                                break;
                                            } else if (userCacherType.readCache().contains("Pacient")) {

                                                if(dss.child(firebaseUser.getUid())
                                                        .getValue(UserPersonalData.class).getPhotoUrl() == null){
                                                    Toast.makeText(Initialization.this, "Nu s-a facut inca internarea inca!",
                                                            Toast.LENGTH_LONG).show();
                                                    Intent mLogIn = new Intent(Initialization.this, LogIn.class);
                                                    mProgressBarLogin.setVisibility(View.GONE);
                                                    FirebaseAuth.getInstance().signOut();
                                                    startActivity(mLogIn);

                                                }else if(dss.child(firebaseUser.getUid())
                                                        .getValue(UserPersonalData.class).getDiagnostic() == null){

                                                    userPacient = new UserPersonalData(
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getuId(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getFirstName(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getLastName(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getEmail(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getCnp(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getId(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getAge(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getPhotoUrl(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getAddress(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getPhoneNumber(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getAssuranceCode(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getDateOfAddmitance(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getSex(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getRoom()
                                                    );
                                                    cacheData();
                                                    Intent mDrawer = new Intent(Initialization.this, MainDrawer.class);
                                                    mProgressBarLogin.setVisibility(View.GONE);
                                                    startActivity(mDrawer);
                                                    break;
                                                } else if(dss.child(firebaseUser.getUid())
                                                        .getValue(UserPersonalData.class).getDiagnostic() != null) {

                                                    userPacient = new UserPersonalData(
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getuId(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getFirstName(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getLastName(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getEmail(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getCnp(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getId(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getAge(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getPhotoUrl(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getAddress(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getPhoneNumber(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getAssuranceCode(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getDateOfAddmitance(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getDiagnostic(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getTreatment(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getSex(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getRoom(),
                                                            dss.child(firebaseUser.getUid())
                                                                    .getValue(UserPersonalData.class).getAnalisys()
                                                    );
                                                    cacheData();
                                                    Intent mDrawer = new Intent(Initialization.this, MainDrawer.class);
                                                    mProgressBarLogin.setVisibility(View.GONE);
                                                    startActivity(mDrawer);
                                                    break;
                                                }
                                            } else if (userCacherType.readCache().contains("Family")) {
                                                userFamily = new UserFamily(
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserFamily.class).getEmail(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserFamily.class).getFirstName(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserFamily.class).getFirstName(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserFamily.class).getPacientUID(),
                                                        dss.child(firebaseUser.getUid())
                                                                .getValue(UserFamily.class).getuId()
                                                );
                                                userFamily.setActivated(dss.child(firebaseUser.getUid())
                                                        .getValue(UserFamily.class).getActivated());
                                                cacheData();
                                                Intent mDrawer = new Intent(Initialization.this, MainDrawer.class);
                                                mProgressBarLogin.setVisibility(View.GONE);
                                                startActivity(mDrawer);
                                                break;
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Intent mModeSelect = new Intent(Initialization.this, ModeSelect.class);
                                            startActivity(mModeSelect);
                                        }
                                    }
//                                    else {
//                                        try {
//                                            if (userCacherType.readCache().contains("Staff")) {
//                                                Intent mPersonalData = new Intent(Initialization.this, SubmitPersonalData.class);
//                                                mProgressBarLogin.setVisibility(View.GONE);
//                                                startActivity(mPersonalData);
//                                            } else if (userCacherType.readCache().contains("Pacient")) {
//                                                Intent mSubmitPacient = new Intent(Initialization.this, SubmitPersonalDataPacient.class);
//                                                mProgressBarLogin.setVisibility(View.GONE);
//                                                startActivity(mSubmitPacient);
//                                            } else if (userCacherType.readCache().contains("Family")) {
//                                                Intent mSubmitFamily = new Intent(Initialization.this, SubmitPersonalDataFamily.class);
//                                                mProgressBarLogin.setVisibility(View.GONE);
//                                                startActivity(mSubmitFamily);
//                                            }
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else{
                    Intent mModeSelect = new Intent (Initialization.this, ModeSelect.class);
                    mProgressBarLogin.setVisibility(View.GONE);
                    startActivity(mModeSelect);
                }
            }
        };
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

    private void cacheData(){
        try {
            if(userCacherType.readCache().contains("Staff")){
                try {
                    userCacher = new FileCacher<>(Initialization.this, firebaseUser.getUid());
                    userCacher.writeCache(userMedic);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (userCacherType.readCache().contains("Pacient")){
                try {
                    userPacientCacher = new FileCacher<>(Initialization.this, firebaseUser.getUid());
                    userPacientCacher.writeCache(userPacient);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (userCacherType.readCache().contains("Family")){
                try {
                    userFamilyCacher = new FileCacher<>(Initialization.this, firebaseUser.getUid());
                    userFamilyCacher.writeCache(userFamily);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
