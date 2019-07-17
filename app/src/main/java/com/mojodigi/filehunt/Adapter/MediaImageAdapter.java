package com.mojodigi.filehunt.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import com.mojodigi.filehunt.R;
import com.mojodigi.filehunt.interfaces.OnClickImage;

import java.util.ArrayList;

public class MediaImageAdapter extends PagerAdapter {



    private ArrayList<String> IMAGES;
    private LayoutInflater inflater;
    private Context context;
    private OnClickImage mOnClickImage;

    public MediaImageAdapter(Context context, ArrayList<String> IMAGES, OnClickImage onClickImage) {
        this.context = context;
        this.IMAGES=IMAGES;
        inflater = LayoutInflater.from(context);
        this.mOnClickImage = onClickImage;
    }




    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }



    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;

       final   SubsamplingScaleImageView mZoomageView = (SubsamplingScaleImageView) imageLayout
                .findViewById(R.id.idMediaZoomView);
       //mZoomageView.setImage(ImageSource.uri(IMAGES.get(position)));
       //Glide.with(context).load(IMAGES.get(position)).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(mZoomageView);

        Glide.with(context).load(IMAGES.get(position)).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mZoomageView.setMinimumDpi(80);
                mZoomageView.setImage(ImageSource.bitmap(resource));
            }
        });


        view.addView(imageLayout, 0);


        mZoomageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mOnClickImage.onClickImage( );
            }
        });


        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
