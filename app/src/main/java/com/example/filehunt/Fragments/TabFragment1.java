package com.example.filehunt.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filehunt.Category_Explore_Activity;
import com.example.filehunt.MainActivity;
import com.example.filehunt.Model.category_Model;
import com.example.filehunt.R;
import com.example.filehunt.Utils.EqualSpacingItemDecoration;
import com.example.filehunt.Utils.GridSpacingItemDecoration;
import com.example.filehunt.Utils.ItemOffsetDecoration;
import com.example.filehunt.Utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.example.filehunt.Class.Constants.POSITION;

public class TabFragment1 extends Fragment {


    private Context ctx;
    private RecyclerView category_recycler_view;
    ProgressBar progressBar;
    TextView avlbMemory,    totalMemmory;
    private List<category_Model>cat_List=new ArrayList<category_Model>();
    private categoryAdapter adapter;
    private int j;

    List<String>pngList=new ArrayList<>();
    List<String>jpgList=new ArrayList<>();
    List<String>pdfList=new ArrayList<>();
    List<String>txtList=new ArrayList<>();
    List<String>mp4List=new ArrayList<>();
    List<String>mp3List=new ArrayList<>();
    List<String>apkList=new ArrayList<>();
    List<String>wordList=new ArrayList<>();
    List<String>excelList=new ArrayList<>();
    List<String>pptlList=new ArrayList<>();
    List<String>flashlList=new ArrayList<>();
    List<String>ziplList=new ArrayList<>();
    List<String>giflList=new ArrayList<>();

    List<String>otherList=new ArrayList<>();

    List<String>downLoadList=new ArrayList<>();
    ArrayList<String> recentFiles = new ArrayList<>();



    category_Model cat_Img;
    category_Model cat_Apk;
    category_Model cat_Animation;
    category_Model cat_Audio;
    category_Model cat_Video;
    category_Model cat_Download;
    category_Model cat_Document;
    category_Model cat_Recent;

    private static final int REQUEST_PERMISSIONS = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_1, container, false);

        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
           ctx=getActivity();
        cat_List.clear();
        category_recycler_view= (RecyclerView) view.findViewById(R.id.category_recycler_view);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        avlbMemory=(TextView)view.findViewById(R.id.avlbMemory);
        totalMemmory=(TextView)view.findViewById(R.id.totalMemmory);



        if ((ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                // for  on fragmnet
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

                        //  for acitivty
//                ActivityCompat.requestPermissions(MainActivity.this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
//                        REQUEST_PERMISSIONS);
            }
        }else {
            Log.e("Else","Else");
            getCategories();
        }



//       ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.dp10);
//        category_recycler_view.addItemDecoration(itemDecoration);


