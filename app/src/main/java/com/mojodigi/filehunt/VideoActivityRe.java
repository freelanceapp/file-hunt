package com.mojodigi.filehunt;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
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
import com.mojodigi.filehunt.Adapter.MultiSelectAdapter_Video;
import com.mojodigi.filehunt.AddsUtility.AddConstants;
import com.mojodigi.filehunt.AddsUtility.AddMobUtils;
import com.mojodigi.filehunt.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.filehunt.AsyncTasks.encryptAsyncTask;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Model.Grid_Model;
import com.mojodigi.filehunt.Utils.AlertDialogHelper;
import com.mojodigi.filehunt.Utils.AutoFitGridLayoutManager;
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
import java.util.Collections;
import java.util.Comparator;

//


public class VideoActivityRe extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener, MultiSelectAdapter_Video.VdoListener,AdListenerInterface,encryptAsyncTask.EncryptListener {

    ActionMode mActionMode;
    Menu context_menu;


    RecyclerView recyclerView;
    MultiSelectAdapter_Video multiSelectAdapter;
    boolean isMultiSelect = false;

    ArrayList<Grid_Model> vidioList = new ArrayList<>();
    ArrayList<Grid_Model> multiselect_list = new ArrayList<>();

    AlertDialogHelper alertDialogHelper;
    int int_position;
    ArrayList<String> Intent_Video_List;
    ArrayList<String> thumbList;
    ArrayList<String> durationList;
    Context mcontext;
    private SearchView searchView;
    ImageView blankIndicator;
    private boolean isUnseleAllEnabled = false;
    private Grid_Model fileTorename;
    private int renamePosition;
    public static VideoActivityRe instance;
    private AdView mAdView;
    private int lastCheckedSortOptions;

    SharedPreferenceUtil addprefs;
    View adContainer;
    RelativeLayout smaaToAddContainer;
    //smaatoAddBanerView
    BannerView smaaTobannerView;
    private int cnt;

    private boolean isSearchModeActive;


    MenuItem sortView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity_re);
              Constants.ACTIVITY_TRACKER= Constants.ACTIVITY_ENUM.ACTIVITY_VIDEO;
        //  banner add
        //add netwrk varibales

        mcontext = VideoActivityRe.this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utility.setstatusBarColorBelowM(VideoActivityRe.this);
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
                adutil.dispFacebookBannerAdd(mcontext,addprefs , VideoActivityRe.this);
            }

        }
        else {
            adutil.displayLocalBannerAdd(mAdView);
        }




        // banner add


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        blankIndicator = (ImageView) findViewById(R.id.blankIndicator);
        mcontext = VideoActivityRe.this;
        instance = this;
        UtilityStorage.InitilaizePrefs(mcontext);

        try {
            //int_position = getIntent().getIntExtra("value", 0);
            if(Constants.model!=null) {
                String tittle = Constants.model.getStr_folder();
                Utility.setActivityTitle2(mcontext, tittle);
                Intent_Video_List = Constants.model.getAl_imagepath();
                thumbList = Constants.model.getAl_vdoThumb();// this list  will be part  of  Intent_Video_List using setter getter
                durationList = Constants.model.getAlVdoDuration();

                if (Intent_Video_List != null) {

                    cnt=Intent_Video_List.size();
                    new dataLoadAsync().execute();
                }
            }
            //data_load();
        }catch (Exception e)
        {
            e.printStackTrace();
        }


