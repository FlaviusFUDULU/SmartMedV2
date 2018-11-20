package com.example.ffudulu.licenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;
import java.util.ArrayList;

import Tratamente.Internare;
import Tratamente.Tratament;
import fragments.MainDrawer;
import users.UserPersonalData;

public class NewInternare extends AppCompatActivity {

    private EditText mUid;
    private EditText mFirstName;
    private EditText mlastName;
    private EditText mDateOfAdmittance;
    private EditText mDiagnostic;
    private EditText mMedication;
    private EditText mDateOfMedication;
    private EditText mDetails;
    private EditText mDateOfRelease;
    private Button mSave;
    private ProgressBar mProgressBar;

    private DataSnapshot ds;

    private Tratament tratament;
    private ArrayList<Tratament> tratamentNou;

    private DatabaseReference databaseRef;

    private FileCacher<String> userPacientUidCacher;
    private FileCacher<UserPersonalData> userPacientCacher;
    private FileCacher<String> actionCacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_internare);

        mUid = (EditText) findViewById(R.id.PacientInt_uID);
        mFirstName = (EditText) findViewById(R.id.PacientInt_txtFirstName);
        mlastName = (EditText) findViewById(R.id.PacientInt_txtLastName);
        mDateOfAdmittance = (EditText) findViewById(R.id.PacientInt_txtdataInternarii);
        mDiagnostic = (EditText) findViewById(R.id.PacientInt_diagnostic);
        mMedication = (EditText) findViewById(R.id.PacientInt_medicatie);
        mDateOfMedication = (EditText) findViewById(R.id.PacientInt_dataMedicatiei);
        mDetails = (EditText) findViewById(R.id.PacientInt_detalii);
        mDateOfRelease = (EditText) findViewById(R.id.PacientInt_dataexternarii);
        mSave = (Button) findViewById(R.id.PacientInt_btnSaveChanges);
        mProgressBar = (ProgressBar) findViewById(R.id.PacientInt_progressBarUpload);
        actionCacher = new FileCacher<>(NewInternare.this, "action");

        try {
            if(actionCacher.readCache().contains("Release")){
                completePersonal();
                completeTreatment();
            }else if(actionCacher.readCache().contains("NewTreatment")){
                completePersonal();
                mDateOfRelease.setText("Data externarii");
                mDateOfRelease.setEnabled(false);
            } else if (actionCacher.readCache().contains("InitialTreatment")){
                completePersonal();
                mDateOfRelease.setEnabled(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tratament = new Tratament(mDiagnostic.getText().toString().trim(),
                       mMedication.getText().toString().trim(),
                        mDateOfMedication.getText().toString().trim(),
                        mDetails.getText().toString().trim());
                userPacientUidCacher = new FileCacher<>(NewInternare.this, "UID");
                databaseRef = FirebaseDatabase.getInstance().getReference();//.child("Tratamente");

                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.child("Tratamente").hasChild(userPacientUidCacher.readCache())) {
                                ds = dataSnapshot.child("Tratamente")
                                        .child(userPacientUidCacher.readCache())
                                        .child(mDateOfAdmittance.getText().toString().trim());
                                if(ds.hasChildren()) {
                                    if (ds.getValue(Internare.class).getTratamente() != null) {
                                        tratamentNou = ds.getValue(Internare.class).getTratamente();
                                        tratamentNou.add(tratament);
                                        Internare internare = new Internare(
                                                mDateOfAdmittance.getText().toString().trim(),
                                                tratamentNou);
                                        saveData(mDateOfAdmittance.getText().toString().trim(), internare);
                                    }
                                }else {
                                    tratamentNou = new ArrayList<Tratament>();
                                    tratamentNou.add(tratament);
                                    Internare internare = new Internare(
                                            mDateOfAdmittance.getText().toString().trim(),
                                            tratamentNou);
                                    saveData(mDateOfAdmittance.getText().toString().trim(), internare);
                                    saveData(mDateOfAdmittance.getText().toString().trim(), internare);
                                }
                            }else {
                                tratamentNou = new ArrayList<Tratament>();
                                tratamentNou.add(tratament);
                                Internare internare = new Internare(
                                        mDateOfAdmittance.getText().toString().trim(),
                                        tratamentNou);
                                String dateOfAddmitance = mDateOfAdmittance.getText().toString().trim();
                                saveData(dateOfAddmitance, internare);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }
    private void saveData(String dateofAdmittance, Internare internare){
        userPacientUidCacher = new FileCacher<>(NewInternare.this, "UID");
        try {
            databaseRef.child("Tratamente").child(userPacientUidCacher.readCache())
                    .child(dateofAdmittance.replace("/","")).setValue(internare).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent mDrawer = new Intent(NewInternare.this, MainDrawer.class);
                    startActivity(mDrawer);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void completePersonal(){
        userPacientUidCacher = new FileCacher<>(NewInternare.this, "UID");
        try {
            userPacientCacher = new FileCacher<>(NewInternare.this,
                    userPacientUidCacher.readCache());
            mUid.setText(userPacientCacher.readCache().getuId());
            mUid.setEnabled(false);
            mFirstName.setText(userPacientCacher.readCache().getFirstName());
            mFirstName.setEnabled(false);
            mlastName.setText(userPacientCacher.readCache().getLastName());
            mlastName.setEnabled(false);
            mDateOfAdmittance.setText(userPacientCacher.readCache().getDateOfAddmitance());
            mDateOfAdmittance.setEnabled(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void completeTreatment(){
        mDiagnostic.setEnabled(false);
        mMedication.setEnabled(false);
        mDateOfMedication.setEnabled(false);
        mDetails.setEnabled(false);

        mDiagnostic.setText("Vezi fisa pacient");
        mMedication.setText("Vezi fisa pacient");
        mDateOfMedication.setText("Vezi fisa pacient");
        mDetails.setText("Vezi fisa pacient");
    }



}
