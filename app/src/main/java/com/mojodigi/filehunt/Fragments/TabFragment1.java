package com.mojodigi.filehunt.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mojodigi.filehunt.AnimationActivityRe;
import com.mojodigi.filehunt.ApkActivityRe;
import com.mojodigi.filehunt.Category_Explore_Activity;
import com.mojodigi.filehunt.DocsActivityRe;
import com.mojodigi.filehunt.DownloadActivityRe;
import com.mojodigi.filehunt.Model.category_Model;
//
import com.mojodigi.filehunt.RecentActivityRe;
import com.mojodigi.filehunt.Utils.EqualSpacingItemDecoration;
import com.mojodigi.filehunt.Utils.Utility;
import com.mojodigi.filehunt.Utils.UtilityStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.mojodigi.filehunt.Class.Constants.DELETED_ANIMATION_FILES;
import static com.mojodigi.filehunt.Class.Constants.DELETED_APK_FILES;
import static com.mojodigi.filehunt.Class.Constants.DELETED_AUDIO_FILES;
import static com.mojodigi.filehunt.Class.Constants.DELETED_DOCUMENT_FILES;
import static com.mojodigi.filehunt.Class.Constants.DELETED_DOWNLOAD_FILES;
import static com.mojodigi.filehunt.Class.Constants.DELETED_IMG_FILES;
import static com.mojodigi.filehunt.Class.Constants.DELETED_RECENT_FILES;
import static com.mojodigi.filehunt.Class.Constants.DELETED_VDO_FILES;
import static com.mojodigi.filehunt.Class.Constants.POSITION;
import com.mojodigi.filehunt.R;
public class TabFragment1 extends Fragment {


    private Context ctx;
    private RecyclerView category_recycler_view;
    ProgressBar progressBar,progressBar_Ext;
    TextView avlbMemory,    totalMemmory,internalTxt,avlbTxt;
    TextView avlbMemory_Ext,    totalMemmory_Ext,internalTxt_Ext,avlbTxt_Ext;
    RelativeLayout ext_layout;
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

    String[] permissionsRequired = new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;


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

        permissionStatus = getActivity().getSharedPreferences("permissionStatus", MODE_PRIVATE);
        ext_layout=(RelativeLayout)view.findViewById(R.id.ext_layout);
        category_recycler_view= (RecyclerView) view.findViewById(R.id.category_recycler_view);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        avlbMemory=(TextView)view.findViewById(R.id.avlbMemory);
        totalMemmory=(TextView)view.findViewById(R.id.totalMemmory);
        internalTxt=(TextView)view.findViewById(R.id.internalTxt);
        avlbTxt=(TextView)view.findViewById(R.id.avlbTxt);

        progressBar_Ext=(ProgressBar)view.findViewById(R.id.progressBar_ext);
        avlbMemory_Ext=(TextView)view.findViewById(R.id.avlbMemory_ext);
        totalMemmory_Ext=(TextView)view.findViewById(R.id.totalMemmory_ext);
        internalTxt_Ext=(TextView)view.findViewById(R.id.externalTxt);
        avlbTxt_Ext=(TextView)view.findViewById(R.id.avlbTxt_ext);

        setTypeFace();



        if (ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[0]) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[1]))
            {
                //Show Information about why you need the permission

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Need Permissions");
                builder.setMessage(getActivity().getString(R.string.app_name)+" needs to access your storage.");

                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                      //  ActivityCompat.requestPermissions((Activity) ctx, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                       requestPermissions(permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Need Permissions");
                builder.setMessage(getActivity().getString(R.string.app_name)+" app need stoarge permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getActivity(), "Go to Permissions to Grant storage access", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
               requestPermissions(permissionsRequired, PERMISSION_CALLBACK_CONSTANT);

            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], false);
            editor.commit();
        }
        else {
            //You already have the permission, just go ahead.
            getCategories();
        }
        //new  permission



//       ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.dp10);
//        category_recycler_view.addItemDecoration(itemDecoration);


