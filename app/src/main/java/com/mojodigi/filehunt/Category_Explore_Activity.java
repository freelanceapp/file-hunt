package com.mojodigi.filehunt;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.gms.ads.AdView;
import com.mojodigi.filehunt.Adapter.Adapter_PhotosFolder;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Model.Model_images;
//

import com.mojodigi.filehunt.Utils.AsynctaskUtility;
import com.mojodigi.filehunt.Utils.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.mojodigi.filehunt.Class.Constants.POSITION;

public class Category_Explore_Activity extends AppCompatActivity implements AsynctaskUtility.AsyncResponse {

    int position;
    Context ctx;
    boolean boolean_folder;
    GridView gv_folder;
    ImageView blank_indicator;
    Adapter_PhotosFolder obj_adapter;
    int AUDIO=3;
    int VIDEO=2;
    int IMAGES=1;

    public static ArrayList<Model_images> al_images = new ArrayList<>();

    Uri uri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=Category_Explore_Activity.this;

        setContentView(R.layout.category_explore_activity);

        blank_indicator=(ImageView)findViewById(R.id.blank_indicatorm);
        gv_folder = (GridView)findViewById(R.id.gv_folder);

        if(getIntent().getExtras() !=null)

           position=getIntent().getIntExtra(POSITION,0) ;


        switch (position)
        {
            case 0:
                Utility.setActivityTitle(ctx,getResources().getString(R.string.cat_Images));
                new AsynctaskUtility<Model_images>(ctx,this,IMAGES).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case 1:
                Utility.setActivityTitle(ctx,getResources().getString(R.string.cat_Videos));
                new AsynctaskUtility<Model_images>(ctx,this,VIDEO).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
            case 2:
                Utility.setActivityTitle(ctx,getResources().getString(R.string.cat_Audio));
                new AsynctaskUtility<Model_images>(ctx,this,AUDIO).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case 3:

                break;

                }



                gv_folder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(position==0)
                {
                Intent intent = new Intent(getApplicationContext(), PhotosActivityRe.class);
                intent.putExtra("value",i);
                startActivity(intent);
                }
                else if(position==1)
                {
                    Intent intent = new Intent(getApplicationContext(), VideoActivityRe.class);
                    intent.putExtra("value",i);
                    startActivity(intent);
                }

                else if(position==2)
                {
                    Intent intent = new Intent(getApplicationContext(), AudioActivityRe.class);
                    intent.putExtra("value",i);
                    startActivity(intent);
                }

            }
        });






    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();


        if(Constants.redirectToStorage)
        {
            finish();  // if copy operation is selected then finish this activity  to open
             // the MainActivity with storage option;
            return;
        }

        if(Constants.DELETED_VDO_FILES>0 && position==1)
        {
            new AsynctaskUtility<Model_images>(ctx,this,VIDEO).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if(Constants.DELETED_AUDIO_FILES>0 && position==2 )
        {
            new AsynctaskUtility<Model_images>(ctx,this,AUDIO).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if(Constants.DELETED_IMG_FILES>0 && position==0)
        {

            new AsynctaskUtility<Model_images>(ctx,this,IMAGES).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }






    }

    // not being used;
      public ArrayList<Model_images>  Load_Media(int  MediaType)
    {
        al_images.clear();

        int int_position = 0;

        Cursor cursor;
        int column_index_data, column_index_folder_name,column_index_date_modified;

        String absolutePathOfImage = null;
        if(MediaType==IMAGES)
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        else if(MediaType==VIDEO)
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//            else if(MediaType==AUDIO)
//            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;



        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.MediaColumns.DATE_MODIFIED};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query( uri, projection, null, null, orderBy + " DESC");

          column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
          column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
          column_index_date_modified= cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));

            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_path.add(absolutePathOfImage);
                al_images.get(int_position).setAl_imagepath(al_path);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setDate_modified(LongToDate(cursor.getString(column_index_date_modified)));
                obj_model.setAl_imagepath(al_path);

                al_images.add(obj_model);


            }


        }


