package com.mojodigi.filehunt.Adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mojodigi.filehunt.Model.Model_images;
import com.mojodigi.filehunt.R;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<Model_images> {

    Context context;
    ViewHolder viewHolder;
    ArrayList<Model_images> al_menu = new ArrayList<>();
    int int_position;


    public GridViewAdapter(Context context, ArrayList<Model_images> al_menu, int int_position) {
        super(context, R.layout.grid_image_layout, al_menu);
        this.al_menu = al_menu;
        this.context = context;
        this.int_position = int_position;


    }

    @Override
    public int getCount() {

        Log.e("ADAPTER LIST SIZE", al_menu.get(int_position).getAl_imagepath().size() + "");
        return al_menu.get(int_position).getAl_imagepath().size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (al_menu.get(int_position).getAl_imagepath().size() > 0) {
            return al_menu.get(int_position).getAl_imagepath().size();
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_image_layout, parent, false);
            viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


                System.out.println("file path->"+"file://" + al_menu.get(int_position).getAl_imagepath().get(position));

                Glide.with(context).load("file://" + al_menu.get(int_position).getAl_imagepath().get(position))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(viewHolder.iv_image);






        return convertView;

    }

    private static class ViewHolder {

        ImageView iv_image;
        CheckBox checkbox;
        int id;


    }


}
