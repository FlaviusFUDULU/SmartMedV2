package fragments;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ffudulu.licenta.NewInternare;
import com.example.ffudulu.licenta.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kosalgeek.android.caching.FileCacher;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import Tratamente.Internare;
import Tratamente.Tratament;
import transformations.CircleTransform;
import users.UserMedic;
import users.UserPersonalData;

public class FragmentTreatmentPacient extends Fragment implements AppBarLayout.OnOffsetChangedListener {


    private int count=1;
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mRef;

    private ImageView mProfileImage;
    private ImageView mProfileImageSmall;
    private int mMaxScrollSize;

    private RecyclerView mRecyclerViewALlPacients;

    private FileCacher<UserMedic> userCacher;
    private FileCacher<UserPersonalData> userCacherPacient;
    private FileCacher<String> userPacientUidCacher;
    private FileCacher<String> userCacherType = new FileCacher<>(getActivity(), "type");
    private FileCacher<String> roomCacher;
    private Vector<String> allRooms = new Vector<>();
    private FileCacher<String> actionCacher;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diagnostic, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        AppBarLayout appbarLayout = (AppBarLayout) view.findViewById(R.id.PacientTreatment_appbar);
        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();

        userPacientUidCacher = new FileCacher<>(getActivity(), "UID");
        try {
            mRef = FirebaseDatabase.getInstance().getReference().child("Tratamente")
                                                        .child(userPacientUidCacher.readCache());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            userCacherPacient = new FileCacher<>(getActivity(), userPacientUidCacher.readCache());
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView mFullName = (TextView) view.findViewById(R.id.PacientTreatment_full_name);
        try {
            mFullName.setText(userCacherPacient.readCache().getFirstName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView mEmail = (TextView) view.findViewById(R.id.PacientTreatment_email);
        try {
            mEmail.setText(userCacherPacient.readCache().getLastName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageView mRelease = (ImageView) view.findViewById(R.id.PacientTreatment_profile_release);
        mRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCacher = new FileCacher<>(getActivity(), "action");
                try {
                    actionCacher.writeCache("Release");
                    Intent mSubmitData = new Intent(getActivity(), NewInternare.class);
                    startActivity(mSubmitData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        ImageView mAddTreatment = (ImageView) view
                                        .findViewById(R.id.PacientTreatment_profile_new_treatment);
        mAddTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCacher = new FileCacher<>(getActivity(), "action");
                try {
                    actionCacher.writeCache("NewTreatment");
                    Intent mSubmitData = new Intent(getActivity(), NewInternare.class);
                    startActivity(mSubmitData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            userCacherType = new FileCacher<>(getActivity(), "type");
            if (userCacherType.readCache().contains("Pacient") ||
                    userCacherType.readCache().contains("Family")){
                mRelease.setVisibility(View.GONE);
                mAddTreatment.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mProfileImage = (ImageView) view.findViewById(R.id.PacientTreatment_profile_image);
        try {
            Picasso.with(getActivity()).load(userCacherPacient.readCache().getPhotoUrl())
                    .transform(new CircleTransform())
                    .fit()
                    .into(mProfileImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        userCacher = new FileCacher<>(getActivity(), firebaseUser.getUid());

        mRecyclerViewALlPacients = (RecyclerView) view.findViewById(R.id.RecycleTreatment);
//        mRecyclerViewALlPacients.setHasFixedSize(true);
        mRecyclerViewALlPacients.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();



        FirebaseRecyclerAdapter<Internare, PersonalDataHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Internare, PersonalDataHolder>(
                        Internare.class,
                        R.layout.fragment_template_treatment,
                        PersonalDataHolder.class,
                        mRef

                ) {
                    @Override
                    protected void populateViewHolder(PersonalDataHolder viewHolder, final Internare model, int position) {
                        viewHolder.setDate(model.getDataInternarii());
                        int count = 1;
                        for (Tratament trat : model.getTratamente()){
                            viewHolder.setTreatment(trat.getMedicatie(),count);
                            viewHolder.setDignostic(trat.getDiagnostic());
                            viewHolder.setDetails(trat.getDetalii(),count);
                            viewHolder.setDateOfApply(trat.getDataAplicariiTratamentului(),count);
                            count+=1;
                        }
                        if(model.getDataExternarii()!=null){
                            viewHolder.setDateOfRelease(model.getDataExternarii());
                        } else {
                            viewHolder.mDateOfRelease.setVisibility(View.GONE);
                        }
                        //viewHolder.setTreatment(model.getTratamente());
                    }
                };

        mRecyclerViewALlPacients.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mProfileImageSmall = (ImageView) getView()
                .findViewById(R.id.PacientTreatment_profile_image_small);
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;


        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

            mProfileImage.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(200)
                    .start();
            mProfileImageSmall.setVisibility(getView().VISIBLE);

            ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
            File directory = cw.getDir("SmartMedProfileGeneral", Context.MODE_PRIVATE);
            File myProfilePicSmall = null;
            try {
                myProfilePicSmall = new File(directory, userCacherPacient.readCache().getPhotoUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Picasso.with(getActivity()).load(userCacherPacient.readCache().getPhotoUrl())
                        .transform(new CircleTransform())
                        .fit().into(mProfileImageSmall);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            mProfileImage.animate()
                    .scaleY(1).scaleX(1)
                    .start();
            mProfileImageSmall.setVisibility(getView().INVISIBLE);
        }
    }

    public static class PersonalDataHolder extends RecyclerView.ViewHolder{

        View mView;
        int status = -1;

        TextView mDate;
        TextView mTreatment1;
        TextView mDetails1;
        TextView mDateOfRelease;
        TextView mDiagnostic;
        TextView mDateOfApply1;

        TextView mTreatment2;
        TextView mDetails2;
        TextView mDateOfApply2;

        TextView mTreatment3;
        TextView mDetails3;
        TextView mDateOfApply3;

        public PersonalDataHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mDate = (TextView) mView.findViewById(R.id.PacientTreatmentPersonal_dateOfAdmittance);
            mTreatment1 = (TextView) mView.findViewById(R.id.PacientTreatmentPersonal_treatment1);
            mDetails1 = (TextView) mView.findViewById(R.id.PacientTreatmentPersonal_details1);
            mDateOfRelease = (TextView) mView.findViewById(R.id.PacientTreatmentPersonal_dateOfRelease);
            mDiagnostic = (TextView) mView.findViewById(R.id.PacientTreatmentPersonal_diagnostic);
            mDateOfApply1 = (TextView) mView.findViewById(R.id.PacientTreatmentPersonal_dataOfApply1);

            mTreatment2 = (TextView) mView.findViewById(R.id.PacientTreatmentPersonal_treatment2);
            mDetails2 = (TextView) mView.findViewById(R.id.PacientTreatmentPersonal_details2);
            mDateOfApply2 = (TextView) mView.findViewById(R.id.PacientTreatmentPersonal_dataOfApply2);

            mTreatment3 = (TextView) mView.findViewById(R.id.PacientTreatmentPersonal_treatment3);
            mDetails3 = (TextView) mView.findViewById(R.id.PacientTreatmentPersonal_details3);
            mDateOfApply3 = (TextView) mView.findViewById(R.id.PacientTreatmentPersonal_dataOfApply3);
        }


        public void setDate(String date){

            mDate.setText("Data Internării: " + date);

        }

        public void setDignostic(String diagnostic){
            mDiagnostic.setText("Diagnostic: " + diagnostic);
        }

        public void setTreatment(String treatment, int count){
            if(count==1){
                mTreatment1.setText("Tratament: " + treatment);
                mTreatment2.setVisibility(View.GONE);
                mTreatment3.setVisibility(View.GONE);
            } else if(count==2){
                mTreatment2.setText("Tratament: " + treatment);
                mTreatment2.setVisibility(View.VISIBLE);
                mTreatment3.setVisibility(View.GONE);
            } else if (count==3){
                mTreatment3.setText("Tratament: " + treatment);
                mTreatment2.setVisibility(View.VISIBLE);
                mTreatment3.setVisibility(View.VISIBLE);
            }

        }

        public void setDetails(String details, int count){
            if(count==1){
                mDetails1.setText("Detalii: " + details);
                mDetails2.setVisibility(View.GONE);
                mDetails3.setVisibility(View.GONE);
            } else if(count==2){
                mDetails2.setText("Detalii: " + details);
                mDetails2.setVisibility(View.VISIBLE);
                mDetails3.setVisibility(View.GONE);
            } else if (count==3){
                mDetails3.setText("Detalii: " + details);
                mDetails2.setVisibility(View.VISIBLE);
                mDetails3.setVisibility(View.VISIBLE);
            }

        }

        public void setDateOfRelease(String dateOfRelease){
            mDateOfRelease.setText("Data externării: " + dateOfRelease);
        }

        public void setDateOfApply(String dateOfApply, int count){
            if(count==1){
                mDateOfApply1.setText("Data prescrierii medicației: " + dateOfApply);
                mDateOfApply2.setVisibility(View.GONE);
                mDateOfApply3.setVisibility(View.GONE);
            } else if(count==2){
                mDateOfApply2.setText("Data prescrierii medicației: " + dateOfApply);
                mDateOfApply2.setVisibility(View.VISIBLE);
                mDateOfApply3.setVisibility(View.GONE);
            } else if (count==3){
                mDateOfApply3.setText("Data prescrierii medicației: " + dateOfApply);
                mDateOfApply2.setVisibility(View.VISIBLE);
                mDateOfApply3.setVisibility(View.VISIBLE);
            }

        }
    }
}