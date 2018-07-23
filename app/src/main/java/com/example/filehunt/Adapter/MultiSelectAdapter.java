package com.example.filehunt.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.filehunt.Model.Grid_Model;
import com.example.filehunt.R;


import java.util.ArrayList;




public class MultiSelectAdapter extends RecyclerView.Adapter<MultiSelectAdapter.MyViewHolder> {

    public ArrayList<Grid_Model> ImgList=new ArrayList<>();
    public ArrayList<Grid_Model> selected_ImgList=new ArrayList<>();
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_image;
        public CheckBox  itemCheckBox;

        public MyViewHolder(View view) {
            super(view);

             iv_image=(ImageView)view.findViewById(R.id.iv_image);
             itemCheckBox=(CheckBox)view.findViewById(R.id.itemCheckBox);

             }
    }


    public MultiSelectAdapter(Context context, ArrayList<Grid_Model> ImgList, ArrayList<Grid_Model> selectedImgList) {
        this.mContext=context;
        this.ImgList = ImgList;
        this.selected_ImgList = selectedImgList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_image_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Grid_Model Img = ImgList.get(position);


        Glide.with(mContext).load("file://" +Img.getImgPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(holder.iv_image);




        if(selected_ImgList.contains(ImgList.get(position)))
            holder.itemCheckBox.setVisibility(View.VISIBLE);
        else
            holder.itemCheckBox.setVisibility(View.INVISIBLE);
        }


    @Override
    public int getItemCount() {
        return ImgList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}

