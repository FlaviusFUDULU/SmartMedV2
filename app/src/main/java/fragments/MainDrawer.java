package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ffudulu.licenta.Initialization;
import com.example.ffudulu.licenta.LogIn;
import com.example.ffudulu.licenta.R;
import com.example.ffudulu.licenta.SubmitPersonalData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.kosalgeek.android.caching.FileCacher;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import transformations.CircleTransform;

public class MainDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTxtViewEmail;
    private TextView mTxtViewName;
    private NavigationView mNavHeaderView;
    private View mHeaderView;
    private FirebaseUser firebaseUser;
    private ImageView mAccountImageView;


    private final FileCacher<String> userCacherType = new FileCacher<>(MainDrawer.this, "type");

    private FileCacher<users.UserMedic> userCacher;
    private FileCacher<users.UserPersonalData> userPacientCacher;
    private FileCacher<users.UserFamily> userFamilyCacher;
    private FileCacher<String> roomCacher;
    private FileCacher<String> userPacientUidCacher;
    private FileCacher<String> actionCacherDrawer;

    ///???
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    //???

    private RecyclerView mRecyclerNewsFeed;
    private DatabaseReference mRef;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        toolbar.setStatusBarTranslucentt(true);

        setSupportActionBar(toolbar);


        roomCacher = new FileCacher<>(MainDrawer.this, "room");
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem seeAllPacients = menu.findItem(R.id.nav_see_all_pacients);
        MenuItem newPacient = menu.findItem(R.id.nav_new_pacient);
        MenuItem rooms = menu.findItem(R.id.nav_see_rooms);
        MenuItem myTreatment = menu.findItem(R.id.nav_my_treatment);
        MenuItem whoIsSeeing = menu.findItem(R.id.nav_who_is_seeing);
        MenuItem analytics = menu.findItem(R.id.nav_my_statistics);
        try {
            if(userCacherType.readCache().contains("Pacient") ||
                    userCacherType.readCache().contains("Family")){
                seeAllPacients.setVisible(false);
                newPacient.setVisible(false);
                rooms.setVisible(false);
                analytics.setVisible(false);
            } else if (userCacherType.readCache().contains("Staff")){
                myTreatment.setVisible(false);
                whoIsSeeing.setVisible(false);

            }
            if (userCacherType.readCache().contains("Family")){
                whoIsSeeing.setVisible(false);
                myTreatment.setTitle("Istoric medical membru familie");
                analytics.setVisible(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        navigationView.setNavigationItemSelectedListener(this);

        //FROM HERE
        //Verify if data is complete
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userPacientCacher = new FileCacher<>(MainDrawer.this, firebaseUser.getUid());
        try {
            if(userCacherType.readCache().contains("Pacient")){
                if (userPacientCacher.readCache().getPhotoUrl() == null){
                    Toast.makeText(MainDrawer.this, "Inca nu este gata internare",
                                                                        Toast.LENGTH_LONG).show();
                    Intent mLogIn = new Intent (MainDrawer.this, LogIn.class);
                    startActivity(mLogIn);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //end

        //Initialize data
        mNavHeaderView = (NavigationView) findViewById(R.id.nav_view);
        mHeaderView = navigationView.getHeaderView(0);

        String fullname = null;
        String email = null;
        String photoUrl = null;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        try {
            if(userCacherType.readCache().contains("Staff")){
                userCacher = new FileCacher<>(MainDrawer.this, firebaseUser.getUid());
                if (userCacher.readCache().getFirstName() == null){
                    Intent mPData = new Intent(MainDrawer.this, SubmitPersonalData.class);
                    startActivity(mPData);
                }else{
                    fullname = userCacher.readCache().getFirstName() + " " +
                                                             userCacher.readCache().getLastName();
                    email = userCacher.readCache().getEmail();
                    photoUrl = firebaseUser.getPhotoUrl().toString();
                }
            }
            else if (userCacherType.readCache().contains("Pacient")){
                userPacientCacher = new FileCacher<>(MainDrawer.this, firebaseUser.getUid());
                if (userPacientCacher.readCache().getFirstName() == null){
                    Intent mPData = new Intent(MainDrawer.this, SubmitPersonalData.class);
                    startActivity(mPData);
                }else{
                    fullname = userPacientCacher.readCache().getFirstName() + " " +
                                                        userPacientCacher.readCache().getLastName();
                    email = userPacientCacher.readCache().getEmail();
                    photoUrl = userPacientCacher.readCache().getPhotoUrl();
                }
            }
            else if (userCacherType.readCache().contains("Family")){
                userFamilyCacher = new FileCacher<>(MainDrawer.this, firebaseUser.getUid());
                if (userFamilyCacher.readCache().getFirstName() == null){
                    Intent mPData = new Intent(MainDrawer.this, SubmitPersonalData.class);
                    startActivity(mPData);
                }else{
                    fullname = userFamilyCacher.readCache().getFirstName() + " " +
                                                        userFamilyCacher.readCache().getLastName();
                    email = userFamilyCacher.readCache().getEmail();
                    photoUrl = firebaseUser.getPhotoUrl().toString();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        mTxtViewEmail = (TextView) mHeaderView.findViewById(R.id.txtViewEmail);
        mTxtViewName = (TextView)  mHeaderView.findViewById(R.id.txtViewName);
        mAccountImageView = (ImageView) mHeaderView.findViewById(R.id.accountImageView);


        mTxtViewEmail.setText(email);
        try{
            mTxtViewName.setText(fullname.split(" ")[0].substring(0, 1).toUpperCase() +
                    fullname.split(" ")[0].substring(1).toLowerCase() + " " +
                    fullname.split(" ")[1].toUpperCase());
        }
        finally {

        }
        if (!TextUtils.isEmpty(fullname.split(" ")[0]) &&
                !TextUtils.isEmpty(fullname.split(" ")[1])) {
            mTxtViewName.setText(fullname.split(" ")[0].substring(0, 1).toUpperCase() +
                    fullname.split(" ")[0].substring(1).toLowerCase() + " " +
                    fullname.split(" ")[1].toUpperCase());
        }
        Picasso.with(MainDrawer.this).load(photoUrl)
                .transform(new CircleTransform())
                .centerInside().resize(mAccountImageView.getMaxWidth(), mAccountImageView.getMaxHeight())
                .noFade()
                .into(mAccountImageView);

        //end
        try {
            if (userCacherType.readCache().contains("Staff")) {
                if (userCacher.readCache().getRank().equals("Admin")){
                    seeAllPacients.setVisible(false);
                    newPacient.setVisible(false);
                    rooms.setVisible(false);
                    analytics.setVisible(false);

                    //TO DO: FF HERE
                    FragmentNewsFeedAdmin fragmentNewsFeedAdmin = new FragmentNewsFeedAdmin();
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fl_content, fragmentNewsFeedAdmin);
                    fragmentTransaction.commit();
                }
                else {
                    FragmentNewsFeed fragmentNewsFeed = new FragmentNewsFeed();
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fl_content, fragmentNewsFeed);
                    fragmentTransaction.commit();
                }
            } else if (userCacherType.readCache().contains("Pacient")){
                FragmentMainDrawerPacient fragmentNewsFeedPacient = new FragmentMainDrawerPacient();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_fl_content, fragmentNewsFeedPacient);
                fragmentTransaction.commit();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_home) {
            //Intent mDrawer = new Intent(this,MainDrawer.class);
            finish();
//            MainDrawer fragMain = new MainDrawer();
//            android.support.v4.app.FragmentTransaction fragmentTransaction =
//                    getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(this, fragMain);
//            fragmentTransaction.commit();


        } else if (id == R.id.nav_personal_data) {

            try {
                if(userCacherType.readCache().contains("Staff")){
                    FragmentAccountStaff fragmentAccount = new FragmentAccountStaff();
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fl_content, fragmentAccount);
                    fragmentTransaction.commit();
                }
                else if(userCacherType.readCache().contains("Pacient")){
                    FragmentAccountPacient fragmentAccountPacient = new FragmentAccountPacient();
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fl_content, fragmentAccountPacient);
                    fragmentTransaction.commit();
                }
                else if(userCacherType.readCache().contains("Family")){
                    FragmentAccountFamily fragmentAccountFamily = new FragmentAccountFamily();
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fl_content, fragmentAccountFamily);
                    fragmentTransaction.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (id == R.id.nav_see_all_pacients) {
            actionCacherDrawer = new FileCacher<>(MainDrawer.this, "drawerAction");
            try {
                actionCacherDrawer.writeCache("allPacients");
            } catch (IOException e) {
                e.printStackTrace();
            }
            FragmentPacients fragmentTest = new FragmentPacients();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fl_content, fragmentTest);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_my_statistics) {
            actionCacherDrawer = new FileCacher<>(MainDrawer.this, "drawerAction");
            try {
                actionCacherDrawer.writeCache("statistici");
            } catch (IOException e) {
                e.printStackTrace();
            }
            FragmentAnalytics fragmentAnalytics = new FragmentAnalytics();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fl_content, fragmentAnalytics);
            fragmentTransaction.commit();
        }else if (id == R.id.nav_see_rooms) {
            actionCacherDrawer = new FileCacher<>(MainDrawer.this, "drawerAction");
            try {
                actionCacherDrawer.writeCache("seeRooms");
            } catch (IOException e) {
                e.printStackTrace();
            }
            FragmentRooms fragmentRooms = new FragmentRooms();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fl_content, fragmentRooms);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_new_pacient) {
            try {
                actionCacherDrawer = new FileCacher<>(MainDrawer.this, "drawerAction");
                actionCacherDrawer.writeCache("faraInternare");
            } catch (IOException e) {
                e.printStackTrace();
            }
            FragmentPacients fragmentTest = new FragmentPacients();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fl_content, fragmentTest);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Intent mInitialize = new Intent(MainDrawer.this, Initialization.class);
            startActivity(mInitialize);
        } else if (id == R.id.nav_my_treatment) {

            try {
                if (userCacherType.readCache().contains("Pacient")) {
                    try {
                        userPacientUidCacher = new FileCacher<>(MainDrawer.this, "UID");
                        userPacientUidCacher.writeCache(firebaseUser.getUid());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FragmentTreatmentPacient fragmentTreatment = new FragmentTreatmentPacient();
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fl_content, fragmentTreatment);
                    fragmentTransaction.commit();
                } else if(userCacherType.readCache().contains("Family")){
                    userFamilyCacher = new FileCacher<>(MainDrawer.this, firebaseUser.getUid());
                    if(userFamilyCacher.readCache().getActivated()!=null) {
                        if (userFamilyCacher.readCache().getActivated().contains("Yes")) {
                            userPacientUidCacher = new FileCacher<>(MainDrawer.this, "UID");
                            userPacientUidCacher.writeCache(userFamilyCacher.readCache().getPacientUID());
                            FragmentTreatmentPacient fragmentTreatment = new FragmentTreatmentPacient();
                            android.support.v4.app.FragmentTransaction fragmentTransaction =
                                    getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_fl_content, fragmentTreatment);
                            fragmentTransaction.commit();
                        } else {
                            Toast.makeText(MainDrawer.this, "Nu ați primit acces de la pacient!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(MainDrawer.this, "Nu ați primit acces de la pacient!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (id == R.id.nav_who_is_seeing){
            FragmentWhoIsSeeing fragmentWho = new FragmentWhoIsSeeing();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fl_content, fragmentWho);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
