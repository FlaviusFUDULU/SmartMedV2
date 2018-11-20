package com.example.ffudulu.licenta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.kosalgeek.android.caching.FileCacher;
import java.io.IOException;

public class ModeSelect extends Activity{

    private ImageView mDoctor;
    private ImageView mPacient;
    private ImageView mFamily;

    private TextView mTxtDoctor;
    private TextView mTxtPacient;
    private TextView mTxtFamily;

    private String userType = null;

    private FileCacher<String> userCacherType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mode_select);

        mDoctor = (ImageView) findViewById(R.id.modeSelect_medic);
        mPacient = (ImageView) findViewById(R.id.modeSelect_pacient);
        mFamily = (ImageView) findViewById(R.id.modeSelect_family);

        mTxtDoctor = (TextView) findViewById(R.id.modeSelect_txtMedic);
        mTxtPacient = (TextView) findViewById(R.id.modeSelect_txtPactient);
        mTxtFamily = (TextView) findViewById(R.id.modeSelect_txtFamily);

        userCacherType = new FileCacher<>(ModeSelect.this, "type");

        try {
            userCacherType.clearCache();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mLogIn = new Intent(ModeSelect.this, LogIn.class);

                userType = "Staff";

                cacheData();

                startActivity(mLogIn);
            }
        });

        mPacient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mLogIn = new Intent(ModeSelect.this, LogIn.class);

                userType = "Pacient";

                cacheData();

                startActivity(mLogIn);
            }
        });

        mFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mLogIn = new Intent(ModeSelect.this, LogIn.class);

                userType = "Family";

                cacheData();

                startActivity(mLogIn);
            }
        });

        mTxtDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mLogIn = new Intent(ModeSelect.this, LogIn.class);

                userType = "Staff";

                cacheData();

                startActivity(mLogIn);

            }
        });

        mTxtPacient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mLogIn = new Intent(ModeSelect.this, LogIn.class);
                userType = "Pacient";

                cacheData();

                startActivity(mLogIn);
            }
        });

        mTxtFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mLogIn = new Intent(ModeSelect.this, LogIn.class);
                userType = "Family";

                cacheData();

                startActivity(mLogIn);
            }
        });
    }

    private void cacheData() {
        try {
            userCacherType.writeCache(userType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
