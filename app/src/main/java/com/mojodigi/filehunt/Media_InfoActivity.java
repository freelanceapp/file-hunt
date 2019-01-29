package com.mojodigi.filehunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.Util;
import com.mojodigi.filehunt.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Utils.Utility;

import java.io.File;
import java.util.Collections;

public class Media_InfoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mInfoBackArrowImage , mInfoFileThumbImage  ;

    private TextView mInfoFileNameText  , mInfoFilePathText , mInfoFileSizeText , mInfoFileTimeText;

    private String selectedPath , mediaType ;
      private Context mContext;
    private SharedPreferenceUtil addprefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media__info);
        mContext=Media_InfoActivity.this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utility.setstatusBarColorBelowM(Media_InfoActivity.this);
        }
        addprefs = new SharedPreferenceUtil(mContext);
        if (addprefs != null) {
            mediaType = addprefs.getStringValue(Constants.mediaType , "");
        }
        initMediaInfoView();
    }

    private void initMediaInfoView() {


        mInfoBackArrowImage = (ImageView) findViewById(R.id.idInfoBackArrowImage);
        mInfoBackArrowImage.setOnClickListener(this);

        mInfoFileThumbImage = (ImageView) findViewById(R.id.idInfoFileThumbImage);

        mInfoFileNameText = (TextView) findViewById(R.id.idInfoFileNameText);
        mInfoFilePathText = (TextView) findViewById(R.id.idInfoFilePathText);
        mInfoFileSizeText = (TextView) findViewById(R.id.idInfoFileSizeText);
        mInfoFileTimeText = (TextView) findViewById(R.id.idInfoFileTimeText);

        mInfoFileNameText.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        mInfoFilePathText.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        mInfoFileSizeText.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        mInfoFileTimeText.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));


        mInfoBackArrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle extrasIntent = getIntent().getExtras();
        if (extrasIntent != null) {
            selectedPath = extrasIntent.getString(Constants.fileInfoPath);

            File f = new File(selectedPath);

            String pth=f.getAbsolutePath();
            long last=f.lastModified();
            String name =f.getName();
            System.out.print(""+pth+last);

            mInfoFileNameText.setText(f.getName());
            mInfoFileSizeText.setText(""+Utility.humanReadableByteCount(f.length(), true));
            mInfoFilePathText.setText(f.getAbsolutePath());
            mInfoFileTimeText.setText("Last Modified : "+Utility.LongToDate(f.lastModified()));



        }
//        if (selectedPath != null) {
//            Glide.with(mContext).load("file://" +selectedPath)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .skipMemoryCache(false).placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder)
//                    .into(mInfoFileThumbImage);
//
//        } else {
//            Utility.dispToast(mContext, "can't load file");
//        }


        if (selectedPath != null && mediaType.equalsIgnoreCase("Video")) {
//            Glide.with(mContext).load("file://" +selectedPath)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .skipMemoryCache(false).placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder)
//                    .into(mInfoFileThumbImage);

            mInfoFileThumbImage.setImageBitmap((Utility.creteVdoBitmapFromPath(selectedPath)));

        } else if (selectedPath != null && mediaType.equalsIgnoreCase("Image")) {
            Glide.with(mContext).load("file://" +selectedPath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder)
                    .into(mInfoFileThumbImage);
        } else if(selectedPath != null && mediaType.equalsIgnoreCase("Audio")){
            Glide.with(mContext).load(R.drawable.ic_mp3_thumb)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).placeholder(R.drawable.ic_mp3_thumb).error(R.drawable.ic_mp3_thumb)
                    .into(mInfoFileThumbImage);
        }else {
            Utility.dispToast(mContext, "can't load file");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.idInfoBackArrowImage:


                break;
        }
    }



    public static Bitmap creteVdoBitmapFromPath(String pathStr)
    {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(pathStr, MediaStore.Images.Thumbnails.MINI_KIND);
        return thumb;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
