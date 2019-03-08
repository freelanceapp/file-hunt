package com.mojodigi.filehunt;

import android.app.WallpaperManager;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mojodigi.filehunt.Adapter.MediaImageAdapter;
import com.mojodigi.filehunt.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Model.Grid_Model;
import com.mojodigi.filehunt.Utils.AlertDialogHelper;
import com.mojodigi.filehunt.Utils.CustomProgressDialog;
import com.mojodigi.filehunt.Utils.Utility;
import com.mojodigi.filehunt.Utils.UtilityStorage;
import com.mojodigi.filehunt.interfaces.OnClickImage;

import java.io.File;
import java.io.FileReader;
import java.lang.annotation.ElementType;
import java.util.ArrayList;

import static com.mojodigi.filehunt.Class.Constants.DELETED_IMG_FILES;


public class Media_ImgActivity extends AppCompatActivity implements View.OnClickListener, OnClickImage, AlertDialogHelper.AlertDialogListener {

    //private ZoomageView mMediaZoomView;

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private Context mContext;
    private ImageView mImageBackArrow,mImageShareView,mImageInfoImgView ,idImageDeleteImgView;
    // TextView mImageSetWallpaperText;
    private ArrayList<String> ImagesArrayList = new ArrayList<String>();
    private WallpaperManager mWallpaperManager;
    private String wallPath , mediaType;
    private OnClickImage mOnClickImage;

    private View mImageBottomLayout   ;
    boolean isUp ;
    ArrayList<File> delete_list=new ArrayList<>();
    private int cur_position;
    private SharedPreferenceUtil addprefs;
    Toolbar mediaImgToolbar ;
    MediaImageAdapter adapter;
    ArrayList<Grid_Model> delete_ImgList_Tracker = new ArrayList<>();
    int activity_Tracker;

    AlertDialogHelper alertDialogHelper;

