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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.filehunt.Model.Grid_Model;
import com.example.filehunt.Model.Model_Audio;
import com.example.filehunt.Model.Model_images;
import com.example.filehunt.R;

import java.util.ArrayList;


public class MultiSelectAdapter_Audio extends RecyclerView.Adapter<MultiSelectAdapter_Audio.MyViewHolder> {

    public ArrayList<Model_Audio> AudioList=new ArrayList<>();
    public ArrayList<Model_Audio> selected_AudioList=new ArrayList<>();
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

             }
    }

     public MultiSelectAdapter_Audio(Context context, ArrayList<Model_Audio> AudioList, ArrayList<Model_Audio> selectedAudioList) {
        this.mContext=context;
        this.AudioList = AudioList;
        this.selected_AudioList = selectedAudioList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_audio_file, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Model_Audio model = AudioList.get(position);

                 holder.fileName.setText(model.getAudiFileName());
                 holder.fileMdate.setText(model.getAudiofileMDate());
                 holder.fileSize.setText(model.getAudioFileSize());
                 holder.fileDuration.setText(model.getAudioFileDuration());



        if(selected_AudioList.contains(AudioList.get(position))) {
            holder.chbx.setVisibility(View.GONE);  // for time being checkbox not shown   layout backgroud being changed
            holder.rellayout.setBackgroundColor(mContext.getResources().getColor(R.color.gradation_04_light));
        }
        else {
            holder.chbx.setVisibility(View.GONE); // for time being checkbox not shown   layout backgroud being changed
            holder.rellayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }


    }

    @Override
    public int getItemCount() {
        return AudioList.size();
    }
}

