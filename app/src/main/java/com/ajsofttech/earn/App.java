package com.ajsofttech.earn;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;


public class App extends Application {
    private static App mInstance;
    public static final String TAG = App.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private static final String ONESIGNAL_APP_ID = "5371a035-7a27-4505-b20a-3a1652fed463";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    long mainurl;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        ///

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final long[] unity = new long[1];

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("unity");


        String s=String.valueOf(mainurl);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("name", s);
        myEdit.commit();

        //


        SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String s1 = sh.getString("name", "");

        //String GAME_ID = s;
//        String GAME_ID = unityid;
//        boolean TEST_MODE = mInstance.getResources().getBoolean(R.bool.test_mode);
//        UnityAds.initialize(mInstance, "4898369", TEST_MODE);

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);




    }

    public static Context getContext() {
        return mInstance;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

}
