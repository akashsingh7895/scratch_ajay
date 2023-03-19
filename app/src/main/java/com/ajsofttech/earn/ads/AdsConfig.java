package com.ajsofttech.earn.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.ajsofttech.earn.FirebaseProperties;
import com.ajsofttech.earn.R;
import com.ajsofttech.earn.activity.MainActivity;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;

public class AdsConfig {
    // applovin
    public static MaxInterstitialAd interstitialAd;

    // unity

    public static boolean testMode = true;

    public static void loadAds(Activity activity){
        if(FirebaseProperties.isEnableAds==true &&activity!=null){
            Log.d("adasdas","adsEnabled");
            if(FirebaseProperties.adsNetworkName.equalsIgnoreCase("applovin")){
                AppLovinSdk.initializeSdk(activity, new AppLovinSdk.SdkInitializationListener() {
                    @Override
                    public void onSdkInitialized(AppLovinSdkConfiguration config) {
                        interstitialAd = new MaxInterstitialAd(FirebaseProperties.intApplovin,activity);
                        interstitialAd.loadAd();
                        Log.d("adasdas","loading applovin...");
                    }
                });
            }
            if(FirebaseProperties.adsNetworkName.equalsIgnoreCase("unity")){
                UnityAds.initialize(activity, FirebaseProperties.unityGameID, testMode, new IUnityAdsInitializationListener() {
                    @Override
                    public void onInitializationComplete() {
                        Log.d("adasdas","Unity Initialize..."+FirebaseProperties.intUnityAd);
                        UnityAds.load(FirebaseProperties.intUnityAd);
                    }

                    @Override
                    public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {

                    }
                });

                Log.d("adasdas","loading unity...");
            }
        }else {
            // ads disabled
            Log.d("adasdas","adsDisabled | activity=null");
        }
    }
    public static void showAd(Activity activity){
        // applovin...
        if(FirebaseProperties.adsNetworkName.equalsIgnoreCase("applovin")) {
            interstitialAd.loadAd();
            if (interstitialAd != null) {
                if (interstitialAd.isReady()) {
                    interstitialAd.showAd();
                    Log.d("adasdas", "applovin show");
                }else {
                    Log.d("adasdas", "applovin not show");
                }
            } else {
                Log.d("adasdas", "applovin null");
            }
        }

        // unity...

        if(FirebaseProperties.adsNetworkName.equalsIgnoreCase("unity")) {
            UnityAds.load(FirebaseProperties.intUnityAd);
            UnityAds.show(activity,FirebaseProperties.intUnityAd);
        }
    }
}
