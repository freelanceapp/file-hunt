package com.mojodigi.filehunt;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.mojodigi.filehunt.Adapter.MultiSelectAdapter_Recent;
import com.mojodigi.filehunt.AddsUtility.AddConstants;
import com.mojodigi.filehunt.AddsUtility.AddMobUtils;
import com.mojodigi.filehunt.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Model.Grid_Model;
import com.mojodigi.filehunt.Model.Model_Recent;
import com.mojodigi.filehunt.Utils.AlertDialogHelper;
import com.mojodigi.filehunt.Utils.AsynctaskUtility;
import com.mojodigi.filehunt.Utils.CustomProgressDialog;
import com.mojodigi.filehunt.Utils.RecyclerItemClickListener;
import com.mojodigi.filehunt.Utils.Utility;
import com.mojodigi.filehunt.Utils.UtilityStorage;
import com.smaato.soma.AdDownloaderInterface;
import com.smaato.soma.AdListenerInterface;
import com.smaato.soma.BannerView;
import com.smaato.soma.ErrorCode;
import com.smaato.soma.ReceivedBannerInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

//


public class RecentActivityRe extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener,MultiSelectAdapter_Recent.RecentListener, AsynctaskUtility.AsyncResponse,AdListenerInterface {

    ActionMode mActionMode;
    Menu context_menu;


    RecyclerView recyclerView;
    MultiSelectAdapter_Recent multiSelectAdapter;
    boolean isMultiSelect = false;

    ArrayList<Model_Recent> RecentList = new ArrayList<>();
    ArrayList<Model_Recent> multiselect_list = new ArrayList<>();

    AlertDialogHelper alertDialogHelper;
    int int_position;
    ArrayList<Model_Recent> Intent_Docs_List;
    private SearchView searchView;
    ImageView blankIndicator;
      Context mcontext;
      int RECENTFILES=7;

   public  static RecentActivityRe instance;

    private boolean isUnseleAllEnabled=false;
    private Model_Recent fileTorename;
    private int renamePosition;
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;
    private int lastCheckedSortOptions;

    SharedPreferenceUtil addprefs;
    View adContainer;
    RelativeLayout smaaToAddContainer;
    //smaatoAddBanerView
    BannerView smaaTobannerView;
    private Model_Recent glob_model;
    private boolean isSearchModeActive;

