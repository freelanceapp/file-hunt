package com.example.filehunt.Adapter;

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

import com.example.filehunt.Model.Model_Apk;
import com.example.filehunt.R;

import java.util.ArrayList;


public class MultiSelectAdapter_Apk extends RecyclerView.Adapter<MultiSelectAdapter_Apk.MyViewHolder>  implements Filterable {

    public ArrayList<Model_Apk> ApkList=new ArrayList<>();
    public ArrayList<Model_Apk> ApkListfiltered=new ArrayList<>();
    public ArrayList<Model_Apk> selected_ApkList=new ArrayList<>();
    private ApkListener listener;
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
                    listener.onApkSelected(ApkListfiltered.get(getAdapterPosition()));
                }
            });



             }
    }

     public MultiSelectAdapter_Apk(Context context, ArrayList<Model_Apk> ApkList, ArrayList<Model_Apk> selectedApkList , ApkListener listener) {
        this.mContext=context;
        this.ApkList = ApkList;
        this.ApkListfiltered=ApkList;
        this.selected_ApkList = selectedApkList;
         this.listener = listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_audio_file, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
                 Model_Apk model = ApkListfiltered.get(position);
                 Drawable iconDrawable=null;
                 holder.fileName.setText(model.getFileName());
                 holder.fileMdate.setText(model.getFileMDate());
                 holder.fileSize.setText(model.getFileSize());


                 iconDrawable=mContext.getResources().getDrawable(R.drawable.ic_apk);
                holder.FileIcon.setImageDrawable(iconDrawable);



        if(selected_ApkList.contains(ApkList.get(position))) {
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
        return ApkListfiltered.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    ApkListfiltered = ApkList;
                } else {
                    ArrayList<Model_Apk> filteredList = new ArrayList<>();
                    for (Model_Apk row : ApkList) {

                        //condition to search for
                        if (row.getFileName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    ApkListfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = ApkListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ApkListfiltered = (ArrayList<Model_Apk>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ApkListener {
        void onApkSelected(Model_Apk apk_model);
    }



}

