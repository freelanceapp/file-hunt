package com.mojodigi.filehunt;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SignalStrength;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.exoplayer2.audio.AudioTrack;
import com.mojodigi.filehunt.AddsUtility.AddConstants;
import com.mojodigi.filehunt.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Utils.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {




    Context mContext;
    TextView txtDispSmallFile, txtHideexternal, txtShowHiddenFiles, txtTextSize,txtSuggestedApps;
    Switch switchHiddenfile, switchHideStorage, switchSmallFile;

    Spinner txtSize_Spinner;
    List<Integer> spinnerArray = new ArrayList<>();
    SharedPreferenceUtil sharedPrefs;
   RelativeLayout suggestAppsLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
         initializeComponent();
        setTextViewFontSize();
    }


    private void initializeComponent() {

        mContext=SettingsActivity.this;
        if(mContext!=null)
        sharedPrefs=new SharedPreferenceUtil(mContext);
        Utility.setActivityTitle2(mContext,getResources().getString(R.string.title_settings));
        txtDispSmallFile= findViewById(R.id.txtDispSmallFile);
        txtHideexternal= findViewById(R.id.txtHideexternal);
        txtShowHiddenFiles=findViewById(R.id.txtShowHiddenFiles);
        txtTextSize=findViewById(R.id.txtTextSize);
        txtSuggestedApps=findViewById(R.id.txtSuggestedApps);

        switchHiddenfile= findViewById(R.id.switchHiddenfile);
        switchHideStorage=findViewById(R.id.switchHideStorage);
        switchSmallFile=findViewById(R.id.switchSmallFile);
        txtSize_Spinner=findViewById(R.id.txtSize_Spinner);
        suggestAppsLayout=findViewById(R.id.suggestAppsLayout);


        spinnerArray.add(16);
        spinnerArray.add(17);
        spinnerArray.add(18);
        spinnerArray.add(19);
        spinnerArray.add(20);
        spinnerArray.add(21);
        spinnerArray.add(22);
        spinnerArray.add(23);
        spinnerArray.add(24);


        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(
                mContext,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray
        );

        txtSize_Spinner.setAdapter(adapter);

        txtDispSmallFile.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        txtHideexternal.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        txtShowHiddenFiles.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        txtTextSize.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        txtSuggestedApps.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));








        txtSize_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                sharedPrefs.setValue(AddConstants.KEY_TEXT_SIZE_INDEX, i);
                Constants.isTextSizeChanged=true;

                txtDispSmallFile.setTextSize(Integer.parseInt(txtSize_Spinner.getSelectedItem().toString()));
                txtHideexternal.setTextSize(Integer.parseInt(txtSize_Spinner.getSelectedItem().toString()));
                txtShowHiddenFiles.setTextSize(Integer.parseInt(txtSize_Spinner.getSelectedItem().toString()));
                txtTextSize.setTextSize(Integer.parseInt(txtSize_Spinner.getSelectedItem().toString()));
                txtSuggestedApps.setTextSize(Integer.parseInt(txtSize_Spinner.getSelectedItem().toString()));