//        int spanCount = 2; // 2 columns
//        int spacing = 25; // 50px
//        boolean includeEdge = true;
//        category_recycler_view.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));



    }
    private void getCategories()
    {


         cat_Img=new category_Model("Images",pngList.size()+jpgList.size()+flashlList.size()+" items",R.mipmap.ic_images);
         cat_Apk=new category_Model("Apk",apkList.size()+" items",R.mipmap.ic_apk);
         cat_Animation=new category_Model("Animation",giflList.size()+" items",R.mipmap.ic_animation);
         cat_Audio=new category_Model("Audio",mp3List.size()+" items",R.mipmap.ic_audio);
         cat_Video=new category_Model("Video",mp4List.size()+" items",R.mipmap.ic_video);
         cat_Download=new category_Model("Download",downLoadList.size()+" items",R.mipmap.ic_download);
         cat_Document=new category_Model("Document",pptlList.size()+wordList.size()+pdfList.size()+txtList.size()+" items",R.mipmap.ic_document);
         cat_Recent=new category_Model("Recent",recentFiles.size()+" items",R.mipmap.ic_recent);

        cat_List.add(cat_Img);
        cat_List.add(cat_Video);
        cat_List.add(cat_Audio);
        cat_List.add(cat_Document);
        cat_List.add(cat_Download);
        cat_List.add(cat_Animation);
        cat_List.add(cat_Recent);
        cat_List.add(cat_Apk);

        adapter= new categoryAdapter(cat_List);
        category_recycler_view.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        category_recycler_view.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.GRID));
        category_recycler_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        fillProgressBar();


        new LoadApkTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadrecentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        new LoadDownloadTask(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        new LoadAnimationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadVideosTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadIamgesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadAudioTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadDocumentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);





    }

    public class categoryAdapter extends  RecyclerView.Adapter<categoryAdapter.categoryViewHolder>
    {
        private List<category_Model> catList;

        @Override
        public categoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_item_layout, parent, false);

            return new categoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(categoryViewHolder holder, final int position)
        {
            holder.catName.setText(catList.get(position).getCatName());
            holder.catIcon.setImageResource(catList.get(position).getCat_icon());
            holder.itemCount.setText(catList.get(position).getIteCount());

            holder.container_Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent  i =  new Intent(getActivity(), Category_Explore_Activity.class);
                    i.putExtra(POSITION,position);
                    startActivity(i);
                }
            });



        }

        @Override
        public int getItemCount() {
            return catList.size();
        }

        public class categoryViewHolder extends RecyclerView.ViewHolder {
            public TextView catName,itemCount;
            ImageView catIcon;
            private RelativeLayout container_Layout;


            public categoryViewHolder(View view) {
                super(view);
                catName = (TextView) view.findViewById(R.id.catName);
                itemCount = (TextView) view.findViewById(R.id.itemCount);
                catIcon=(ImageView)view.findViewById(R.id.cat_Icon);
                container_Layout=(RelativeLayout)view.findViewById(R.id.container_Layout);
            }
        }

        public categoryAdapter(List<category_Model> catList) {
            this.catList = catList;
        }

    }
    private List<String>getDownLoad(File dir)
    {
        List<String>downLoadListLocal=new ArrayList<>();
        File[] listFile;
        listFile = dir.listFiles();
        System.out.print(""+listFile.toString());

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getDownLoad(listFile[i]);
                } else
                    {
                        downLoadListLocal.add(listFile[i].toString());
                     }
            }
        }
        return downLoadListLocal;

    }

    public void walkdir(File dir) {

        File[] listFile;
        listFile = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    walkdir(listFile[i]);
                } else {
                    j++;

                    /*
                          M4A is a file extension for an audio file encoded with advanced audio coding (AAC) which is a lossy compression. M4A was generally
                          intended as the successor to MP3,
                          which had not been originally designed for audio only but was layer III in an MPEG 1 or 2 video files. M4A stands for MPEG 4 Audio.


                        Opus is a lossy audio coding format developed by the Xiph.Org Foundation and standardized by the Internet Engineering Task Force, designed to
                        efficiently code speech and general audio in a single format,
                        while remaining low-latency enough for real-time interactive communication and
                        low-complexity enough for low-end embedded processors



                        A NOMEDIA file is a file stored on an Android mobile device, or on an external storage card connected to an Android device.
                        It marks its enclosing folder as having no multimedia data so
                        that the folder will not be scanned and indexed by multimedia players.
                        NOMEDIA files have no filename prefix and is named .nomedia.

                          The use of NOMEDIA files helps boost performance by excluding folders that do not need to be scanned. For example,
                         a folder that has thousands of songs or images can be excluded. NOMEDIA files can also be used to hide ads featured
                         in free apps when placed in the same directory of the ad.
                         Many Android audio and video players as well as image browsers (Gallery, MP3 Player, Video Player, etc.) recognize NOMEDIA files.




                     */
                    if (listFile[i].getName().toLowerCase().endsWith(".png")||  listFile[i].getName().toLowerCase().endsWith(".bmp")) { pngList.add(listFile[i].toString()); }
                    else if (listFile[i].getName().toLowerCase().endsWith(".pdf")) { pdfList.add(listFile[i].toString()); }
                    else if (listFile[i].getName().toLowerCase().endsWith(".txt")||listFile[i].getName().toLowerCase().endsWith(".rtf")) { txtList.add(listFile[i].toString()); }
                    else if (listFile[i].getName().toLowerCase().endsWith(".mp4") || listFile[i].getName().toLowerCase().endsWith(".mpg") || listFile[i].getName().toLowerCase().endsWith(".mpe") ) { mp4List.add(listFile[i].toString()); }
                    else if (listFile[i].getName().toLowerCase().endsWith(".mp3")|| listFile[i].getName().toLowerCase().endsWith(".m4a") || listFile[i].getName().toLowerCase().endsWith(".amr") ||  listFile[i].getName().toLowerCase().endsWith(".aac") ||  listFile[i].getName().toLowerCase().endsWith(".opus")) { mp3List.add(listFile[i].toString()); }
                    else if (listFile[i].getName().toLowerCase().endsWith(".apk")) { apkList.add(listFile[i].toString()); }
                    else if (listFile[i].getName().toLowerCase().endsWith(".jpg")  ||listFile[i].getName().toLowerCase().endsWith(".jpeg") ||listFile[i].getName().toLowerCase().endsWith(".jpe") ) { jpgList.add(listFile[i].toString()); }
                    else if (listFile[i].getName().toLowerCase().endsWith(".docx") || listFile[i].getName().toLowerCase().endsWith(".doc") || listFile[i].getName().toLowerCase().endsWith(".dot")  ) { wordList.add(listFile[i].toString()); }
                    else if (listFile[i].getName().toLowerCase().endsWith(".xlsx") || (listFile[i].getName().toLowerCase().endsWith(".xlt") )) { excelList.add(listFile[i].toString()); }
                    else if (listFile[i].getName().toLowerCase().endsWith(".ppt")) { pptlList.add(listFile[i].toString()); }
                    else if (listFile[i].getName().toLowerCase().endsWith(".swf")) { flashlList.add(listFile[i].toString()); }
                    else if (listFile[i].getName().toLowerCase().endsWith(".zip")) { ziplList.add(listFile[i].toString()); }
                    else if (listFile[i].getName().toLowerCase().endsWith(".gif")) { giflList.add(listFile[i].toString()); }

                    else { otherList.add(listFile[i].toString());    System.out.println(j + " OtherFiles-->" + listFile[i] + " \n"); }


                }
            }
        }
       // String str="png files-> "+pngList.size()+"\n jpg files-> "+jpgList.size()+" \n Gif files"+giflList.size()+"\n docs file-> "+wordList.size()+"\n ppt files ->"+pptlList.size()+"\n Excel files->"+excelList.size()+"\n flash files->"+flashlList.size()+"\n zip files->"+ziplList.size()+"\n pdf files->"+pdfList.size()+"\n Notepad file -> "+txtList.size()+" \n mp4 file->"+mp4List.size()+" \n Audio files->"+mp3List.size()+"\n apk files->"+apkList.size()+"\n other files->"+otherList.size()+"\n Total files"+j;
       // info.setText(str);
    }
    private void fillProgressBar()
    {
           progressBar.setProgress(0);
           progressBar.setMax(100);
           long TotalInternalMemory=   Utility.getTotalInternalMemorySize();
           long AvailableInternalMemory=Utility.getAvailableInternalMemorySize();


           long per= AvailableInternalMemory / (TotalInternalMemory/100);
           System.out.print("Memory Stats--> Total "+TotalInternalMemory+" Avaailable"+AvailableInternalMemory+""+per);
          // avlbMemory.setText(Utility.formatSize(Utility.getAvailableInternalMemorySize()));
           avlbMemory.setText(Utility.humanReadableByteCount(Utility.getAvailableInternalMemorySize(),true));
           // totalMemmory.setText("Total "+Utility.formatSize(Utility.getTotalInternalMemorySize()));
            totalMemmory.setText("Total "+Utility.humanReadableByteCount(Utility.getTotalInternalMemorySize(),true));
           int progress=100 -(int) per;
           progressBar.setProgress(progress);


    }


    private int listRecentFiles()
    {

        final String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DATE_MODIFIED};
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - 2);
        Date d = c.getTime();

        Cursor cursor = ctx.getContentResolver().query(MediaStore.Files
                        .getContentUri("external"), projection,
                null,
                null, null);
        if (cursor == null)
            return recentFiles.size();
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Files.FileColumns.DATA));
                File f = new File(path);
                if (d.compareTo(new Date(f.lastModified())) != 1 && !f.isDirectory()) {

                    recentFiles.add(f.getAbsolutePath());

                    }
            } while (cursor.moveToNext());
        }
        cursor.close();
        //Collections.sort(recentFiles, (lhs, rhs) -> -1 * Long.valueOf(lhs.date).compareTo(rhs.date));

        if (recentFiles.size() > 20)
            for (int i = recentFiles.size() - 1; i > 20; i--) {
                recentFiles.remove(i);
            }
        return recentFiles.size();


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                         getCategories();
                    } else {
                        Toast.makeText(getActivity(), "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
    private class LoadIamgesTask extends AsyncTask<Void,Void,Integer>
    {


        @Override
        protected Integer doInBackground(Void... voids) {
           return listImages();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);

                  cat_Img=new category_Model("Images",count+" items",R.mipmap.ic_images);
                  cat_List.set(0,cat_Img);
                  adapter.notifyDataSetChanged();

                  }
    }

    private int listImages()
    {
        ArrayList<String> images = new ArrayList<>();
        final String[] projection = {MediaStore.Images.Media.DATA};
        final Cursor cursor = ctx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        if (cursor == null) {
            System.out.println("Iamge data Count"+0);
            return 0;
        }
         else
        {
            int count=cursor.getCount();
            cursor.close();
            System.out.println("Iamge data Count"+count);
            return count;
        }



    }


    private class LoadVideosTask extends AsyncTask<Void,Void,Integer>
    {

        @Override
        protected Integer doInBackground(Void... voids) {

            return listVideos();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);

            cat_Video=new category_Model("Video",count+" items",R.mipmap.ic_video);
            cat_List.set(1,cat_Video);
            adapter.notifyDataSetChanged();

            }

    }

    private int listVideos() {

        final String[] projection = {MediaStore.Images.Media.DATA};
        final Cursor cursor = ctx.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        if (cursor == null) {
            return 0;
        }
        else
        {
            int count=cursor.getCount();
            System.out.println("video data Count"+count);
            cursor.close();
            return count;
        }


    }

    private class LoadDocumentTask extends AsyncTask<Void,Void,Integer>
    {
        @Override
        protected Integer doInBackground(Void... voids) {




            return  listDocs() ;
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);

            cat_Document=new category_Model("Document",count+" items",R.mipmap.ic_document);
            cat_List.set(3,cat_Document);
            adapter.notifyDataSetChanged();


            }
    }


    private int listDocs()
    {
        ArrayList<String> docs = new ArrayList<>();
        final String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = getContext().getContentResolver().query(MediaStore.Files.getContentUri("external"),
                projection, null, null, null);
        String[] types = new String[]{"pdf",      // if any file type needed add extension here and task is done
                "doc", "docx", "rtf", "txt", "wpd", "wps"};
        if (cursor == null) {
            System.out.println("docs data count" + 0);
            return 0;
        } else if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String FileType="";
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String[] filePath=   path.split("/");
                 String FileName=filePath[filePath.length-1];
                 String[] s1=FileName.split("\\.");
                if(s1.length==1)
                 FileType=s1[0];
                else if(s1.length==2)
                    FileType=s1[1];


                System.out.println("ArrayLength-> "+s1.length);
                if (path != null && Arrays.asList(types).contains(FileType)) {    //need to work here
                    docs.add(path);
                }
            }
            while (cursor.moveToNext());
        }
                cursor.close();
                System.out.println("docs data count" + docs.size());
                return docs.size();


    }
    private class LoadAudioTask extends AsyncTask<Void,Void,Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {

            return listaudio();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            cat_Audio = new category_Model("Audio", count + " items", R.mipmap.ic_audio);
            cat_List.set(2, cat_Audio);
            adapter.notifyDataSetChanged();


        }
    }
    private int listaudio() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.DATA
        };

        Cursor cursor = ctx.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);


        if (cursor == null) {
            System.out.println("audio data Count"+0);
            return 0;
        }
        else {
            System.out.println("audio data Count"+cursor.getCount());
            return cursor.getCount();
        }

    }

        private class LoadDownloadTask extends AsyncTask<Void,Void,Void> {
            File dir;

            private LoadDownloadTask(File dir) {
                super();
                this.dir = dir;
            }


            @Override
            protected Void doInBackground(Void... voids) {
               downLoadList.clear();

               downLoadList=getDownLoad(dir);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                cat_Download = new category_Model("Download", downLoadList.size() + " items", R.mipmap.ic_download);
                cat_List.set(4, cat_Download);
                adapter.notifyDataSetChanged();


            }
        }

    private class LoadAnimationTask extends AsyncTask<Void,Void,Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {



            return  listAnimation();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            cat_Animation=new category_Model("Animation",count+" items",R.mipmap.ic_animation);
            cat_List.set(5, cat_Animation);
            adapter.notifyDataSetChanged();

            }

    }
    private int listAnimation() {
        ArrayList<String> animation = new ArrayList<>();
        final String[] projection = {MediaStore.Files.FileColumns.DATA};

        Cursor cursor = ctx.getContentResolver()
                .query(MediaStore.Files.getContentUri("external"), projection, null, null, null);
        if (cursor == null)
            return animation.size();
        else if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Files.FileColumns.DATA));
                if (path != null && path.endsWith(".gif") ||  path.endsWith(".swf")|| path.endsWith(".ani")) {
                    animation.add(path);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return animation.size();
    }

    private class LoadrecentTask extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            listRecentFiles();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            cat_Recent=new category_Model("Recent",recentFiles.size()+" items",R.mipmap.ic_recent);
            cat_List.set(6, cat_Recent);
            adapter.notifyDataSetChanged();

        }

    }

    private class LoadApkTask extends AsyncTask<Void,Void,Integer> {


        @Override
        protected Integer doInBackground(Void... voids) {

            return listApks();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);

            cat_Apk=new category_Model("Apk",count+" items",R.mipmap.ic_apk);
            cat_List.set(7, cat_Apk);
            adapter.notifyDataSetChanged();

        }

    }
    private int listApks() {
        ArrayList<String> apkList = new ArrayList<>();
        final String[] projection = {MediaStore.Files.FileColumns.DATA};

        Cursor cursor = ctx.getContentResolver()
                .query(MediaStore.Files.getContentUri("external"), projection, null, null, null);
        if (cursor == null)
            return apkList.size();
        else if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Files.FileColumns.DATA));
                if (path != null && path.endsWith(".apk")) {
                    apkList.add(path);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return apkList.size();
    }





    public void ListAudioFile(){

            // will be used  later to fetch audio  file
            ContentResolver contentResolver = getActivity().getContentResolver();
            Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

            if(songCursor != null && songCursor.moveToFirst())
            {
                int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);

                do {
                    long currentId = songCursor.getLong(songId);
                    String currentTitle = songCursor.getString(songTitle);
                    if(!mp3List.contains(currentTitle))
                     mp3List.add(currentTitle);
                } while(songCursor.moveToNext());
            }
        }
    }


