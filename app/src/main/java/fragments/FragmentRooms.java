package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ffudulu.licenta.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;
import java.util.Vector;

import users.UserMedic;
import users.UserPersonalData;

public class FragmentRooms extends Fragment {


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
    private FileCacher<UserPersonalData> userPacientCacher;
    private FileCacher<String> userPacientUidCacher;
    private final FileCacher<String> userCacherType = new FileCacher<>(getActivity(), "type");
    private FileCacher<String> roomCacher;
    private Vector<String> allRooms = new Vector<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_pacients, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Pacient");

        userCacher = new FileCacher<>(getActivity(), firebaseUser.getUid());

        mRecyclerViewALlPacients = (RecyclerView) view.findViewById(R.id.RecycleAllPacients);
//        mRecyclerViewALlPacients.setHasFixedSize(true);
        mRecyclerViewALlPacients.setLayoutManager(new GridLayoutManager(getActivity(), 3));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();



        FirebaseRecyclerAdapter<UserPersonalData, PersonalDataHolder> firebaseRecyclerAdapter =
            new FirebaseRecyclerAdapter<UserPersonalData, PersonalDataHolder>(
                    UserPersonalData.class,
                    R.layout.fragment_display_rooms,
                    PersonalDataHolder.class,
                    mRef

            ) {
                @Override
                protected void populateViewHolder(PersonalDataHolder viewHolder, final UserPersonalData model, int position) {

                    if (model.getRoom() != null) {
                        if (allRooms.contains(model.getRoom())) {
                            viewHolder.mCardViewSalon.setVisibility(View.GONE);
                        } else {
                            allRooms.add(model.getRoom());
                            viewHolder.setSalon(model.getRoom());
                            viewHolder.mCardViewSalon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        roomCacher = new FileCacher<>(getActivity(), "room");
                                        roomCacher.writeCache(model.getRoom().toString());
                                        FragmentRoomsPacients fragmentRoomPacients =
                                                new FragmentRoomsPacients();
                                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                                getFragmentManager().beginTransaction();
                                        fragmentTransaction.replace(R.id.main_fl_content, fragmentRoomPacients);
                                        fragmentTransaction.commit();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } else {
                       viewHolder.mCardViewSalon.setVisibility(View.GONE);
                    }
                }
            };

        mRecyclerViewALlPacients.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PersonalDataHolder extends RecyclerView.ViewHolder{

        View mView;
        int status = -1;

        TextView mSalon;
        CardView mCardViewSalon;


        public PersonalDataHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mCardViewSalon = (CardView) mView.findViewById(R.id.Salon_cardViewSalon);

        }

        public void setSalon(String salon){
            mSalon = (TextView) mView.findViewById(R.id.Salon_txt_salon);
            mSalon.setText(salon);

        }
    }
}