package com.example.filehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    private Timer timer;
    private ProgressBar progressBar;
    private int i=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //https://www.truiton.com/2015/06/android-tabs-example-fragments-viewpager/
        setContentView(R.layout.splash_layout);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setMax(40);
        timer=new Timer();
        final long period = 40;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //this repeats every 100 ms
                if (i<=40){
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