//        alertDialogHelper = new AlertDialogHelper(this);
//
//
//        if (Intent_Video_List.size() != 0) {
//
//            multiSelectAdapter = new MultiSelectAdapter_Video(this, vidioList, multiselect_list, this);
//            // AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, (int)Utility.px2dip(mcontext,150.0f));  // did not work on high resolution phones
//
//
//            //set the width of column 20 %  of width of screen
//            int columnWidthPercent = (getScreenWidth() * 20) / 100;
//            AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, columnWidthPercent);
//            recyclerView.setLayoutManager(layoutManager);
//            //set the width of column 20 %  of width of screen
////        //set the number of columns as  per  width of screen
////         int columnCount=getScreenWidth()/100;
////        System.out.println(""+columnCount);
////        recyclerView.setLayoutManager(new GridLayoutManager(this, columnCount));
////        //set the number of columns as  per  width of screen
//
//
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
//            recyclerView.setAdapter(multiSelectAdapter);
//        } else {
//            blankIndicator.setVisibility(View.VISIBLE);
//        }
//
//
//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                if (isMultiSelect && position != RecyclerView.NO_POSITION)
//                    multi_select(position);
//
//                else {
//                    //send the file to player
////                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Category_Explore_Activity.al_images.get(int_position).getAl_imagepath().get(position)));
////                    intent.setDataAndType(Uri.parse(Category_Explore_Activity.al_images.get(int_position).getAl_imagepath().get(position)), "video/*");
////                    startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//                if (!isMultiSelect) {
//                    multiselect_list = new ArrayList<Grid_Model>();
//                    isMultiSelect = true;
//
//                    if (mActionMode == null) {
//                        mActionMode = startActionMode(mActionModeCallback);
//                    }
//                }
//
//                multi_select(position);
//
//            }
//        }));




    }

    @Override
    protected void onResume() {
        super.onResume();
        Utility.log_FirebaseActivity_Events(VideoActivityRe.this,"VideosActivity");
    }


    @Override
    protected void onStart() {
        super.onStart();
        // AddMobUtils addutil= new AddMobUtils();
        // addutil.showInterstitial(mcontext);

    }


    @Override
    protected void onRestart() {
        super.onRestart();


        if(mActionMode !=null)
        {
            mActionMode.finish();

        }

        if(multiSelectAdapter !=null)
        {
            multiSelectAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.FILE_DELETE_REQUEST_CODE) {
            boolean isPersistUriSet = UtilityStorage.setUriForStorage(requestCode, resultCode, data);
            if (isPersistUriSet && multiselect_list.size() > 0)
                new DeleteFileTask(multiselect_list).execute();
        }
        if (requestCode == Constants.FILE_RENAME_REQUEST_CODE) {
            boolean isPersistUriSet = UtilityStorage.setUriForStorage(requestCode, resultCode, data);
            if (isPersistUriSet && multiselect_list.size() == 1) {
                // call rename function  here in the case of premission granted first  time;

                Utility.renameFile(mcontext, multiselect_list.get(0).getImgPath(), Constants.Global_File_Rename_NewName, 1);

            }

        }


    }

    public static VideoActivityRe getInstance() {
        return instance;
    }

    public void refreshAdapterAfterRename(String newPath, String newName) {

        // finish  acrtion mode aftr  rename  file is  done
        if(mActionMode!=null) {
            mActionMode.finish();
        }

        fileTorename.setImgPath(newPath);
        fileTorename.setImgBitmap(newPath);
        fileTorename.setFileName(newName);
        vidioList.set(renamePosition, fileTorename);
        refreshAdapter();

    }
    public boolean checkForFileExist(String newFPath)
    {

        for(int i=0;i<vidioList.size();i++)
        {
            String listFile=vidioList.get(i).getImgPath().toString();
            boolean status=listFile.equalsIgnoreCase(newFPath);
            if(status)
                return true;

        }

        return  false;

    }


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

        Utility.setCustomizeSeachBar(mcontext, searchView);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                if (multiSelectAdapter != null)
                    multiSelectAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if (multiSelectAdapter != null)
                    multiSelectAdapter.getFilter().filter(query);
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
                Utility.hideKeyboard(VideoActivityRe.this);
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
                Utility.showKeyboard(VideoActivityRe.this);
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

    @Override
    public void onEncryptSuccessful() {


        // remove  the  file from the lsit  and refresh the adapte and finish  action mode;
        if(multiselect_list.size()>0)
        {
            for(int i=0;i<multiselect_list.size();i++)
            {
                vidioList.remove(multiselect_list.get(i));
            }
        }
        multiselect_list.clear();
        multiSelectAdapter.notifyDataSetChanged();
        if(mActionMode !=null)
            mActionMode.finish();

        // setting this  to  >0 calls  refresh  the  video  on  category_explore_activity;
        Constants.DELETED_VDO_FILES=1;
    }

    private  class dataLoadAsync extends  AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

                CustomProgressDialog.show(mcontext, mcontext.getResources().getString(R.string.loading_msg));

        }

        @Override
        protected Void doInBackground(Void... voids) {

            data_load();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initView();

            CustomProgressDialog.dismiss();


//            if (cnt > 100) {
//                try {
//                    Thread.sleep(3000);
//                    CustomProgressDialog.dismiss();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }

        }
    }
    
    private  void initView()
    {
        alertDialogHelper = new AlertDialogHelper(this);


        if (Intent_Video_List.size() != 0) {

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            multiSelectAdapter = new MultiSelectAdapter_Video(this, vidioList, multiselect_list, this);
            recyclerView.setAdapter(multiSelectAdapter);

        } else {
            blankIndicator.setVisibility(View.VISIBLE);
        }


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect && position != RecyclerView.NO_POSITION)
                    multi_select(position);

                else {
                    //send the file to player
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Category_Explore_Activity.al_images.get(int_position).getAl_imagepath().get(position)));
//                    intent.setDataAndType(Uri.parse(Category_Explore_Activity.al_images.get(int_position).getAl_imagepath().get(position)), "video/*");
//                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<Grid_Model>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);

                        Utility.hideKeyboard(VideoActivityRe.this);
                        isSearchModeActive = false;
                        searchView.onActionViewCollapsed();

                        sortView.setVisible(true);
                    }
                }

                multi_select(position);

            }
        }));

    }
    public void data_load() {

        for (int i = 0; i < Intent_Video_List.size(); i++)
        {
            Grid_Model gridImg = new Grid_Model();
            try
            {
                File f=new File(Intent_Video_List.get(i));
                gridImg.setDateToSort(f.lastModified());
                gridImg.setFileName(f.getName());
                gridImg.setFileSizeCmpr(f.length());
                gridImg.setFileType(Utility.getFileExtensionfromPath(f.getAbsolutePath()));
            }
            catch (Exception e){}

            gridImg.setImgPath(Intent_Video_List.get(i));
            if(i<thumbList.size())
                gridImg.setImgBitmap(thumbList.get(i));
            if(i<durationList.size())
                gridImg.setVdoDuration(durationList.get(i));
            vidioList.add(gridImg);
        }
    }


    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(vidioList.get(position)))
                multiselect_list.remove(vidioList.get(position));
            else {
                multiselect_list.add(vidioList.get(position));
                // to  rename file contain old file;
                if (multiselect_list.size() == 1) {
                    fileTorename = vidioList.get(position);
                    renamePosition = position;
                }
                // to  rename file contain old file;

            }

            if (multiselect_list.size() > 0) {
                mActionMode.setTitle("" + multiselect_list.size());
                //keep  the reference of file to  be renamed
                if (vidioList.contains(multiselect_list.get(0))) {
                    renamePosition = vidioList.indexOf(multiselect_list.get(0));
                    fileTorename = multiselect_list.get(0);
                }
                //keep  the reference of file to  be renamed
            }
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }


    public void refreshAdapter() {

        if(multiSelectAdapter !=null) {

            multiSelectAdapter.selected_VdoList = multiselect_list;
            multiSelectAdapter.VdoList = vidioList;
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

    private void DispDetailsDialog(Grid_Model fileProperty) {

        if (fileProperty.getImgPath() != null) {
            File f = new File(fileProperty.getImgPath());
            String[] splitPath = fileProperty.getImgPath().split("/");
            String fName = splitPath[splitPath.length - 1];

            Dialog dialog = new Dialog(VideoActivityRe.this);
            dialog.setContentView(R.layout.dialog_file_property);
            // Set dialog title

            TextView FileName = dialog.findViewById(R.id.FileName);
            TextView FilePath = dialog.findViewById(R.id.FilePath);
            TextView FileSize = dialog.findViewById(R.id.FileSize);
            TextView FileDate = dialog.findViewById(R.id.FileDate);
            TextView Resolution = dialog.findViewById(R.id.Resolution);
            TextView resltxt = dialog.findViewById(R.id.resltxt);
            TextView Oreintation = dialog.findViewById(R.id.ort);
            TextView oreinttxt = dialog.findViewById(R.id.oreinttxt);
            Oreintation.setVisibility(View.GONE);
            oreinttxt.setVisibility(View.GONE);
            resltxt.setText("Duration");


            FileName.setText(fName);
            FilePath.setText(fileProperty.getImgPath());
            FileSize.setText(Utility.humanReadableByteCount(f.length(), true));
            FileDate.setText(Utility.LongToDate((f.lastModified())));
            Resolution.setText(fileProperty.getVdoDuration());

            // Oreintation.setText("06:00");
            // Oreintation.setText(String.valueOf(Utility.getOrintatin(f))+"");

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


                case R.id.action_hide:
                    if(Utility.isManualPasswordSet()) {
                        if (multiselect_list.size() >= 1) {


                            if (Utility.createOrFindAppDirectory(Constants.MEDIA_TYPE_VDO))
                            {
                                File[] f = new File[multiselect_list.size()];
                                for (int i = 0; i < multiselect_list.size(); i++) {
                                    File file = new File(multiselect_list.get(i).getImgPath());
                                    f[i] = file;
                                }
                                if (f.length >= 1)
                                    new encryptAsyncTask(mcontext, f, Constants.encryptionPassword,Constants.MEDIA_TYPE_VDO,instance).execute();
                                else
                                    Utility.dispToast(mcontext, getResources().getString(R.string.filenotfound));
                            }

                            else
                            {
                                Utility.dispToast(mcontext,getResources().getString(R.string.directorynotfound));
                            }



                        }
                    }
                    else {
                        Intent i = new Intent(mcontext,LockerPasswordActivity.class);
                        startActivity(i);
                    }
                    return  true;

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
                            String fPath=multiselect_list.get(0).getImgPath().toString();
                            System.out.println(""+fPath);
                            if(!Constants.filesToCopy.contains(multiselect_list.get(i).getImgPath())) {
                                Constants.filesToCopy.add(multiselect_list.get(i).getImgPath().toString());
                            }
                        }
                        if(Constants.filesToCopy.size()>=1) {
                            Utility.dispLocalStorages(mcontext,1);
                        }




                    }
                    return true ;

                case R.id.action_rename:
                    if (multiselect_list.size() == 1)
                        Utility.fileRenameDialog(mcontext, multiselect_list.get(0).getImgPath(), Constants.VIDEO,false);
                    return true;
                case R.id.action_delete:
                    if(multiselect_list.size()>=1) {
                        int mFileCount = multiselect_list.size();
                        String msgDeleteFile = mFileCount > 1 ? mFileCount + " " + getResources().getString(R.string.delfiles) : mFileCount + " " + getResources().getString(R.string.delfile);
                       // alertDialogHelper.showAlertDialog("", "Delete Video"+" ("+msgDeleteFile+")", "DELETE", "CANCEL", 1, true);

                        alertDialogHelper.showAlertDialog("", getResources().getString(R.string.delete_file_msgs)+" ("+msgDeleteFile+")", getResources().getString(R.string.menu_item_delete), getResources().getString(R.string.cancel), 1, true);
                    }
                    return true;
                case R.id.action_select:
                    if (vidioList.size() == multiselect_list.size() || isUnseleAllEnabled == true)
                        unSelectAll();
                    else
                        selectAll();
                    return true;
                case R.id.action_Share:
                    shareMultipleVideo();
                    return true;
                case R.id.action_details:
                    if (multiselect_list.size() == 1) {
                        DispDetailsDialog(multiselect_list.get(0));
                    } else {
                        String size = calcSelectFileSize(multiselect_list);
                        System.out.println("" + size);
                        if (size != null)
                            Utility.multiFileDetailsDlg(mcontext, size, multiselect_list.size());
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
            multiselect_list = new ArrayList<Grid_Model>();

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
        if (from == 1) {
            if (multiselect_list.size() > 0) {

                //
                File f = new File(multiselect_list.get(0).getImgPath());

                if (UtilityStorage.isWritableNormalOrSaf(f, mcontext)) {
                    new DeleteFileTask(multiselect_list).execute();
                } else {
                    UtilityStorage.guideDialogForLEXA(mcontext, f.getParent(), Constants.FILE_DELETE_REQUEST_CODE);
                }

                //

                // now this task  is being done  on post execute of delete task


//                for(int i=0;i<multiselect_list.size();i++)
//                    vidioList.remove(multiselect_list.get(i));
//
//                multiSelectAdapter.notifyDataSetChanged();
//
//                if (mActionMode != null) {
//                    mActionMode.finish();
//                }

            }
        } else if (from == 2) {
            if (mActionMode != null) {
                mActionMode.finish();
            }

            //this yet to be implemented
//            Grid_Model mImg = new Grid_Model();
//            mImg.setImgPath("");
//            vidioList.add(mImg);
            multiSelectAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }

    @Override
    public void onReceiveAd(AdDownloaderInterface adDownloaderInterface, ReceivedBannerInterface receivedBannerInterface) {
        if(receivedBannerInterface.getErrorCode() != ErrorCode.NO_ERROR){
            //Toast.makeText(getBaseContext(), receivedBannerInterface.getErrorMessage(), Toast.LENGTH_SHORT).show();
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

    private class DeleteFileTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomProgressDialog.show(mcontext, getResources().getString(R.string.deleting_file));
        }

        ArrayList<Grid_Model> multiselect_list;

        DeleteFileTask(ArrayList<Grid_Model> multiselect_list) {
            this.multiselect_list = multiselect_list;
        }


        @Override
        protected Integer doInBackground(Void... voids) {
            return deleteFile(multiselect_list);
        }

        @Override
        protected void onPostExecute(Integer FileCount) {
            super.onPostExecute(FileCount);

            if (FileCount > 0) {
                for (int i = 0; i < multiselect_list.size(); i++) {
                    vidioList.remove(multiselect_list.get(i));
                    Utility.removeFileFromCopyList(multiselect_list.get(i).getImgPath());
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

    private void selectAll() {
        if (mActionMode != null) {
            multiselect_list.clear();

            for (int i = 0; i < vidioList.size(); i++) {
                if (!multiselect_list.contains(multiselect_list.contains(vidioList.get(i)))) {
                    multiselect_list.add(vidioList.get(i));
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

    private void unSelectAll() {
        if (mActionMode != null) {
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

    private void selectMenuChnage() {
        if (context_menu != null) {
            if (vidioList.size() == multiselect_list.size()) {
                for (int i = 0; i < context_menu.size(); i++) {
                    MenuItem item = context_menu.getItem(i);
                    if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.menu_selectAll))) {
                        item.setTitle(getResources().getString(R.string.menu_unselectAll));
                        isUnseleAllEnabled = true;
                    }
                }
            } else {

                for (int i = 0; i < context_menu.size(); i++) {
                    MenuItem item = context_menu.getItem(i);
                    if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.menu_unselectAll))) {
                        item.setTitle(getResources().getString(R.string.menu_selectAll));
                        isUnseleAllEnabled = false;
                    }
                }

            }

            // rename  options will be visible if only i file is selected

            MenuItem item = context_menu.findItem(R.id.action_rename);
            if (multiselect_list.size() == 1)
                item.setVisible(true);
            else
                item.setVisible(false);

            // rename  options will be visible if only i file is selected

        }
        invalidateOptionsMenu();
    }

    private int deleteFile(ArrayList<Grid_Model> delete_list) {
        int count = 0;

        for (int i = 0; i < delete_list.size(); i++) {
            // File f=new File(String.valueOf(delete_list.get(i).getImgPath().toLowerCase()));
            File f = new File(String.valueOf(delete_list.get(i).getImgPath()));
            if (f.exists()) {
                if (f.delete()) {
                    count++;
                    sendBroadcast(f);
                }
                // if normal methos fails to  delete data
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
            }
            //new
            // if normal methos fails to  delete data

        }
        Constants.DELETED_VDO_FILES = count;

        return count;
    }

    private void sendBroadcast(File outputFile) {
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

    private void shareMultipleVideo() {

        if (multiselect_list.size() > 0) {

            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            sharingIntent.setType("video/*");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                ArrayList<Uri> files = new ArrayList<Uri>();

                for (int i = 0; i < multiselect_list.size(); i++) {
                    File file = new File(multiselect_list.get(i).getImgPath());
                    Uri uri = Uri.fromFile(file);
                    files.add(uri);
                }
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                startActivity(sharingIntent);
            } else {
                ArrayList<Uri> files = new ArrayList<Uri>();

                for (int i = 0; i < multiselect_list.size(); i++) {
                    File file = new File(multiselect_list.get(i).getImgPath());
                    Uri uri = FileProvider.getUriForFile(mcontext, getResources().getString(R.string.file_provider_authority), file);
                    files.add(uri);
                }
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(sharingIntent);

            }
            Utility.shareTracker("Video", "Video shared");
        } else {
            //Toast.makeText(mcontext, "No files to share", Toast.LENGTH_SHORT).show();
            Utility.dispToast(mcontext, getResources().getString(R.string.nofile));
        }

    }

    private int getScreenWidth() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        System.out.println(width);
        return width;
    }

    @Override
    public void onVdoSelected(Grid_Model vdoModel) {


        // //function not being used can be deleted as  this code  is a part  of utility  now


        // send the file to player
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(vdoModel.getImgBitmapStr()));
//                    intent.setDataAndType(Uri.parse(vdoModel.getImgBitmapStr()), "video/*");
//                    startActivity(intent);


        // Utility.OpenFile(mcontext,model_apk.getFilePath()); // open file below  Android N
        if (mActionMode == null) {
              // this  line used to  open with sstem apps
            //  Utility.OpenFileWithNoughtAndAll(vdoModel.getImgBitmapStr(), mcontext, getResources().getString(R.string.file_provider_authority));

            // this will  open in app Video;

            if(vdoModel.getImgBitmapStr()!=null) {
                addprefs.setIntValue("position", 0);
                Intent intentVideoGallary = new Intent(mcontext, Media_VdoActivity.class);
                intentVideoGallary.putExtra(Constants.selectedVdo, vdoModel.getImgBitmapStr());
                intentVideoGallary.putExtra(Constants.MEDIA_DELETE_ACTIVITY_TRACKER, Constants.VIDEO);
                startActivity(intentVideoGallary);
            }
            else {
                Utility.dispToast(mcontext, getResources().getString(R.string.play_error));
            }

        }
    }

    public String calcSelectFileSize(ArrayList<Grid_Model> fileList) {
        long totalSize = 0;

        for (int i = 0; i < fileList.size(); i++) {
            Grid_Model m = fileList.get(i);
            File f = new File(m.getImgPath());
            totalSize += f.length();
        }

        return Utility.humanReadableByteCount(totalSize, true);
    }

    String action="Name";
    public  void sortDialog(final Context ctx)
    {
        //action="Name";
        System.out.print(""+vidioList);
        android.support.v7.app.AlertDialog.Builder  dialog=new android.support.v7.app.AlertDialog.Builder(ctx) ;
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.dialog_sort, null);
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
        else  if(lastCheckedSortOptions==3)
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
                    //Toast.makeText(ctx, action, Toast.LENGTH_SHORT).show();


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
                    Collections.sort(vidioList, new Comparator<Grid_Model>() {
                        public int compare(Grid_Model o1, Grid_Model o2) {
                            return o2.getFileName().compareToIgnoreCase(o1.getFileName());

                        }
                    });
                }
                else if(action.equalsIgnoreCase("Last"))
                {
                    Collections.sort(vidioList, new Comparator<Grid_Model>() {
                        public int compare(Grid_Model o1, Grid_Model o2) {
                            return Utility.longToDate(o2.getDateToSort()).compareTo(Utility.longToDate(o1.getDateToSort()));

                        }
                    });
                }
                else if(action.equalsIgnoreCase("Size"))
                {
                    Collections.sort(vidioList, new Comparator<Grid_Model>()
                    {
                        public int compare(Grid_Model o1, Grid_Model o2) {

                            return (int) (o2.getFileSizeCmpr() - o1.getFileSizeCmpr());

                        }
                    });
                }
                else if(action.equalsIgnoreCase("Type"))
                {

                    Collections.sort(vidioList, new Comparator<Grid_Model>() {
                        public int compare(Grid_Model o1, Grid_Model o2) {
                            return o2.getFileType().compareToIgnoreCase(o1.getFileType());

                        }
                    });

                }

                System.out.print(""+vidioList);
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
                    Collections.sort(vidioList, new Comparator<Grid_Model>() {
                        public int compare(Grid_Model o1, Grid_Model o2) {
                            return o1.getFileName().compareToIgnoreCase(o2.getFileName());

                        }
                    });


                }
                else if(action.equalsIgnoreCase("Last"))
                {
                    Collections.sort(vidioList, new Comparator<Grid_Model>() {
                        public int compare(Grid_Model o1, Grid_Model o2) {
                            return Utility.longToDate(o1.getDateToSort()).compareTo(Utility.longToDate(o2.getDateToSort()));

                        }
                    });
                }
                else if(action.equalsIgnoreCase("Size"))
                {


                    Collections.sort(vidioList, new Comparator<Grid_Model>()
                    {
                        public int compare(Grid_Model o1, Grid_Model o2) {

                            return (int) (o1.getFileSizeCmpr() - o2.getFileSizeCmpr());

                        }
                    });


                }
                else if(action.equalsIgnoreCase("Type"))
                {
                    Collections.sort(vidioList, new Comparator<Grid_Model>() {
                        public int compare(Grid_Model o1, Grid_Model o2) {
                            return o1.getFileType().compareToIgnoreCase(o2.getFileType());

                        }
                    });
                }


                System.out.print(""+vidioList);
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
        else if(action.equalsIgnoreCase("Type"))
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
                Utility.hideKeyboard(VideoActivityRe.this);
                isSearchModeActive=false;
                resetAdapter();
                searchView.onActionViewCollapsed();

                sortView.setVisible(true);
            }

            return;
        }
        else
            super.onBackPressed();
        // AddMobUtils addutil= new AddMobUtils();
        // addutil.showInterstitial(ctx);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    public void  resetAdapter(){
        multiSelectAdapter = new MultiSelectAdapter_Video(this, vidioList, multiselect_list, this);
        recyclerView.setAdapter(multiSelectAdapter);

    }
}
