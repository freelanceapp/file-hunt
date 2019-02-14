package com.mojodigi.filehunt.Adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mojodigi.filehunt.Model.Model_images;
import com.mojodigi.filehunt.R;
import com.mojodigi.filehunt.Utils.Utility;
import java.util.ArrayList;


public class Adapter_PhotosFolder extends RecyclerView.Adapter<Adapter_PhotosFolder.MyViewHolder>  implements Filterable {

    Context context;

    public ArrayList<Model_images> al_menu=new ArrayList<>();
    public ArrayList<Model_images> folderListfiltered=new ArrayList<>();


    private  FolderListener folderListener;

    int media_position;

    public class MyViewHolder extends RecyclerView.ViewHolder {



       TextView tv_foldern, tv_foldersize, tv_date_modified;
       ImageView iv_image;



        public MyViewHolder(View view) {
            super(view);

             tv_foldern = (TextView) view.findViewById(R.id.tv_folder);
             tv_foldersize = (TextView) view.findViewById(R.id.tv_folder2);
             tv_date_modified=(TextView)view.findViewById(R.id.tv_folder3);
             iv_image = (ImageView) view.findViewById(R.id.iv_image);


            tv_date_modified.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(context));
            tv_foldern.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(context));
            tv_foldersize.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(context));


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected docs in callback
                    int pos=getAdapterPosition();
                    Model_images model=folderListfiltered.get(pos);
                    if(pos!= RecyclerView.NO_POSITION)
                        folderListener.onFolderSelected(model,pos);
                }
            });

        }
    }

    public Adapter_PhotosFolder(Context context, ArrayList<Model_images> al_menu, int media_position ) {
        this.al_menu = al_menu;
        this.folderListfiltered = al_menu;
        this.context = context;
        this.media_position=media_position;
    }


    public Adapter_PhotosFolder(Context context, ArrayList<Model_images> al_menu, int media_position , FolderListener folderListener) {

        this.al_menu = al_menu;
        this.folderListfiltered = al_menu;
        this.context = context;
        this.media_position = media_position;
        this.folderListener = folderListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_photosfolder, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {

        Drawable iconDrawable = null;
        Model_images model = folderListfiltered.get(position);

        viewHolder.tv_date_modified.setText(folderListfiltered.get(position).getDate_modified());
        viewHolder.tv_foldern.setText(folderListfiltered.get(position).getStr_folder());
        viewHolder.tv_foldersize.setText(folderListfiltered.get(position).getAl_imagepath().size()+"");


        switch (media_position)
        {
            case  2:
                viewHolder.iv_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_music));
                break;
                default:
                    String string = folderListfiltered.get(position).getAl_imagepath().get(0);
                    System.out.print("pathFile"+string);
                    Glide.with(context).load("file://" + folderListfiltered.get(position).getAl_imagepath().get(0))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(false)
                    .into(viewHolder.iv_image);
                    break;
        }



    }

    @Override
    public int getItemCount() {
        return folderListfiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    folderListfiltered = al_menu;
                } else {

                    ArrayList<Model_images> filteredList = new ArrayList<>();
                    for (Model_images row : al_menu) {

                        //condition to search for
                        if (row.getStr_folder().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    folderListfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();

                filterResults.values = folderListfiltered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                folderListfiltered = (ArrayList<Model_images>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }




    public interface FolderListener {
        void onFolderSelected(Model_images model,int position);
    }



}

