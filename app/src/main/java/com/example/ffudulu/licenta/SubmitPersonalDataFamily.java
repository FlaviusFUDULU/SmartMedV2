package com.example.ffudulu.licenta;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import users.UserFamily;

public class SubmitPersonalDataFamily extends Activity {

    private ImageView mFamilyProfilePic;
    private EditText mFamilyFirstName;
    private EditText mFamilyLastName;
    private ImageButton mFamilyUploadPhoto;
    private TextView mFamilyUploadPhotoTxt;
    private EditText mFamilyUID;
    private Button mFamilySaveChanges;
    private ProgressBar mFamiliProgressBar;

    private String familyFirstNme = null;
    private String familyLastName = null;
    private String familyPacientUID = null;

    private Uri photoUrl;

    private DatabaseReference databaseRef;

    private static final int CAMERA_REQUEST_CODE = 1;

    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;

    private StorageReference mStorage;

    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_submit_personal_data_family);

        mFamilyProfilePic = (ImageView) findViewById(R.id.Family_ProfilePicture);

        mFamilyFirstName = (EditText) findViewById(R.id.Family_txtFirstName);
        mFamilyLastName = (EditText) findViewById(R.id.Family_txtLastName);
        mFamilyUID = (EditText) findViewById(R.id.Family_txtPacientID);

        mFamilyUploadPhoto = (ImageButton) findViewById(R.id.Family_imageButtonUpload);

        mFamilyUploadPhotoTxt = (TextView) findViewById(R.id.Family_lblUploadPhoto);


        mFamilySaveChanges = (Button) findViewById(R.id.Family_btnSaveChanges);

        mFamiliProgressBar = (ProgressBar) findViewById(R.id.Family_progressBarUpload);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        mStorage = FirebaseStorage.getInstance().getReference();

        mFamilyUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Alege poza!"), SELECT_PICTURE);
            }
        });

        mFamilySaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                familyFirstNme = mFamilyFirstName.getText().toString().trim();
                familyLastName = mFamilyLastName.getText().toString().trim();
                familyPacientUID = mFamilyUID.getText().toString().trim();

                if(!TextUtils.isEmpty(familyFirstNme) || !TextUtils.isEmpty(familyLastName) ||
                        !TextUtils.isEmpty(familyPacientUID)){
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(familyFirstNme + " " + familyLastName).setPhotoUri(photoUrl)
                            .build();
                    firebaseUser.updateProfile(profileUpdate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        UserFamily userFamily = new UserFamily(
                                                firebaseUser.getEmail(),
                                                familyFirstNme,familyLastName,
                                                familyPacientUID,firebaseUser.getUid()
                                        );


                                        savePersonalData(firebaseUser, userFamily);

                                        Toast.makeText(SubmitPersonalDataFamily.this,
                                                "înregistrare reușită!",
                                                Toast.LENGTH_SHORT).show();
                                        enableAll();
                                    }
                                    Intent mInitialize = new Intent(SubmitPersonalDataFamily.this,
                                                                              Initialization.class);
                                    startActivity(mInitialize);
                                }
                            });
                }
                else{
                    Toast.makeText(SubmitPersonalDataFamily.this, "Toate câmpurile sunt necesare!",
                            Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);

                StorageReference filepath = mStorage.child(selectedImageUri.getLastPathSegment()).
                        child(selectedImageUri.getLastPathSegment());

                mFamiliProgressBar.setVisibility(View.VISIBLE);
                disableAll();
                mFamilyProfilePic.setVisibility(View.GONE);

                filepath.putFile(selectedImageUri).addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(SubmitPersonalDataFamily.this,
                                        "Încărcare reușită!" ,
                                        Toast.LENGTH_SHORT).show();
                                mFamiliProgressBar.setVisibility(View.GONE);
                                photoUrl = taskSnapshot.getDownloadUrl();
                                Picasso.with(SubmitPersonalDataFamily.this).load(photoUrl)
                                        .resize(200, 200).noFade()
                                        .into(mFamilyProfilePic);
                                enableAll();
                                mFamilyProfilePic.setVisibility(View.VISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            Uri uri = data.getData();
        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    private void enableAll(){

    }

    private void disableAll(){

    }

    private void savePersonalData(FirebaseUser firebaseUser, UserFamily userFamily){
        databaseRef.child("Users").child("Family").child(firebaseUser.getUid())
                .setValue(userFamily);
    }
}
