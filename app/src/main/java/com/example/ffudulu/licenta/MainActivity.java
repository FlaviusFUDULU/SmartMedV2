package com.example.ffudulu.licenta;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

import java.util.Map;

import fragments.MainDrawer;

public class MainActivity extends Activity {

    private Button mSendData;
    private Firebase mRoofRef;

    private Firebase mRef;
    private EditText mValueField;
    private Button mAddButton;
    private EditText mKeyValue;
    private TextView mValueView;
    private Button mBtnTest;
    private FirebaseUser firebaseUser;
    private ImageView mTestImageView;
    private Button mBtnTest2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //Pentru butonul de jos ce adauga o val in baza de date
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://licenta-bc669.firebaseio.com/");
        mSendData = (Button) findViewById(R.id.sendData);

        mSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Firebase mrefChild = mRef.child("Name");
                mrefChild.setValue("Signed Out");
                FirebaseAuth.getInstance().signOut();
                Intent mLogIn = new Intent(MainActivity.this, LogIn.class);
                startActivity(mLogIn);
            }
        });
        //END
//        mTestImageView = (ImageView) findViewById(R.id.testImageView);
//        Picasso.with(MainActivity.this).load("http://i.imgur.com/DvpvklR.png").into(mTestImageView);
        //Pentru butonul si field-ul de sus ce adauga valoarea
        //din field in baza de date
        mRoofRef = new Firebase("https://licenta-bc669.firebaseio.com/Users");

        mValueField = (EditText) findViewById(R.id.valueField);
        mAddButton = (Button) findViewById(R.id.addButton);
        mKeyValue = (EditText) findViewById(R.id.kyeField);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String value = mValueField.getText().toString();
                String key = mKeyValue.getText().toString();

                Firebase childRef = mRoofRef.child(key);

                childRef.setValue(value);
                //mRoofRef.push().setValue(value);
            }
        });
        //END
        mValueView = (TextView) findViewById(R.id.valueView);

        mRef = new Firebase("https://licenta-bc669.firebaseio.com/Users/");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, String> map = dataSnapshot.getValue(Map.class);
                String name = map.get("Name");
                String age = map.get("Age");

                //String valoare = dataSnapshot.getValue(String.class);
                mValueView.setText(name+"\n"+age);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mBtnTest = (Button) findViewById(R.id.btnTest);
        mBtnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                        .setDisplayName("Flavius FUDULU")
                        .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/licenta-bc669.appspot.com/o/pp.jpg?alt=media&token=58d45479-b91e-401b-883a-7ffbb59bf9a0"))
                        .build();
                firebaseUser.updateProfile(profileUpdate);
                Intent mDrawer = new Intent(MainActivity.this, MainDrawer.class);
                startActivity(mDrawer);
            }
        });
        //

        mBtnTest2 = (Button) findViewById(R.id.btnTest2);
        mBtnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mPData = new Intent(MainActivity.this, SubmitPersonalData.class);
                startActivity(mPData);
            }
        });
    }
}
