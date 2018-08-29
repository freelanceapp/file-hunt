package com.example.filehunt.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.example.filehunt.Class.Constants;
import com.example.filehunt.Model.Model_Storage;
import com.example.filehunt.R;
import com.example.filehunt.Utils.Utility;

import java.util.ArrayList;


public class Adapter_Storage extends RecyclerView.Adapter<Adapter_Storage.MyViewHolder>  implements Filterable {

    public ArrayList<Model_Storage> modelStorageList =new ArrayList<>();
    public ArrayList<Model_Storage> modelStorageListfiltered =new ArrayList<>();
    public ArrayList<Model_Storage> selected_ModelStorageList =new ArrayList<>();
    private ItemListener listener;
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
             fileDuration=(TextView)view.findViewById(R.id.FileDuration);
              chbx=(CheckBox) view.findViewById(R.id.chbx);
             rellayout=(RelativeLayout)view.findViewById(R.id.rellayout);
             FileIcon=(ImageView)view.findViewById(R.id.FileIcon);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected apk in callback
                    listener.onItemSelected(modelStorageList.get(getAdapterPosition()));
                }
            });



             }
    }

     public Adapter_Storage(Context context, ArrayList<Model_Storage> modelStorageList, ArrayList<Model_Storage> selectedModelStorageList, ItemListener listener) {
        this.mContext=context;
        this.modelStorageList = modelStorageList;
        this.modelStorageListfiltered = modelStorageList;
        this.selected_ModelStorageList = selectedModelStorageList;
         this.listener = listener;

    }
    public Adapter_Storage(Context context, ArrayList<Model_Storage> modelStorageList, ItemListener listener) {
        this.mContext=context;
        this.modelStorageList = modelStorageList;
        this.listener=listener;



    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_storage_file, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
                 Model_Storage model = modelStorageList.get(position);
                 int icon=0;


        holder.fileName.setText(model.getFile());
        holder.fileMdate.setText(model.getFileModifiedDate());
        icon=model.getIcon();
        holder.FileIcon.setImageResource(icon);
        boolean isdirecotory=model.getisDirecoty();

        holder.fileName.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        holder.fileMdate.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        holder.fileSize.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));

        if(isdirecotory)
        {
            holder.fileSize.setText(""+model.getItemcount());
        }
        else {

            holder.fileSize.setText(model.getFilesize());

            String FileType = Utility.getMimiTypefromPath(model.getFilePath().toLowerCase());
            Log.d("FileType Mime" , ""+FileType);
            if(FileType !=null) {
                if (FileType.contains("image"))
                {
                    Glide.with(mContext).load("file://" + model.getFilePath())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false).placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder)
                            .into(holder.FileIcon);
                } else if (FileType.contains("pdf"))
                {
                    holder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pdf));

                }
                else if (FileType.contains("audio"))
                {
                    holder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_audio));
                }
                else if(FileType.contains("video"))
                {

                    //video/mp4  will  be type of File,using video to  play  ant vdo file
                     // animation file  will also  be covered
                   holder.FileIcon.setImageBitmap(Utility.creteVdoBitmapFromPath(model.getFilePath()));

                }
                else if (FileType.contains("msword") || FileType.contains(Constants.WordMimeType))
                {
                    holder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_word));

                }
                else if(FileType.contains("text/plain"))
                {
                    holder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_txt));
                }


            }
        }





        if(selected_ModelStorageList.contains(modelStorageList.get(position))) {
            holder.chbx.setVisibility(View.VISIBLE);  // for time being checkbox not shown   layout backgroud being changed
            holder.rellayout.setBackgroundColor(mContext.getResources().getColor(R.color.gradation_04_light));
        }
        else {
            holder.chbx.setVisibility(View.INVISIBLE); // for time being checkbox not shown   layout backgroud being changed
            holder.rellayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }


    }

    @Override
    public int getItemCount() {
        return modelStorageList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    modelStorageListfiltered = modelStorageList;
                } else {
                    ArrayList<Model_Storage> filteredList = new ArrayList<>();
                    for (Model_Storage row : modelStorageList) {

                        //condition to search for
                        if (row.file.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    modelStorageListfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = modelStorageListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                modelStorageListfiltered = (ArrayList<Model_Storage>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ItemListener {
        void onItemSelected(Model_Storage modelStorage_model);
    }



}

