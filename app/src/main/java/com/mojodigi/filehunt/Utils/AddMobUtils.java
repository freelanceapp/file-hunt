package com.mojodigi.filehunt.Utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.mojodigi.filehunt.ApkActivityRe;
import com.mojodigi.filehunt.R;

public class AddMobUtils extends Activity
{



    private String ADDLOGTAG="BANNER_ADD_LOGTAG";
    private String ADDLOGTAG_INTERESRT="INTERESTIAL_ADD_LOGTAG";
    private String ADDLOGTAG_VIDEO="VIDEO_ADD_LOGTAG";
    RewardedVideoAd mRewardedVideoAd;

  public  void displayBannerAdd(final AdView mAdView)
  {


      AdRequest adRequest = new AdRequest.Builder()
              .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
              // Check the LogCat to get your test device ID

             // .addTestDevice("33BE2250B43518CCDA7DE426D04EE231")

              .build();

      mAdView.setAdListener(new AdListener() {
          @Override
          public void onAdLoaded() {
              // Toast.makeText(getApplicationContext(), "Ad loaded!", Toast.LENGTH_SHORT).show();
              Log.d(ADDLOGTAG,"Add is Loaded");
              mAdView.setVisibility(View.VISIBLE);
          }

          @Override
          public void onAdClosed() {
              // Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
              Log.d(ADDLOGTAG,"Ad is closed!");
          }

          @Override
          public void onAdFailedToLoad(int errorCode) {
              //Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
              Log.d(ADDLOGTAG,""+"Ad failed to load! error code: " + errorCode);
          }

          @Override
          public void onAdLeftApplication() {
              // Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
              Log.d(ADDLOGTAG,"Ad left application!");
          }

          @Override
          public void onAdOpened() {
              super.onAdOpened();
          }
      });

      mAdView.loadAd(adRequest);

      //return  mAdView;
  }
    private InterstitialAd interstitialAd;
    public void showInterstitial(final Context ctx)
    {
        interstitialAd = new InterstitialAd(ctx);

        // set the ad unit ID
        interstitialAd.setAdUnitId(ctx.getResources().getString(R.string.ad_unit_id));

//        AdRequest adRequest = new AdRequest.Builder()
//                //.addTestDevice("1A4C168E6A1C14BCF1F65DA2AC9E8C43")
//                .build();

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID)).build();
        interstitialAd.loadAd(adRequest);

        // Load ads into Interstitial Ads
        interstitialAd.loadAd(adRequest);


        interstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });


    }
    private void showInterstitial()
    {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            Log.d(ADDLOGTAG_INTERESRT,"interestial Add Loaded");
        }
        else
        {
            Log.d(ADDLOGTAG_INTERESRT,"interestial add did not load");

        }
    }


    public  RewardedVideoAd displayRewaredVideoAdd(final Context ctx, RewardedVideoAd mRewardedVideoAdp)
    {

        this.mRewardedVideoAd=mRewardedVideoAdp;
        MobileAds.initialize(this,ctx.getString(R.string.admob_app_id));

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(ctx);

        mRewardedVideoAd.loadAd(ctx.getString(R.string.ad_unit_id_reward), new AdRequest.Builder().addTestDevice(Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID))
                //.addTestDevice("1A4C168E6A1C14BCF1F65DA2AC9E8C43")
                .build());

        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {

            @Override
            public void onRewarded(RewardItem rewardItem) {
                //Toast.makeText(RewardedVideoAdActivity.this, "onRewarded! currency: " + rewardItem.getType() + "  amount: " +
                       // rewardItem.getAmount(), Toast.LENGTH_SHORT).show();

                Log.d(ADDLOGTAG_VIDEO,"onRewarded Executed");
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

                Log.d(ADDLOGTAG_VIDEO,"onRewardedVideoAdLeftApplication");
            }

            @Override
            public void onRewardedVideoAdClosed() {


                Log.d(ADDLOGTAG_VIDEO,"onRewardedVideoAdClosed");
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {

                Log.d(ADDLOGTAG_VIDEO,"onRewardedVideoAdFailedToLoad "+errorCode);
                //mRewardedVideoAd.loadAd(ctx.getString(R.string.ad_unit_id_reward), new AdRequest.Builder().addTestDevice(Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID)).addTestDevice("1A4C168E6A1C14BCF1F65DA2AC9E8C43").build());
            }

            @Override
            public void onRewardedVideoCompleted() {
                Log.d(ADDLOGTAG_VIDEO,"onRewardedCompleted");
            }

            @Override
            public void onRewardedVideoAdLoaded() {

                Log.d(ADDLOGTAG_VIDEO,"onRewardedVideoAdLoaded");

              //  Toast.makeText(ctx, "video add  loaded", Toast.LENGTH_SHORT).show();
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                }

            }

            @Override
            public void onRewardedVideoAdOpened() {
                Log.d(ADDLOGTAG_VIDEO,"onRewardedVideoAdOpened");
            }

            @Override
            public void onRewardedVideoStarted() {
                Log.d(ADDLOGTAG_VIDEO,"onRewardedVideoStarted");
            }
        });

       // loadRewardedVideoAd(ctx,mRewardedVideoAd);

               return  mRewardedVideoAd;

    }
    private void loadRewardedVideoAd(Context ctx,RewardedVideoAd mRewardedVideoAd) {
       // mRewardedVideoAd.loadAd(ctx.getString(R.string.ad_unit_id_reward), new AdRequest.Builder().addTestDevice("1A4C168E6A1C14BCF1F65DA2AC9E8C43").build());
        mRewardedVideoAd.loadAd(ctx.getString(R.string.ad_unit_id_reward), new AdRequest.Builder().addTestDevice(Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID)).addTestDevice("1A4C168E6A1C14BCF1F65DA2AC9E8C43").build());
        // showing the ad to user
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
        else {
            Toast.makeText(ctx, "video add not loaded", Toast.LENGTH_SHORT).show();
        }
    }





}
