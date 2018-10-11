package com.mojodigi.filehunt;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;
import android.widget.TextView;

//
import com.mojodigi.filehunt.Utils.Utility;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    TextView apptxt,versiontxt;
    private Timer timer;
    private ProgressBar progressBar;
    private int i=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //https://www.truiton.com/2015/06/android-tabs-example-fragments-viewpager/
        setContentView(R.layout.splash_layout);
       apptxt=(TextView)findViewById(R.id.apptxt);
       apptxt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(SplashActivity.this));

        versiontxt=(TextView)findViewById(R.id.versiontxt);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versiontxt.setText("v "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception e){}


        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setMax(30);
        timer=new Timer();
        final long period = 30;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //this repeats every 100 ms
                if (i<=30){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //any ui pdate will be here
                         //   textView.setText(String.valueOf(i)+"%");
                        }
                    });
                    progressBar.setProgress(i);
                    i++;
                }else
                    {
                    //closing the timer
                    timer.cancel();
                    redirectActivity(MainActivity.class,true);
                }
            }
        }, 0, period);

    }



    private void redirectActivity(final Class<? extends Activity> ActivityToOpen, boolean finish)
    {
        Intent i  = new Intent(getBaseContext(),ActivityToOpen);
        startActivity(i);
        if(finish)
            finish();
    }
}
