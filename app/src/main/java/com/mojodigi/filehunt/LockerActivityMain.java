package com.mojodigi.filehunt;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Model.category_Model;
import com.mojodigi.filehunt.Utils.EncryptDialogUtility;
import com.mojodigi.filehunt.Utils.Utility;

import java.util.ArrayList;
import java.util.List;

import static com.mojodigi.filehunt.Class.Constants.POSITION;

public class LockerActivityMain extends AppCompatActivity
{
    RecyclerView recyclerView;
    List<category_Model> catList = new ArrayList<category_Model>();
    Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=LockerActivityMain.this;
        Utility.setActivityTitle2(mContext, getResources().getString(R.string.hdnfiles));
      setContentView(R.layout.activity_locker_main);
        iniVars();
    }

    private void iniVars() {

        recyclerView = findViewById(R.id.recycler_view_locker);
        category_Model cat_Img=new category_Model(getResources().getString(R.string.cat_Images));
        category_Model cat_Video=new category_Model(getResources().getString(R.string.cat_Videos));
        category_Model cat_Audio=new category_Model(getResources().getString(R.string.cat_Audio));
        category_Model cat_Document=new category_Model(getResources().getString(R.string.cat_Documents));


       catList.add(cat_Img);
       catList.add(cat_Video);
       catList.add(cat_Audio);
       catList.add(cat_Document);


        categoryAdapter cat_adapter = new categoryAdapter(catList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL));
        // recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cat_adapter);




    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class categoryAdapter extends RecyclerView.Adapter<LockerActivityMain.categoryAdapter.categoryViewHolder> {

        List<category_Model> catList;
        @Override
        public LockerActivityMain.categoryAdapter.categoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_item_layout, parent, false);

            return new categoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(LockerActivityMain.categoryAdapter.categoryViewHolder holder, final int position) {
            holder.catName.setText(catList.get(position).getCatName());


            switch (position) {
                case 0:
                    holder.catIcon.setImageResource(R.drawable.locker_ic_image);
                    //holder.catIcon.setImageResource(R.drawable.ic_image);
                    break;
                case 1:
                    holder.catIcon.setImageResource(R.drawable.locker_ic_video);
                    break;
                case 2:
                    holder.catIcon.setImageResource(R.drawable.locker_ic_audio);
                    break;
                case 3:
                    holder.catIcon.setImageResource(R.drawable.locker_ic_docs);
                    break;

                    }

            holder.catName.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            //holder.itemCount.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));



            holder.container_Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    switch (position)
                    {
                        case 0:
                            redirectToActivity(List_Hidden_Files_Activity.class, Constants.MEDIA_TYPE_IMG);
                            break;
                        case 1:
                            redirectToActivity(List_Hidden_Files_Activity.class,Constants.MEDIA_TYPE_VDO);

                            break;
                        case 2:
                            redirectToActivity(List_Hidden_Files_Activity.class,Constants.MEDIA_TYPE_ADO);

                            break;
                        case 3:
                            redirectToActivity(List_Hidden_Files_Activity.class,Constants.MEDIA_TYPE_DOC);

                        break;



                    }




                }
            });


        }

        @Override
        public int getItemCount() {
            return catList.size();
        }

        public class categoryViewHolder extends RecyclerView.ViewHolder {
            public TextView catName;
            RelativeLayout container_Layout;
            ImageView catIcon;



            public categoryViewHolder(View view) {
                super(view);
                catName =  view.findViewById(R.id.catName);
                catIcon =  view.findViewById(R.id.cat_Icon);
                container_Layout=view.findViewById(R.id.container_Layout);
                catName.setTextSize(Utility.getFontSizeValueHeading(mContext));
            }
        }

        public categoryAdapter(List<category_Model> catList) {
            this.catList=catList;
        }



    }
    private void redirectToActivity(Class targetClass,int catType)
    {

        Intent i =  new Intent(LockerActivityMain.this,targetClass);
        i.putExtra("mediaKey", catType);
        startActivity(i);
    }
}
