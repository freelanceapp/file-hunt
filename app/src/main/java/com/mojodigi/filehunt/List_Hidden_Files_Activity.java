package com.mojodigi.filehunt;

import android.content.Context;
import android.content.Intent;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.mojodigi.filehunt.Adapter.MultiSelectAdapter_Locker;
import com.mojodigi.filehunt.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.filehunt.AsyncTasks.decryptAsynscTask;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Model.Grid_Model;
import com.mojodigi.filehunt.Model.Model_Locker;
import com.mojodigi.filehunt.Utils.Utility;

import java.io.File;
import java.util.ArrayList;

public class List_Hidden_Files_Activity extends AppCompatActivity  implements  MultiSelectAdapter_Locker.fileSelectListener ,decryptAsynscTask.decryptListener {


    RecyclerView recyclerView;
    MultiSelectAdapter_Locker multiSelectAdapter;
    Context mContext;
    ImageView blankIndicator;
    int media_Type;
    ArrayList<Model_Locker> fileList=new ArrayList<Model_Locker>();
    ArrayList<Model_Locker> multiselect_list = new ArrayList<>();
    File fileToDelete;
    SharedPreferenceUtil addprefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_hidden_file);

        mContext=List_Hidden_Files_Activity.this;
        addprefs=new SharedPreferenceUtil(mContext);
        initViews();

        Bundle extrasIntent = getIntent().getExtras();
        if (extrasIntent != null) {
            media_Type=extrasIntent.getInt("mediaKey");
            }

        switch (media_Type)
        {
            case 1:  //img
                getFilesfromAppFolder(media_Type);
                Utility.setActivityTitle2(mContext, "Image");
                break;

            case 2:  //vdo
                getFilesfromAppFolder(media_Type);
                Utility.setActivityTitle2(mContext, "Video");
                break;

            case 3:  //ado
                getFilesfromAppFolder(media_Type);
                Utility.setActivityTitle2(mContext, "Audio");
                break;

            case 4:  //docs
                getFilesfromAppFolder(media_Type);
                Utility.setActivityTitle2(mContext, "Document");
                break;

        }


        }

    private void initViews() {
        recyclerView =  findViewById(R.id.recycler_view);
        blankIndicator=findViewById(R.id.blankIndicator);




    }

    private void  getFilesfromAppFolder(int cat_Type)
    {



        String folderPath= Utility.setDecryptFilePath(cat_Type);
        File fpath  = new File(folderPath);
        if(fpath.exists() && fpath.isDirectory()) {

            File[] files = fpath.listFiles();



            for(int i=0;i<files.length;i++)
            {
                File f = files[i];
                String fname = f.getName();
                System.out.print(""+fname);

                Model_Locker model=new Model_Locker();
                model.setFileName(f.getName());
                model.setFileSize(Utility.humanReadableByteCount(f.length(),true));
                model.setFilePath(f.getPath());
                model.setFileSizeCmpr(f.length());
                model.setDateCmpr(f.lastModified());
                model.setFileMDate(Utility.LongToDate(f.lastModified()));
                fileList.add(model);
                }




            if(fileList.size()!=0) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
                multiSelectAdapter = new MultiSelectAdapter_Locker(this, fileList, multiselect_list, this,media_Type);
                recyclerView.setAdapter(multiSelectAdapter);
            }
            else
            {
                blankIndicator.setVisibility(View.VISIBLE);
            }


        }
        else
        {
            blankIndicator.setVisibility(View.VISIBLE);
        }


    }


    @Override
    public void onfileSelectListener(Model_Locker locker_model) {

        new decryptAsynscTask(mContext, new File[]{new File(locker_model.getFilePath().toString())}, this, Constants.encryptionPassword,media_Type).execute();
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

    @Override
    protected void onRestart() {
        super.onRestart();


        // deletes the viewd file from memory;
        if(fileToDelete!=null && fileToDelete.exists())
            fileToDelete.delete();

    }

    @Override
    public void OnDeCryptFinish(File fileDecrypted) {
              if(fileDecrypted !=null)
             fileToDelete=fileDecrypted;  //  keeps the reference of the file  to  be deleted from the memory after  user  used it  ;
        switch (media_Type)
        {
            case 1:  //img
                Grid_Model model = new Grid_Model();
                model.setImgPath(fileDecrypted.getAbsolutePath());
                Constants.img_ArrayImgList.clear();
                Constants.img_ArrayImgList.add(model);
                Intent intentImageGallary = new Intent(mContext, Media_ImgActivity.class);
                intentImageGallary.putExtra(Constants.CUR_POS_VIEW_PAGER, 0);
                //intentImageGallary.putExtra(Constants.MEDIA_DELETE_ACTIVITY_TRACKER, Constants.RECENT);
                startActivity(intentImageGallary);
                break;

            case 2:  //vdo
                addprefs.setIntValue("position", 0);
                Intent intentVideoGallary = new Intent(mContext, Media_VdoActivity.class);
                intentVideoGallary.putExtra(Constants.selectedVdo, fileDecrypted.getAbsolutePath());
                //intentVideoGallary.putExtra(Constants.MEDIA_DELETE_ACTIVITY_TRACKER, Constants.RECENT);
                startActivity(intentVideoGallary);

                break;

            case 3:  //ado
                addprefs.setIntValue("position", 0);
                Intent intentAudioGallary = new Intent(mContext, Media_AdoActivity.class);
                intentAudioGallary.putExtra(Constants.selectedAdo, fileDecrypted.getAbsolutePath());
                //intentAudioGallary.putExtra(Constants.MEDIA_DELETE_ACTIVITY_TRACKER, Constants.STORAGE);
                startActivity(intentAudioGallary);

                break;

            case 4:  //docs

                Utility.OpenFileWithNoughtAndAll(fileDecrypted.getAbsolutePath(), mContext, getResources().getString(R.string.file_provider_authority));

                break;

        }


    }
}
