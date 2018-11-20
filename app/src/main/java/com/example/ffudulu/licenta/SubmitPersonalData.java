package com.example.ffudulu.licenta;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import users.UserMedic;

public class SubmitPersonalData extends Activity {

    private String[] personalType = {"Doctor" , "Asistent"};
    private ArrayAdapter<String> adapterPersonalType;
    private Spinner mPersonalTypeSpinner;
    private FirebaseUser firebaseUser;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mHospitalName;
    private EditText mSectionName;
    private EditText mDateofBirth;
    private EditText mCNP;
    private EditText mID;
    private EditText mAddress;
    private EditText mPhoneNumber;

    private String phoneNumber;
    private String dateBirth;
    private String cnp;
    private String id;
    private String address;
    private Button mBtnSave;
    private String firstName;
    private String lastName;
    private String hospitalName;
    private String sectionName;
    private String rank;
    private FirebaseAuth mAuth;
    private ImageButton mPhotoUploadBtn;
    private ImageButton mTakePhotoBtn;
    private ImageView mImgProfilePic;
    private ProgressBar mProgressBarUpload;
    private Uri photoUrl;

    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    private DatabaseReference databaseRef;

    private static final int CAMERA_REQUEST_CODE = 1;

    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;

    private StorageReference mStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_personal_data);


        mTakePhotoBtn = (ImageButton) findViewById(R.id.imageButtonPhoto);
        mPhotoUploadBtn = (ImageButton) findViewById(R.id.imageButtonUpload);
        mImgProfilePic = (ImageView) findViewById(R.id.ProfilePicture);

        mProgressBarUpload = (ProgressBar) findViewById(R.id.progressBarUpload);

        mStorage = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        mFirstName = (EditText) findViewById(R.id.txtFirstName);
        mLastName = (EditText) findViewById(R.id.txtLastName);

        mHospitalName = (EditText) findViewById(R.id.txtHospitalName);
        mSectionName = (EditText) findViewById(R.id.txtSectia);

        mDateofBirth = (EditText) findViewById(R.id.editTextDate);
        mPhoneNumber = (EditText) findViewById(R.id.txtPhone);
        mCNP = (EditText) findViewById(R.id.txtCNP);
        mID = (EditText) findViewById(R.id.txtID);
        mAddress = (EditText) findViewById(R.id.txtAdresa);

        //Spinner
        mPersonalTypeSpinner = (Spinner) findViewById(R.id.spinnerFunction);
        adapterPersonalType = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, personalType);
        adapterPersonalType = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, personalType);
        adapterPersonalType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPersonalTypeSpinner.setAdapter(adapterPersonalType);

        //END

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();//.child("Doctor");
        String fullname = null;

        //The rest

        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                myCalendar.set(Calendar.YEAR, i);
                myCalendar.set(Calendar.MONTH, i1);
                myCalendar.set(Calendar.DAY_OF_MONTH, i2);
                updateLabel();
            }


        };

        mDateofBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SubmitPersonalData.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        mBtnSave = (Button) findViewById(R.id.btnSaveChanges);

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableAll();
                firstName = mFirstName.getText().toString().trim();
                lastName = mLastName.getText().toString().trim();
                hospitalName = mHospitalName.getText().toString().trim();
                sectionName = mSectionName.getText().toString().trim();
                rank = mPersonalTypeSpinner.getSelectedItem().toString().trim();
                cnp = mCNP.getText().toString().trim();
                id = mID.getText().toString().trim();
                address = mAddress.getText().toString().trim();
                dateBirth = mDateofBirth.getText().toString().trim();
                phoneNumber = mPhoneNumber.getText().toString().trim();

                if (!TextUtils.isEmpty(firstName) || !TextUtils.isEmpty(lastName) ||
                        !TextUtils.isEmpty(hospitalName) || !TextUtils.isEmpty(sectionName)
                        || !TextUtils.isEmpty(firebaseUser.getPhotoUrl().toString().trim())) {

                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(firstName + " " + lastName).setPhotoUri(photoUrl)
                            .build();
                    firebaseUser.updateProfile(profileUpdate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        UserMedic userMedic = new UserMedic(
                                                firebaseUser.getUid(), firebaseUser.getEmail(),
                                                firstName, hospitalName, lastName,
                                                rank, sectionName, cnp, id, address, phoneNumber,
                                                dateBirth, photoUrl.toString());

                                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                        Date date = new Date();

                                        Notification notif = new Notification(
                                            "Cont nou de asistent creat", userMedic, "Doctor Only",
                                                "1", "New Medic", dateFormat.format(date).toString()
                                        );

                                        savePersonalData(firebaseUser, userMedic);
                                        saveNotification(notif);

                                        Toast.makeText(SubmitPersonalData.this,
                                                "înregistrare reușită!" ,
                                                Toast.LENGTH_SHORT).show();
                                        enableAll();
                                    }
                                    Intent mInitialize = new Intent(SubmitPersonalData.this, Initialization.class);
                                    startActivity(mInitialize);
                                }
                            });
                }
                else{
                    Toast.makeText(SubmitPersonalData.this, "Toate câmpurile sunt necesare!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        mTakePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mCamera= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (mCamera.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(mCamera, CAMERA_REQUEST_CODE);
                }
                //mProgressBarUpload.setVisibility(View.VISIBLE);
            }
        });
        mPhotoUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Alege poza!"), SELECT_PICTURE);

            }
        });
        //end

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
                mImgProfilePic.setVisibility(View.GONE);

                filepath.putFile(selectedImageUri).addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(SubmitPersonalData.this,
                                "Încărcare reușită!" ,
                                Toast.LENGTH_SHORT).show();
                        mProgressBarUpload.setVisibility(View.GONE);
                        photoUrl = taskSnapshot.getDownloadUrl();
                        Picasso.with(SubmitPersonalData.this).load(photoUrl)
                                .resize(200, 200).noFade()
                                .into(mImgProfilePic);
                        enableAll();
                        mImgProfilePic.setVisibility(View.VISIBLE);
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

    private void disableAll(){
        mFirstName.setEnabled(false);
        mLastName.setEnabled(false);
        mTakePhotoBtn.setEnabled(false);
        mPhotoUploadBtn.setEnabled(false);
        mBtnSave.setEnabled(false);
    }

    private void enableAll(){
        mFirstName.setEnabled(true);
        mLastName.setEnabled(true);
        mTakePhotoBtn.setEnabled(true);
        mPhotoUploadBtn.setEnabled(true);
        mBtnSave.setEnabled(true);
    }

    private void savePersonalData(FirebaseUser firebaseUser, UserMedic userMedic){
        databaseRef.child("Users").child(userMedic.getRank()).child(firebaseUser.getUid()).setValue(userMedic);
    }

    private void saveNotification(Notification notif){
        databaseRef.child("Notifications").child(notif.getDateAndTime().toString().replace("/","")
                .replace(" ","").replace(":","")).setValue(notif);
    }

    private void updateLabel() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mDateofBirth.setText(sdf.format(myCalendar.getTime()));
    }
}
