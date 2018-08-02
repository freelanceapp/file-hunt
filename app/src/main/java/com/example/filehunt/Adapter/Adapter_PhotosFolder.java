package com.example.filehunt.Adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.filehunt.Model.Model_images;
import com.example.filehunt.R;

import java.util.ArrayList;


public class Adapter_PhotosFolder extends ArrayAdapter<Model_images> {

    Context context;
    ViewHolder viewHolder;
    ArrayList<Model_images> al_menu = new ArrayList<>();
   int media_position;

    public Adapter_PhotosFolder(Context context, ArrayList<Model_images> al_menu, int media_position) {
        super(context, R.layout.adapter_photosfolder, al_menu);
        this.al_menu = al_menu;
        this.context = context;
        this.media_position=media_position;


    }

    @Override
    public int getCount() {

        Log.e("ADAPTER LIST SIZE", al_menu.size() + "");
        return al_menu.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (al_menu.size() > 0) {
            return al_menu.size();
        } else {
            return 1;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_photosfolder, parent, false);
            viewHolder.tv_foldern = (TextView) convertView.findViewById(R.id.tv_folder);
            viewHolder.tv_foldersize = (TextView) convertView.findViewById(R.id.tv_folder2);
            viewHolder.tv_date_modified=(TextView)convertView.findViewById(R.id.tv_folder3);
            viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);




            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
           viewHolder.tv_date_modified.setText(al_menu.get(position).getDate_modified());
            viewHolder.tv_foldern.setText(al_menu.get(position).getStr_folder());
            viewHolder.tv_foldersize.setText(al_menu.get(position).getAl_imagepath().size()+"");


   if (media_position==2)
   {
       viewHolder.iv_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_music));
   }
   else {
        Glide.with(context).load("file://" + al_menu.get(position).getAl_imagepath().get(0))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(viewHolder.iv_image);
   }

        return convertView;

    }

    private static class ViewHolder {
        TextView tv_foldern, tv_foldersize,tv_date_modified;
        ImageView iv_image;


    }


}
