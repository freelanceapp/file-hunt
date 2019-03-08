package com.mojodigi.filehunt;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SignalStrength;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.audio.AudioTrack;
import com.mojodigi.filehunt.AddsUtility.AddConstants;
import com.mojodigi.filehunt.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.filehunt.Utils.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {




    Context mContext;
    TextView txtDispSmallFile, txtHideexternal, txtShowHiddenFiles, txtTextSize;
    CheckBox checkHiddenfile, checkHideStorage, checkSmallFile;
    Spinner txtSize_Spinner;
    List<Integer> spinnerArray = new ArrayList<>();
    SharedPreferenceUtil sharedPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
         initializeComponent();
    }

    private void initializeComponent() {

        mContext=SettingsActivity.this;
        if(mContext!=null)
        sharedPrefs=new SharedPreferenceUtil(mContext);
        Utility.setActivityTitle(mContext,getResources().getString(R.string.title_settings));
        txtDispSmallFile= findViewById(R.id.txtDispSmallFile);
        txtHideexternal= findViewById(R.id.txtHideexternal);
        txtShowHiddenFiles=findViewById(R.id.txtShowHiddenFiles);
        txtTextSize=findViewById(R.id.txtTextSize);

        checkHiddenfile= findViewById(R.id.checkHiddenfile);
        checkHideStorage=findViewById(R.id.checkHideStorage);
        checkSmallFile=findViewById(R.id.checkSmallFile);
        txtSize_Spinner=findViewById(R.id.txtSize_Spinner);

        spinnerArray.add(10);
        spinnerArray.add(11);
        spinnerArray.add(12);
        spinnerArray.add(13);
        spinnerArray.add(14);
        spinnerArray.add(15);
        spinnerArray.add(16);
        spinnerArray.add(17);
        spinnerArray.add(18);
        spinnerArray.add(19);
        spinnerArray.add(20);


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








        txtSize_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                sharedPrefs.setValue(AddConstants.KEY_TEXT_SIZE_INDEX, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        checkHideStorage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        checkHiddenfile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if(checked) {
                    sharedPrefs.setValue(AddConstants.KEY_SHOW_HIDDEN_FILE, true);
                    }
                else {
                    sharedPrefs.setValue(AddConstants.KEY_SHOW_HIDDEN_FILE, false); }
            }
        });

        checkSmallFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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






    setSelectedValueInField();

    }

    private void setSelectedValueInField() {

        if(mContext !=null && sharedPrefs!=null)
        {
            boolean hideExtPrefValue=sharedPrefs.getBoolanValue(AddConstants.KEY_HIDE_EXTERNAL_STORAGE, false);
            boolean showHiddenFile=sharedPrefs.getBoolanValue(AddConstants.KEY_SHOW_HIDDEN_FILE, false);
            boolean showSmallFile=sharedPrefs.getBoolanValue(AddConstants.KEY_DISPLAY_SMALL_FILE, false);
            int  txtSizeIndex= sharedPrefs.getIntValue(AddConstants.KEY_TEXT_SIZE_INDEX, 0);


            checkHideStorage.setChecked(hideExtPrefValue);
            checkHiddenfile.setChecked(showHiddenFile);
            checkSmallFile.setChecked(showSmallFile);

            //txtSize_Spinner.setSelection(txtSize);

            txtSize_Spinner.setSelection(txtSizeIndex);
            Log.d("fontSize  -index", ""+txtSizeIndex);



            sharedPrefs.setValue(AddConstants.KEY_TEXT_SIZE, spinnerArray.get(txtSizeIndex));

            // hold the the size of text  toi  be applied on  txt of the app;
            int txtSize=sharedPrefs.getIntValue(AddConstants.KEY_TEXT_SIZE, 10);
            Log.d("fontSize- size", ""+txtSize);

            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