//        int spanCount = 2; // 2 columns
//        int spacing = 25; // 50px
//        boolean includeEdge = true;
//        category_recycler_view.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));



    }

    @Override
    public void onResume() {
        super.onResume();


        // detetct  which activity  was in stack  then  apply a swich case  for better  result;
        if(DELETED_APK_FILES>0) {
            if (cat_Apk != null) {
                try {

                    int newtcount = Integer.parseInt(cat_Apk.getIteCount()) - DELETED_APK_FILES;
                    cat_Apk = new category_Model(getResources().getString(R.string.cat_Apk), String.valueOf(newtcount), R.mipmap.cat_ic_apk);
                    cat_List.set(7, cat_Apk);
                    adapter.notifyDataSetChanged();
                    DELETED_APK_FILES = 0;
                } catch (Exception e) {
                    String msg = e.getMessage();
                    System.out.print("" + msg);
                }
            }
        }

        if(DELETED_ANIMATION_FILES>0) {
            if (cat_Animation != null) {
                try {

                    int newtcount = Integer.parseInt(cat_Animation.getIteCount()) - DELETED_ANIMATION_FILES;
                    cat_Animation = new category_Model(getResources().getString(R.string.cat_Animation), String.valueOf(newtcount), R.mipmap.cat_ic_animation);
                    cat_List.set(5, cat_Animation);
                    adapter.notifyDataSetChanged();
                    DELETED_ANIMATION_FILES = 0;
                } catch (Exception e) {
                    String msg = e.getMessage();
                    System.out.print("" + msg);
                }
            }
        }

        if(DELETED_IMG_FILES>0) {
            if (cat_Img != null) {
                try {

                    int newtcount = Integer.parseInt(cat_Img.getIteCount()) - DELETED_IMG_FILES;
                    cat_Img = new category_Model(getResources().getString(R.string.cat_Images), String.valueOf(newtcount), R.mipmap.cat_ic_image);
                    cat_List.set(0, cat_Img);
                    adapter.notifyDataSetChanged();
                    DELETED_IMG_FILES = 0;
                } catch (Exception e) {
                    String msg = e.getMessage();
                    System.out.print("" + msg);
                }
            }
        }

        if(DELETED_VDO_FILES>0) {
            if (cat_Video != null) {
                try {

                    int newtcount = Integer.parseInt(cat_Video.getIteCount()) - DELETED_VDO_FILES;
                    cat_Video = new category_Model(getResources().getString(R.string.cat_Videos), String.valueOf(newtcount), R.mipmap.cat_ic_vdo);
                    cat_List.set(1, cat_Video);
                    adapter.notifyDataSetChanged();
                    DELETED_VDO_FILES = 0;
                } catch (Exception e) {
                    String msg = e.getMessage();
                    System.out.print("" + msg);
                }
            }
        }

        if(DELETED_AUDIO_FILES>0) {
            if (cat_Audio != null) {
                try {

                    int newtcount = Integer.parseInt(cat_Audio.getIteCount()) - DELETED_AUDIO_FILES;
                    cat_Audio = new category_Model(getResources().getString(R.string.cat_Audio), String.valueOf(newtcount), R.mipmap.cat_ic_music);
                    cat_List.set(2, cat_Audio);
                    adapter.notifyDataSetChanged();
                    DELETED_AUDIO_FILES = 0;
                } catch (Exception e) {
                    String msg = e.getMessage();
                    System.out.print("" + msg);
                }
            }
        }

        if(DELETED_DOCUMENT_FILES>0) {
            if (cat_Document != null) {
                try {

                    int newtcount = Integer.parseInt(cat_Document.getIteCount()) - DELETED_DOCUMENT_FILES;
                    cat_Document = new category_Model(getResources().getString(R.string.cat_Documents), String.valueOf(newtcount), R.mipmap.cat_ic_docs);
                    cat_List.set(3, cat_Document);
                    adapter.notifyDataSetChanged();
                    DELETED_DOCUMENT_FILES = 0;
                } catch (Exception e) {
                    String msg = e.getMessage();
                    System.out.print("" + msg);
                }
            }
        }

        if(DELETED_DOWNLOAD_FILES>0) {
            if (cat_Download != null) {
                try {

                    int newtcount = Integer.parseInt(cat_Download.getIteCount()) - DELETED_DOWNLOAD_FILES;
                    cat_Download = new category_Model(getResources().getString(R.string.cat_Download), String.valueOf(newtcount), R.mipmap.cat_ic_download);
                    cat_List.set(4, cat_Download);
                    adapter.notifyDataSetChanged();
                    DELETED_DOWNLOAD_FILES = 0;
                } catch (Exception e) {
                    String msg = e.getMessage();
                    System.out.print("" + msg);
                }
            }
        }

        if(DELETED_RECENT_FILES>0) {
            if (cat_Recent != null) {
                try {

                    int newtcount = Integer.parseInt(cat_Recent.getIteCount()) - DELETED_RECENT_FILES;
                    cat_Recent = new category_Model(getResources().getString(R.string.cat_Recent), String.valueOf(newtcount), R.mipmap.cat_ic_recent);
                    cat_List.set(6, cat_Recent);
                    adapter.notifyDataSetChanged();
                    DELETED_RECENT_FILES = 0;
                } catch (Exception e) {
                    String msg = e.getMessage();
                    System.out.print("" + msg);
                }
            }
        }








    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser)
        {





        }
    }

    private void getCategories()
    {

         cat_List.clear();

         cat_Img=new category_Model(getResources().getString(R.string.cat_Images),String.valueOf(pngList.size()+jpgList.size()+flashlList.size()),R.mipmap.cat_ic_image);
         cat_Apk=new category_Model(getResources().getString(R.string.cat_Apk),String.valueOf(apkList.size()),R.mipmap.cat_ic_apk);
         cat_Animation=new category_Model(getResources().getString(R.string.cat_Animation),String.valueOf(giflList.size()),R.mipmap.cat_ic_animation);
         cat_Audio=new category_Model(getResources().getString(R.string.cat_Audio),String.valueOf(mp3List.size()),R.mipmap.cat_ic_music);
         cat_Video=new category_Model(getResources().getString(R.string.cat_Videos),String.valueOf(mp4List.size()),R.mipmap.cat_ic_vdo);
         cat_Download=new category_Model(getResources().getString(R.string.cat_Download),String.valueOf(downLoadList.size()),R.mipmap.cat_ic_download);
         cat_Document=new category_Model(getResources().getString(R.string.cat_Documents),String.valueOf(pptlList.size()+wordList.size()+pdfList.size()+txtList.size()),R.mipmap.cat_ic_docs);
         cat_Recent=new category_Model(getResources().getString(R.string.cat_Recent),String.valueOf(recentFiles.size()),R.mipmap.cat_ic_recent);

        cat_List.add(cat_Img);
        cat_List.add(cat_Video);
        cat_List.add(cat_Audio);
        cat_List.add(cat_Document);
        cat_List.add(cat_Download);
        cat_List.add(cat_Animation);
        cat_List.add(cat_Recent);
        cat_List.add(cat_Apk);

         adapter= new categoryAdapter(cat_List);
         int per_col_spacing= Utility.percentOfValue(getScreenHeight(),2);

        category_recycler_view.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        category_recycler_view.addItemDecoration(new EqualSpacingItemDecoration(per_col_spacing, EqualSpacingItemDecoration.GRID));

        category_recycler_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        fillProgressBar();
        //



     try {
         String sdCardPath = UtilityStorage.getExternalStoragePath(ctx, true);
         // if sdcard is ejected the returned path will not exist;
         if (sdCardPath != null && Utility.isPathExist(sdCardPath,getActivity())) {
             ext_layout.setVisibility(View.VISIBLE);
             fillProgressBar_Ext();

         }
     }catch (Exception e)
     {

     }


        //



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
                    .inflate(R.layout.category_item_layout1, parent, false);

            return new categoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(categoryViewHolder holder, final int position)
        {
            holder.catName.setText(catList.get(position).getCatName());

            holder.catIcon.setImageResource(catList.get(position).getCat_icon());
            holder.itemCount.setText(Utility.putStrinBrckt(catList.get(position).getIteCount()+" items"));
           holder.catName.setTextColor(getTextColor(position));

            holder.catName.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));
            holder.itemCount.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));

             ViewGroup.LayoutParams params =  holder.container_Layout.getLayoutParams();
             params.width=(getScreenWidth()-Utility.dpToPx(24,ctx))*45/100;
             holder.container_Layout.setLayoutParams(params);

            holder.container_Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    if(position==3)
                    {
                        Intent i = new Intent(getActivity(), DocsActivityRe.class);
                        startActivity(i);
                    }
                    else  if(position==4)
                    {
                        Intent i = new Intent(getActivity(), DownloadActivityRe.class);
                        startActivity(i);
                    }
                    else  if(position==5)
                    {
                        Intent i = new Intent(getActivity(), AnimationActivityRe.class);
                        startActivity(i);

                    }
                    else if(position==6)
                    {
                        Intent i = new Intent(getActivity(), RecentActivityRe.class);
                        startActivity(i);
                    }
                    else  if(position==7)
                    {
                        Intent i = new Intent(getActivity(), ApkActivityRe.class);
                        startActivity(i);
                    }
                    else {
                        Intent i = new Intent(getActivity(), Category_Explore_Activity.class);
                        i.putExtra(POSITION, position);
                        startActivity(i);
                    }


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

    private int getTextColor(int position) {

        switch (position) {
            case  0: return   getActivity().getResources().getColor(R.color.color_img);
            case  1: return   getActivity().getResources().getColor(R.color.color_vdo);
            case  2: return   getActivity().getResources().getColor(R.color.color_audio);
            case  3: return   getActivity().getResources().getColor(R.color.color_docs);
            case  4: return   getActivity().getResources().getColor(R.color.color_download);
            case  5: return   getActivity().getResources().getColor(R.color.color_anim);
            case  6: return   getActivity().getResources().getColor(R.color.color_recent);
            case  7: return   getActivity().getResources().getColor(R.color.color_apk);
            default: return   getActivity().getResources().getColor(R.color.black);

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
                    //j++;

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


           float per= (float)AvailableInternalMemory / (float) (TotalInternalMemory/100);
           System.out.print("Memory Stats--> Total "+TotalInternalMemory+" Avaailable"+AvailableInternalMemory+""+Utility.setdecimalPoints(String.valueOf(per),2));
          // avlbMemory.setText(Utility.formatSize(Utility.getAvailableInternalMemorySize()));
           avlbMemory.setText(Utility.humanReadableByteCount(Utility.getAvailableInternalMemorySize(),true)+"("+Utility.setdecimalPoints(String.valueOf(per),2)+"%)");
           // totalMemmory.setText("Total "+Utility.formatSize(Utility.getTotalInternalMemorySize()));
            totalMemmory.setText("Total "+Utility.humanReadableByteCount(Utility.getTotalInternalMemorySize(),true));
           int progress=100 -(int) per;
           progressBar.setProgress(progress);



    }
    private void fillProgressBar_Ext()
    {




        progressBar_Ext.setProgress(0);
        progressBar_Ext.setMax(100);
        long TotalMemory_Ext=   Utility.getTotalExternalMemorySize(UtilityStorage.getExternalStoragePath(ctx,true));
        long AvailableMemory_Ext=Utility.getAvailableExternalMemorySize(UtilityStorage.getExternalStoragePath(ctx,true));


        long per= AvailableMemory_Ext / (TotalMemory_Ext/100);
        System.out.print("Memory Stats--> Total "+TotalMemory_Ext+" Avaailable"+AvailableMemory_Ext+""+per);

        avlbMemory_Ext.setText(Utility.humanReadableByteCount(AvailableMemory_Ext,true)+"("+per+"%)");

        totalMemmory_Ext.setText("Total "+Utility.humanReadableByteCount(TotalMemory_Ext,true));
        int progress=100 -(int) per;
        progressBar_Ext.setProgress(progress);



    }
    private void setTypeFace()
    {
        avlbMemory.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));
        totalMemmory.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));
        internalTxt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));
        avlbTxt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));

        avlbMemory_Ext.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));
        totalMemmory_Ext.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));
        internalTxt_Ext.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));
        avlbTxt_Ext.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));

    }



    private int listRecentFiles()
    {

        final String[] projection = {MediaStore.Files.FileColumns.DATA};
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - 7);
        Date d = c.getTime();

        Cursor cursor = ctx.getContentResolver().query(MediaStore.Files
                        .getContentUri("external"), projection,
                null,
                null, null);


        if (cursor == null)
            return recentFiles.size();
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {

            String[] types = new String[]{"pdf","png","jpeg","jpg","mp4","mp3","aac","amr","gif","doc", "docx", "txt", "wpd", "wps","xls","xlsx",
                    "pptx"
            };
            // if any file type needed add extension here and task is done

            do {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String FileType=Utility.getFileExtensionfromPath(path);
                File f = new File(path);
                if (d.compareTo(new Date(f.lastModified())) != 1 && !f.isDirectory() && Arrays.asList(types).contains(FileType)) {

                    recentFiles.add(f.getAbsolutePath());

                    }
            } while (cursor.moveToNext());
        }
        cursor.close();


        //Collections.sort(recentFiles, (lhs, rhs) -> -1 * Long.valueOf(lhs.date).compareTo(rhs.date));

