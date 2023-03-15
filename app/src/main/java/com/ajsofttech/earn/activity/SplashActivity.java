package com.ajsofttech.earn.activity;

import static com.ajsofttech.earn.FirebaseProperties.types;
import static com.ajsofttech.earn.FirebaseProperties.unityid;
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ajsofttech.earn.FirebaseProperties;
import com.ajsofttech.earn.FirebasePropertiesX;
import com.ajsofttech.earn.Metadata;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.ajsofttech.earn.App;
import com.ajsofttech.earn.R;
import com.ajsofttech.earn.models.User;
import com.ajsofttech.earn.utils.BaseUrl;
import com.ajsofttech.earn.utils.Constant;
import com.ajsofttech.earn.utils.CustomVolleyJsonRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unity3d.ads.UnityAds;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {
    boolean LOGIN = false;
    private AppUpdateManager appUpdateManager;
    public static final int RC_APP_UPDATE = 101;
    SplashActivity activity;
    long mainurl;
    DatabaseReference rootRef, demoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        activity = this;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String[] home = new String[1];
        final long[] unity = new long[1];
        rootRef = FirebaseDatabase.getInstance().getReference();
        demoRef = rootRef.child("adunits");



        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Database")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebasePropertiesX metadata=dataSnapshot.getValue(FirebasePropertiesX.class);

                FirebaseProperties.setHomelink(metadata.bannerId);
                FirebaseProperties.setUnityid(metadata.unityId);
                FirebaseProperties.setSilverlink(metadata.silverLink);
                FirebaseProperties.setGoldenlink(metadata.goldenLink);
                FirebaseProperties.setBanner_id(metadata.bannerId);
                FirebaseProperties.setInterstitial_id(metadata.interstitialId);
                FirebaseProperties.setTypes(metadata.typesOf);
                FirebaseProperties.setPlatinumlink(metadata.platinumLink);
                FirebaseProperties.setRewarded_id(metadata.rewardedId);
               // Toast.makeText(getApplicationContext(),"snapshot: "+FirebaseProperties.getGoldenlink(),Toast.LENGTH_LONG).show();



                if(TextUtils.isEmpty(types)){
                    Log.d("sadsd","first");
                }else {
                    Log.d("sadsd","second");
                    String s=String.valueOf(mainurl);
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("name", s);
                    myEdit.commit();
                    UnityAds.initialize(SplashActivity.this, unityid, false);
                    //Constant.GotoNextActivity(activity, MainActivity.class, "");

                    String is_login = Constant.getString(activity, Constant.IS_LOGIN);
                    if (is_login.equals("true")) {
                        LOGIN = true;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Log.e("TAG", "onCreate:if part activarte ");
                        appUpdateManager = AppUpdateManagerFactory.create(SplashActivity.this);
                        UpdateApp();
                    } else {
                        Log.e("TAG", "onCreate:else part activarte ");
                        onInit();
                    }
                }


            }
            public void onCancelled(DatabaseError databaseError) {
                Log.d("fff", "onCancelled " + databaseError.getMessage());
               // Toast.makeText(getApplicationContext(),"databaseError",Toast.LENGTH_LONG).show();
            }
        });








    }

    private void onInit() {
        if (Constant.isNetworkAvailable(activity)) {
            if (LOGIN) {
                try {
                    String tag_json_obj = "json_login_req";
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("get_login", "any");
                    params.put("email", Constant.getString(activity, Constant.USER_EMAIL));
                    params.put("password", Constant.getString(activity, Constant.USER_PASSWORD));

                    CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                            BaseUrl.LOGIN_API, params, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("TAG", response.toString());

                            try {
                                boolean status = response.getBoolean("status");
                                if (status) {
                                    JSONObject jsonObject = response.getJSONObject("0");
                                    Constant.setString(activity, Constant.USER_ID, jsonObject.getString("id"));
                                    final User user = new User(jsonObject.getString("name"), jsonObject.getString("number"), jsonObject.getString("email"), jsonObject.getString("points"), jsonObject.getString("referraled_with"), jsonObject.getString("image"), jsonObject.getString("status"), jsonObject.getString("referral_code"));

                                    if (Constant.getDate(SplashActivity.this) == 0) {
                                        Constant.showToastMessage(SplashActivity.this, "First Time");
                                        Constant.storeDate(SplashActivity.this);
                                        Constant.setString(activity, Constant.SCRATCH_COUNT_PLATINUM, getResources().getString(R.string.scratch_count));
                                        Constant.setString(activity, Constant.SCRATCH_COUNT_GOLD, getResources().getString(R.string.scratch_count));
                                        Constant.setString(activity, Constant.SCRATCH_COUNT_SILVER, getResources().getString(R.string.scratch_count));
                                    } else {
                                        if (Constant.getDate(SplashActivity.this) == Constant.checkDate()) {
                                            // do nothing
                                            Constant.showToastMessage(SplashActivity.this, "Same Day");
                                        } else {
                                            Constant.showToastMessage(SplashActivity.this, "New Day");
                                            Constant.setString(activity, Constant.SCRATCH_COUNT_PLATINUM, getResources().getString(R.string.scratch_count));
                                            Constant.setString(activity, Constant.SCRATCH_COUNT_GOLD, getResources().getString(R.string.scratch_count));
                                            Constant.setString(activity, Constant.SCRATCH_COUNT_SILVER, getResources().getString(R.string.scratch_count));
                                            Constant.storeDate(SplashActivity.this);
                                            String scratchCount = Constant.getString(activity, Constant.SCRATCH_COUNT_PLATINUM);
                                            Constant.showToastMessage(SplashActivity.this, scratchCount);
                                        }
                                    }
                                    if (user.getName() != null) {
                                        Constant.setString(activity, Constant.USER_NAME, user.getName());
                                        Log.e("TAG", "onDataChange: " + user.getName());
                                    }
                                    if (user.getNumber() != null) {
                                        Constant.setString(activity, Constant.USER_NUMBER, user.getNumber());
                                        Log.e("TAG", "onDataChange: " + user.getNumber());
                                    }
                                    if (user.getEmail() != null) {
                                        Constant.setString(activity, Constant.USER_EMAIL, user.getEmail());
                                        Log.e("TAG", "onDataChange: " + user.getEmail());
                                    }
                                    if (user.getPoints() != null) {
                                        if (!Constant.getString(activity, Constant.LAST_TIME_ADD_TO_SERVER).equals("")) {
                                            Log.e("TAG", "onDataChange: Last time not empty");
                                            if (!Constant.getString(activity, Constant.USER_POINTS).equals("")) {
                                                Log.e("TAG", "onDataChange: user points not empty");
                                                if (Constant.getString(activity, Constant.IS_UPDATE).equalsIgnoreCase("")) {
                                                    Constant.setString(activity, Constant.USER_POINTS, Constant.getString(activity, Constant.USER_POINTS));
                                                    Log.e("TAG", "onDataChange: " + user.getPoints());
                                                    Constant.setString(activity, Constant.IS_UPDATE, "true");
                                                } else {
                                                    Constant.setString(activity, Constant.IS_UPDATE, "true");
                                                    Constant.setString(activity, Constant.USER_POINTS, user.getPoints());
                                                    Log.e("TAG", "onDataChange: " + user.getPoints());
                                                }
                                            }
                                        }
                                    }
                                    if (user.getReferCode() != null) {
                                        Constant.setString(activity, Constant.REFER_CODE, user.getReferCode());
                                        Log.e("TAG", "onDataChange: " + user.getReferCode());
                                    }
                                    if (user.getIsBLocked() != null) {
                                        Constant.setString(activity, Constant.USER_BLOCKED, user.getIsBLocked());
                                        Log.e("TAG", "onDataChange: " + user.getIsBLocked());
                                    }
                                    if (user.getUserReferCode() != null) {
                                        Constant.setString(activity, Constant.USER_REFFER_CODE, user.getUserReferCode());
                                        Log.e("TAG", "onDataChange: " + user.getUserReferCode());
                                    }
                                    if (user.getImage() != null) {
                                        Constant.setString(activity, Constant.USER_IMAGE, user.getImage());
                                        Log.e("TAG", "onDataChange: " + user.getUserReferCode());
                                    }


                                    if (Constant.getString(activity, Constant.USER_BLOCKED).equals("0")) {
                                        Constant.showBlockedDialog(activity, getResources().getString(R.string.you_are_blocked));
                                    } else {
                                        Log.e("TAG", "onInit: login pART");
                                        Constant.GotoNextActivity(activity, MainActivity.class, "");
                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                        finish();
                                    }
                                } else {
/*
                                    if (Constant.getString(activity, Constant.TODAY_DATE).isEmpty()) {
                                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                        Constant.setString(activity, Constant.TODAY_DATE, currentDate);
                                        Constant.setString(activity, Constant.SCRATCH_COUNT_PLATINUM, getResources().getString(R.string.scratch_count));
                                        Constant.setString(activity, Constant.LAST_DATE_SCRATCH_PLATINUM, currentDate);
                                    } else {
                                        String currentDate2 = Constant.getString(activity, Constant.TODAY_DATE);
                                        Constant.setString(activity, Constant.TODAY_DATE, currentDate2);
                                    }
*/
                                    if (Constant.getDate(SplashActivity.this) == 0) {
                                        Constant.showToastMessage(SplashActivity.this, "First Time");
                                        Constant.storeDate(SplashActivity.this);
                                        Constant.setString(activity, Constant.SCRATCH_COUNT_PLATINUM, getResources().getString(R.string.scratch_count));
                                        Constant.setString(activity, Constant.SCRATCH_COUNT_GOLD, getResources().getString(R.string.scratch_count));
                                        Constant.setString(activity, Constant.SCRATCH_COUNT_SILVER, getResources().getString(R.string.scratch_count));
                                    } else {
                                        if (Constant.getDate(SplashActivity.this) == Constant.checkDate()) {
                                            // do nothing
                                            Constant.showToastMessage(SplashActivity.this, "Same Day");
                                        } else {
                                            Constant.showToastMessage(SplashActivity.this, "New Day");
                                            Constant.setString(activity, Constant.SCRATCH_COUNT_PLATINUM, getResources().getString(R.string.scratch_count));
                                            Constant.setString(activity, Constant.SCRATCH_COUNT_GOLD, getResources().getString(R.string.scratch_count));
                                            Constant.setString(activity, Constant.SCRATCH_COUNT_SILVER, getResources().getString(R.string.scratch_count));
                                            Constant.storeDate(SplashActivity.this);
                                            String scratchCount = Constant.getString(activity, Constant.SCRATCH_COUNT_PLATINUM);
                                            Constant.showToastMessage(SplashActivity.this, scratchCount);
                                        }
                                    }

                                    Log.e("TAG", "onInit: user_information from database");
                                    Constant.setString(activity, Constant.IS_LOGIN, "");
                                    Constant.GotoNextActivity(activity, MainActivity.class, "");
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            VolleyLog.d("TAG", "Error: " + error.getMessage());
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Constant.showToastMessage(activity, getResources().getString(R.string.slow_internet_connection));
                            }
                        }
                    });
                    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                            1000 * 20,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    App.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

                } catch (Exception e) {
                    Log.e("TAG", "onInit: excption " + e.getMessage());
                }
            } else {
                if (Constant.getString(activity, Constant.USER_BLOCKED).equals("0")) {
                    Constant.showBlockedDialog(activity, getResources().getString(R.string.you_are_blocked));
                    return;
                }
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                Constant.setString(activity, Constant.TODAY_DATE, currentDate);
                if (Constant.getDate(SplashActivity.this) == 0) {
                    //Constant.showToastMessage(SplashActivity.this, "First Time");
                    Constant.storeDate(SplashActivity.this);
                    Constant.setString(activity, Constant.SCRATCH_COUNT_PLATINUM, getResources().getString(R.string.scratch_count));
                    Constant.setString(activity, Constant.SCRATCH_COUNT_GOLD, getResources().getString(R.string.scratch_count));
                    Constant.setString(activity, Constant.SCRATCH_COUNT_SILVER, getResources().getString(R.string.scratch_count));
                } else {
                    if (Constant.getDate(SplashActivity.this) == Constant.checkDate()) {
                        // do nothing
                        // Constant.showToastMessage(SplashActivity.this, "Same Day");
                    } else {
                        //Constant.showToastMessage(SplashActivity.this, "New Day");
                        Constant.setString(activity, Constant.SCRATCH_COUNT_PLATINUM, getResources().getString(R.string.scratch_count));
                        Constant.setString(activity, Constant.SCRATCH_COUNT_GOLD, getResources().getString(R.string.scratch_count));
                        Constant.setString(activity, Constant.SCRATCH_COUNT_SILVER, getResources().getString(R.string.scratch_count));
                        Constant.storeDate(SplashActivity.this);
                        String scratchCount = Constant.getString(activity, Constant.SCRATCH_COUNT_PLATINUM);
                        //Constant.showToastMessage(SplashActivity.this, scratchCount);
                    }
                }
                Log.e("TAG", "onInit: else part of no login");
                Constant.GotoNextActivity(activity, MainActivity.class, "");
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        } else {
            Constant.showInternetErrorDialog(activity, getResources().getString(R.string.no_internet_connection));
        }
    }

    public void UpdateApp() {
        try {
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                @Override
                public void onSuccess(AppUpdateInfo appUpdateInfo) {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                        try {
                            appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo, IMMEDIATE, activity, RC_APP_UPDATE);
                            Log.e("TAG", "onCreate:startUpdateFlowForResult part activarte ");
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        try {
                            appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo, IMMEDIATE, activity, RC_APP_UPDATE);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("TAG", "onCreate:startUpdateFlowForResult else part activarte ");
                        activity.onInit();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                    Log.e("TAG", "onCreate:addOnFailureListener else part activarte ");
                    activity.onInit();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                onInit();
            } else {
                onInit();
            }
        }
    }
}