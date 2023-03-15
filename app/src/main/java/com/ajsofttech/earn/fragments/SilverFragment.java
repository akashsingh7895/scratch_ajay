package com.ajsofttech.earn.fragments;



import static com.ajsofttech.earn.FirebaseProperties.banner_id;
import static com.ajsofttech.earn.FirebaseProperties.interstitial_id;
import static com.ajsofttech.earn.FirebaseProperties.rewarded_id;
import static com.ajsofttech.earn.FirebaseProperties.silverlink;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ajsofttech.earn.R;
import com.ajsofttech.earn.services.PointsService;
import com.ajsofttech.earn.utils.Constant;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import dev.skymansandy.scratchcardlayout.listener.ScratchListener;
import dev.skymansandy.scratchcardlayout.ui.ScratchCardLayout;

public class SilverFragment extends Fragment implements ScratchListener {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference reference = firebaseDatabase.getReference();
    private DatabaseReference childReference = reference.child("silver");

    private LinearLayout adLayout;
    private Activity activity;
    private Toolbar toolbar;
    private TextView scratch_count_textView, points_textView, points_text;
    boolean first_time = true, scratch_first = true;
    private int scratch_count = 1;
    private int counter_dialog = 0;
    private final String TAG = "Silver Fragment";
    private String random_points;
    public int poiints = 0;
    public boolean rewardShow = true, interstitialShow = true, isFullRewardShow = false;
    ScratchCardLayout scratchCardLayout;
    private UnityAdsListener unityAdsListener;
    private CardView QurekaCardView , Qureka;

