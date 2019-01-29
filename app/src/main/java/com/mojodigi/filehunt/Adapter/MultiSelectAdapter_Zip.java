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

import com.mojodigi.filehunt.Model.Model_Zip;
import com.mojodigi.filehunt.R;
import com.mojodigi.filehunt.Utils.Utility;

import java.util.ArrayList;

public class MultiSelectAdapter_Zip extends RecyclerView.Adapter<MultiSelectAdapter_Zip.MyViewHolder>  implements Filterable {

    public ArrayList<Model_Zip> ZipList=new ArrayList<>();
    public ArrayList<Model_Zip> ZipListfiltered=new ArrayList<>();
    public ArrayList<Model_Zip> selected_ZipList=new ArrayList<>();
    private ZipListener listener;
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

             fileName.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
             fileSize.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
             fileMdate.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));





            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected Zip in callback
                    int pos=getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION)
                    listener.onZipSelected(ZipListfiltered.get(getAdapterPosition()));
                }
            });



             }
    }

     public MultiSelectAdapter_Zip(Context context, ArrayList<Model_Zip> ZipList, ArrayList<Model_Zip> selectedZipList , ZipListener listener) {
        this.mContext=context;
        this.ZipList = ZipList;
        this.ZipListfiltered=ZipList;
        this.selected_ZipList = selectedZipList;
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
                 Model_Zip model = ZipListfiltered.get(position);
                 Drawable iconDrawable=null;
                 holder.fileName.setText(model.getFileName());
                 holder.fileMdate.setText(model.getFileMDate());
                 holder.fileSize.setText(model.getFileSize());

                // String extn=Utility.getFileExtensionfromPath(model.getFilePath());
                 //if(extn.equalsIgnoreCase("zip"))   // this check is for zip  and rar file icon;
                     iconDrawable=mContext.getResources().getDrawable(R.drawable.cat_ic_zip);

                holder.FileIcon.setImageDrawable(iconDrawable);



        if(selected_ZipList.contains(ZipList.get(position))) {
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
        return ZipListfiltered.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    ZipListfiltered = ZipList;
                } else {
                    ArrayList<Model_Zip> filteredList = new ArrayList<>();
                    for (Model_Zip row : ZipList) {

                        //condition to search for
                        if (row.getFileName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    ZipListfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = ZipListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ZipListfiltered = (ArrayList<Model_Zip>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ZipListener {
        void onZipSelected(Model_Zip Zip_model);
    }



}

