package com.mojodigi.filehunt.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mojodigi.filehunt.Model.Grid_Model;
import com.mojodigi.filehunt.R;


import java.util.ArrayList;

public class MultiSelectAdapter extends RecyclerView.Adapter<MultiSelectAdapter.MyViewHolder>  implements Filterable {

    public ArrayList<Grid_Model> ImgList=new ArrayList<>();
    public ArrayList<Grid_Model> ImgListfiltered=new ArrayList<>();
    public ArrayList<Grid_Model> selected_ImgList=new ArrayList<>();
    private ImgListener listener;
    Context mContext;



    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_image;
        public CheckBox itemCheckBox;

        public MyViewHolder(View view) {
            super(view);

             iv_image=(ImageView)view.findViewById(R.id.iv_image);
             itemCheckBox=(CheckBox)view.findViewById(R.id.itemCheckBox);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected docs in callback
                    int pos=getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION)
                    listener.onImageSelected(ImgListfiltered.get(getAdapterPosition()),pos);
                }
            });

             }
    }


    public MultiSelectAdapter(Context context, ArrayList<Grid_Model> ImgList, ArrayList<Grid_Model> selectedImgList, ImgListener listener ) {
        this.mContext=context;
        this.ImgList = ImgList;
        this.ImgListfiltered=ImgList;
        this.selected_ImgList = selectedImgList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_image_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Grid_Model Img = ImgListfiltered.get(position);


                 Glide.with(mContext).load("file://" +Img.getImgPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false).placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder)
                .into(holder.iv_image);


                 if(selected_ImgList.contains(ImgList.get(position)))
            holder.itemCheckBox.setVisibility(View.VISIBLE);
        else
            holder.itemCheckBox.setVisibility(View.INVISIBLE);





        }




    @Override
    public int getItemCount() {
        return ImgListfiltered.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    ImgListfiltered = ImgList;
                } else {
                    ArrayList<Grid_Model> filteredList = new ArrayList<>();
                    for (Grid_Model row : ImgList) {

                        //condition to search for
                        if (row.getImgPath().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    ImgListfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = ImgListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ImgListfiltered = (ArrayList<Grid_Model>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ImgListener {
        void onImageSelected(Grid_Model imgModel,int position);
    }


}

