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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mojodigi.filehunt.Model.Model_Recent;
//
import com.mojodigi.filehunt.Utils.Utility;

import java.util.ArrayList;
import com.mojodigi.filehunt.R;

public class MultiSelectAdapter_Recent extends RecyclerView.Adapter<MultiSelectAdapter_Recent.MyViewHolder>  implements Filterable {

    public ArrayList<Model_Recent> RecentList=new ArrayList<>();
    public ArrayList<Model_Recent> RecentListfiltered=new ArrayList<>();
    public ArrayList<Model_Recent> selected_RecentList=new ArrayList<>();
    private  RecentListener listener;
    Context mContext;
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

            rellayout=(RelativeLayout)view.findViewById(R.id.rellayout);

            fileName.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            fileSize.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            fileMdate.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected docs in callback
                    int pos=getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION)
                    listener.onRecentSelected(RecentListfiltered.get(getAdapterPosition()));
                }
            });

             }
    }

     public MultiSelectAdapter_Recent(Context context, ArrayList<Model_Recent> RecentList, ArrayList<Model_Recent> selectedRecentList, RecentListener listener) {
        this.mContext=context;
        this.RecentList = RecentList;
        this.RecentListfiltered=RecentList;
        this.selected_RecentList = selectedRecentList;
        this.listener=listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_audio_file, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Drawable iconDrawable = null;
        Model_Recent model = RecentListfiltered.get(position);


        holder.fileName.setText(model.getFileName());
        holder.fileMdate.setText(model.getFileMdate());
        holder.fileSize.setText(model.getFileSize());

        if (model.getFileType().equalsIgnoreCase("jpg") || model.getFileType().equalsIgnoreCase("jpeg") || model.getFileType().equalsIgnoreCase("png")) {

            Glide.with(mContext).load("file://" + model.getFilePath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder)
                    .into(holder.FileIcon);
            }
          else if(model.getFileType().equalsIgnoreCase("mp4"))
        {

            holder.FileIcon.setImageBitmap(Utility.creteVdoBitmapFromPath(model.getFilePath()));

        }
        else if(model.getFileType().equalsIgnoreCase("gif"))
        {
            Glide.with(mContext).load("file://" + model.getFilePath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).placeholder(R.drawable.ic_gif).error(R.drawable.ic_gif)
                    .into(holder.FileIcon);
        }
        else if(model.getFileType().equalsIgnoreCase("mp3")|| model.getFileType().equalsIgnoreCase("aac") || model.getFileType().equalsIgnoreCase("amr"))
        {
            iconDrawable = mContext.getResources().getDrawable(R.mipmap.ic_audio);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
       else if (model.getFileType().equalsIgnoreCase("pdf")) {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_pdf);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if (model.getFileType().equalsIgnoreCase("txt")) {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_txt);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if (model.getFileType().equalsIgnoreCase("docx") || model.getFileType().equalsIgnoreCase("doc"))
        {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_word);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if (model.getFileType().equalsIgnoreCase("rtf")) {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_rtf);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if (model.getFileType().equalsIgnoreCase("json")) {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_json);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if (model.getFileType().equalsIgnoreCase("xlsx") || model.getFileType().equalsIgnoreCase("xls")) {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_excel);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if(model.getFileType().equalsIgnoreCase("apk"))
        {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_apk);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if(model.getFileType().equalsIgnoreCase("db")) {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_db);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
       else
           {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_other);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }


        if (selected_RecentList.contains(RecentList.get(position))) {
            holder.chbx.setVisibility(View.VISIBLE);
           // holder.rellayout.setBackgroundColor(mContext.getResources().getColor(R.color.gradation_04_light));
        } else {
            holder.chbx.setVisibility(View.INVISIBLE);
           // holder.rellayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return RecentListfiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    RecentListfiltered = RecentList;
                } else {
                    ArrayList<Model_Recent> filteredList = new ArrayList<>();
                    for (Model_Recent row : RecentList) {

                        //condition to search for
                        if (row.getFileName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    RecentListfiltered= filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = RecentListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                RecentListfiltered = (ArrayList<Model_Recent>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface RecentListener {
        void onRecentSelected(Model_Recent downlaod_Model);
    }



}

