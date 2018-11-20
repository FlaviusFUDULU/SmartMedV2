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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kosalgeek.android.caching.FileCacher;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import fragments.MainDrawer;
import users.UserPersonalData;

public class SubmitPersonalDataPacient extends Activity {

    private EditText mPacientFirstName;
    private EditText mPacientLastName;
    private EditText mpacientCNP;
    private EditText mPacientID;
    private EditText mPacientAge;
    private EditText mUID;
    private EditText mAddress;
    private EditText mPhoneNumber;
    private EditText mAssuranceNumber;
    private EditText mEmail;
    private EditText mDateofAdmittance;
    private EditText mRoom;

    private String room = null;
    private String dateOfAdmittande = null;
    private String gender = null;
    private String email = null;
    private String uID = null;
    private String address = null;
    private String phoneNumber = null;
    private String assuranceNumber = null;
    private String pacientFirstName = null;
    private String pacientLastName = null;
    private String pacientCNP = null;
    private String pacientID = null;
    private String pacientAge = null;

    private ImageView mPacientProfilePic;

    private ImageButton mPacientTakePhoto;
    private ImageButton mPacientUploadPhoto;

    private TextView mPacientTakePhotoLbl;
    private TextView mPacientUploadPhotoLbl;

    private Button mPacientSaveData;

    private String[] personalType = {"Masculin" , "Feminin"};
    private ArrayAdapter<String> adapterPersonalType;
    private Spinner mPersonalTypeSpinner;

    private FirebaseUser firebaseUser;

    private ProgressBar mProgressBarUpload;

    private Uri photoUrl;

    private DatabaseReference databaseRef;

    private static final int CAMERA_REQUEST_CODE = 1;

    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;

    private StorageReference mStorage;

