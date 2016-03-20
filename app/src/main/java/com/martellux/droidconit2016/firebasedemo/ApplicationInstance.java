package com.martellux.droidconit2016.firebasedemo;

import android.app.Application;
import android.content.Context;

import com.firebase.client.Firebase;

/**
 * Created by alessandromartellucci on 20/02/16.
 */
public class ApplicationInstance extends Application {

    private static ApplicationInstance mInstance;

    /**
     *
     * @return
     */
    public static Context getAndroidContext() {
        return mInstance.getApplicationContext();
    }

    /**
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Firebase.setAndroidContext(this);
    }
}
