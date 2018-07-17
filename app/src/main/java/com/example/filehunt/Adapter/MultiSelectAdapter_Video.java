package com.example.filehunt.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.filehunt.Model.Grid_Model;
import com.example.filehunt.R;

import java.util.ArrayList;


public class MultiSelectAdapter_Video extends RecyclerView.Adapter<MultiSelectAdapter_Video.MyViewHolder> {

    public ArrayList<Grid_Model> VdoList=new ArrayList<>();
    public ArrayList<Grid_Model> selected_VdoList=new ArrayList<>();
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_image;
        public CheckBox  itemCheckBox;

        public MyViewHolder(View view) {
            super(view);

             iv_image=(ImageView)view.findViewById(R.id.vdoThumbNail);
             itemCheckBox=(CheckBox)view.findViewById(R.id.itemCheckBoxVdo);

             }
    }

     public MultiSelectAdapter_Video(Context context, ArrayList<Grid_Model> VdoList, ArrayList<Grid_Model> selectedVdoList) {
        this.mContext=context;
        this.VdoList = VdoList;
        this.selected_VdoList = selectedVdoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_vdo_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Grid_Model Img = VdoList.get(position);


                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(Img.getImgPath(),
                MediaStore.Images.Thumbnails.MINI_KIND);

                holder.iv_image.setImageBitmap(thumb);

                if(selected_VdoList.contains(VdoList.get(position)))
            holder.itemCheckBox.setVisibility(View.VISIBLE);
        else
            holder.itemCheckBox.setVisibility(View.GONE);
        }

    @Override
    public int getItemCount() {
        return VdoList.size();
    }
}

