package fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ffudulu.licenta.Notification;
import com.example.ffudulu.licenta.R;
import com.example.ffudulu.licenta.SubmitPersonalDataPacient;
import com.firebase.client.collection.LLRBNode;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kosalgeek.android.caching.FileCacher;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import transformations.CircleTransform;
import users.UserMedic;
import users.UserPersonalData;

public class FragmentNewsFeed extends Fragment {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mRef;
    private DatabaseReference mRef2;

    private ImageView mProfileImage;
    private ImageView mProfileImageSmall;
    private int mMaxScrollSize;
    private MediaPlayer mMediaPlayer;
    private RecyclerView mRecyclerNewsFeed;

    private FileCacher<UserMedic> userCacher;
    private FileCacher<UserPersonalData> userPacientCacher;
    private FileCacher<String> userPacientUidCacher;
    private FileCacher<String> actionCacher;
    private FileCacher<String> actionCacherDrawer;
    private FileCacher<String> userCacherType;
    private FileCacher<Notification> notifCacher;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed_empty, container, false);

        mRef2 = FirebaseDatabase.getInstance().getReference();

        userCacher = new FileCacher<>(getActivity(), firebaseUser.getUid());

        mRecyclerNewsFeed = (RecyclerView) view.findViewById(R.id.RecycleNewsFeed);
