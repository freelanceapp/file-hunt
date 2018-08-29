package com.example.filehunt.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
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

import com.example.filehunt.Model.Grid_Model;
import com.example.filehunt.Model.Model_Audio;
import com.example.filehunt.Model.Model_images;
import com.example.filehunt.R;
import com.example.filehunt.Utils.Utility;

import java.util.ArrayList;


public class MultiSelectAdapter_Audio extends RecyclerView.Adapter<MultiSelectAdapter_Audio.MyViewHolder>   implements Filterable {

    public ArrayList<Model_Audio> AudioList=new ArrayList<>();
    public ArrayList<Model_Audio> AudioListfiltered=new ArrayList<>();
    public ArrayList<Model_Audio> selected_AudioList=new ArrayList<>();
    private AudioListener listener;
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
            FileIcon=(ImageView)view.findViewById(R.id.FileIcon) ;

            fileName.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            fileSize.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            fileMdate.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            fileDuration.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected docs in callback
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onAudioSelected(AudioListfiltered.get(getAdapterPosition()));
                    }
                }
            });


        }
    }

     public MultiSelectAdapter_Audio(Context context, ArrayList<Model_Audio> AudioList, ArrayList<Model_Audio> selectedAudioList,AudioListener listener) {
        this.mContext=context;
        this.AudioList = AudioList;
        this.AudioListfiltered=AudioList;
        this.selected_AudioList = selectedAudioList;
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
        Model_Audio model = AudioListfiltered.get(position);

                 holder.fileName.setText(model.getAudiFileName());
                 holder.fileMdate.setText(model.getAudiofileMDate());
                 holder.fileSize.setText(model.getAudioFileSize());
                 holder.fileDuration.setText(model.getAudioFileDuration());
                 holder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_audio));



        if(selected_AudioList.contains(AudioList.get(position))) {
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
        return AudioListfiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    AudioListfiltered = AudioList;
                } else {
                    ArrayList<Model_Audio> filteredList = new ArrayList<>();
                    for (Model_Audio row : AudioList) {

                        //condition to search for
                        if (row.getAudiFileName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    AudioListfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = AudioListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                AudioListfiltered = (ArrayList<Model_Audio>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public interface AudioListener {
        void onAudioSelected(Model_Audio audioModel);
    }

}

