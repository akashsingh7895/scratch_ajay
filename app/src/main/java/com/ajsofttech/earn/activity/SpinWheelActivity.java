package com.ajsofttech.earn.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajsofttech.earn.R;
import com.ajsofttech.earn.utils.Constant;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

public class SpinWheelActivity extends AppCompatActivity implements MaxAdListener {

    Dialog dialog;
    private static final int[]  sectors = {50,40,35,30,25,20,15,10,5,0};
    private static final int[] sectorsDegrees = new int[sectors.length];
    private static final Random random = new Random();

    private int degree = 0;
    private boolean isSpinning = false;

    int wonAmount;

    int spinCounter = 0;
    int spinTotal = 20;
    String todayDate,currentDate;

    int spinCounterPlus ;
    int spinTotalLeft ;


    ImageView spinWheel,spinButton;
    TextView spinLeft,points_textView;

    String points;
    int poits_user;


    private MaxInterstitialAd interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_wheel);


        spinWheel = findViewById(R.id.spin_wheel);
        spinButton = findViewById(R.id.spin_btn);
        spinLeft = findViewById(R.id.total_spin);
        points_textView = findViewById(R.id.points_text_in_toolbar);

        points = Constant.getString(getApplicationContext(), Constant.USER_POINTS);
      //  poits_user=Integer.parseInt(points);


        interstitialAd = new MaxInterstitialAd(getString(R.string.Applovin_Inter),this);
        interstitialAd.setListener(this);
        interstitialAd.loadAd();




        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        todayDate = df.format(Calendar.getInstance().getTime());
        dialog = new Dialog(this);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.putInt("spinCounter",spinCounter);
        myEdit.putInt("spinTotal",spinTotal);
        myEdit.putString("date",todayDate);

        myEdit.commit();

        // FetchData from shredpref
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        spinCounter  = sh.getInt("spinCounter",0 );
        currentDate  = sh.getString("date", "");
        spinTotal  = sh.getInt("spinTotalLeft", 0);
        spinLeft.setText(String.valueOf("Your Today Spin count Left : " +spinTotal));
       // binding.spinAvail.setText(String.valueOf(spinCounter));



        getDegreesForSectors();
        setPointsText();

        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinTotal>spinCounter && currentDate.equals(todayDate)){
                    if (!isSpinning){
                        Log.d("left", "bvalue:"+spinTotalLeft);
                        spin();
                        isSpinning = true;
                        // spinCounterPlus =  spinCounter++;
                         spinTotalLeft = --spinTotal;
                        Log.d("left", "value:"+spinTotalLeft);
                        //Log.d(String.valueOf(spinTotalLeft),"how");
                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putInt("spinCounter",spinCounterPlus);
                        myEdit.putInt("spinTotalLeft",spinTotalLeft);
                        myEdit.commit();
                       // binding.spinAvail.setText(String.valueOf(spinCounterPlus));
                      spinLeft.setText(String.valueOf("Your Today Spin count Left " + spinTotalLeft));
                    }
                }else {
                    Toast.makeText(SpinWheelActivity.this, "you Don't have chance", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
    private void spin(){
        degree = random.nextInt(sectors.length-1);
        RotateAnimation rotateAnimation = new RotateAnimation(0,(360*sectors.length)+sectorsDegrees[degree],
                RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);

        rotateAnimation.setDuration(3600);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                wonAmount= sectors[sectors.length-(degree+1)];


                dialog.setContentView(R.layout.show_points_dialog);
                dialog.setCancelable(false);
                Objects.requireNonNull(dialog
                        .getWindow()).setBackgroundDrawable
                        (new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                Button addButton = dialog.findViewById(R.id.add_btn);
                TextView textView = dialog.findViewById(R.id.points);
                textView.setText(String.valueOf(wonAmount));


                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int poiints = poits_user + wonAmount;

                        if (interstitialAd.isReady()){
                            interstitialAd.showAd();
                            Constant.addPoints(getApplicationContext(), poiints, 0);
                            setPointsText();
                            dialog.dismiss();
                        }else {
                            Constant.addPoints(getApplicationContext(), poiints, 0);
                            setPointsText();
                            dialog.dismiss();
                        }


                    //    Toast.makeText(SpinWheelActivity.this, "point_not_add", Toast.LENGTH_SHORT).show();
//                        startActivity(getIntent());
//                        overridePendingTransition(0,0);
                       // dialog.dismiss();
                    }
                });


               // Toast.makeText(SpinWheelActivity.this, ""+wonAmount, Toast.LENGTH_SHORT).show();




                isSpinning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        spinWheel.startAnimation(rotateAnimation);



    }
    public void getDegreesForSectors(){
        int sectorsDegree = 360/sectors.length;

        for (int i = 0;i<sectors.length;i++){
            sectorsDegrees[i] =(i+1)*sectorsDegree;
        }
    }

    private void setPointsText() {
        if (points_textView != null) {
            String userPoints = Constant.getString(getApplicationContext(), Constant.USER_POINTS);
            if (userPoints.equalsIgnoreCase("")) {
                userPoints = "0";
            }
            points_textView.setText(userPoints);
        }
    }


    @Override
    public void onAdLoaded(MaxAd ad) {

    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {

    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {

    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {

    }
}