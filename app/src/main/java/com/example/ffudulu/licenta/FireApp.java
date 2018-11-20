package com.example.ffudulu.licenta;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by ffudulu on 27-Jan-17.
 */
public class FireApp extends Application{

    @Override
    public void onCreate(){
        super.onCreate();

        Firebase.setAndroidContext(this);
    }

}
