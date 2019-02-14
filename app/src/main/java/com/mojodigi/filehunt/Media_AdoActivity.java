package com.mojodigi.filehunt;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuWrapperFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.mojodigi.filehunt.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Utils.AlertDialogHelper;
import com.mojodigi.filehunt.Utils.CustomProgressDialog;
import com.mojodigi.filehunt.Utils.Utility;
import com.mojodigi.filehunt.Utils.UtilityStorage;

import java.io.File;
import java.util.ArrayList;

public class Media_AdoActivity extends AppCompatActivity implements View.OnClickListener, AlertDialogHelper.AlertDialogListener {

    private TextView mSongNameText;
    private VideoView mAdoVideoView;
    private MediaController mediaController;
    private Context mContex;
    private ImageView idInfoBackArrowImage,idAudioShareImgView,idAudioInfoImgView,idAudioDeleteImgView;
    private TextView idSongNameText;
    private File file =null;

    private ArrayList<File> delete_list=new ArrayList<>();
    private int stopPosition;
    private SharedPreferenceUtil addprefs;
    private int activity_Tracker;
    private AlertDialogHelper alertDialogHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media__ado);

       mContex=Media_AdoActivity.this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utility.setstatusBarColorBelowM(Media_AdoActivity.this);
        }

        addprefs = new SharedPreferenceUtil(mContex);
        if (addprefs != null) {
            stopPosition = addprefs.getIntValue("position",0);
        }

        initAdoView();
    }

    private void initAdoView() {


        UtilityStorage.InitilaizePrefs(mContex);
        alertDialogHelper =new AlertDialogHelper(this);
        mSongNameText = (TextView) findViewById(R.id.idSongNameText);
        mAdoVideoView = (VideoView) findViewById(R.id.idAdoAudioView);

        idAudioShareImgView=findViewById(R.id.idAudioShareImgView);
        idInfoBackArrowImage=findViewById(R.id.idInfoBackArrowImage);
        idAudioInfoImgView=findViewById(R.id.idAudioInfoImgView);
        idAudioDeleteImgView=findViewById(R.id.idAudioDeleteImgView);
        idSongNameText=findViewById(R.id.idSongNameText);
        idSongNameText.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContex));


        idInfoBackArrowImage.setOnClickListener(this);
        idAudioShareImgView.setOnClickListener(this);
        idAudioInfoImgView.setOnClickListener(this);
        idAudioDeleteImgView.setOnClickListener(this);


        mediaController = new MediaController(this);
        mAdoVideoView.setMediaController(mediaController);
        mediaController.setAnchorView(mAdoVideoView);
        mediaController.setPadding(0, 0, 0, 10);
        //mediaController.show((stopPosition / 1000));






        String selectedAudioPath = null;
        Uri selectedVideoUri = null;

        Intent extrasIntent = getIntent();
        if (extrasIntent != null) {
            selectedAudioPath = extrasIntent.getStringExtra(Constants.selectedAdo);
            activity_Tracker=extrasIntent.getIntExtra(Constants.MEDIA_DELETE_ACTIVITY_TRACKER,100 );
            if(selectedAudioPath!=null)
            {
                file = new File(selectedAudioPath);
                selectedVideoUri = Uri.parse(file.toString());
            }
            else
            {
                Utility.dispToast(mContex, "can't play file");
            }
        }

        if (selectedVideoUri != null) {

            mSongNameText.setText(file.getName());
            mAdoVideoView.setMediaController(mediaController);
            mAdoVideoView.setVideoURI(selectedVideoUri);
            mAdoVideoView.requestFocus();
            mAdoVideoView.start();


            mAdoVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mAdoVideoView.start();
                    mediaController.show(900000000);
                }
            });

            //finish after playing
            mAdoVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    finish();
                }
            });

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPosition = mAdoVideoView.getCurrentPosition();
        addprefs.setIntValue("position", stopPosition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdoVideoView.seekTo(addprefs.getIntValue("position",0));
        mAdoVideoView.start();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.idAudioShareImgView:
                if(file!= null)
                Utility.shareFile(mContex,file.getAbsolutePath());
                break;
            case R.id.idInfoBackArrowImage:
                finish();
                break;
            case R.id.idAudioInfoImgView:
                if(file!=null) {
                    if(addprefs !=null) {
                        addprefs.setValue(Constants.mediaType, "Audio");
                    }
                    Intent intentImageGallary = new Intent(mContex, Media_InfoActivity.class);
                    intentImageGallary.putExtra(Constants.fileInfoPath, file.getAbsolutePath());
                    startActivity(intentImageGallary);
                }
                break;

            case R.id.idAudioDeleteImgView:
                if(file!=null) {
                    delete_list.add(file);
                    if(alertDialogHelper !=null && delete_list.size()>0)
                    {
                        alertDialogHelper.showAlertDialog("", "Delete Audio", "DELETE", "CANCEL", 1, true);
                    }
                }
                break;



        }
    }

    @Override
    public void onPositiveClick(int from) {
        if(from==1)
        {
            File file = delete_list.get(0);
            if(UtilityStorage.isWritableNormalOrSaf(file,mContex)) {
                new DeleteFileTask(delete_list).execute();
            }
            else
            {
                UtilityStorage.guideDialogForLEXA(mContex,file.getParent(), Constants.FILE_DELETE_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }

    private class DeleteFileTask extends AsyncTask<Void,Void,Integer>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomProgressDialog.show(mContex,getResources().getString(R.string.deleting_file));
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
            Toast.makeText(mContex, FileCount+" file deleted", Toast.LENGTH_SHORT).show();
            CustomProgressDialog.dismiss();

            if(FileCount >0) {

                switch (activity_Tracker)
                {
                    case 2: //Audio
                    {
                        AudioActivityRe obj = AudioActivityRe.getInstance();
                        if (obj != null) {

                            if (delete_list.size() >= 1) {


                                for (int i = 0; i < obj.audioList.size(); i++) {

                                    if (obj.audioList.get(i).getAudioPath().equalsIgnoreCase(delete_list.get(0).getPath())) {
                                        obj.audioList.remove(i);
                                        Constants.DELETED_AUDIO_FILES++;
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
                    boolean st = UtilityStorage.isWritableNormalOrSaf(f, mContex);
                    System.out.println("" + st);
                    if (st) {
                        boolean status = UtilityStorage.deleteWithAccesFramework(mContex, f);
                        if (status) {
                            count++;
                            Utility.RunMediaScan(mContex, f);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== Constants.FILE_DELETE_REQUEST_CODE) {
            boolean isPersistUriSet = UtilityStorage.setUriForStorage(requestCode, resultCode, data);
            if (isPersistUriSet && delete_list.size() > 0)
                new DeleteFileTask(delete_list).execute();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



}