//        if (recentFiles.size() > 20)
//            for (int i = recentFiles.size() - 1; i > 20; i--) {
//                recentFiles.remove(i);
//            }


        return recentFiles.size();


    }



    //new
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted)
            {
              getCategories();

            }
            else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[0]) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[1])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Need Permissions");
                builder.setMessage(getActivity().getString(R.string.app_name)+" app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                       requestPermissions(permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else
            {
                Toast.makeText(getActivity(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }


    }
    //new


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
               getCategories();
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

                  cat_Img=new category_Model(getResources().getString(R.string.cat_Images),String.valueOf(count),R.mipmap.cat_ic_image);
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

            cat_Video=new category_Model(getResources().getString(R.string.cat_Videos),String.valueOf(count),R.mipmap.cat_ic_vdo);
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

            cat_Document=new category_Model(getResources().getString(R.string.cat_Documents),String.valueOf(count),R.mipmap.cat_ic_docs);
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
        String[] types = new String[]{"pdf","doc", "docx", "txt", "wpd", "wps","xls","xlsx","ppt",
                "pptx"
        };     // if any file type needed add extension here and task is done

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
                if (path != null && Arrays.asList(types).contains(FileType)) {
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
            cat_Audio = new category_Model(getResources().getString(R.string.cat_Audio), String.valueOf(count), R.mipmap.cat_ic_music);
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

                cat_Download = new category_Model(getResources().getString(R.string.cat_Download), String.valueOf(downLoadList.size()), R.mipmap.cat_ic_download);
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
            cat_Animation=new category_Model(getResources().getString(R.string.cat_Animation),String.valueOf(count),R.mipmap.cat_ic_animation);
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

            cat_Recent=new category_Model(getResources().getString(R.string.cat_Recent),String.valueOf(recentFiles.size()),R.mipmap.cat_ic_recent);
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

            cat_Apk=new category_Model(getResources().getString(R.string.cat_Apk),String.valueOf(count),R.mipmap.cat_ic_apk);
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

    private  int getScreenWidth() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        System.out.println(width);
        return  width;
    }
    private  int getScreenHeight() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         int height = displayMetrics.heightPixels;
        //int width = displayMetrics.widthPixels;
        System.out.println(height);
        return  height;
    }

}




