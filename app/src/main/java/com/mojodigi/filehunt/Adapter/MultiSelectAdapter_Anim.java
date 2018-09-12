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
import com.mojodigi.filehunt.Model.Model_Anim;

import com.mojodigi.filehunt.Utils.Utility;

import java.util.ArrayList;
import com.mojodigi.filehunt.R;

public class MultiSelectAdapter_Anim extends RecyclerView.Adapter<MultiSelectAdapter_Anim.MyViewHolder>  implements Filterable {

    public ArrayList<Model_Anim> AnimList=new ArrayList<>();
    public ArrayList<Model_Anim> AnimListfiltered=new ArrayList<>();
    public ArrayList<Model_Anim> selected_AnimList=new ArrayList<>();
    private AnimListener listener;
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

              fileName.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
              fileSize.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
              fileMdate.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected docs in callback
                    int pos=getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION)
                    listener.onAnimSelected(AnimListfiltered.get(getAdapterPosition()));
                }
            });



             }
    }

     public MultiSelectAdapter_Anim(Context context, ArrayList<Model_Anim> AnimList, ArrayList<Model_Anim> selectedAnimList , AnimListener listener) {
        this.mContext=context;
        this.AnimList = AnimList;
        this.AnimListfiltered=AnimList;
        this.selected_AnimList = selectedAnimList;
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
        Model_Anim model = AnimListfiltered.get(position);
       Drawable iconDrawable=null;
                 holder.fileName.setText(model.getFileName());
                 holder.fileMdate.setText(model.getFileMDate());
                 holder.fileSize.setText(model.getFileSize());

                 if(model.getFilePath().endsWith("gif"))
                 {

                     Glide.with(mContext).load("file://" + model.getFilePath())
                             .diskCacheStrategy(DiskCacheStrategy.ALL)
                             .skipMemoryCache(false).placeholder(R.drawable.ic_gif).error(R.drawable.ic_gif)
                             .into(holder.FileIcon);
                     }
                     else {

                     iconDrawable = mContext.getResources().getDrawable(R.drawable.ic_gif);
                     holder.FileIcon.setImageDrawable(iconDrawable);
                 }



        if(selected_AnimList.contains(AnimList.get(position))) {
            holder.chbx.setVisibility(View.VISIBLE);  // for time being checkbox not shown   layout backgroud being changed
           // holder.rellayout.setBackgroundColor(mContext.getResources().getColor(R.color.gradation_04_light));
        }
        else {
            holder.chbx.setVisibility(View.INVISIBLE); // for time being checkbox not shown   layout backgroud being changed
          //  holder.rellayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }


    }

    @Override
    public int getItemCount() {
        return AnimListfiltered.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    AnimListfiltered = AnimList;
                } else {
                    ArrayList<Model_Anim> filteredList = new ArrayList<>();
                    for (Model_Anim row : AnimList) {

                        //condition to search for
                        if (row.getFileName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    AnimListfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = AnimListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                AnimListfiltered = (ArrayList<Model_Anim>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface AnimListener {
        void onAnimSelected(Model_Anim contact);
    }



}