    public SilverFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public static SilverFragment newInstance() {
        SilverFragment fragment = new SilverFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_silver, container, false);
        if (getActivity() != null) {
            activity = getActivity();
        }
        adLayout = view.findViewById(R.id.banner_container);
        toolbar = view.findViewById(R.id.toolbar);
        points_text = view.findViewById(R.id.textView_points_show);
        scratch_count_textView = view.findViewById(R.id.limit_text);
        scratchCardLayout = view.findViewById(R.id.scratch_view_layout);
        scratchCardLayout.setScratchListener(this);
        View play = view.findViewById(R.id.Qureka);
//        UnityAds.initialize(getActivity(), unityid, false);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qurekaopen();
            }
        });
        try {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView titleText = toolbar.findViewById(R.id.toolbarText);
            titleText.setText("Silver Scratch");
            points_textView = toolbar.findViewById(R.id.points_text_in_toolbar);
            setPointsText();
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Constant.isNetworkAvailable(activity)) {
            loadBanner();
            LoadInterstitial();
            loadRewardedVideo();
        } else {
            Constant.showInternetErrorDialog(activity, getResources().getString(R.string.no_internet_connection));
        }
        onInit();
        return view;
    }


    private void setPointsText() {
        if (points_textView != null) {
            String userPoints = Constant.getString(activity, Constant.USER_POINTS);
            if (userPoints.equalsIgnoreCase("")) {
                userPoints = "0";
            }
            points_textView.setText(userPoints);
        }
    }

    private void onInit() {

        String scratchCount = Constant.getString(activity, Constant.SCRATCH_COUNT_SILVER);
        if (scratchCount.equals("0")) {
            scratchCount = "";
            Log.e("TAG", "onInit: scratch card 0");
        }
        if (scratchCount.equals("")) {
            Log.e("TAG", "onInit: scratch card empty vala part");
            String currentDate = Constant.getString(activity, Constant.TODAY_DATE);
            Log.e("TAG", "onClick: Current Date" + currentDate);
            String last_date = Constant.getString(activity, Constant.LAST_DATE_SCRATCH_SILVER);
            Log.e("TAG", "Lat date" + last_date);
            if (last_date.equals("")) {
                Log.e("TAG", "onInit: last date empty part");
                setScratchCount(getResources().getString(R.string.scratch_count));
                Constant.setString(activity, Constant.SCRATCH_COUNT_SILVER, getResources().getString(R.string.scratch_count));
                Constant.setString(activity, Constant.LAST_DATE_SCRATCH_SILVER, currentDate);
                // Constant.addDate(activity, "silver", Constant.getString(activity, Constant.LAST_DATE_SCRATCH_SILVER));
            } else {
                Log.e("TAG", "onInit: last date not empty part");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date current_date = sdf.parse(currentDate);
                    Date lastDate = sdf.parse(last_date);
                    long diff = current_date.getTime() - lastDate.getTime();
                    long difference_In_Days = (diff / (1000 * 60 * 60 * 24)) % 365;
                    Log.e("TAG", "onClick: Days Difference" + difference_In_Days);
                    if (difference_In_Days > 0) {
                        Constant.setString(activity, Constant.LAST_DATE_SCRATCH_SILVER, currentDate);
                        Constant.setString(activity, Constant.SCRATCH_COUNT_SILVER, getResources().getString(R.string.scratch_count));
                        setScratchCount(Constant.getString(activity, Constant.SCRATCH_COUNT_SILVER));
                        // Constant.addDate(activity, "silver", Constant.getString(activity, Constant.LAST_DATE_SCRATCH_SILVER));
                        Log.e("TAG", "onClick: today date added to preference" + currentDate);
                    } else {
                        setScratchCount("0");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.e("TAG", "onInit: scracth card in preference part");
            setScratchCount(scratchCount);
        }
    }

    private void setScratchCount(String string) {
        if (string != null || !string.equalsIgnoreCase(""))
            scratch_count_textView.setText("Your Today Scratch Count left = " + string);
    }

    private void loadBanner() {
        BannerView bannerView = new BannerView(activity, banner_id, new UnityBannerSize(320, 50));
        bannerView.load();
        bannerView.setListener(new BannerView.IListener() {
            @Override
            public void onBannerLoaded(BannerView bannerView) {
                adLayout.addView(bannerView);
            }

            @Override
            public void onBannerClick(BannerView bannerView) {

            }

            @Override
            public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {

            }

            @Override
            public void onBannerLeftApplication(BannerView bannerView) {

            }
        });
    }

    private void showDialogPoints(final int addOrNoAddValue, final String points, final int counter, boolean isShowDialog) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_points_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageView imageView = dialog.findViewById(R.id.points_image);
        TextView title_text = dialog.findViewById(R.id.title_text_points);
        TextView points_text = dialog.findViewById(R.id.points);
        AppCompatButton add_btn = dialog.findViewById(R.id.add_btn);
        if (Constant.isNetworkAvailable(activity)) {
            if (addOrNoAddValue == 1) {
                if (points.equals("0")) {
                    Log.e("TAG", "showDialogPoints: 0 points");
                    imageView.setImageResource(R.drawable.sad);
                    title_text.setText(getResources().getString(R.string.better_luck));
                    points_text.setVisibility(View.VISIBLE);
                    points_text.setText(points);
                    add_btn.setText(getResources().getString(R.string.okk));
                } else if (points.equals("no")) {
                    Log.e("TAG", "showDialogPoints: rewared cancel");
                    imageView.setImageResource(R.drawable.sad);
                    title_text.setText(getResources().getString(R.string.not_watch_full_video));
                    points_text.setVisibility(View.VISIBLE);
                    points_text.setText(points);
                    add_btn.setText(getResources().getString(R.string.okk));
                } else {
                    Log.e("TAG", "showDialogPoints: points");
                    imageView.setImageResource(R.drawable.congo);
                    title_text.setText(getResources().getString(R.string.you_won));
                    points_text.setVisibility(View.VISIBLE);
                    points_text.setText(points);
                    add_btn.setText(getResources().getString(R.string.add_to_wallet));
                }
            } else {
                Log.e("TAG", "showDialogPoints: chance over");
                imageView.setImageResource(R.drawable.donee);
                title_text.setText(getResources().getString(R.string.today_chance_over));
                points_text.setVisibility(View.GONE);
                add_btn.setText(getResources().getString(R.string.okk));
            }
            add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    first_time = true;
                    scratch_first = true;
                    scratchCardLayout.resetScratch();
                    poiints = 0;
                    if (addOrNoAddValue == 1) {
                        if (points.equals("0") || points.equalsIgnoreCase("no")) {
                            String current_counter = String.valueOf(counter - 1);
                            Constant.setString(activity, Constant.SCRATCH_COUNT_SILVER, current_counter);
                            setScratchCount(Constant.getString(activity, Constant.SCRATCH_COUNT_SILVER));
                            dialog.dismiss();
                        } else {
                            String current_counter = String.valueOf(counter - 1);
                            Constant.setString(activity, Constant.SCRATCH_COUNT_SILVER, current_counter);
                            setScratchCount(Constant.getString(activity, Constant.SCRATCH_COUNT_SILVER));
                            try {
                                int finalPoint;
                                if (points.equals("")) {
                                    finalPoint = 0;
                                } else {
                                    finalPoint = Integer.parseInt(points);
                                }
                                poiints = finalPoint;
                                Constant.addPoints(activity, finalPoint, 0);
                                setPointsText();
                            } catch (NumberFormatException ex) {
                                Log.e("TAG", "onScratchComplete: " + ex.getMessage());
                            }
                            dialog.dismiss();
                        }
                    } else {
                        dialog.dismiss();
                    }
                    if (addOrNoAddValue == 1) {
                        if (scratch_count == Integer.parseInt(getResources().getString(R.string.rewarded_and_interstitial_ads_between_count))) {
                            if (rewardShow) {
                                Log.e(TAG, "onReachTarget: rewaded ads showing method");
                                showDailog();
                                rewardShow = false;
                                interstitialShow = true;
                                scratch_count = 0;
                            } else if (interstitialShow) {
                                Log.e(TAG, "onReachTarget: interstital ads showing method");
                                ShowInterstital();
                                rewardShow = true;
                                interstitialShow = false;
                                scratch_count = 0;
                            }
                        } else {
                            scratch_count += 1;
                        }
                    }
                }
            });
        } else {
            Constant.showInternetErrorDialog(activity, getResources().getString(R.string.no_internet_connection));
        }
        if (isShowDialog) {
            dialog.show();
        } else {
            rewardShow = false;
            interstitialShow = true;
            scratch_count = 0;
            showDailog();
        }
    }

    public void showDailog() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_points_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageView imageView = dialog.findViewById(R.id.points_image);
        TextView title_text = dialog.findViewById(R.id.title_text_points);
        TextView points_text = dialog.findViewById(R.id.points);
        AppCompatButton add_btn = dialog.findViewById(R.id.add_btn);
        AppCompatButton cancel_btn = dialog.findViewById(R.id.cancel_btn);
        cancel_btn.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.watched);
        add_btn.setText("Watch Video (Ad)");
        title_text.setText("Watch Full Video");
        points_text.setText("To Unlock this Reward Points");

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ShowRewarded();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (poiints != 0) {
                    String po = Constant.getString(activity, Constant.USER_POINTS);
                    if (po.equals("")) {
                        po = "0";
                    }
                    int current_Points = Integer.parseInt(po);
                    int finalPoints = current_Points - poiints;
                    Constant.setString(activity, Constant.USER_POINTS, String.valueOf(finalPoints));
                    Intent serviceIntent = new Intent(activity, PointsService.class);
                    serviceIntent.putExtra("points", Constant.getString(activity, Constant.USER_POINTS));
                    activity.startService(serviceIntent);
                    setPointsText();
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onScratchComplete() {
        if (first_time) {
            first_time = false;
            Log.e("onScratch", "Complete");
            Log.e("onScratch", "Complete" + random_points);
            String count = scratch_count_textView.getText().toString();
            String[] counteee = count.split("=", 2);
            String ran = counteee[1];
            Log.e(TAG, "onScratchComplete: " + ran);
            int counter = Integer.parseInt(ran.trim());
            counter_dialog = Integer.parseInt(ran.trim());
            if (counter <= 0) {
                showDialogPoints(0, "0", counter, true);
            } else {
                if (scratch_count == Integer.parseInt(getResources().getString(R.string.rewarded_and_interstitial_ads_between_count))) {
                    if (rewardShow) {
                        showDialogPoints(1, points_text.getText().toString(), counter, false);
                    } else {
                        showDialogPoints(1, points_text.getText().toString(), counter, true);
                    }
                } else {
                    showDialogPoints(1, points_text.getText().toString(), counter, true);
                }
            }
        }
    }

    @Override
    public void onScratchProgress(@NonNull ScratchCardLayout scratchCardLayout, int i) {
        if (scratch_first) {
            scratch_first = false;
            Log.e(TAG, "onScratchStarted: " + random_points);
            if (Constant.isNetworkAvailable(activity)) {
                random_points = "";
                Random random = new Random();
                random_points = String.valueOf(random.nextInt(10));
                if (random_points == null || random_points.equalsIgnoreCase("null")) {
                    random_points = String.valueOf(1);
                }
                points_text.setText(random_points);
                Log.e(TAG, "onScratchStarted: " + random_points);
            } else {
                Constant.showInternetErrorDialog(activity, getResources().getString(R.string.no_internet_connection));
            }
        }
    }

    @Override
    public void onScratchStarted() {
        if (Integer.parseInt(scratch_count_textView.getText().toString().trim()) <= 0) {
            showDialogPoints(0, "0", 0, true);
        }
    }

    private void LoadInterstitial() {
        if (UnityAds.isInitialized()) {
            UnityAds.load(interstitial_id, null);
        }
    }

    private void ShowInterstital() {
        if (adLoaded) {
            UnityAds.show(activity, interstitial_id, new UnityAdsListener());
            adLoaded = false;
        } else {
            LoadInterstitial();
        }
    }

    private final IUnityAdsShowListener unityListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {

        }

        @Override
        public void onUnityAdsShowStart(String s) {

        }

        @Override
        public void onUnityAdsShowClick(String s) {

        }

        @Override
        public void onUnityAdsShowComplete(String s, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {
            LoadInterstitial();
        }
    };


    public void loadRewardedVideo() {
        if (UnityAds.isInitialized()) {
            UnityAds.load(rewarded_id, new UnityLoadListener());
        }
    }

    private void ShowRewarded() {
        if (adLoaded) {
            UnityAds.show(activity,rewarded_id, new UnityAdsListener());
            adLoaded = false;
        } else {
            showDialogPoints(1, points_text.getText().toString().trim(), counter_dialog, true);
            loadRewardedVideo();
        }
    }


    public static boolean adLoaded = false;

    private class UnityLoadListener implements IUnityAdsLoadListener {


        @Override
        public void onUnityAdsAdLoaded(String s) {
            adLoaded = true;
        }

        @Override
        public void onUnityAdsFailedToLoad(String s, UnityAds.UnityAdsLoadError unityAdsLoadError, String s1) {
            adLoaded = false;
        }
    }

    private class UnityAdsListener implements IUnityAdsShowListener {

        @Override
        public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {

        }

        @Override
        public void onUnityAdsShowStart(String s) {

        }

        @Override
        public void onUnityAdsShowClick(String s) {

        }

        @Override
        public void onUnityAdsShowComplete(String s, UnityAds.UnityAdsShowCompletionState finishState) {
            if (activity == null) {
                return;
            }

            if (rewardShow) {
                return;
            }
            // Implement conditional logic for each ad completion status:
            if (finishState.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED)) {
                // Reward the user for watching the ad to completion.
                showDialogPoints(1, points_text.getText().toString().trim(), counter_dialog, true);
            } else if (finishState == UnityAds.UnityAdsShowCompletionState.SKIPPED) {
                // Do not reward the user for skipping the ad.
                showDialogPoints(1, "no", counter_dialog, true);
            }
            loadRewardedVideo();
        }
    }

    public void openPortal(){
        String url = this.getResources().getString(R.string.play_qureka);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.blue));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.setPackage("com.android.chrome");
        customTabsIntent.launchUrl(getContext(), Uri.parse(url));}

    @Override
    public void onDestroy() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    public void qurekaopen(){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.blue));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.setPackage("com.android.chrome");
        customTabsIntent.launchUrl(requireContext(), Uri.parse(silverlink));
    }
}