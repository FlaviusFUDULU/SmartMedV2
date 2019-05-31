package fragments;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ffudulu.licenta.Notification;
import com.example.ffudulu.licenta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.kosalgeek.android.caching.FileCacher;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Model.DateAndOccurance;
import transformations.CircleTransform;
import transformations.FakePageFragment;
import users.UserMedic;
import users.UserPersonalData;

public class FragmentNewsFeedAdmin extends Fragment
        {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private ImageView mProfileImage;
    private ImageView mProfileImageSmall;
    private int mMaxScrollSize;

    private FileCacher<UserMedic> userCacher;
    private FileCacher<UserPersonalData> userPaientCacher;
    private FileCacher<UserMedic> userAnalytics;

    private int bestTime = 0;
    private int worstTime = 0;
    private int allTime = 0;
    private int allTimeThisWeek = 0;
    private int nrOfInterventions = 0;
    private int countWeek = 0;
    float mediumWeek = 0;
    float mediumAll = 0;
    String durWeek;
    String durAll;
    UserMedic worstUser;
    UserMedic bestMedic;
    List<DateAndOccurance> dateAndOccuranceList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed_admin_empty, container, false);

//        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//        toolbar.setVisibility(View.GONE);

        final GraphView mLinegraph = (GraphView) view.findViewById(R.id.NewFeedTmpAdmin_lineGraph4);
        final CardView mCardView4 = (CardView) view.findViewById(R.id.NewFeedTmpAdmin_cardView4);
        mCardView4.setBackgroundColor(Color.parseColor("#e8f7f5"));
        final GraphView mLinegraph2 = (GraphView) view.findViewById(R.id.NewFeedTmpAdmin_lineGraph5);
        final CardView mCardView5 = (CardView) view.findViewById(R.id.NewFeedTmpAdmin_cardView5);
        mCardView5.setBackgroundColor(Color.parseColor("#e8f7f5"));

        TextView mCatergory = (TextView) view.findViewById(R.id.NewsFeedTmpAdmin_category);
        mCatergory.setText("Cel mai satisfăcător timp");
        final TextView mName = (TextView) view.findViewById(R.id.NewsFeedTmpAdmin_fullName);
        final TextView mTime = (TextView) view.findViewById(R.id.NewsFeedTmpAdmin_timp);
        final ImageView mProfilePic = (ImageView) view.findViewById(R.id.NewsFeedTmpAdmin_photo);
        final CardView mCardView = (CardView) view.findViewById(R.id.NewFeedTmpAdmin_cardView);
        mCardView.setBackgroundColor(Color.parseColor("#c9efc9"));


        TextView mCatergory2 = (TextView) view.findViewById(R.id.NewsFeedTmpAdmin_category2);
        mCatergory2.setText("Timp mediu de răspuns\nUltima săptămână");
        final TextView mTime2 = (TextView) view.findViewById(R.id.NewsFeedTmpAdmin_timp2);
        final CardView mCardView2 = (CardView) view.findViewById(R.id.NewFeedTmpAdmin_cardView2);
        mCardView2.setBackgroundColor(Color.parseColor("#efedc9"));

        TextView mCatergory2_2 = (TextView) view.findViewById(R.id.NewsFeedTmpAdmin_category2_2);
        mCatergory2_2.setText("Timp mediu de răspuns general");
        final TextView mTime2_2 = (TextView) view.findViewById(R.id.NewsFeedTmpAdmin_timp2_2);
        final CardView mCardView2_2 = (CardView) view.findViewById(R.id.NewFeedTmpAdmin_cardView2_2);
        mCardView2_2.setBackgroundColor(Color.parseColor("#efedc9"));

        TextView mCatergory3 = (TextView) view.findViewById(R.id.NewsFeedTmpAdmin_category3);
        mCatergory3.setText("Cel mai nesatisfăcător timp");
        final TextView mName3 = (TextView) view.findViewById(R.id.NewsFeedTmpAdmin_fullName3);
        final TextView mTime3 = (TextView) view.findViewById(R.id.NewsFeedTmpAdmin_timp3);
        final ImageView mProfilePic3 = (ImageView) view.findViewById(R.id.NewsFeedTmpAdmin_photo3);
        final CardView mCardView3 = (CardView) view.findViewById(R.id.NewFeedTmpAdmin_cardView3);
        mCardView3.setBackgroundColor(Color.parseColor("#efc9c9"));

        FirebaseDatabase.getInstance().getReference().child("Notifications")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Notification notification = snapshot.getValue(Notification.class);
                            if (notification.getHandeled().equals("Yes")) {
                                String duration =
                                        Integer.toString((int) Math.floor(Integer.parseInt(notification.getDuration()) / 60))
                                                + "m:"
                                                + Integer.toString(Math.round(((Integer.parseInt(notification.getDuration()) % 60))))
                                                + "s";
                                nrOfInterventions += 1;
                                if (Integer.parseInt(notification.getDuration()) < bestTime) {
                                    bestTime = Integer.parseInt(notification.getDuration());
                                    bestMedic = notification.getUserMedic();
                                }

                                if (Integer.parseInt(notification.getDuration()) > worstTime) {
                                    worstTime = Integer.parseInt(notification.getDuration());
                                    worstUser = notification.getUserMedic();
                                }

                                if (bestTime == 0) {
                                    bestTime = Integer.parseInt(notification.getDuration());
                                    bestMedic = notification.getUserMedic();
                                }

                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date dateNow1 = new Date();

                                String dateNow = dateFormat.format(dateNow1);

                                String day = dateNow.toString().split("/")[2];
                                String actualDay = day.split(" ")[0];

                                String dayRep = notification.getDateAndTime().split("/")[2];
                                String actualDayRep = dayRep.split(" ")[0];



                                allTime += Integer.parseInt(notification.getDuration());

                                if (Math.abs(Integer.parseInt(actualDay) - Integer.parseInt(actualDayRep)) <= 7) {
                                    allTimeThisWeek += Integer.parseInt(notification.getDuration());
                                    countWeek += 1;
                                }


                                if(countWeek != 0){
                                    mediumWeek = allTimeThisWeek / countWeek;
                                }
                                else{
                                    mediumWeek = 0;
                                }


                                mediumAll = allTime / nrOfInterventions;

                                durWeek = Integer.toString((int) Math.floor(mediumWeek / 60))
                                        + "m:"
                                        + Integer.toString((int) (Math.floor(mediumWeek) % 60))
                                        + "s";

                                durAll = Integer.toString((int) Math.floor(mediumAll / 60))
                                        + "m:"
                                        + Integer.toString((int) (Math.floor(mediumAll) % 60))
                                        + "s";

                                if (dateAndOccuranceList.isEmpty()){
                                    dateAndOccuranceList.add(new DateAndOccurance(
                                            notification.getDateAndTime().split(" ")[0],
                                            1,
                                            Integer.parseInt(notification.getDuration()),mediumAll));
                                }
                                else{
                                    boolean found = false;
                                    for (DateAndOccurance dateandoc : dateAndOccuranceList){
                                        if (dateandoc.getDate().equals(notification.getDateAndTime().split(" ")[0])){
                                            dateAndOccuranceList.remove(dateandoc);
                                            dateandoc.setOccurance(dateandoc.getOccurance() + 1);
                                            dateandoc.setDuration(dateandoc.getDuration()+Integer.parseInt(notification.getDuration()));
                                            found = true;
                                            dateandoc.setMedium(mediumAll);
                                            dateAndOccuranceList.add(dateandoc);
                                            break;
                                        }
                                    }
                                    if (!found){
                                        dateAndOccuranceList.add(new DateAndOccurance(
                                                notification.getDateAndTime().split(" ")[0],
                                                1,
                                                Integer.parseInt(notification.getDuration()),mediumAll));
                                    }
                                }

                            }
                        }
                        mName.setText("Nume: "+bestMedic.getFirstName() + " " + bestMedic.getLastName());
                        mTime.setText("Timp: "+Integer.toString((int) Math.floor(bestTime / 60))
                            + "m:"
                            + Integer.toString((bestTime % 60))
                            + "s");
                        Picasso.with(getActivity()).load(bestMedic.getPhotoUrl())
                            .transform(new CircleTransform())
                            .centerInside().resize(200, 200)
                            .noFade()
                            .into(mProfilePic);

                        mTime2.setText("Timo: "+durWeek);
                        mTime2_2.setText("Timp: "+durAll);

                        mName3.setText("Nume: " +worstUser.getFirstName() + " " + worstUser.getLastName());
                        mTime3.setText("Timp: "+Integer.toString((int) Math.floor(worstTime / 60))
                                + "m:"
                                + Integer.toString((worstTime % 60))
                                + "s");
                        Picasso.with(getActivity()).load(worstUser.getPhotoUrl())
                                .transform(new CircleTransform())
                                .centerInside().resize(200, 200)
                                .noFade()
                                .into(mProfilePic3);

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        try {
                            LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<DataPoint>();
                            boolean minSet = false;
                            for (DateAndOccurance date : dateAndOccuranceList){
                                lineSeries.appendData(
                                        new DataPoint(dateFormat.parse(date.getDate()),date.getOccurance()),false,30
                                );
                                if(!minSet){
                                    mLinegraph.getViewport().setMinX(dateFormat.parse(date.getDate()).getTime());
                                    minSet = true;
                                }
                                mLinegraph.getViewport().setMaxX(dateFormat.parse(date.getDate()).getTime());

                            }
                            lineSeries.setDrawDataPoints(true);
                            lineSeries.setBackgroundColor(Color.WHITE);

                            mLinegraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
                            //mLinegraph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
                            mLinegraph.getViewport().setScrollable(true);
                            mLinegraph.getViewport().setScalable(true);
                            //mLinegraph.getViewport().setScalableY(true);
                            //mLinegraph.getViewport().setMaxXAxisSize(3);
                            mLinegraph.getViewport().setXAxisBoundsManual(true);
                            mLinegraph.getViewport().setMinY(0);
                            mLinegraph.getViewport().setMaxY(10);
                            mLinegraph.getViewport().setYAxisBoundsManual(true);

                            lineSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                                @Override
                                public void onTap(Series series, DataPointInterface dataPoint) {
                                    Toast.makeText(getActivity(),
                                            "Au fost: "+
                                                    dataPoint.toString().split("/")[1]
                                                            .replace("]","")+
                                                    " asistențe", Toast.LENGTH_SHORT).show();

                                }
                            });
                            //mLinegraph.getGridLabelRenderer().setHumanRounding(false);

                            mLinegraph.addSeries(lineSeries);
                            mLinegraph.setTitle("Numar de chemari/zi");

                            LineGraphSeries<DataPoint> lineSeries2 = new LineGraphSeries<DataPoint>();
                            boolean minSet2 = false;
                            for (DateAndOccurance date : dateAndOccuranceList){
                                lineSeries2.appendData(
                                        new DataPoint(dateFormat.parse(date.getDate()),
                                                Double.parseDouble(Integer.toString((int) Math.floor(date.getMedium() / 60))
                                                + "."
                                                + Integer.toString(Math.round((date.getMedium() % 60))))
                                                ),false,30
                                );
                                if(!minSet2){
                                    mLinegraph2.getViewport().setMinX(dateFormat.parse(date.getDate()).getTime());
                                    //mLinegraph2.getViewport().setMinY(date.getDuration());
                                    minSet2 = true;
                                }
                                mLinegraph2.getViewport().setMaxX(dateFormat.parse(date.getDate()).getTime());
                                mLinegraph2.getViewport().setMaxY(date.getDuration());

                            }
                            lineSeries2.setDrawDataPoints(true);
                            lineSeries2.setBackgroundColor(Color.WHITE);

                            mLinegraph2.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
                            //mLinegraph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
                            mLinegraph2.getViewport().setScrollable(true);
                            mLinegraph2.getViewport().setScalable(true);
                            //mLinegraph.getViewport().setScalableY(true);
                            //mLinegraph.getViewport().setMaxXAxisSize(3);
                            mLinegraph2.getViewport().setXAxisBoundsManual(true);
                            mLinegraph2.getViewport().setMinY(0);
                            //mLinegraph2.getViewport().setMaxY(10);
                            mLinegraph2.getViewport().setScalableY(true);
                            mLinegraph2.getViewport().setScrollableY(true);
                            mLinegraph2.getViewport().setYAxisBoundsManual(true);
                            mLinegraph2.getViewport().setMinY(0);

                            lineSeries2.setOnDataPointTapListener(new OnDataPointTapListener() {
                                @Override
                                public void onTap(Series series, DataPointInterface dataPoint) {
                                    String time = dataPoint.toString().split("/")[1]
                                            .replace("]","");
                                    String min = time.split("\\.")[0];
                                    String sec = time.split("\\.")[1];
                                    Toast.makeText(getActivity(),
                                            "Timp mediu: "+
                                                   min + "m:"+sec+"s"
                                                    , Toast.LENGTH_SHORT).show();

                                }
                            });
                            //mLinegraph.getGridLabelRenderer().setHumanRounding(false);

                            mLinegraph2.addSeries(lineSeries2);
                            mLinegraph2.setTitle("Timp/zi");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        mCardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                userAnalytics = new FileCacher<>(getActivity(), "userAnalytics");
                                try {
                                    userAnalytics.writeCache(bestMedic);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                FragmentAnalyticsAdmin fragmentAnalyticsAdmin = new FragmentAnalyticsAdmin();
                                android.support.v4.app.FragmentTransaction fragmentTransaction =
                                        getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.main_fl_content, fragmentAnalyticsAdmin);
                                fragmentTransaction.commit();
                            }
                        });

                        mCardView3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                userAnalytics = new FileCacher<>(getActivity(), "userAnalytics");
                                try {
                                    userAnalytics.writeCache(worstUser);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                FragmentAnalyticsAdmin fragmentAnalyticsAdmin = new FragmentAnalyticsAdmin();
                                android.support.v4.app.FragmentTransaction fragmentTransaction =
                                        getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.main_fl_content, fragmentAnalyticsAdmin);
                                fragmentTransaction.commit();
                            }
                        });


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