    private long AvailableInternalMemory;
    private long Size100Mb= 1024*1024*100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media__img);

        mediaImgToolbar = (Toolbar) findViewById(R.id.mediaImgToolbar);

        setSupportActionBar(mediaImgToolbar);

        mContext=Media_ImgActivity.this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utility.setstatusBarColorBelowM(Media_ImgActivity.this);
        }
        addprefs = new SharedPreferenceUtil(mContext);


        initImgView();
    }


    private void initImgView() {
        this.mOnClickImage = this ;
        //isUp = false;
        isUp = true;

        UtilityStorage.InitilaizePrefs(mContext);
        AvailableInternalMemory = Utility.getAvailableInternalMemorySize();

        mImageBottomLayout =  findViewById(R.id.mImageBottomLayout);
        mImageBottomLayout.setVisibility(View.INVISIBLE);
        slideUp(mImageBottomLayout);

        //mImageSetWallpaperText=findViewById(R.id.mImageSetWallpaperText);
        //mImageSetWallpaperText.setOnClickListener(this);
        alertDialogHelper =new AlertDialogHelper(this);

        mImageBackArrow=findViewById(R.id.mImageBackArrow);
        mImageBackArrow.setOnClickListener(this);
        mImageShareView=findViewById(R.id.mImageShareView);
        mImageShareView.setOnClickListener(this);
        mImageInfoImgView=findViewById(R.id.mImageInfoImgView);

        mImageInfoImgView.setOnClickListener(this);


        idImageDeleteImgView=findViewById(R.id.idImageDeleteImgView);
        idImageDeleteImgView.setOnClickListener(this);

        Intent extrasIntent = getIntent();
        if (extrasIntent != null) {

            cur_position = extrasIntent.getIntExtra(Constants.CUR_POS_VIEW_PAGER,0);
            activity_Tracker=extrasIntent.getIntExtra(Constants.MEDIA_DELETE_ACTIVITY_TRACKER,100 );
            wallPath=Constants.img_ArrayImgList.get(cur_position).getImgPath();
            delete_ImgList_Tracker.add(Constants.img_ArrayImgList.get(cur_position));
        }

        if(Constants.img_ArrayImgList!=null) {
            for (int i = 0; i < Constants.img_ArrayImgList.size(); i++) {

                ImagesArrayList.add(Constants.img_ArrayImgList.get(i).getImgPath());
            }
        }

        mPager = (ViewPager) findViewById(R.id.idMediaImgViewPager);
      //  mPager.setAdapter(new MediaImageAdapter(this,ImagesArrayList ,  mOnClickImage));
        adapter=new MediaImageAdapter(this, ImagesArrayList ,  mOnClickImage);
        mPager.setAdapter(adapter);
        mPager.setOffscreenPageLimit(2);

        mPager.setCurrentItem(cur_position);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                wallPath = ImagesArrayList.get(i);
                delete_ImgList_Tracker.clear();
                delete_ImgList_Tracker.add(Constants.img_ArrayImgList.get(i));
                // Utility.dispToast(mContext, wallPath);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manu_wallpaper, menu);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Utility.log_FirebaseActivity_Events(Media_ImgActivity.this,"ImageViewActivity");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.ic_setwallpaper) {
            //Toast.makeText(this, "Action clicked", Toast.LENGTH_LONG).show();
            if(wallPath!=null)

                if(AvailableInternalMemory>Size100Mb){
                    new setwallPaperAsync().execute();
                }else {
                    Utility.dispToast(mContext, "No space available.");
                }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.mImageShareView:
                if(wallPath!=null)
                    Utility.shareFile(mContext, wallPath);
                break;

            case R.id.mImageBackArrow:
                finish();
                break;

//            case R.id.mImageSetWallpaperText:
//                if(wallPath!=null)
//                 setWallPaper(wallPath);
//                break;

            case R.id.mImageInfoImgView:

                if(wallPath!=null) {
                    addprefs.setValue(Constants.mediaType, "Image");
                    Intent intentImageGallary = new Intent(mContext, Media_InfoActivity.class);
                    intentImageGallary.putExtra(Constants.fileInfoPath, wallPath);
                    startActivity(intentImageGallary);
                };

                break;

            case  R.id.idImageDeleteImgView:
                if(wallPath!=null) {

                    File file  =  new File(wallPath);
                    if(file!=null) {
                        delete_list.add(file);
                        if(alertDialogHelper !=null && delete_list.size()>0)
                        {
                            alertDialogHelper.showAlertDialog("", "Delete Image", "DELETE", "CANCEL", 1, true);
                        }
                    }


                }

        }
    }

    @Override
    public void onPositiveClick(int from) {
        if(from==1)
        {
            if(delete_list.size()>0) {
                File file = delete_list.get(0);
                if (file != null) {
                    if (UtilityStorage.isWritableNormalOrSaf(file, mContext)) {
                        new DeleteFileTask(delete_list).execute();
                    } else {
                        UtilityStorage.guideDialogForLEXA(mContext, file.getParent(), Constants.FILE_DELETE_REQUEST_CODE);
                    }
                }
            }
        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }

    private class asy extends AsyncTask<Integer,Void,Integer>
    {


        @Override
        protected Integer doInBackground(Integer... integers) {
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }
    private class setwallPaperAsync extends AsyncTask<Integer,Void,Integer>
    {



        protected void onPreExecute() {
            super.onPreExecute();

            CustomProgressDialog.show(mContext, mContext.getResources().getString(R.string.msg_set_wall));
        }
        @Override
        protected Integer doInBackground(Integer... integers) {

            return setWallPaper(wallPath);

        }



        @Override
        protected void onPostExecute(Integer flag) {
            super.onPostExecute(flag);
            if(flag==1)
            {
                Utility.dispToast(mContext, "Wallpaper set successfully.");
            }
            else {
                Utility.dispToast(mContext, "Could not set wallpaper.");
            }
            CustomProgressDialog.dismiss();
        }
    }

    private int setWallPaper(String wallPath) {

        // Retrieve a WallpaperManager
        mWallpaperManager = WallpaperManager.getInstance(Media_ImgActivity.this);

        try {

            if(wallPath !=null)
            {
                File image= new File(wallPath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                bitmap = Bitmap.createScaledBitmap(bitmap,500,500,true);
                if(bitmap!=null) {
                    mWallpaperManager.setBitmap(bitmap);
                    // Utility.dispToast(mContext,"Wallpaper set successfully");
                    return 1;
                }
            }
        } catch (Exception e) {
            Utility.dispToast(mContext,"Image size too large.");
        }
        return 0;
    }


    @Override
    public void onClickImage() {
        if (isUp) {
            //mImageBottomLayout.setVisibility(View.GONE);
            slideDown(mImageBottomLayout);
        } else {
            //mImageBottomLayout.setVisibility(View.VISIBLE);
            slideUp(mImageBottomLayout);
        }
        isUp = !isUp;

    }


    // slide the view from below itself to the current position
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){

        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== Constants.FILE_DELETE_REQUEST_CODE) {
            boolean isPersistUriSet = UtilityStorage.setUriForStorage(requestCode, resultCode, data);
            if (isPersistUriSet && delete_list.size() > 0)
                new DeleteFileTask(delete_list).execute();
        }
    }
    private class DeleteFileTask extends AsyncTask<Void,Void,Integer>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomProgressDialog.show(mContext,getResources().getString(R.string.deleting_file));
        }

        ArrayList<File> delete_list;
        DeleteFileTask( ArrayList<File> delete_list)
        {
            this.delete_list=delete_list;
        }


        @Override
        protected Integer doInBackground(Void... voids) {
            return deleteFile(delete_list);
        }

        @Override
        protected void onPostExecute(Integer FileCount) {
            super.onPostExecute(FileCount);
            Toast.makeText(mContext, FileCount+" file deleted", Toast.LENGTH_SHORT).show();


            CustomProgressDialog.dismiss();

            if(FileCount >0) {
                switch (activity_Tracker) {
                    case 0: //images
                    {
                        PhotosActivityRe obj = PhotosActivityRe.getInstance();
                        if (obj != null) {

                            if (delete_list.size() >= 1) {


                                for (int i = 0; i < obj.img_ImgList.size(); i++) {

                                    if (obj.img_ImgList.get(i).getImgPath().equalsIgnoreCase(delete_list.get(0).getPath())) {
                                        obj.img_ImgList.remove(i);
                                        Constants.DELETED_IMG_FILES++;
                                    }

                                }
                                finish();
                            }
                        }
                        return;
                    }

                    case 4://download
                    {
                        //DownloadList
                        DownloadActivityRe obj = DownloadActivityRe.getInstance();
                        if (obj != null) {

                            if (delete_list.size() >= 1) {


                                for (int i = 0; i < obj.DownloadList.size(); i++) {

                                    if (obj.DownloadList.get(i).getFilePath().equalsIgnoreCase(delete_list.get(0).getPath())) {
                                        obj.DownloadList.remove(i);
                                        //Constants.DELETED_DOWNLOAD_FILES++;
                                    }

                                }
                                finish();
                            }
                        }
                        return;
                    }

                    case 6: //recent
                    {


                        RecentActivityRe obj = RecentActivityRe.getInstance();
                        if (obj != null) {

                            if (delete_list.size() >= 1) {


                                for (int i = 0; i < obj.RecentList.size(); i++) {

                                    if (obj.RecentList.get(i).getFilePath().equalsIgnoreCase(delete_list.get(0).getPath())) {
                                        obj.RecentList.remove(i);
                                    }

                                }
                                finish();
                            }
                        }

                        return;
                    }

                    case 10:// storage
                    {


                        Activity_Stotrage obj = Activity_Stotrage.getInstance();
                        if (obj != null) {

                            if (delete_list.size() >= 1) {


                                for (int i = 0; i < obj.folderList.size(); i++) {

                                    if (obj.folderList.get(i).getFilePath().equalsIgnoreCase(delete_list.get(0).getPath())) {
                                        obj.folderList.remove(i);
                                    }

                                }
                                finish();
                            }
                        }

                        return;
                    }
                    default:

                        finish();
                        return;

                }
            }






//            if(ImagesArrayList.size()>=1 && delete_list.size()>=0)
//            {
//
//
//                finish();
//                if (ImagesArrayList.contains(delete_list.get(0).getPath())) {
//                    ImagesArrayList.remove(delete_list.get(0).getPath());
//                   if(adapter!=null) {
//                       adapter.notifyDataSetChanged();
//                       adapter=new MediaImageAdapter(mContext,ImagesArrayList ,  mOnClickImage);
//                       mPager.setAdapter(null);
//                       mPager.setAdapter(adapter);
//
//
//                   }
//                }
//
//                if(ImagesArrayList.size()==0) //send to  previous screen if last item removed;
//                    finish();




// as we have finished the  activity  no need to  update position
//            if(mPager!=null)
//            {
//                int curPage =mPager.getCurrentItem();
//                mPager.setCurrentItem(curPage+1);
//            }




    }
    }
    private int deleteFile( ArrayList<File> delete_list)
    {
        int count=0;

        for(int i=0;i<delete_list.size();i++)
        {
            File f=delete_list.get(i);
            if(f.exists()) {
                if (f.delete()) {
                    count++;
                    sendBroadcast(f);
                }
                //new
                else {
                    boolean st = UtilityStorage.isWritableNormalOrSaf(f, mContext);
                    System.out.println("" + st);
                    if (st) {
                        boolean status = UtilityStorage.deleteWithAccesFramework(mContext, f);
                        if (status) {
                            count++;
                            Utility.RunMediaScan(mContext, f);
                        }
                    } else {
                        //UtilityStorage.triggerStorageAccessFramework(mcontext);
                    }


                }
            }
            //new

        }


        return count;
    }
    private void sendBroadcast(File outputFile)
    {
        //  https://stackoverflow.com/questions/4430888/android-file-delete-leaves-empty-placeholder-in-gallery
        //this broadcast clear the deleted images from  android file system
        //it makes the MediaScanner service run again that keep  track of files in android
        // to  run it a permission  in manifest file has been given
        // <protected-broadcast android:name="android.intent.action.MEDIA_MOUNTED" />


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = Uri.fromFile(outputFile);
            scanIntent.setData(contentUri);
            sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
            sendBroadcast(intent);
        }

    }







}
