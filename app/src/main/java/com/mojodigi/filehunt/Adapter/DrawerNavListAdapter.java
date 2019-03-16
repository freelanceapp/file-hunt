package com.mojodigi.filehunt.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mojodigi.filehunt.Model.DrawerObjectItemList;
import com.mojodigi.filehunt.R;
import com.mojodigi.filehunt.Utils.Utility;

public class DrawerNavListAdapter extends ArrayAdapter<DrawerObjectItemList> {

    private Context mContext;
    private int layoutResourceId;
    private DrawerObjectItemList data[] = null;

    public DrawerNavListAdapter(Context mContext, int layoutResourceId, DrawerObjectItemList[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView navMenuIconView = (ImageView) listItem.findViewById(R.id.navMenuIconView);
        TextView navMenuTextView = (TextView) listItem.findViewById(R.id.navMenuTextView);


        DrawerObjectItemList folder = data[position];

        navMenuTextView.setTextSize(Utility.getFontSizeValueHeading(mContext));
        navMenuTextView.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));

        navMenuIconView.setImageResource(folder.icon);
        navMenuTextView.setText(folder.name);

        return listItem;
    }

}