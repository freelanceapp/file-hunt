package com.mojodigi.filehunt.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mojodigi.filehunt.Model.Grid_Model;
import com.mojodigi.filehunt.R;

import java.util.ArrayList;

//

public class MultiSelectAdapter_Video extends RecyclerView.Adapter<MultiSelectAdapter_Video.MyViewHolder>  implements Filterable {

    public ArrayList<Grid_Model> VdoList=new ArrayList<>();
    public ArrayList<Grid_Model> VdoListfiltered=new ArrayList<>();
    public ArrayList<Grid_Model> selected_VdoList=new ArrayList<>();
    private  VdoListener listener;
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_image;
        public CheckBox itemCheckBox;

        public MyViewHolder(View view) {
            super(view);

             iv_image=(ImageView)view.findViewById(R.id.vdoThumbNail);
             itemCheckBox=(CheckBox)view.findViewById(R.id.itemCheckBoxVdo);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected docs in callback
                    int pos=getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION)
                    listener.onVdoSelected(VdoListfiltered.get(getAdapterPosition()));
                }
            });

             }
    }

     public MultiSelectAdapter_Video(Context context, ArrayList<Grid_Model> VdoList, ArrayList<Grid_Model> selectedVdoList, VdoListener listener) {
        this.mContext=context;
        this.VdoList = VdoList;
        this.VdoListfiltered=VdoList;
        this.selected_VdoList = selectedVdoList;
        this.listener=listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_vdo_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Grid_Model Img = VdoListfiltered.get(position);

                   //  long t1=System.currentTimeMillis();

                    if(Img.getImgBitmapStr()!=null)
                    {
                             Glide.with(mContext).load("file://" + Img.getImgBitmapStr())
                                .skipMemoryCache(false).placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder)
                                .into(holder.iv_image);
                             }


                  // holder.iv_image.setImageBitmap(Img.getImgBitmap());

//                long t2=System.currentTimeMillis();
//                long timeTaken=t2-t1;
//                String time= Utility.convertDuration(timeTaken);
//                System.out.println(""+time);


                if(selected_VdoList.contains(VdoList.get(position)))
            holder.itemCheckBox.setVisibility(View.VISIBLE);
        else
            holder.itemCheckBox.setVisibility(View.GONE);
        }

    @Override
    public int getItemCount() {
        return VdoListfiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    VdoListfiltered = VdoList;
                } else {
                    ArrayList<Grid_Model> filteredList = new ArrayList<>();
                    for (Grid_Model row : VdoList) {

                        //condition to search for
                        if (row.getImgBitmapStr().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    VdoListfiltered= filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = VdoListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                VdoListfiltered = (ArrayList<Grid_Model>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface VdoListener {
        void onVdoSelected(Grid_Model imgModel);
    }



}

