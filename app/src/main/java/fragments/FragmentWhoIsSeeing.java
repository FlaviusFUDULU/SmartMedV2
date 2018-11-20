package fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ffudulu.licenta.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kosalgeek.android.caching.FileCacher;

import users.UserFamily;
import users.UserMedic;
import users.UserPersonalData;

public class FragmentWhoIsSeeing extends Fragment {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mRef;
    private DatabaseReference mRef2;

    private ImageView mProfileImage;
    private ImageView mProfileImageSmall;
    private int mMaxScrollSize;

    private RecyclerView mRecyclerViewALlPacients;

    private FileCacher<UserMedic> userCacher;
    private FileCacher<UserPersonalData> userPacientCacher;
    private FileCacher<String> userPacientUidCacher;
    private FileCacher<String> actionCacher;
    private FileCacher<String> actionCacherDrawer;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_who_is_seeing, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Family");
        mRef2 = FirebaseDatabase.getInstance().getReference();

        userCacher = new FileCacher<>(getActivity(), firebaseUser.getUid());

        mRecyclerViewALlPacients = (RecyclerView) view.findViewById(R.id.RecycleWhoSeePage);
//        mRecyclerViewALlPacients.setHasFixedSize(true);
        mRecyclerViewALlPacients.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<UserFamily, PersonalDataHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<UserFamily, PersonalDataHolder>(
                        UserFamily.class,
                        R.layout.fragment_template_who_is_seeing,
                        PersonalDataHolder.class,
                        mRef

                ) {
                    @Override
                    protected void populateViewHolder(final PersonalDataHolder viewHolder,
                                                      final UserFamily model, int position) {
                        if(model.getPacientUID().matches(firebaseUser.getUid())){
                            viewHolder.setEmail(model.getEmail());
                            viewHolder.setName(model.getFirstName()+" "+model.getLastName());
                            //viewHolder.setPicture(model.get);
                            viewHolder.mAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View view) {
                                    view.setEnabled(false);
                                    model.setActivated("Yes");
                                    mRef.child(model.getuId()).setValue(model)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            getActivity().finish();
                                        }
                                    });
                                }
                            });

                            viewHolder.mDecline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    model.setActivated("No");
                                    view.setEnabled(false);
                                    mRef.child(model.getuId()).setValue(model).
                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            getActivity().finish();
                                        }
                                    });
                                }
                            });

                            if(model.getActivated() == null){
                                viewHolder.mAccept.setVisibility(View.VISIBLE);
                                viewHolder.mDecline.setVisibility(View.VISIBLE);
                            } else {
                                if (model.getActivated().contains("Yes")){
                                    viewHolder.mAccept.setVisibility(View.GONE);
                                    viewHolder.mDecline.setVisibility(View.VISIBLE);
                                }
                                if(model.getActivated().contains("No")){
                                    viewHolder.mAccept.setVisibility(View.VISIBLE);
                                    viewHolder.mDecline.setVisibility(View.GONE);
                                }

                            }

                        } else {
                            viewHolder.mCardView.setVisibility(View.GONE);
                        }

                    }
                };

        mRecyclerViewALlPacients.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PersonalDataHolder extends RecyclerView.ViewHolder{

        View mView;
        int status = -1;

        ImageView mAccept;
        ImageView mDecline;
        CardView mCardView;


        public PersonalDataHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mAccept = (ImageView) mView.findViewById(R.id.WhoIsSeeingTmp_Accept);
            mDecline = (ImageView) mView.findViewById(R.id.WhoIsSeeingTmp_Decline);
            mCardView = (CardView) mView.findViewById(R.id.WhoIsSeeingTmp_cardViewNume);

        }

        public void setName(String fullName){
            TextView mFullName = (TextView) mView.findViewById(R.id.WhoIsSeeingTmp_fullName);
            mFullName.setText(fullName);

        }

        public void setEmail(String salon){
            TextView mEmail = (TextView) mView.findViewById(R.id.WhoIsSeeingTmp_email);
            mEmail.setText(salon);
        }


    }

}