//        mRecyclerViewALlPacients.setHasFixedSize(true);
        LinearLayoutManager linear = new LinearLayoutManager(getActivity());
        linear.setReverseLayout(true);
        linear.setStackFromEnd(true);
        mRecyclerNewsFeed.setLayoutManager(linear);
        userCacherType = new FileCacher<>(getActivity(), "type");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        FirebaseRecyclerAdapter<Notification, PersonalDataHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Notification, PersonalDataHolder>(
                        Notification.class,
                        R.layout.fragment_template_news_feed,
                        PersonalDataHolder.class,
                        mRef

                ) {
                    @Override
                    protected void populateViewHolder(final PersonalDataHolder viewHolder,
                                                      final Notification model, int position) {
                        try {
                            if(model.getHandeled() == null || !model.getHandeled().contains("Yes")) {
                                viewHolder.setDate(model.getDateAndTime());
                                if (userCacherType.readCache().contains("Staff")) {
                                    if (model.getType().contains("New Medic")) {
                                        viewHolder.setName(model.getUserMedic().getFirstName() + " " +
                                                model.getUserMedic().getLastName());
                                        viewHolder.setMessage(model.getMessage());
                                        viewHolder.setPhoto(model.getUserMedic().getPhotoUrl());
                                        viewHolder.mNewInternare.setVisibility(View.GONE);
                                        viewHolder.mgoToPacient.setVisibility(View.GONE);
                                    }
                                    if (model.getType().contains("New Pacient")) {
                                        viewHolder.setName(model.getUserPacient().getEmail());
                                        viewHolder.setMessage(model.getMessage());
                                        //viewHolder.setPhoto(model.getUserMedic().getPhotoUrl());
                                        viewHolder.mNewInternare.setVisibility(View.VISIBLE);
                                        viewHolder.mgoToPacient.setVisibility(View.GONE);
                                        viewHolder.mNewInternare
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        try {
                                                            userPacientUidCacher = new FileCacher<>(getActivity(), "UID");
                                                            userPacientUidCacher.writeCache(model.getUserPacient().getuId().toString());
                                                            userPacientCacher = new FileCacher<>(getActivity(), model.getUserPacient().getuId());
                                                            userPacientCacher.writeCache(model.getUserPacient());
                                                            notifCacher = new FileCacher<>(getActivity(), "notif");
                                                            notifCacher.writeCache(model);

                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        Intent mSubmitData = new Intent(getActivity(), SubmitPersonalDataPacient.class);
                                                        startActivity(mSubmitData);
                                                    }
                                                });

                                    }
                                    if(model.getType().contains("Asistenta Medicala")) {
                                        viewHolder.setName(model.getUserPacient().getFirstName() + " "
                                                + model.getUserPacient().getLastName());
                                        viewHolder.mNewInternare.setVisibility(View.GONE);
                                        viewHolder.mCard.setBackgroundColor(Color.parseColor("#F56C61"));
                                        viewHolder.mFullName.setTextColor(Color.parseColor("#ffffff"));
                                        viewHolder.mDate.setTextColor(Color.parseColor("#ffffff"));
                                        viewHolder.mMessage.setTextColor(Color.parseColor("#ffffff"));
                                        viewHolder.setMessage(model.getMessage());
                                        viewHolder.setPhoto(model.getUserPacient().getPhotoUrl());

                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
                                        mMediaPlayer = new MediaPlayer();
                                        mMediaPlayer.setDataSource(getContext(), notification);
                                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                                        if (model.getMessage().startsWith("[S.O.S.]")) {
                                            mMediaPlayer.setLooping(false);
                                        }
                                        else{
                                            r.play();
                                        }
                                        //mMediaPlayer.prepare();
                                        //mMediaPlayer.start();
//                                        r.play();

                                        if (model.getHandeled().contains("accesed")) {
                                            viewHolder.mgoToPacient.setVisibility(View.VISIBLE);
                                            viewHolder.mgoToPacient.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    mMediaPlayer.stop();
                                                    mMediaPlayer.setLooping(false);
                                                    Notification notif = model;
                                                    notif.setHandeled("In progress");
                                                    mRef.child(notif.getDateAndTime().toString().replace("/", "")
                                                            .replace(" ", "").replace(":", "")).setValue(notif);
                                                    notifCacher = new FileCacher<>(getActivity(), "notif");
                                                    try {
                                                        notifCacher.writeCache(model);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        } else if (model.getHandeled().contains("In progress")){
                                            viewHolder.setMessage(model.getMessage()+"\nIn curs...");
                                            viewHolder.mgoToPacient.setVisibility(View.GONE);
                                            mMediaPlayer.setLooping(false);
                                            mMediaPlayer.stop();
                                        }
                                    }
                                }
                            } else if (model.getHandeled().contains("Yes")){
                                viewHolder.mCard.setVisibility(View.GONE);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

        mRecyclerNewsFeed.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PersonalDataHolder extends RecyclerView.ViewHolder{

        View mView;
        int status = -1;

        TextView mFullName;
        TextView mMessage;
        ImageView mProfilePic;
        ImageView mNewInternare;
        CardView mCard;
        ImageView mgoToPacient;
        TextView mDate;


        public PersonalDataHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mFullName = (TextView) mView.findViewById(R.id.NewsFeedTmp_fullName);
            mMessage = (TextView) mView.findViewById(R.id.NewsFeedTmp_message);
            mProfilePic = (ImageView) mView.findViewById(R.id.NewsFeedTmp_photo);
            mNewInternare = (ImageView) mView.findViewById(R.id.NewsFeedTmp_newInternare);
            mCard = (CardView) mView.findViewById(R.id.NewFeedTmp_cardViewNume);
            mgoToPacient = (ImageView) mView.findViewById(R.id.NewsFeedTmp_call);
            mDate = (TextView) mView.findViewById(R.id.NewsFeedTmp_date);

        }

        public void setName(String fullName){
            mFullName.setText(fullName);

        }
        public void setMessage(String message){
            mMessage.setText(message);

        }public void setPhoto(String photoUrl){
            Picasso.with(mView.getContext()).load(photoUrl)
                    .transform(new CircleTransform())
                    .centerInside().resize(mProfilePic.getMaxWidth(), mProfilePic.getMaxHeight())
                    .noFade()
                    .into(mProfilePic);

        }
        public void setDate(String date){
            mDate.setText(date);
        }

    }

}
