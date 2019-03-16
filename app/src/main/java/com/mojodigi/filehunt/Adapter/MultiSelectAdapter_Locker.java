package com.mojodigi.filehunt.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.internal.ServiceSpecificExtraArgs;
import com.mojodigi.filehunt.Model.Model_Locker;
import com.mojodigi.filehunt.R;
import com.mojodigi.filehunt.Utils.Utility;

import java.util.ArrayList;

public class MultiSelectAdapter_Locker extends RecyclerView.Adapter<MultiSelectAdapter_Locker.MyViewHolder>  implements Filterable {

    public ArrayList<Model_Locker> LockerList=new ArrayList<>();
    public ArrayList<Model_Locker> LockerListfiltered=new ArrayList<>();
    public ArrayList<Model_Locker> selected_LockerList=new ArrayList<>();
    private fileSelectListener listener;
    Context mContext;
    int media_Type;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView FileIcon;
        public CheckBox chbx;

      public TextView fileName,fileSize,fileMdate,fileDuration;
            RelativeLayout rellayout;
        public MyViewHolder(View view) {
            super(view);

             fileName=(TextView) view.findViewById(R.id.AudioFileName);
             fileSize=(TextView)view.findViewById(R.id.FileSize);
             fileMdate=(TextView)view.findViewById(R.id.FileMdate);

              chbx=(CheckBox) view.findViewById(R.id.chbx);
             rellayout=(RelativeLayout)view.findViewById(R.id.rellayout);
             FileIcon=(ImageView)view.findViewById(R.id.FileIcon);

             fileName.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
             fileSize.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
             fileMdate.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));

            fileName.setTextSize(Utility.getFontSizeValueHeading(mContext));

            fileSize.setTextSize(Utility.getFontSizeValueSubHead(mContext));
            fileMdate.setTextSize(Utility.getFontSizeValueSubHead(mContext));



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected Locker in callback
                    int pos=getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION)
                    listener.onfileSelectListener(LockerListfiltered.get(getAdapterPosition()));
                }
            });



             }
    }

     public MultiSelectAdapter_Locker(Context context, ArrayList<Model_Locker> LockerList, ArrayList<Model_Locker> selectedLockerList , fileSelectListener listener, int media_Type) {
        this.mContext=context;
        this.LockerList = LockerList;
        this.LockerListfiltered=LockerList;
        this.selected_LockerList = selectedLockerList;
        this.listener = listener;
        this.media_Type=media_Type;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_audio_file, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
                 Model_Locker model = LockerListfiltered.get(position);
                 Drawable iconDrawable=null;
                 holder.fileName.setText(model.getFileName());
                 holder.fileMdate.setText(model.getFileMDate());
                 holder.fileSize.setText(model.getFileSize());




            switch (media_Type) {
                case 1:  // img
                iconDrawable = mContext.getResources().getDrawable(R.drawable.locker_ic_img_plachldr);
                holder.FileIcon.setImageDrawable(iconDrawable);
                break;
                case 2:  //vdo
                    iconDrawable = mContext.getResources().getDrawable(R.drawable.locker_ic_videoplaceholder);
                    holder.FileIcon.setImageDrawable(iconDrawable);
                    break;
                case 3:  // audio
                    iconDrawable = mContext.getResources().getDrawable(R.drawable.locker_ic_audio_placeholder);
                    holder.FileIcon.setImageDrawable(iconDrawable);
                    break;
                case 4:  //docs
                    iconDrawable = mContext.getResources().getDrawable(R.drawable.locker_ic_document_placeholder);
                    holder.FileIcon.setImageDrawable(iconDrawable);
                    break;
            }



        if(selected_LockerList.contains(LockerList.get(position))) {
            holder.chbx.setVisibility(View.VISIBLE);  // for time being checkbox not shown   layout backgroud being changed
           // holder.rellayout.setBackgroundColor(mContext.getResources().getColor(R.color.gradation_04_light));
        }
        else {
            holder.chbx.setVisibility(View.INVISIBLE); // for time being checkbox not shown   layout backgroud being changed
           // holder.rellayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }


    }

    @Override
    public int getItemCount() {
        return LockerListfiltered.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    LockerListfiltered = LockerList;
                } else {
                    ArrayList<Model_Locker> filteredList = new ArrayList<>();
                    for (Model_Locker row : LockerList) {

                        //condition to search for
                        if (row.getFileName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    LockerListfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = LockerListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                LockerListfiltered = (ArrayList<Model_Locker>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface fileSelectListener {
        void onfileSelectListener(Model_Locker Locker_model);
    }



}