//        for (int i = 0; i < al_images.size(); i++) {
////            Log.e("FOLDER", al_images.get(i).getStr_folder());
////            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
////                Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
////            }
////        }

        obj_adapter = new Adapter_PhotosFolder(getApplicationContext(),al_images,position);
        gv_folder.setAdapter(obj_adapter);
        return al_images;
    }
    //not being used
    private  ArrayList<Model_images>   FetchVideos()
    {
        al_images.clear();
        int int_position = 0;
        Cursor cursor;
        int column_index_data, column_index_folder_name,column_index_date_modified,thumb,column_index_duration;
        String absolutePathOfImage = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.MediaColumns.DATE_MODIFIED,MediaStore.Video.Thumbnails.DATA,MediaStore.Video.VideoColumns.DURATION};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query( uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        column_index_date_modified= cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);
        thumb= cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
        column_index_duration=cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));
                 String thumbstr=cursor.getString(thumb);
                 long duration=cursor.getLong(column_index_duration);


            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                ArrayList<String> al_vdoThumb = new ArrayList<>();
                ArrayList<String> al_duration = new ArrayList<>();

                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_vdoThumb.addAll(al_images.get(int_position).getAl_vdoThumb());
                al_duration.addAll(al_images.get(int_position).getAlVdoDuration());

                al_path.add(absolutePathOfImage);
                al_vdoThumb.add(thumbstr);
                al_duration.add(Utility.convertDuration(duration));

                al_images.get(int_position).setAl_imagepath(al_path);
                al_images.get(int_position).setAl_vdoThumb(al_vdoThumb);
                al_images.get(int_position).setAlVdoDuration(al_duration);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                ArrayList<String> al_vdoThumb = new ArrayList<>();
                ArrayList<String> al_duration = new ArrayList<>();

                al_path.add(absolutePathOfImage);
                al_vdoThumb.add(thumbstr);
                al_duration.add(Utility.convertDuration(duration));

                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setDate_modified(LongToDate(cursor.getString(column_index_date_modified)));
                obj_model.setAl_imagepath(al_path);
                obj_model.setAl_vdoThumb(al_vdoThumb);
                obj_model.setAlVdoDuration(al_duration);

                al_images.add(obj_model);
                }



        }



        obj_adapter = new Adapter_PhotosFolder(getApplicationContext(),al_images,position);
        gv_folder.setAdapter(obj_adapter);
        return al_images;
    }

   //  not being used
    private ArrayList<Model_images>  FetchAudio()
    {
        al_images.clear();
        int int_position = 0;
        Cursor cursor;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";  // only music file will be fetched
        int column_index_data, column_index_duration,column_index_folder_name,column_index_date_modified;
        String absolutePathOfImage = null;
        String fileDuration=null;
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.DATE_MODIFIED,MediaStore.Audio.Media.DURATION};

        final String orderBy = MediaStore.Audio.Media.DATE_MODIFIED;
        cursor = getApplicationContext().getContentResolver().query( uri, projection, selection, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
        column_index_date_modified= cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);
        column_index_duration=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            fileDuration= Utility.convertDuration(cursor.getLong(column_index_duration));

            Log.e("Duration",fileDuration);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));

            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                ArrayList<String> al_fileduration = new ArrayList<>();

                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_fileduration.addAll(al_images.get(int_position).getAl_FileDuration());

                al_path.add(absolutePathOfImage);
                al_fileduration.add(fileDuration);

                al_images.get(int_position).setAl_imagepath(al_path);
                al_images.get(int_position).setAl_FileDuration(al_fileduration);


                } else {
                ArrayList<String> al_path = new ArrayList<>();
                ArrayList<String> al_fileduration = new ArrayList<>();


                al_path.add(absolutePathOfImage);
                al_fileduration.add(fileDuration);
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setDate_modified(LongToDate(cursor.getString(column_index_date_modified)));
                obj_model.setAl_imagepath(al_path);
                obj_model.setAl_FileDuration(al_fileduration);
                al_images.add(obj_model);


            }


        }


//        for (int i = 0; i < al_images.size(); i++) {
//            Log.e("FOLDER", al_images.get(i).getStr_folder());
//            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
//                Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
//            }
//        }
//

        obj_adapter = new Adapter_PhotosFolder(getApplicationContext(),al_images,position);
        gv_folder.setAdapter(obj_adapter);
        return al_images;


    }



    private String LongToDate(String longV)
    {
        long input=Long.parseLong(longV);
        Date date = new Date(input*1000); // *1000 gives accurate date otherwise returns 1970
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setCalendar(cal);
        cal.setTime(date);
        return sdf.format(date);

    }

  //function from asyncTask class
    @Override
    public void processFinish(ArrayList output) {

          al_images=output;
          if(al_images.size()!=0) {
              blank_indicator.setVisibility(View.GONE);
              obj_adapter = new Adapter_PhotosFolder(getApplicationContext(), al_images, position);
              gv_folder.setAdapter(obj_adapter);
          }
          else
          {
              obj_adapter = new Adapter_PhotosFolder(getApplicationContext(), al_images, position);
              gv_folder.setAdapter(obj_adapter);
              blank_indicator.setVisibility(View.VISIBLE);
          }

    }
}

