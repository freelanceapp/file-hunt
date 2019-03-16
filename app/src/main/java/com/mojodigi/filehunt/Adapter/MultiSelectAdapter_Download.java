package com.mojodigi.filehunt.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.mojodigi.filehunt.Model.Model_Download;
import com.mojodigi.filehunt.R;
import com.mojodigi.filehunt.Utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Handler;

public class MultiSelectAdapter_Download extends RecyclerView.Adapter<MultiSelectAdapter_Download.MyViewHolder>  implements Filterable {

    public ArrayList<Model_Download> DownloadList=new ArrayList<>();
    public ArrayList<Model_Download> DownloadListfiltered=new ArrayList<>();
    public ArrayList<Model_Download> selected_DownloadList=new ArrayList<>();
    private  DownloadListener listener;
    Context mContext;
    Drawable iconDrawable = null;
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

            rellayout=(RelativeLayout)view.findViewById(R.id.rellayout);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected docs in callback
                    int pos=getAdapterPosition();
                            if(pos!= RecyclerView.NO_POSITION)
                    listener.onDownloadSelected(DownloadListfiltered.get(getAdapterPosition()));
                }
            });

             }
    }

     public MultiSelectAdapter_Download(Context context, ArrayList<Model_Download> DownloadList, ArrayList<Model_Download> selectedDownloadList, DownloadListener listener) {
        this.mContext=context;
        this.DownloadList = DownloadList;
        this.DownloadListfiltered=DownloadList;
        this.selected_DownloadList = selectedDownloadList;
        this.listener=listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_audio_file, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


       Model_Download model = DownloadListfiltered.get(position);


        holder.fileName.setText(model.getFileName());
        holder.fileMdate.setText(model.getFileDateModified());
        holder.fileSize.setText(model.getFileSize());

        if (model.getFiletype().equalsIgnoreCase("jpg") || model.getFiletype().equalsIgnoreCase("jpeg") || model.getFiletype().equalsIgnoreCase("png")) {

            Glide.with(mContext).load("file://" + model.getFilePath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder)
                    .into(holder.FileIcon);
        }
        else if(model.getFiletype().equalsIgnoreCase("mp4") || model.getFiletype().equalsIgnoreCase("mov") ||model.getFiletype().equalsIgnoreCase("webm")   )
        {
//            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(model.getFilePath(),
//                    MediaStore.Images.Thumbnails.MINI_KIND);
//            holder.FileIcon.setImageBitmap(thumb);

            Glide.with(mContext)
                    .load(Uri.fromFile(new File(model.getFilePath())))
                    .into(holder.FileIcon);


        }
        else if(model.getFiletype().equalsIgnoreCase("gif"))
        {
            Glide.with(mContext).load("file://" + model.getFilePath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false).placeholder(R.drawable.ic_gif).error(R.drawable.ic_gif)
                    .into(holder.FileIcon);
        }
        else if(model.getFiletype().equalsIgnoreCase("mp3")|| model.getFiletype().equalsIgnoreCase("aac") || model.getFiletype().equalsIgnoreCase("amr"))
        {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.cat_ic_music);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if (model.getFiletype().equalsIgnoreCase("pdf")) {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_pdf);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if (model.getFiletype().equalsIgnoreCase("txt")) {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_txt);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if (model.getFiletype().equalsIgnoreCase("docx") || model.getFiletype().equalsIgnoreCase("doc"))
        {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_word);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if (model.getFiletype().equalsIgnoreCase("rtf")) {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_rtf);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if (model.getFiletype().equalsIgnoreCase("json")) {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_json);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if (model.getFiletype().equalsIgnoreCase("xlsx") || model.getFiletype().equalsIgnoreCase("xls")) {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_excel);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if(model.getFiletype().equalsIgnoreCase("db")) {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_db);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if(model.getFiletype().equalsIgnoreCase("apk"))
        {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.cat_ic_apk);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else  if(model.getFiletype().equalsIgnoreCase("webm") || model.getFiletype().equalsIgnoreCase("mp4"))
        {
            Glide.with(mContext)
                    .load(Uri.fromFile(new File(model.getFilePath())))
                    .into(holder.FileIcon);
        }
        else if(model.getFiletype().equalsIgnoreCase("zip"))
        {
            iconDrawable = mContext.getResources().getDrawable(R.drawable.cat_ic_zip);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else if(model.getFiletype().equalsIgnoreCase("pptx") || model.getFiletype().equalsIgnoreCase("pptx"))
        {
            iconDrawable=mContext.getResources().getDrawable(R.drawable.ic_powerpnt );
            holder.FileIcon.setImageDrawable(iconDrawable);
        }
        else
        {
            iconDrawable = mContext.getResources().getDrawable(R.mipmap.file_icon);
            holder.FileIcon.setImageDrawable(iconDrawable);
        }





        if (selected_DownloadList.contains(DownloadList.get(position))) {
            holder.chbx.setVisibility(View.VISIBLE);
          //  holder.rellayout.setBackgroundColor(mContext.getResources().getColor(R.color.gradation_04_light));
        } else {
            holder.chbx.setVisibility(View.INVISIBLE);
            //holder.rellayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }




    }


    @Override
    public int getItemCount() {
        return DownloadListfiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    DownloadListfiltered = DownloadList;
                } else {
                    ArrayList<Model_Download> filteredList = new ArrayList<>();
                    for (Model_Download row : DownloadList) {

                        //condition to search for
                        if (row.getFileName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    DownloadListfiltered= filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = DownloadListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                DownloadListfiltered = (ArrayList<Model_Download>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface DownloadListener {
        void onDownloadSelected(Model_Download downlaod_Model);
    }



}