    MenuItem sortView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity_re);

        Constants.ACTIVITY_TRACKER= Constants.ACTIVITY_ENUM.ACTIVITY_RECENT;
        //  banner add
        //add netwrk varibales

        mcontext=RecentActivityRe.this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utility.setstatusBarColorBelowM(RecentActivityRe.this);
        }

        mAdView = (AdView) findViewById(R.id.adView);
        adContainer = findViewById(R.id.adMobView);
        smaaToAddContainer = findViewById(R.id.smaaToAddContainer);
        smaaToAddContainer.setVisibility(View.GONE);

        smaaTobannerView = new BannerView((this).getApplication());
        smaaTobannerView.addAdListener(this);

        addprefs = new SharedPreferenceUtil(mcontext);

        AddMobUtils adutil = new AddMobUtils();

        if(AddConstants.checkIsOnline(mcontext) && adContainer !=null && addprefs !=null)
        {
            String AddPrioverId=addprefs.getStringValue(AddConstants.ADD_PROVIDER_ID, AddConstants.NOT_FOUND);
            if(AddPrioverId.equalsIgnoreCase(AddConstants.Adsense_Admob_GooglePrivideId))
                adutil.displayServerBannerAdd(addprefs,adContainer , mcontext);
            else if(AddPrioverId.equalsIgnoreCase(AddConstants.SmaatoProvideId))
            {
                try {
                    int publisherId = Integer.parseInt(addprefs.getStringValue(AddConstants.APP_ID, AddConstants.NOT_FOUND));
                    int addSpaceId = Integer.parseInt(addprefs.getStringValue(AddConstants.BANNER_ADD_ID, AddConstants.NOT_FOUND));
                    adutil.displaySmaatoBannerAdd(smaaTobannerView, smaaToAddContainer, publisherId, addSpaceId);
                }catch (Exception e)
                {
                    String string = e.getMessage();
                    System.out.print(""+string);
                }
            }
            else if(AddPrioverId.equalsIgnoreCase(AddConstants.FaceBookAddProividerId))
            {
                adutil.dispFacebookBannerAdd(mcontext,addprefs , RecentActivityRe.this);
            }

        }
        else {
            adutil.displayLocalBannerAdd(mAdView);
        }



        // banner add

        // banner add

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        blankIndicator=(ImageView) findViewById(R.id.blankIndicator);


        instance=this;
        UtilityStorage.InitilaizePrefs(mcontext);
        Utility.setActivityTitle2(mcontext,getResources().getString(R.string.cat_Recent));
        //execute the async task
        new AsynctaskUtility<Model_Recent>(mcontext,this, RECENTFILES).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        alertDialogHelper =new AlertDialogHelper(this);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect && position!= RecyclerView.NO_POSITION)
                    multi_select(position);

                else {

                   // openDocument(RecentList.get(position).getFilePath());
                }
            }




            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<Model_Recent>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);

                        Utility.hideKeyboard(RecentActivityRe.this);
                        isSearchModeActive = false;
                        searchView.onActionViewCollapsed();

                        sortView.setVisible(true);
                    }
                }

                multi_select(position);

            }
        }));


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Constants.APK_INSTALL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (getPackageManager().canRequestPackageInstalls()) {
                if(glob_model !=null)
                    Utility.OpenFileWithNoughtAndAll_Apk(glob_model.getFilePath(), mcontext, getResources().getString(R.string.file_provider_authority));
            }
        } else {
            //give the error
        }


        if(requestCode== Constants.FILE_DELETE_REQUEST_CODE) {
            boolean isPersistUriSet = UtilityStorage.setUriForStorage(requestCode, resultCode, data);
            if (isPersistUriSet && multiselect_list.size() > 0)
                new DeleteFileTask(multiselect_list).execute();
        }
        if(requestCode== Constants.FILE_RENAME_REQUEST_CODE)
        {
            boolean isPersistUriSet = UtilityStorage.setUriForStorage(requestCode, resultCode, data);
            if (isPersistUriSet && multiselect_list.size() > 0)
            {
                // call rename function  here in the case of premission granted first  time;

                Utility.renameFile(mcontext,multiselect_list.get(0).getFilePath(), Constants.Global_File_Rename_NewName,6);
            }

        }


    }

    public static RecentActivityRe getInstance() {
        return instance;
    }
    public void refreshAdapterAfterRename(String newPath, String newName)
    {

        // finish  acrtion mode aftr  rename  file is  done
        if(mActionMode!=null) {
            mActionMode.finish();
        }
        fileTorename.setFilePath(newPath);
        fileTorename.setFileName(newName);
        RecentList.set(renamePosition,fileTorename);
        refreshAdapter();

    }
    public boolean checkForFileExist(String newFPath)
    {

        for(int i=0;i<RecentList.size();i++)
        {
            String listFile=RecentList.get(i).getFilePath().toString();
            boolean status=listFile.equalsIgnoreCase(newFPath);
            if(status)
                return true;

        }

        return  false;

    }



    @Override
    protected void onResume() {
        super.onResume();
        Utility.log_FirebaseActivity_Events(RecentActivityRe.this,"RecentActivity");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(mActionMode !=null)
        {
            mActionMode.finish();

        }
    // update the  list  if file deleted from media_img,media_audio,media_vdo
        if (multiSelectAdapter != null) {
            multiSelectAdapter.notifyDataSetChanged();
        }
    }

    protected void onStart() {
        super.onStart();

        AddMobUtils addutil= new AddMobUtils();
       // addutil.displayRewaredVideoAdd(mcontext,mRewardedVideoAd);
    }








    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.menu_common_activity, menu);

        sortView = menu.findItem(R.id.action_sort);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);


        Utility.setCustomizeSeachBar(mcontext,searchView);
        // listening to search query text change

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                if(multiSelectAdapter!=null)
                    multiSelectAdapter.getFilter().filter(query.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                   if(multiSelectAdapter!=null)
                    multiSelectAdapter.getFilter().filter(query.trim());
                    return false;

            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                MenuItem item= menu.findItem(R.id.action_sort);
                item.setVisible(true);
                //invalidateOptionsMenu();
                searchView.requestFocus(0);
                //searchView.setFocusable(false);

                isSearchModeActive=false;
                Utility.hideKeyboard(RecentActivityRe.this);

                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItem item= menu.findItem(R.id.action_sort);
                item.setVisible(false);
                //invalidateOptionsMenu();
                searchView.requestFocus(1);
                searchView.setFocusable(true);
                isSearchModeActive=true;
                Utility.showKeyboard(RecentActivityRe.this);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        if(id== R.id.action_sort)
        {
            sortDialog(mcontext);

        }

        return super.onOptionsItemSelected(item);
    }




    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(RecentList.get(position)))
                multiselect_list.remove(RecentList.get(position));
            else {
                multiselect_list.add(RecentList.get(position));
                // to  rename file contain old file;
                if(multiselect_list.size()==1) {
                    fileTorename = RecentList.get(position);
                    renamePosition=position;
                }
                // to  rename file contain old file;
            }

            if (multiselect_list.size() > 0) {
                mActionMode.setTitle("" + multiselect_list.size());
                //keep  the reference of file to  be renamed
                if (RecentList.contains(multiselect_list.get(0))) {
                    renamePosition = RecentList.indexOf(multiselect_list.get(0));
                    fileTorename = multiselect_list.get(0);
                }
                //keep  the reference of file to  be renamed
            }
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }


    public void refreshAdapter()
    {
        if(multiSelectAdapter !=null) {
            multiSelectAdapter.selected_RecentList = multiselect_list;
            multiSelectAdapter.RecentList = RecentList;
            multiSelectAdapter.notifyDataSetChanged();
            selectMenuChnage();

            //finish action mode when user deselect files one by one ;
            if (multiselect_list.size() == 0) {
                if (mActionMode != null) {
                    mActionMode.finish();
                }
            }
        }

    }
    private void DispDetailsDialog( Model_Recent fileProperty )
    {

        if(fileProperty.getFilePath() !=null)
        {
            String[] splitPath = fileProperty.getFilePath().split("/");
            String fName = splitPath[splitPath.length - 1];

            Dialog dialog = new Dialog(RecentActivityRe.this);
            dialog.setContentView(R.layout.dialog_file_property);
            // Set dialog title

            TextView FileName = dialog.findViewById(R.id.FileName);
            TextView FilePath = dialog.findViewById(R.id.FilePath);
            TextView FileSize = dialog.findViewById(R.id.FileSize);
            TextView FileDate = dialog.findViewById(R.id.FileDate);
            TextView Resolution = dialog.findViewById(R.id.Resolution);
            TextView resltxt=dialog.findViewById(R.id.resltxt);

            TextView Oreintation = dialog.findViewById(R.id.ort);
            TextView oreinttxt=dialog.findViewById(R.id.oreinttxt);

            Oreintation.setVisibility(View.GONE);
            oreinttxt.setVisibility(View.GONE);
            resltxt.setVisibility(View.GONE);
            Resolution.setVisibility(View.GONE);



            FileName.setText(fName);
            FilePath.setText(fileProperty.getFilePath());
            FileSize.setText(fileProperty.getFileSize());
            FileDate.setText(fileProperty.getFileMdate());



            dialog.show();
        }
    }

    private int statusBarColor;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;

            // hide the action_hide menu as no  recent  will  be  encrypted;
            MenuItem action_hide=context_menu.findItem(R.id.action_hide);
            if(action_hide!=null)
                action_hide.setVisible(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //hold current color of status bar
                statusBarColor = getWindow().getStatusBarColor();
                //set your gray color
                getWindow().setStatusBarColor(getResources().getColor(R.color.onePlusAccentColor_device_default_dark));
            }

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {

                case R.id.action_move:
                    Utility.dispToast(mcontext,getResources().getString(R.string.menu_item_move));
                    return true;
                case R.id.action_encrypt:
                    Utility.dispToast(mcontext,"encrypt");
                    return true;

                case R.id.action_copy:
                    if(multiselect_list.size()>0)
                    {
                        for(int i=0;i<multiselect_list.size();i++)
                        {
                            String fPath=multiselect_list.get(0).getFilePath().toString();
                            System.out.println(""+fPath);
                            if(!Constants.filesToCopy.contains(multiselect_list.get(i).getFilePath())) {
                                Constants.filesToCopy.add(multiselect_list.get(i).getFilePath().toString());
                            }
                        }
                        if(Constants.filesToCopy.size()>=1) {
                            Utility.dispLocalStorages(mcontext,1);
                        }




                    }
                    return true ;

                case R.id.action_rename:
                    if(multiselect_list.size()==1)
                        Utility.fileRenameDialog(mcontext,multiselect_list.get(0).getFilePath(), Constants.RECENT,false);
                    return  true;
                case R.id.action_delete:
                    if(multiselect_list.size()>=1) {
                        int mFileCount = multiselect_list.size();
                        String msgDeleteFile = mFileCount > 1 ? mFileCount + " " + getResources().getString(R.string.delfiles) : mFileCount + " " + getResources().getString(R.string.delfile);
                        //alertDialogHelper.showAlertDialog("", "Delete file"+" ("+msgDeleteFile+")", "DELETE", "CANCEL", 1, true);

                        alertDialogHelper.showAlertDialog("", getResources().getString(R.string.delete_file_msgs)+" ("+msgDeleteFile+")", getResources().getString(R.string.menu_item_delete), getResources().getString(R.string.cancel), 1, true);
                    }
                    return true;
                case R.id.action_select:
                    if(RecentList.size()==multiselect_list.size() || isUnseleAllEnabled==true)
                        unSelectAll();
                    else
                        selectAll();
                    return  true;
                case  R.id.action_Share:
                    shareFileMultipleFilesWithNoughatAndAll();
                    return  true;
                case R.id.action_details:
                    if(multiselect_list.size()==1)//diplay details only for one selected file for now
                        DispDetailsDialog(multiselect_list.get(0));
                    else {
                        String size =calcSelectFileSize(multiselect_list);
                        System.out.println("" + size);
                        if(size!=null)
                            Utility.multiFileDetailsDlg(mcontext,size,multiselect_list.size());
                    }
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<Model_Recent>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //return to "old" color of status bar
                getWindow().setStatusBarColor(statusBarColor);
            }
            refreshAdapter();
        }
    };





    // AlertDialog Callback Functions

    @Override
    public void onPositiveClick(int from) {
        if(from==1)
        {
            if(multiselect_list.size()>0)
            {

                //
                File f =new File(multiselect_list.get(0).getFilePath());

                if(UtilityStorage.isWritableNormalOrSaf(f,mcontext)) {
                    new DeleteFileTask(multiselect_list).execute();
                }
                else
                {
                    UtilityStorage.guideDialogForLEXA(mcontext,f.getParent(), Constants.FILE_DELETE_REQUEST_CODE);
                }

                //

                // now this task  is being done  on post execute of delete task

//                for(int i=0;i<multiselect_list.size();i++)
//                    RecentList.remove(multiselect_list.get(i));
//
//                multiSelectAdapter.notifyDataSetChanged();
//
//                if (mActionMode != null) {
//                    mActionMode.finish();
//                }

            }
        }
        else if(from==2)
        {
            if (mActionMode != null) {
                mActionMode.finish();
            }

            //this yet to be implemented
//            Grid_Model mImg = new Grid_Model();
//            mImg.setImgPath("");
//            RecentList.add(mImg);
            multiSelectAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }
   // new function

    @Override
    public void processFinish(ArrayList output) {
         System.out.print(""+output);
        RecentList=output;
        multiSelectAdapter = new MultiSelectAdapter_Recent(this, RecentList, multiselect_list, this);

       if(RecentList.size()!=0) {
           System.out.print("" + RecentList);

           LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
           recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
           //recyclerView.addItemDecoration(new DividerItemDecoration(mcontext,
             //      DividerItemDecoration.VERTICAL));
           recyclerView.setAdapter(multiSelectAdapter);


       }else
       {
           blankIndicator.setVisibility(View.VISIBLE);
       }



    }

    @Override
    public void onReceiveAd(AdDownloaderInterface adDownloaderInterface, ReceivedBannerInterface receivedBannerInterface) {
        if(receivedBannerInterface.getErrorCode() != ErrorCode.NO_ERROR){
           // Toast.makeText(getBaseContext(), receivedBannerInterface.getErrorMessage(), Toast.LENGTH_SHORT).show();

            Log.d("SmaatoErrorMsg", ""+receivedBannerInterface.getErrorMessage());

            if(receivedBannerInterface.getErrorMessage().equalsIgnoreCase(AddConstants.NO_ADDS))
            {
                smaaToAddContainer.setVisibility(View.GONE);
            }
        }
        else if(receivedBannerInterface.getErrorCode() == ErrorCode.NO_ERROR)
        {
            smaaToAddContainer.setVisibility(View.VISIBLE);
        }
    }

    private class DeleteFileTask extends AsyncTask<Void,Void,Integer>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomProgressDialog.show(mcontext,getResources().getString(R.string.deleting_file));
        }

        ArrayList<Model_Recent> multiselect_list;
        DeleteFileTask( ArrayList<Model_Recent> multiselect_list)
        {
            this.multiselect_list=multiselect_list;
        }


        @Override
        protected Integer doInBackground(Void... voids) {
             return deleteFile(multiselect_list);
        }

        @Override
        protected void onPostExecute(Integer FileCount) {
            super.onPostExecute(FileCount);

            if(FileCount>0)
            {
                for (int i = 0; i < multiselect_list.size(); i++) {
                    RecentList.remove(multiselect_list.get(i));
                    Utility.removeFileFromCopyList(multiselect_list.get(i).getFilePath());
                }

                multiSelectAdapter.notifyDataSetChanged();

                if (mActionMode != null) {
                    mActionMode.finish();
                }
            }

            String msg=FileCount>1 ? FileCount+" "+getResources().getString(R.string.delmsg1) : FileCount+" "+getResources().getString(R.string.delmsg2);
            Utility.dispToast(mcontext, msg);

            CustomProgressDialog.dismiss();


        }
    }
    private void selectAll()
    {
        if (mActionMode != null)
        {
            multiselect_list.clear();

            for(int i=0;i<RecentList.size();i++)
            {
               if(!multiselect_list.contains(multiselect_list.contains(RecentList.get(i))))
               {
                    multiselect_list.add(RecentList.get(i));
               }
            }
            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();
            //to change  the unselectAll  menu  to  selectAll
            selectMenuChnage();
            //to change  the unselectAll  menu  to  selectAll

        }
        }
    private void unSelectAll()
    {
        if (mActionMode != null)
        {
            multiselect_list.clear();

            if (multiselect_list.size() >= 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            //to change  the unselectAll  menu  to  selectAll
            selectMenuChnage();
            //to change  the unselectAll  menu  to  selectAll

            if (mActionMode != null) {
                mActionMode.finish();
            }



            refreshAdapter();

        }
    }

    private void selectMenuChnage()
    {
        if(context_menu!=null)
        {
            if(RecentList.size()==multiselect_list.size()) {
                for (int i = 0; i < context_menu.size(); i++) {
                    MenuItem item = context_menu.getItem(i);
                    if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.menu_selectAll))) {
                        item.setTitle(getResources().getString(R.string.menu_unselectAll));
                        isUnseleAllEnabled=true;
                    }
                }
            }
            else {

                for (int i = 0; i < context_menu.size(); i++) {
                    MenuItem item = context_menu.getItem(i);
                    if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.menu_unselectAll))) {
                        item.setTitle(getResources().getString(R.string.menu_selectAll));
                        isUnseleAllEnabled=false;
                    }
                }

            }

            // rename  options will be visible if only i file is selected

            MenuItem item= context_menu.findItem(R.id.action_rename);
            if (multiselect_list.size()==1)
                item.setVisible(true);
            else
                item.setVisible(false);

            // rename  options will be visible if only i file is selected

        }
        invalidateOptionsMenu();
    }

    private int deleteFile(ArrayList<Model_Recent> delete_list)
    {
        int count=0;

        for(int i=0;i<delete_list.size();i++)
        {
            File f=new File(String.valueOf(delete_list.get(i).getFilePath()));
            if(f.exists()) {
                if (f.delete()) {
                    count++;
                    sendBroadcast(f);
                }


                //new
                else {
                    boolean st = UtilityStorage.isWritableNormalOrSaf(f, mcontext);
                    System.out.println("" + st);
                    if (st) {
                        boolean status = UtilityStorage.deleteWithAccesFramework(mcontext, f);
                        if (status) {
                            count++;
                            Utility.RunMediaScan(mcontext, f);
                        }
                    } else {
                       // UtilityStorage.triggerStorageAccessFramework(mcontext);
                    }


                }
                //new
            }


        }
        Constants.DELETED_RECENT_FILES=count;
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
    private void  shareFile() {

         if(multiselect_list.size()>0)
         {
             ArrayList<Uri> uris = new ArrayList<>();
             Intent intent = new Intent();
             intent.setAction(Intent.ACTION_SEND_MULTIPLE);
             intent.setType("*/*");
             for (int i=0;i<multiselect_list.size();i++) {
                 File file = new File(multiselect_list.get(i).getFilePath());
                 uris.add(Uri.fromFile(file));
             }
              intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
              startActivity(intent);
             }
             else
         {
             //Toast.makeText(mcontext, "No files to share", Toast.LENGTH_SHORT).show();
             Utility.dispToast(mcontext,getResources().getString(R.string.nofile));
         }

    }
    private void  shareFileMultipleFilesWithNoughatAndAll() {

        if(multiselect_list.size()>0)
        {
            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            sharingIntent.setType("*/*");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            {
                ArrayList<Uri> files = new ArrayList<Uri>();

                for (int i = 0; i < multiselect_list.size(); i++) {
                    File file = new File(multiselect_list.get(i).getFilePath());
                    Uri uri = Uri.fromFile(file);
                    files.add(uri);
                }
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                startActivity(sharingIntent);
            }
            else
            {
                ArrayList<Uri> files = new ArrayList<Uri>();

                for (int i = 0; i < multiselect_list.size(); i++)
                {
                    File file = new File(multiselect_list.get(i).getFilePath());
                    Uri uri = FileProvider.getUriForFile(mcontext, getResources().getString(R.string.file_provider_authority), file);
                    files.add(uri);
                }
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(sharingIntent);

            }
            Utility.shareTracker("Recent","File from recent shared");
        }
        else
        {
           // Toast.makeText(mcontext, "No files to share", Toast.LENGTH_SHORT).show();
            Utility.dispToast(mcontext, getResources().getString(R.string.nofile));
        }

    }

    private ArrayList<Model_Recent> listRecentFiles()
    {
        //MediaStore.Files.FileColumns.DISPLYA_NAME returns null on some devices
          ArrayList<Model_Recent> RecentListLocal = new ArrayList<>();
        final String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.MIME_TYPE};
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - 2);
        Date d = c.getTime();

        Cursor cursor = mcontext.getContentResolver().query(MediaStore.Files
                        .getContentUri("external"), projection,
                null,
                null, null);
        if (cursor == null)
            return RecentListLocal;

        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {

                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String fileName=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                long fileSize=cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                long mDate=cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
               // String type=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));




                File f = new File(path);
                if (d.compareTo(new Date(f.lastModified())) != 1 && !f.isDirectory()) {
                   Model_Recent model=new Model_Recent();

                   model.setFileSize(Utility.humanReadableByteCount(fileSize,true));
                   model.setFileName(fileName);
                   model.setFileMdate(Utility.LongToDate(mDate));
                   model.setFilePath(path);
                   model.setFileType(Utility.getFileExtensionfromPath(path));
                   RecentListLocal.add(model);

                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        //Collections.sort(recentFiles, (lhs, rhs) -> -1 * Long.valueOf(lhs.date).compareTo(rhs.date));

        if (RecentListLocal.size() > 20)
            for (int i = RecentListLocal.size() - 1; i > 20; i--) {
                RecentListLocal.remove(i);
            }
        return RecentListLocal;


    }
    @Override
    public void onRecentSelected(Model_Recent recent_Model) {

        // Utility.OpenFile(mcontext,model_apk.getFilePath()); // open file below  Android N
        if(mActionMode==null) {
            String type = Utility.getMimiTypefromPath(recent_Model.getFilePath());
            System.out.print("" + type);
            if (type != null) {
                if (type.contains(Constants.mimeType_Img_Registries)) {
                    Grid_Model model = new Grid_Model();
                    model.setImgPath(recent_Model.getFilePath());
                    Constants.img_ArrayImgList.clear();
                    Constants.img_ArrayImgList.add(model);
                    Intent intentImageGallary = new Intent(mcontext, Media_ImgActivity.class);
                    intentImageGallary.putExtra(Constants.CUR_POS_VIEW_PAGER, 0);
                    intentImageGallary.putExtra(Constants.MEDIA_DELETE_ACTIVITY_TRACKER, Constants.RECENT);
                    startActivity(intentImageGallary);
                } else if (type.contains(Constants.mimeType_Audio_Registries)) {
                    if (addprefs != null) {
                        addprefs.setIntValue("position", 0);
                    }
                    Intent intentAudioGallary = new Intent(mcontext, Media_AdoActivity.class);
                    intentAudioGallary.putExtra(Constants.selectedAdo, recent_Model.getFilePath());
                    intentAudioGallary.putExtra(Constants.MEDIA_DELETE_ACTIVITY_TRACKER, Constants.RECENT);
                    startActivity(intentAudioGallary);
                } else if (type.contains(Constants.mimeType_Video_Registries)) {
                    if (addprefs != null) {
                        addprefs.setIntValue("position", 0);
                    }
                    Intent intentVideoGallary = new Intent(mcontext, Media_VdoActivity.class);
                    intentVideoGallary.putExtra(Constants.selectedVdo, recent_Model.getFilePath());
                    intentVideoGallary.putExtra(Constants.MEDIA_DELETE_ACTIVITY_TRACKER, Constants.RECENT);
                    startActivity(intentVideoGallary);
                }
                else if(recent_Model.getFilePath().endsWith(".apk"))
                {

                    glob_model=recent_Model;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!getPackageManager().canRequestPackageInstalls()) {
                            startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), Constants.APK_INSTALL_REQUEST_CODE);
                        } else {
                            Utility.OpenFileWithNoughtAndAll_Apk(recent_Model.getFilePath(), mcontext, getResources().getString(R.string.file_provider_authority));
                        }
                    } else {
                        Utility.OpenFileWithNoughtAndAll_Apk(recent_Model.getFilePath(), mcontext, getResources().getString(R.string.file_provider_authority));
                    }
                }
                else
                    Utility.OpenFileWithNoughtAndAll(recent_Model.getFilePath(), mcontext, getResources().getString(R.string.file_provider_authority));

            }
            else
            {
                Utility.OpenFileWithNoughtAndAll(recent_Model.getFilePath(), mcontext, getResources().getString(R.string.file_provider_authority));
            }
        }


    }

    public String calcSelectFileSize(ArrayList<Model_Recent> fileList)
    {
        long totalSize=0;

        for(int i=0;i<fileList.size();i++)
        {
            Model_Recent m =  fileList.get(i);
            File f= new File(m.getFilePath());
            totalSize+=f.length();
        }

        return  Utility.humanReadableByteCount(totalSize,true);
    }
    String action="Name";
    public  void sortDialog(final Context ctx)
    {
        // action="Name";
        System.out.print(""+RecentList);
        android.support.v7.app.AlertDialog.Builder  dialog=new android.support.v7.app.AlertDialog.Builder(ctx) ;
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_sort, null);
        dialog.setView(view);

        RadioGroup radioGroup =view.findViewById(R.id.radioGroup);
        RadioButton name=view.findViewById(R.id.sort_name);
        RadioButton last=view.findViewById(R.id.sort_last_modified);
        RadioButton size=view.findViewById(R.id.sort_size);
        RadioButton type=view.findViewById(R.id.sort_type);

        name.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(ctx));
        last.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(ctx));
        size.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(ctx));
        type.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(ctx));

        if(lastCheckedSortOptions==0)
            name.setChecked(true);
        else if(lastCheckedSortOptions==1)
            last.setChecked(true);
        else if(lastCheckedSortOptions==2)
            size.setChecked(true);
        else if(lastCheckedSortOptions==3)
            type.setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb)
                {



                    switch (checkedId)
                    {
                        case R.id.sort_name:
                            action="Name";
                            break;

                        case R.id.sort_last_modified:
                            action="Last";
                            break;
                        case R.id.sort_size:
                            action="Size";
                            break;
                        case R.id.sort_type:
                            action="Type";
                            break;



                    }



                }

            }
        });


        dialog.setPositiveButton(ctx.getResources().getString(R.string.descending), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                setLastSelectedCheckBoxFlag(action);
                if(action.equalsIgnoreCase("Name"))
                {
                    Collections.sort(RecentList, new Comparator<Model_Recent>() {
                        public int compare(Model_Recent o1, Model_Recent o2) {
                            return o2.getFileName().compareToIgnoreCase(o1.getFileName());

                        }
                    });
                }
                else if(action.equalsIgnoreCase("Last"))
                {
                    Collections.sort(RecentList, new Comparator<Model_Recent>() {
                        public int compare(Model_Recent o1, Model_Recent o2) {
                            return Utility.longToDate(o2.getDateToSort()).compareTo(Utility.longToDate(o1.getDateToSort()));

                        }
                    });
                }
                else if(action.equalsIgnoreCase("Size"))
                {
                    Collections.sort(RecentList, new Comparator<Model_Recent>()
                    {
                        public int compare(Model_Recent o1, Model_Recent o2) {

                            return (int) (o2.getFileSizeCmpr() - o1.getFileSizeCmpr());

                        }
                    });
                }
                else if(action.equalsIgnoreCase("Type"))
                {

                    Collections.sort(RecentList, new Comparator<Model_Recent>() {
                        public int compare(Model_Recent o1, Model_Recent o2) {
                            return o2.getFileType().compareToIgnoreCase(o1.getFileType());

                        }
                    });

                }

                System.out.print(""+RecentList);
                refreshAdapter();

            }
        });
        dialog.setNegativeButton(ctx.getResources().getString(R.string.ascending), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                setLastSelectedCheckBoxFlag(action);

                if(action.equalsIgnoreCase("Name"))
                {
                    Collections.sort(RecentList, new Comparator<Model_Recent>() {
                        public int compare(Model_Recent o1, Model_Recent o2) {
                            return o1.getFileName().compareToIgnoreCase(o2.getFileName());

                        }
                    });


                }
                else if(action.equalsIgnoreCase("Last"))
                {
                    Collections.sort(RecentList, new Comparator<Model_Recent>() {
                        public int compare(Model_Recent o1, Model_Recent o2) {
                            return Utility.longToDate(o1.getDateToSort()).compareTo(Utility.longToDate(o2.getDateToSort()));

                        }
                    });
                }
                else if(action.equalsIgnoreCase("Size"))
                {


                    Collections.sort(RecentList, new Comparator<Model_Recent>()
                    {
                        public int compare(Model_Recent o1, Model_Recent o2) {

                            return (int) (o1.getFileSizeCmpr() - o2.getFileSizeCmpr());

                        }
                    });


                }
                else if(action.equalsIgnoreCase("Type"))
                {
                    Collections.sort(RecentList, new Comparator<Model_Recent>() {
                        public int compare(Model_Recent o1, Model_Recent o2) {
                            return o1.getFileType().compareToIgnoreCase(o2.getFileType());

                        }
                    });
                }
                System.out.print(""+RecentList);
                refreshAdapter();
            }
        });
        dialog.show();
    }
    private void setLastSelectedCheckBoxFlag(String action) {

        if(action.equalsIgnoreCase("Name"))
        {
            lastCheckedSortOptions=0;
            this.action=action;
        }
        else if(action.equalsIgnoreCase("Last"))
        {
            lastCheckedSortOptions=1;
            this.action=action;
        }
        else if(action.equalsIgnoreCase("Size"))
        {
            lastCheckedSortOptions=2;
            this.action=action;
        }
        else  if(action.equalsIgnoreCase("Type"))
        {
            lastCheckedSortOptions=3;
            this.action=action;
        }
    }



    @Override
    public void onBackPressed() {



        if(isSearchModeActive)
        {
            if(searchView !=null)
            {
                Utility.hideKeyboard(RecentActivityRe.this);
                isSearchModeActive=false;
                resetAdapter();
                searchView.onActionViewCollapsed();

                sortView.setVisible(true);
            }

            return;
        }
        else
            super.onBackPressed();

        AsyncTask.Status st=new AsynctaskUtility<Model_Recent>(mcontext,this,RECENTFILES).getStatus();
        String str=st.name();
        System.out.println(""+str);

        if(new AsynctaskUtility<Model_Recent>(mcontext,this,RECENTFILES).getStatus()== AsyncTask.Status.PENDING || new AsynctaskUtility<Model_Recent>(mcontext,this,RECENTFILES).getStatus()== AsyncTask.Status.RUNNING)
        {
            boolean taskStopped=  new AsynctaskUtility<Model_Recent>(mcontext,this,RECENTFILES).cancel(true);
            System.out.println(""+taskStopped);
        }
        // AddMobUtils addutil= new AddMobUtils();
        // addutil.showInterstitial(ctx);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /******************************************/

    public void  resetAdapter(){

        multiSelectAdapter = new MultiSelectAdapter_Recent(this, RecentList, multiselect_list, this);
        recyclerView.setAdapter(multiSelectAdapter);

    }


}
