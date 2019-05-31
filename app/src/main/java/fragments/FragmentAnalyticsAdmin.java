package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ffudulu.licenta.Notification;
import com.example.ffudulu.licenta.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kosalgeek.android.caching.FileCacher;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import transformations.CircleTransform;
import users.UserMedic;
import users.UserPersonalData;

public class FragmentAnalyticsAdmin extends Fragment {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private ValueEventListener mListener;

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FileCacher<UserMedic> userAnalytics;
    private DatabaseReference mRef;
    private DatabaseReference mRef2;

    private ImageView mProfileImage;
    private ImageView mProfileImageSmall;
    private int mMaxScrollSize;

    private ImageView mSearchBtn;
    private EditText mNameToSearch;

    private RecyclerView mRecyclerViewAllRequests;

    private FileCacher<UserMedic> userCacher;
    private FileCacher<UserPersonalData> userPacientCacher;
    private FileCacher<String> userPacientUidCacher;
    private FileCacher<String> actionCacher;
    private FileCacher<String> actionCacherDrawer;

    private int bestTime = 0;
    private int worstTime = 0;
    private int allTime = 0;
    private int allTimeThisWeek = 0;
    private int nrOfInterventions = 0;
    private int countWeek = 0;

    UserMedic userCached;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics_admin, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        try {
            userAnalytics = new FileCacher<>(getActivity(), "userAnalytics");
            userCached = userAnalytics.readCache();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mRef2 = FirebaseDatabase.getInstance().getReference();

        userCacher = new FileCacher<>(getActivity(), firebaseUser.getUid());

        mRecyclerViewAllRequests = (RecyclerView) view.findViewById(R.id.RecycleAllPacients);
//        mRecyclerViewALlPacients.setHasFixedSize(true);
        mRecyclerViewAllRequests.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNameToSearch = (EditText) view.findViewById(R.id.search_nametxt);
        mSearchBtn = (ImageView) view.findViewById(R.id.search_userBtn);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseRecyclerAdapter<Notification, PersonalDataHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Notification, PersonalDataHolder>(
                        Notification.class,
                        R.layout.fragment_display_request,
                        PersonalDataHolder.class,
                        mRef

                ) {
                    @Override
                    protected void populateViewHolder(final PersonalDataHolder viewHolder, final Notification model, int position) {
                        if (mNameToSearch.getText() != null &&
                                (model.getDuration().startsWith(String.valueOf(mNameToSearch.getText()))
                                || model.getUserPacient().getLastName().startsWith(String.valueOf(mNameToSearch.getText()))
                                || model.getUserPacient().getFirstName().startsWith(String.valueOf(mNameToSearch.getText()))
                                || model.getUserPacient().getRoom().equals(mNameToSearch.getText()))) {
                            if (model.getHandeled().equals("Yes") &&
                                    model.getUserMedic().getuId().equals(userCached.getuId())) {
                                viewHolder.setName(model.getUserPacient().getFirstName() + " " +
                                        model.getUserPacient().getLastName());
                                viewHolder.setPicture(model.getUserPacient().getPhotoUrl());
                                viewHolder.setSalon(model.getUserPacient().getRoom());
                                String duration =
                                        Integer.toString((int) Math.floor(Integer.parseInt(model.getDuration()) / 60))
                                                + "m:"
                                                + Integer.toString(Math.round(((Integer.parseInt(model.getDuration()) % 60))))
                                                + "s";
                                nrOfInterventions += 1;
                                if (Integer.parseInt(model.getDuration()) < bestTime) {
                                    bestTime = Integer.parseInt(model.getDuration());
                                }

                                if (Integer.parseInt(model.getDuration()) > worstTime) {
                                    worstTime = Integer.parseInt(model.getDuration());
                                }

                                if (bestTime == 0) {
                                    bestTime = Integer.parseInt(model.getDuration());
                                }

                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date dateNow1 = new Date();
                                String dateNow = dateFormat.format(dateNow1);
                                String day = dateNow.toString().split("/")[2];
                                String actualDay = day.split(" ")[0];

                                String dayRep = model.getDateAndTime().split("/")[2];
                                String actualDayRep = dayRep.split(" ")[0];

                                allTime += Integer.parseInt(model.getDuration());

                                if (Math.abs(Integer.parseInt(actualDay) - Integer.parseInt(actualDayRep)) <= 7) {
                                    allTimeThisWeek += Integer.parseInt(model.getDuration());
                                    countWeek += 1;
                                }

                                setBestTime(Integer.toString((int) Math.floor(bestTime / 60))
                                        + "m:"
                                        + Integer.toString((bestTime % 60))
                                        + "s");
                                setWorstTime(Integer.toString((int) Math.floor(worstTime / 60))
                                        + "m:"
                                        + Integer.toString((worstTime % 60))
                                        + "s");

                                if (allTimeThisWeek != 0) {
                                    float mediumWeek = allTimeThisWeek / countWeek;
                                    String durWeek = Integer.toString((int)Math.floor(mediumWeek / 60))
                                            +"m:"
                                            + Integer.toString((int)(Math.floor(mediumWeek) % 60))
                                            +"s";
                                    setMediumTimeWeek(durWeek);
                                }
                                else{
                                    setMediumTimeWeek("0m:0s");
                                }
                                float mediumAll = allTime / nrOfInterventions;

                                String durAll = Integer.toString((int) Math.floor(mediumAll / 60))
                                        + "m:"
                                        + Integer.toString((int) (Math.floor(mediumAll) % 60))
                                        + "s";

                                setMediumTImeAllTime(durAll);
                                viewHolder.setDuration(duration);
                            }
                            else{
                                viewHolder.mCardView.setVisibility(View.GONE);
                            }
                        }
                        else {
                            viewHolder.mCardView.setVisibility(View.GONE);
                        }
                    }

                };

                mRecyclerViewAllRequests.setAdapter(firebaseRecyclerAdapter);
            }
        });

        return view;
    }

    public void setWorstTime(String worstTime){
        TextView mWorstTime = (TextView) getView().findViewById(R.id.Pacients_worstTime);
        mWorstTime.setText("Cea mai inceata asistență: " + worstTime);

    }

    public void setBestTime(String bestTime){
        TextView mBestTime = (TextView) getView().findViewById(R.id.Pacients_bestTime);
        mBestTime.setText("Cea mai rapida asistență: " + bestTime);

    }

    public void setMediumTimeWeek(String mediumTimeWeek){
        TextView mMediumTimeWeek = (TextView) getView().findViewById(R.id.Pacients_mediumTimeWeek);
        mMediumTimeWeek.setText("Timp mediu de raspuns / saptamana: " + mediumTimeWeek);

    }

    public void setMediumTImeAllTime (String mediumTImeAllTime){
        TextView mMediumTimeAllTime = (TextView) getView().findViewById(R.id.Pacients_mediumTimeAllTime);
        mMediumTimeAllTime.setText("Timpul mediu de raspuns: " + mediumTImeAllTime);

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Notification, PersonalDataHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Notification, PersonalDataHolder>(
                        Notification.class,
                        R.layout.fragment_display_request,
                        PersonalDataHolder.class,
                        mRef

                ) {
                    @Override
                    protected void populateViewHolder(final PersonalDataHolder viewHolder, final Notification model, int position) {
                        if (model.getHandeled().equals("Yes") &&
                                model.getUserMedic().getuId().equals(userCached.getuId()) ){
                            viewHolder.setName(model.getUserPacient().getFirstName() + " " +
                                    model.getUserPacient().getLastName());
                            viewHolder.setPicture(model.getUserPacient().getPhotoUrl());
                            viewHolder.setSalon(model.getUserPacient().getRoom());
                            String duration =
                                    Integer.toString((int)Math.floor(Integer.parseInt(model.getDuration()) / 60))
                                    +"m:"
                                    + Integer.toString(Math.round(((Integer.parseInt(model.getDuration()) % 60))))
                                    +"s";
                            nrOfInterventions+=1;
                            if (Integer.parseInt(model.getDuration()) < bestTime) {
                                bestTime = Integer.parseInt(model.getDuration());
                            }

                            if (Integer.parseInt(model.getDuration()) > worstTime) {
                                worstTime = Integer.parseInt(model.getDuration());
                            }

                            if (bestTime == 0) {
                                bestTime = Integer.parseInt(model.getDuration());
                            }

                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date dateNow1 = new Date();
                            String dateNow = dateFormat.format(dateNow1);
                            String day = dateNow.toString().split("/")[2];
                            String actualDay = day.split(" ")[0];

                            String dayRep = model.getDateAndTime().split("/")[2];
                            String actualDayRep = dayRep.split(" ")[0];

                            allTime += Integer.parseInt(model.getDuration());

                            if(Math.abs(Integer.parseInt(actualDay) - Integer.parseInt(actualDayRep)) < 7){
                                allTimeThisWeek += Integer.parseInt(model.getDuration());
                                countWeek+=1;
                            }

                            setBestTime(Integer.toString((int)Math.floor(bestTime / 60))
                                    +"m:"
                                    + Integer.toString((bestTime % 60))
                                    +"s");
                            setWorstTime(Integer.toString((int)Math.floor(worstTime / 60))
                                    +"m:"
                                    + Integer.toString((worstTime % 60))
                                    +"s");

                            if (allTimeThisWeek != 0) {
                                float mediumWeek = allTimeThisWeek / countWeek;
                                String durWeek = Integer.toString((int)Math.floor(mediumWeek / 60))
                                        +"m:"
                                        + Integer.toString((int)(Math.floor(mediumWeek) % 60))
                                        +"s";
                                setMediumTimeWeek(durWeek);
                            }
                            else{
                                setMediumTimeWeek("0m:0s");
                            }
                            float mediumAll = allTime / nrOfInterventions;



                            String durAll = Integer.toString((int)Math.floor(mediumAll / 60))
                                    +"m:"
                                    + Integer.toString((int)(Math.floor(mediumAll) % 60))
                                    +"s";

                            setMediumTImeAllTime(durAll);
                            viewHolder.setDuration(duration);
                        }
                        else {
                            viewHolder.mCardView.setVisibility(View.GONE);
                        }
                    }
                };

        mRecyclerViewAllRequests.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PersonalDataHolder extends RecyclerView.ViewHolder{

        View mView;
        int status = -1;

        ImageView mSeeProfile;
        ImageView mSeeMedicalRecord;
        ImageView mInternare;
        ImageView mFirstTreatment;
        CardView mCardView;


        public PersonalDataHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mSeeProfile = (ImageView) mView.findViewById(R.id.AllPacients_seeProfile);
            mSeeMedicalRecord = (ImageView) mView.findViewById(R.id.AllPacients_seeMedicalRecord);
            mInternare = (ImageView) mView.findViewById(R.id.AllPacients_makeInternare);
            mFirstTreatment = (ImageView) mView.findViewById(R.id.AllPacients_firstTreatment);
            mCardView = (CardView) mView.findViewById(R.id.AllPacients_cardViewNume);

        }

        public void setName(String fullName){
            TextView mFullName = (TextView) mView.findViewById(R.id.AllPacients_fullName);
            mFullName.setText("Nume pacient: " + fullName);

        }

        public void setSalon(String salon){
            TextView mSalon = (TextView) mView.findViewById(R.id.AllPacients_salon);
            mSalon.setText("Salon:" + salon);
        }
        public void setDuration(String duration){
            TextView mDuration = (TextView) mView.findViewById(R.id.AllPacients_duration);
            mDuration.setText("Durata de rasppuns: " + duration);
        }

        public void setPicture(final String photoUrl){
            final ImageView mImgProfilePic = (ImageView) mView.findViewById(R.id.AllPacients_photo);
//            Picasso.with(mView.getContext()).load(photoUrl).transform(new CircleTransform())
//                    .networkPolicy(NetworkPolicy.OFFLINE)
//                    .fit().into(mImgProfilePic, new Callback() {
//                @Override
//                public void onSuccess() {
//
//                }
//
//                @Override
//                public void onError() {
//
//                    Picasso.with(mView.getContext()).load(photoUrl).into(mImgProfilePic);
//
//                }
//            });
            Picasso.with(mView.getContext()).load(photoUrl)
                    .centerCrop().fit()
                    .transform(new CircleTransform())
                    .noFade()
                    .into(mImgProfilePic);
        }

    }

}