//                setBestTime(Integer.toString((int) Math.floor(bestTime / 60))
//                    + "m:"
//                    + Integer.toString((bestTime % 60))
//                    + "s");
//                setWorstTime(Integer.toString((int) Math.floor(worstTime / 60))
//                    + "m:"
//                    + Integer.toString((worstTime % 60))
//                    + "s");
//
//                setMediumTimeWeek(durWeek);
//                setMediumTImeAllTime(durAll);
//                viewHolder.setDuration(duration);

//        TextView textView = (TextView)toolbar.findViewById(R.id.textTitle);
//        textView.setText("Metrici");



//        userCacher = new FileCacher<>(getActivity(), firebaseUser.getUid());
//        TextView mName = (TextView) view.findViewById(R.id.cardView_text_nume);
//        try {
//            mName.setText(userCacher.readCache().getFirstName()+ " "
//                    + userCacher.readCache().getLastName());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        TextView mEmailCard = (TextView) view.findViewById(R.id.cardView_text_email);
//        try {
//            mEmailCard.setText(userCacher.readCache().getEmail());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        AppBarLayout appbarLayout = (AppBarLayout) view.findViewById(R.id.materialup_appbar);
//
//        appbarLayout.addOnOffsetChangedListener(this);
//        mMaxScrollSize = appbarLayout.getTotalScrollRange();
//
//        TextView mFullName = (TextView) view.findViewById(R.id.materialup_full_name);
//        try {
//            mFullName.setText(userCacher.readCache().getFirstName());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        TextView mEmail = (TextView) view.findViewById(R.id.materialup_email);
//        try {
//            mEmail.setText(userCacher.readCache().getLastName());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        TextView mHospitalName = (TextView) view.findViewById(R.id.cardView_text_spital);
//        try {
//            mHospitalName.setText(userCacher.readCache().getHospitalName());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        TextView mSectionName = (TextView) view.findViewById(R.id.cardView_text_sectie);
//        try {
//            mSectionName.setText(userCacher.readCache().getSectionName());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        TextView mCNP = (TextView) view.findViewById(R.id.cardView_text_CNP);
//        try {
//            mCNP.setText(userCacher.readCache().getCnp());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        TextView mId = (TextView) view.findViewById(R.id.cardView_text_ID);
//        try {
//            mId.setText(userCacher.readCache().getId());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        TextView mAddress = (TextView) view.findViewById(R.id.cardView_text_address);
//        try {
//            mAddress.setText(userCacher.readCache().getAddress());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        TextView mDateOfBirth = (TextView) view.findViewById(R.id.cardView_text_dateOfBirth);
//        try {
//            mDateOfBirth.setText(userCacher.readCache().getDateOfBirth().toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        TextView mRank = (TextView) view.findViewById(R.id.cardView_text_Rank);
//        try {
//            mRank.setText(userCacher.readCache().getRank());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        TextView mPhone = (TextView) view.findViewById(R.id.cardView_text_phoneNumber);
//        try {
//            mPhone.setText(userCacher.readCache().getPhoneNumber());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
//        File directory = cw.getDir("SmartMedProfile", Context.MODE_PRIVATE);
//        File myProfilePic = new File(directory, firebaseUser.getUid());
//        mProfileImage = (ImageView) view.findViewById(R.id.materialup_profile_image);
//        Picasso.with(getActivity()).load(firebaseUser.getPhotoUrl())
//                .transform(new CircleTransform())
//                .centerInside().resize(200, 200)
//                .noFade()
//                .into(mProfileImage);
//
//

        return view;
    }


//    private static class TabsAdapter extends FragmentPagerAdapter {
//        private static final int TAB_COUNT = 2;
//
//        TabsAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public int getCount() {
//            return TAB_COUNT;
//        }
//
//        @Override
//        public Fragment getItem(int i) {
//            return FakePageFragment.newInstance();
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return "Tab " + String.valueOf(position);
//        }
//    }
}
