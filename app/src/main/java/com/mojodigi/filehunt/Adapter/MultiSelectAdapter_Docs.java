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

import com.mojodigi.filehunt.Model.Model_Docs;
import com.mojodigi.filehunt.R;
import com.mojodigi.filehunt.Utils.Utility;

import java.util.ArrayList;

public class MultiSelectAdapter_Docs extends RecyclerView.Adapter<MultiSelectAdapter_Docs.MyViewHolder>  implements Filterable {

    public ArrayList<Model_Docs> DocsList=new ArrayList<>();
    public ArrayList<Model_Docs> DocsListfiltered=new ArrayList<>();
    public ArrayList<Model_Docs> selected_DocsList=new ArrayList<>();
    private DocsListener listener;
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
            fileDuration.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));


            fileName.setTextSize(Utility.getFontSizeValueHeading(mContext));

            fileSize.setTextSize(Utility.getFontSizeValueSubHead(mContext));
            fileMdate.setTextSize(Utility.getFontSizeValueSubHead(mContext));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected docs in callback
                    int pos=getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION)
                    listener.onDocsSelected(DocsListfiltered.get(getAdapterPosition()));
                }
            });



             }
    }

     public MultiSelectAdapter_Docs(Context context, ArrayList<Model_Docs> DocsList, ArrayList<Model_Docs> selectedDocsList , DocsListener listener) {
        this.mContext=context;
        this.DocsList = DocsList;
        this.DocsListfiltered=DocsList;
        this.selected_DocsList = selectedDocsList;
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
        Model_Docs model = DocsListfiltered.get(position);
       Drawable iconDrawable=null;
                 holder.fileName.setText(model.getFileName());
                 holder.fileMdate.setText(model.getFileMDate());
                 holder.fileSize.setText(model.getFileSize());
                // holder.fileDuration.setText(model.getFileType());

                 if(model.getFileType().equalsIgnoreCase("pdf"))
                     iconDrawable=mContext.getResources().getDrawable(R.drawable.ic_pdf);
                 else if(model.getFileType().equalsIgnoreCase("txt"))
                     iconDrawable=mContext.getResources().getDrawable(R.drawable.ic_txt);
                 else if(model.getFileType().equalsIgnoreCase("docx")|| model.getFileType().equalsIgnoreCase("doc"))
                     iconDrawable=mContext.getResources().getDrawable(R.drawable.ic_word);
                 else if(model.getFileType().equalsIgnoreCase("rtf"))
                     iconDrawable=mContext.getResources().getDrawable(R.drawable.ic_rtf);
                 else if(model.getFileType().equalsIgnoreCase("json"))
                     iconDrawable=mContext.getResources().getDrawable(R.drawable.ic_json);
                 else if(model.getFileType().equalsIgnoreCase("xlsx")||model.getFileType().equalsIgnoreCase("xls"))
                     iconDrawable=mContext.getResources().getDrawable(R.drawable.ic_excel);
                 else if(model.getFileType().equalsIgnoreCase("ppt") || model.getFileType().equalsIgnoreCase("pptx"))
                     iconDrawable=mContext.getResources().getDrawable(R.drawable.ic_powerpnt );
                 else
                     iconDrawable=mContext.getResources().getDrawable(R.drawable.ic_other);

                holder.FileIcon.setImageDrawable(iconDrawable);



        if(selected_DocsList.contains(DocsList.get(position))) {
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
        return DocsListfiltered.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    DocsListfiltered = DocsList;
                } else {
                    ArrayList<Model_Docs> filteredList = new ArrayList<>();
                    for (Model_Docs row : DocsList) {
                        // search condition here
                        if (row.getFileName().toLowerCase().contains(charString.toLowerCase()) || row.getFileType().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    DocsListfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = DocsListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                DocsListfiltered = (ArrayList<Model_Docs>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface DocsListener {
        void onDocsSelected(Model_Docs contact);
    }



}