//                Utility.setActivityTitle2(mContext,getResources().getString(R.string.title_settings));




            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        switchHideStorage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if(checked) {
                    sharedPrefs.setValue(AddConstants.KEY_HIDE_EXTERNAL_STORAGE, true);
                    }
                else {
                    sharedPrefs.setValue(AddConstants.KEY_HIDE_EXTERNAL_STORAGE, false);
                }

            }
        });

        switchHiddenfile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if(checked) {
                    sharedPrefs.setValue(AddConstants.KEY_SHOW_HIDDEN_FILE, true);
                    }
                else {
                    sharedPrefs.setValue(AddConstants.KEY_SHOW_HIDDEN_FILE, false); }
            }
        });

        switchSmallFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked) {
                    sharedPrefs.setValue(AddConstants.KEY_DISPLAY_SMALL_FILE, true);
                    }
                else {
                    sharedPrefs.setValue(AddConstants.KEY_DISPLAY_SMALL_FILE, false);
                }
            }
        });


     suggestAppsLayout.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

             final Dialog dialog = new Dialog(mContext);
             dialog.setContentView(R.layout.dialog_suggest_apps);
             dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

             TextView txtApp1=dialog.findViewById(R.id.txtApp1);
             TextView txtApp2=dialog.findViewById(R.id.txtApp2);

             txtApp1.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
             txtApp2.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));

             final ImageView appicon1=dialog.findViewById(R.id.appIcon);
             final ImageView appicon2=dialog.findViewById(R.id.appIcon2);

             RelativeLayout app1Layout=dialog.findViewById(R.id.app1Layout);
             RelativeLayout app2Layout=dialog.findViewById(R.id.app2Layout);




             app1Layout.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {

                     try {
                         startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Constants.screenLock)));
                     } catch (android.content.ActivityNotFoundException anfe) {
                         startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Constants.screenLock)));
                     }
                     dialog.dismiss();
                 }
             });


             app2Layout.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {

                     try {
                         startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Constants.videoPlayer)));
                     } catch (android.content.ActivityNotFoundException anfe) {
                         startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Constants.videoPlayer)));
                     }
                     dialog.dismiss();
                 }

             });



             //its heavier to use

            /* Glide.with(mContext).load(R.drawable.suggestapplockscreen).asBitmap().centerCrop().into(new BitmapImageViewTarget(appicon1) {
                 @Override
                 protected void setResource(Bitmap resource) {
                     RoundedBitmapDrawable circularBitmapDrawable =
                             RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                     circularBitmapDrawable.setCircular(true);
                     appicon1.setImageDrawable(circularBitmapDrawable);
                 }
             });*/


            /* Glide.with(mContext).load(R.drawable.suggestappvdo).asBitmap().centerCrop().into(new BitmapImageViewTarget(appicon2) {
                 @Override
                 protected void setResource(Bitmap resource) {
                     RoundedBitmapDrawable circularBitmapDrawable =
                             RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                     circularBitmapDrawable.setCircular(true);
                     appicon2.setImageDrawable(circularBitmapDrawable);
                 }
             });*/



             dialog.show();
         }
     });



    setSelectedValueInField();

    }

    private void setSelectedValueInField() {

        if(mContext !=null && sharedPrefs!=null)
        {
            boolean hideExtPrefValue=sharedPrefs.getBoolanValue(AddConstants.KEY_HIDE_EXTERNAL_STORAGE, false);
            boolean showHiddenFile=sharedPrefs.getBoolanValue(AddConstants.KEY_SHOW_HIDDEN_FILE, false);
            boolean showSmallFile=sharedPrefs.getBoolanValue(AddConstants.KEY_DISPLAY_SMALL_FILE, false);
            int  txtSizeIndex= sharedPrefs.getIntValue(AddConstants.KEY_TEXT_SIZE_INDEX, 0);


            switchHideStorage.setChecked(hideExtPrefValue);
            switchHiddenfile.setChecked(showHiddenFile);
            switchSmallFile.setChecked(showSmallFile);

            //txtSize_Spinner.setSelection(txtSize);

            txtSize_Spinner.setSelection(txtSizeIndex);
            Log.d("fontSize  -index", ""+txtSizeIndex);



            sharedPrefs.setValue(AddConstants.KEY_TEXT_SIZE, spinnerArray.get(txtSizeIndex));

            // hold the the size of text  to  be applied on  txt of the app;
            int txtSize=sharedPrefs.getIntValue(AddConstants.KEY_TEXT_SIZE, 10);
            Log.d("fontSize- size", ""+txtSize);

            }
    }

    private  void setTextViewFontSize()
    {
        txtDispSmallFile.setTextSize(Utility.getFontSizeValueHeading(mContext));
        txtHideexternal.setTextSize(Utility.getFontSizeValueHeading(mContext));
        txtShowHiddenFiles.setTextSize(Utility.getFontSizeValueHeading(mContext));
        txtTextSize.setTextSize(Utility.getFontSizeValueHeading(mContext));
        txtSuggestedApps.setTextSize(Utility.getFontSizeValueHeading(mContext));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();

        int abc=(Integer) txtSize_Spinner.getSelectedItem();
        System.out.print(""+abc);
        sharedPrefs.setValue(AddConstants.KEY_TEXT_SIZE, (Integer) txtSize_Spinner.getSelectedItem());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