    private FileCacher<UserPersonalData> userCacherPacient;
    private FileCacher<String> userPacientUidCacher;
    private FileCacher<Notification> notifCacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_submit_personal_data_pacient);

        //Spinner
        mPersonalTypeSpinner = (Spinner) findViewById(R.id.Pacient_spinnerFunction);
        adapterPersonalType = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, personalType);
        adapterPersonalType = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, personalType);
        adapterPersonalType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPersonalTypeSpinner.setAdapter(adapterPersonalType);
        //END

        mPacientFirstName = (EditText) findViewById(R.id.Pacient_txtFirstName);
        mPacientLastName = (EditText) findViewById(R.id.Pacient_txtLastName);
        mpacientCNP = (EditText) findViewById(R.id.Pacient_txtCNP);
        mPacientID = (EditText) findViewById(R.id.Pacient_txtID);
        mPacientAge = (EditText) findViewById(R.id.Pacient_txtAge);

        mPacientProfilePic = (ImageView) findViewById(R.id.Pacient_ProfilePicture);

        mPacientTakePhoto = (ImageButton) findViewById(R.id.Pacient_imageButtonPhoto);
        mPacientUploadPhoto = (ImageButton) findViewById(R.id.Pacient_imageButtonUpload);

        mPacientTakePhotoLbl = (TextView) findViewById(R.id.Pacient_lblTakePhoto);
        mPacientUploadPhotoLbl = (TextView) findViewById(R.id.Pacient_lblUploadPhoto);

        mPacientSaveData = (Button) findViewById(R.id.Pacient_btnSaveChanges);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        mStorage = FirebaseStorage.getInstance().getReference();

        mProgressBarUpload = (ProgressBar) findViewById(R.id.Pacient_progressBarUpload);

        mPacientUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Alege poza!"), SELECT_PICTURE);
            }
        });

        mUID = (EditText) findViewById(R.id.Pacient_uID);
        mAddress = (EditText) findViewById(R.id.Pacient_txtAddress);
        mPhoneNumber = (EditText) findViewById(R.id.Pacient_txtPhoneNumber);
        mAssuranceNumber = (EditText) findViewById(R.id.Pacient_txtAssunrance);
        mEmail = (EditText) findViewById(R.id.Pacient_email);
        mDateofAdmittance = (EditText) findViewById(R.id.Pacient_txtDatainternarii);
        mRoom = (EditText) findViewById(R.id.Pacient_txtSalon);

        userPacientUidCacher = new FileCacher<>(SubmitPersonalDataPacient.this, "UID");
        try {
            userCacherPacient = new FileCacher<>(SubmitPersonalDataPacient.this,
                                                                userPacientUidCacher.readCache());
            mUID.setText(userCacherPacient.readCache().getuId());
            mEmail.setText(userCacherPacient.readCache().getEmail());
            mUID.setEnabled(false);
            mEmail.setEnabled(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPacientSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pacientFirstName = mPacientFirstName.getText().toString().trim();
                pacientLastName = mPacientLastName.getText().toString().trim();
                pacientCNP = mpacientCNP.getText().toString().trim();
                pacientID = mPacientID.getText().toString().trim();
                pacientAge =  mPacientAge.getText().toString().trim();
                uID = mUID.getText().toString().trim();
                address = mAddress.getText().toString().trim();
                phoneNumber = mPhoneNumber.getText().toString().trim();
                assuranceNumber = mAssuranceNumber.getText().toString().trim();
                email = mEmail.getText().toString().trim();
                gender = mPersonalTypeSpinner.getSelectedItem().toString().trim();
                dateOfAdmittande = mDateofAdmittance.getText().toString().trim();
                room = mRoom.getText().toString().trim();

                if(!TextUtils.isEmpty(pacientFirstName) || !TextUtils.isEmpty(pacientLastName) ||
                        !TextUtils.isEmpty(pacientCNP) || !TextUtils.isEmpty(pacientID) ||
                        !TextUtils.isEmpty(pacientAge)){
//                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
//                            .setDisplayName(pacientFirstName + " " + pacientLastName).setPhotoUri(photoUrl)
//                            .build();
//                    firebaseUser.updateProfile(profileUpdate)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        UserPersonalData userPacient = new UserPersonalData(
//
//                                        );
                    UserPersonalData userPacient = new UserPersonalData(uID,pacientFirstName,
                            pacientLastName, email, pacientCNP, pacientID, pacientAge,
                            photoUrl.toString(), address, phoneNumber, assuranceNumber,
                            dateOfAdmittande, gender, room);

                    savePersonalData(uID, userPacient);

                    Toast.makeText(SubmitPersonalDataPacient.this,
                            "Internare reușită!",
                            Toast.LENGTH_SHORT).show();
                    enableAll();
                    try {
                        userPacientUidCacher.clearCache();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    notifCacher = new FileCacher<>(SubmitPersonalDataPacient.this, "notif");
                    try {
                        Notification notif  = notifCacher.readCache();
                        notif.setHandeled("Yes");
                        databaseRef.child("Notifications").child(notif.getDateAndTime().toString().replace("/","")
                                .replace(" ","").replace(":","")).setValue(notif);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                                    }
                    Intent mInitialize = new Intent(SubmitPersonalDataPacient.this, MainDrawer.class);
                    startActivity(mInitialize);
//                                }
//                            });
                }
                else{
                    Toast.makeText(SubmitPersonalDataPacient.this, "Toate câmpurile sunt necesare!",
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

                mProgressBarUpload.setVisibility(View.VISIBLE);
                disableAll();
                mPacientProfilePic.setVisibility(View.GONE);

                filepath.putFile(selectedImageUri).addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(SubmitPersonalDataPacient.this,
                                        "Încărcare reușită!" ,
                                        Toast.LENGTH_SHORT).show();
                                mProgressBarUpload.setVisibility(View.GONE);
                                photoUrl = taskSnapshot.getDownloadUrl();
                                Picasso.with(SubmitPersonalDataPacient.this).load(photoUrl)
                                        .resize(200, 200).noFade()
                                        .into(mPacientProfilePic);
                                enableAll();
                                mPacientProfilePic.setVisibility(View.VISIBLE);
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
        mPacientFirstName.setEnabled(true);
        mPacientLastName.setEnabled(true);
        mpacientCNP.setEnabled(true);
        mPacientID.setEnabled(true);
        mPacientAge.setEnabled(true);
        mPacientTakePhoto.setEnabled(true);
        mPacientUploadPhoto.setEnabled(true);
        mPacientTakePhotoLbl.setEnabled(true);
        mPacientUploadPhotoLbl.setEnabled(true);
        mPacientSaveData.setEnabled(true);
    }

    private void disableAll(){
        mPacientFirstName.setEnabled(false);
        mPacientLastName.setEnabled(false);
        mpacientCNP.setEnabled(false);
        mPacientID.setEnabled(false);
        mPacientAge.setEnabled(false);
        mPacientTakePhoto.setEnabled(false);
        mPacientUploadPhoto.setEnabled(false);
        mPacientTakePhotoLbl.setEnabled(false);
        mPacientUploadPhotoLbl.setEnabled(false);
        mPacientSaveData.setEnabled(false);
    }

    private void savePersonalData(String uId, UserPersonalData userPacient){
        databaseRef.child("Users").child("Pacient").child(uId)
                .setValue(userPacient);
    }
}
