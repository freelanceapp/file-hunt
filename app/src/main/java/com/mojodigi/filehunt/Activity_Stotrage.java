package com.mojodigi.filehunt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.mojodigi.filehunt.Adapter.Adapter_Storage;
import com.mojodigi.filehunt.AddsUtility.AddConstants;
import com.mojodigi.filehunt.AddsUtility.AddMobUtils;
import com.mojodigi.filehunt.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.filehunt.AsyncTasks.copyAsyncTask;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Model.Grid_Model;
import com.mojodigi.filehunt.Model.Model_Storage;
import com.mojodigi.filehunt.Utils.AlertDialogHelper;
import com.mojodigi.filehunt.Utils.CustomProgressDialog;
import com.mojodigi.filehunt.Utils.RecyclerItemClickListener;
import com.mojodigi.filehunt.Utils.Utility;
import com.mojodigi.filehunt.Utils.UtilityStorage;
import com.smaato.soma.AdDownloaderInterface;
import com.smaato.soma.AdListenerInterface;
import com.smaato.soma.BannerView;
import com.smaato.soma.ErrorCode;
import com.smaato.soma.ReceivedBannerInterface;
import com.smaato.soma.internal.utilities.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.mojodigi.filehunt.Utils.Utility.isShowHiddenFiles;

public class Activity_Stotrage extends AppCompatActivity implements Adapter_Storage.ItemListener , copyAsyncTask.AsyncResponse , AlertDialogHelper.AlertDialogListener ,AdListenerInterface {

    private   File path = new File(Environment.getExternalStorageDirectory() + "");
    private   File initial_path = new File(Environment.getExternalStorageDirectory() + "");
    private   File previous_path = new File(Environment.getExternalStorageDirectory() + "");

     // variables for sdcard
     // private   File path_sdcard;
     // private   File initial_path;
    //  private   File previous_path ;


    // variables for sdcard
    static ArrayList<String> str = new ArrayList<String>();
    static ArrayList<File> pathList=new ArrayList<>();
    private static final String TAG = "F_PATH";
    private Model_Storage[] fileList;
    public ArrayList<Model_Storage> folderList;  // contains the final data
    private ArrayList<Model_Storage> fileList_root;  // constans all files and folder after sorting;
    private ArrayList<Model_Storage> multiselect_list=new ArrayList<>();

    ImageView blankIndicator;
    private Boolean firstLvl = true;
    RecyclerView recyclerView;
    Context mContext;
    Adapter_Storage multiSelectAdapter;
    TextView currentPath;
    private String sdCardPath;

    TextView internal_txt,sdcard_txt;
    RelativeLayout sdcard_change,internal_change;
    CardView storage_Layout;

    // actionMode vars
    ActionMode mActionMode;
    boolean isMultiSelect = false;
    Menu context_menu;
    boolean isUnseleAllEnabled = false;
    AlertDialogHelper alertDialogHelper;
    // actionMode vars
    //fab variables

    private boolean fabExpanded = false;
    private FloatingActionButton fab;
    //Linear layout holding the Save submenu
    private LinearLayout layoutFabSave;

    //fab variables

    // rename  vars
    int TYPE_STORAGE=8;
    private int renamePosition;
    private static Model_Storage fileTorename;
    private boolean isPastingInInterNal=true;
    private String storageType;
    private File folderPath;

    Toolbar toolbarStorage;
    static Activity_Stotrage instance;

    // addvars
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;
    private int lastCheckedSortOptions;
    private SharedPreferenceUtil addprefs;
    View adContainer;
    RelativeLayout smaaToAddContainer;
    BannerView smaaTobannerView;
    private Model_Storage glob_model;

    Button pasteButton;
    RelativeLayout pastelayout;

    // addvars
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage2);
        instance=this;
        toolbarStorage = (Toolbar) findViewById(R.id.toolbarStorage);
        setSupportActionBar(toolbarStorage);

//        addprefs = new SharedPreferenceUtil(this);
//        if(addprefs!=null){
//          //  getSupportActionBar().setTitle(addprefs.getStringValue(Constants.storageType , ""));
//          //  getSupportActionBar().setTitle(addprefs.getStringValue(Constants.storageType , ""));
//        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setOverflowIconColor(R.color.titleColor);
        mContext = Activity_Stotrage.this;
        initActivityViews();


        //  banner add
        //add netwrk varibales

        mAdView = (AdView) findViewById(R.id.adView);
        adContainer = findViewById(R.id.adMobView);
        smaaToAddContainer = findViewById(R.id.smaaToAddContainer);
        smaaToAddContainer.setVisibility(View.GONE);

        //smaaTobannerView =  findViewById(R.id.smaaTobannerView);

        smaaTobannerView = new BannerView((this).getApplication());
        smaaTobannerView.addAdListener(this);

        addprefs = new SharedPreferenceUtil(mContext);

        AddMobUtils adutil = new AddMobUtils();

        if(AddConstants.checkIsOnline(mContext) && adContainer !=null && addprefs !=null)
        {
            String AddPrioverId=addprefs.getStringValue(AddConstants.ADD_PROVIDER_ID, AddConstants.NOT_FOUND);
            if(AddPrioverId.equalsIgnoreCase(AddConstants.Adsense_Admob_GooglePrivideId))
                adutil.displayServerBannerAdd(addprefs,adContainer , mContext);
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
                 adutil.dispFacebookBannerAdd(mContext,addprefs , Activity_Stotrage.this);
            }

        }
        else {
            adutil.displayLocalBannerAdd(mAdView);
        }


        //  banner add
    }


    private void setOverflowIconColor(int color) {
        Drawable overflowIcon = toolbarStorage.getOverflowIcon();
        Drawable backArrow=toolbarStorage.getNavigationIcon();
        if(backArrow !=null)
        {
            backArrow.setColorFilter(getResources().getColor(R.color.titleColor), PorterDuff.Mode.SRC_ATOP);
            toolbarStorage.setNavigationIcon(backArrow);
        }

        if (overflowIcon != null) {
            //Drawable newIcon = overflowIcon.mutate();
            // newIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            //overflowIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
           // toolbarStorage.setOverflowIcon(overflowIcon);
        }
    }


    private void initActivityViews() {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utility.setstatusBarColorBelowM(Activity_Stotrage.this);
        }

        UtilityStorage.InitilaizePrefs(mContext);



        pasteButton=findViewById(R.id.pasteButton);
        pastelayout=findViewById(R.id.pastelayout);



        alertDialogHelper =new AlertDialogHelper(mContext,this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        blankIndicator = (ImageView) findViewById(R.id.blankIndicator);
        currentPath = (TextView) findViewById(R.id.currentPath);
        currentPath.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext.getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        //recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        folderList = new ArrayList<>();
        fileList_root = new ArrayList<>();





        pathList.clear();




        if (folderList.size() != 0) {
            multiSelectAdapter = new Adapter_Storage(mContext, folderList, multiselect_list, this);
            recyclerView.setAdapter(multiSelectAdapter);
        } else {
            blankIndicator.setVisibility(View.VISIBLE);
        }

        Bundle extrasIntent = getIntent().getExtras();
        if (extrasIntent != null) {
            storageType = extrasIntent.getString(Constants.storageType);
        }

        if(storageType!=null) {
            if (storageType.equalsIgnoreCase(Constants.sdCard)) {

                isPastingInInterNal = false;
                if (isSdcardPresent()) {
                    if (sdCardPath != null) {
                        path = new File(sdCardPath);
                        getSupportActionBar().setTitle(""+setActivityTitleForSdCard(sdCardPath));
                        if(toolbarStorage !=null)
                            changeToolbarFont(toolbarStorage, this);


                        initial_path = new File(sdCardPath);
                        previous_path = new File(sdCardPath);
                        pathList.clear();


                        setCurrentDispPath();
                        loadFileList();

                        if (folderList.size() == 0) {

                            blankIndicator.setVisibility(View.VISIBLE);
                        } else {
                            blankIndicator.setVisibility(View.GONE);
                            multiSelectAdapter = new Adapter_Storage(mContext, folderList, multiselect_list, this);
                            recyclerView.setAdapter(multiSelectAdapter);
                        }
                    }
                }


            }
        }
        if(storageType.equalsIgnoreCase(Constants.interNal))
        {
            isPastingInInterNal=true;
            getSupportActionBar().setTitle("Internal Storage");
            getSupportActionBar().setTitle(getResources().getString(R.string.internalstorage));

            if(toolbarStorage !=null)
            changeToolbarFont(toolbarStorage, this);


            path = new File(Environment.getExternalStorageDirectory() + "");
            initial_path = new File(Environment.getExternalStorageDirectory() + "");
            previous_path = new File(Environment.getExternalStorageDirectory() + "");
            pathList.clear();
            loadFileList();
            setCurrentDispPath();
            //multiSelectAdapter.notifyDataSetChanged();


            if (folderList.size() == 0) {

                blankIndicator.setVisibility(View.VISIBLE);
            } else {
                blankIndicator.setVisibility(View.GONE);
                multiSelectAdapter = new Adapter_Storage(mContext, folderList, multiselect_list, this);
                recyclerView.setAdapter(multiSelectAdapter);
            }


        }








        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (isMultiSelect) {
                    if(position!=RecyclerView.NO_POSITION)
                        multi_select(position);
                }

                else {


                }
            }

            @Override
            public void onItemLongClick(View view, int position) {


                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<Model_Storage>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                       // mActionMode = getActivity().startActionMode(mActionModeCallback);
                        mActionMode = Activity_Stotrage.this.startActionMode(mActionModeCallback);

                    }
                }

                multi_select(position);


            }
        }));


        pasteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pasteData();
            }
        });
        //delete task


    }
    public static void changeToolbarFont(Toolbar toolbar, Activity context) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    applyFont(tv, context);
                    break;
                }
            }
        }
    }
    public static void applyFont(TextView tv, Activity context) {
        tv.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(context));
    }


    private String setActivityTitleForSdCard(String sdCardPath) {

        String[] arr=sdCardPath.split("/");
        if(arr.length==3)
      // Utility.setActivityTitle(mContext, arr[2]);
        return  arr[2];
       else
           return  "SdCard";


    }

    private int statusBarColor;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;

            // hide the action_hide menu as no  file will be encrypted from storage ;
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
                    Utility.dispToast(mContext, getResources().getString(R.string.menu_item_move));
                    return true;
                case R.id.action_encrypt:
                    Utility.dispToast(mContext, "encrypt");
                    return true;

                case R.id.action_copy:
                    if (multiselect_list.size() > 0) {
                        for (int i = 0; i < multiselect_list.size(); i++) {
                            String fPath = multiselect_list.get(i).getFilePath().toString();
                            System.out.println("" + fPath);
                            if (!Constants.filesToCopy.contains(multiselect_list.get(i).getFilePath())) {
                                Constants.filesToCopy.add(multiselect_list.get(i).getFilePath().toString());
                            }
                        }

                        // as per discussion we are displaying both availabe storages always.
                        //

                        if(Constants.filesToCopy.size()>=1) {
                            if(isPastingInInterNal)
                                Utility.dispLocalStorages(mContext,1);
                            else
                                Utility.dispLocalStorages(mContext,1);

                        }

//                        if (Constants.filesToCopy.size() > 0) {
//                            finish_Action_Mode();
//                            updateVisibilityPasteMenu();
//                        }
                    }


                    return true;
                case R.id.action_rename:
                    Utility.fileRenameDialog(mContext, multiselect_list.get(0).getFilePath(), TYPE_STORAGE, false);

                    return true;

                case R.id.action_delete:
                    if(multiselect_list.size()>=1) {
                        int mFileCount = multiselect_list.size();
                        String msgDeleteFile = mFileCount > 1 ? mFileCount + " " + getResources().getString(R.string.delfiles) : mFileCount + " " + getResources().getString(R.string.delfile);
                        alertDialogHelper.showAlertDialog("", getResources().getString(R.string.delete_file_msgs)+" ("+msgDeleteFile+")", getResources().getString(R.string.menu_item_delete), getResources().getString(R.string.cancel), 1, true);
                    }
                    return true;
                case R.id.action_select:

                    if (folderList.size() == multiselect_list.size() || isUnseleAllEnabled == true)
                        unSelectAll();
                    else
                        selectAll();

                    return true;
                case R.id.action_Share:
                    shareApkMultipleFilesWithNoughatAndAll();
                    return true;
                case R.id.action_details:

                    if (multiselect_list.size() == 1)
                        DispDetailsDialog(multiselect_list.get(0));
                    else {
                        String size = calcSelectFileSize(multiselect_list);
                        System.out.println("" + size);
                        if (size != null)
                            Utility.multiFileDetailsDlg(mContext, size, multiselect_list.size());
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
            isUnseleAllEnabled=false;
            multiselect_list = new ArrayList<Model_Storage>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //return to "old" color of status bar
                getWindow().setStatusBarColor(statusBarColor);
            }

            refreshAdapter();
        }
    };
    public  void refreshAdapter()

    {
        if(folderList.size()!=0) {

            blankIndicator.setVisibility(View.GONE);
            multiSelectAdapter = new Adapter_Storage(mContext, folderList,multiselect_list, this);
            recyclerView.setAdapter(multiSelectAdapter);
        }
        else {
            blankIndicator.setVisibility(View.VISIBLE);
        }

    }

    public void refreshAdapterAfterRename(String newPath,String newName)
    {
        try {
            fileTorename.setFilePath(newPath);
            fileTorename.setFile(newName);
            folderList.set(renamePosition, fileTorename);
            finish_Action_Mode();
            refreshAdapter_ActionMode();
        }catch (Exception e)
        {
            String  string=e.getMessage();
              Log.d("AdapterAfterRename", ""+string);
        }

        //Utility.RunMediaScan(mContext, new File(fileTorename.getFilePath()));


    }
    public boolean checkForFileExist(String newFPath)
    {

        for(int i=0;i<folderList.size();i++)
        {
            String listFile=folderList.get(i).getFilePath().toString();
            boolean status=listFile.equalsIgnoreCase(newFPath);
            if(status)
                return true;

        }

        return  false;

    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(folderList.get(position))) {
                multiselect_list.remove(folderList.get(position));
                // if user removes selected file randomly  then it keeps track of last selected file for renaming
                if(multiselect_list.size()==1) {
                    fileTorename =multiselect_list.get(0);
                    int index=folderList.indexOf(fileTorename);
                    renamePosition = index;
                }
                // if user removes selected file randomly  then it keeps track of last selected file for renaming

            }
            else {
                multiselect_list.add(folderList.get(position));

                if(multiselect_list.size()==1) {
                    fileTorename = folderList.get(position);
                    renamePosition = position;
                }

            }

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter_ActionMode();

        }
    }

    private void refreshAdapter_ActionMode()
    {
        if(folderList.size()!=0) {

            blankIndicator.setVisibility(View.GONE);

        }
        else {
            blankIndicator.setVisibility(View.VISIBLE);
        }

        multiSelectAdapter.selected_ModelStorageList=multiselect_list;
        multiSelectAdapter.modelStorageList=folderList;
        multiSelectAdapter.notifyDataSetChanged();

        selectMenuChnage();

        //finish action mode when user deselect files one by one ;
        if(multiselect_list.size()==0) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
        }

    }
    private void selectMenuChnage()
    {
        if(context_menu!=null)
        {
            if(folderList.size()==multiselect_list.size()) {
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

            // rename  options will be visible if only 1 file is selected

            MenuItem item= context_menu.findItem(R.id.action_rename);
            if (multiselect_list.size()==1)
                item.setVisible(true);
            else
                item.setVisible(false);
            // rename  options will be visible if only 1 file is selected



            // will  hide the share option if  any directory is selected in list
            for(int i=0;i<multiselect_list.size();i++)
            {
                MenuItem share_item= context_menu.findItem(R.id.action_Share);
                if(multiselect_list.get(i).getisDirecoty())
                {
                    share_item.setVisible(false);
                    break;
                }
                else {

                    share_item.setVisible(true);

                }

            }


        }


        //getActivity().invalidateOptionsMenu();
   invalidateOptionsMenu();
    }
    public static Activity_Stotrage getInstance() {
        return instance;
    }
    public void rotateFabForward() {
        ViewCompat.animate(fab)
                .rotation(135.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(10.0F))
                .start();
    }

    public void rotateFabBackward() {
        ViewCompat.animate(fab)
                .rotation(0.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(10.0F))
                .start();
    }



    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutFabSave.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        fab.setImageResource(R.drawable.ic_close_black_24dp);
        fabExpanded = true;
    }
    public  void folderCreateDialog()
    {
        //https://github.com/sang89vh/easyfilemanager/blob/master/AmazeFileManagerSang89vhAdmob/src/main/java/com/mybox/filemanager/services/httpservice/FileUtil.java

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_folder_create);
        // Set dialog title

        TextView headertxt = dialog.findViewById(R.id.headertxt);
        final EditText Edit_FolderName = dialog.findViewById(R.id.Edit_FolderName);

        TextView View_crete = dialog.findViewById(R.id.View_crete);
        TextView View_cancel = dialog.findViewById(R.id.View_cancel);

        headertxt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        Edit_FolderName.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        View_cancel.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        View_crete.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));

        View_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        View_crete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utility.IsNotEmpty(Edit_FolderName)) {

                    if(isWhitespace(Edit_FolderName.getText().toString()))
                    {
                        Edit_FolderName.setError(mContext.getResources().getString(R.string.foldernamerequire));

                        return;
                    }

                    if(createFolder(Edit_FolderName.getText().toString())) {
                        loadFileList();
                        refreshAdapter();
                        Utility.dispToast(mContext, getResources().getString(R.string.foldercreatedmsg));
                    }
                    else {

                       // Utility.dispToast(mContext,"Error while creating folder" );
                    }

                    dialog.dismiss();
                } else {
                    Edit_FolderName.setError(mContext.getResources().getString(R.string.emty_error));
                }



            }
        });


        dialog.show();


    }
    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }


    private boolean createFolder(String folderName) {

        String curPath=currentPath.getText().toString();
        System.out.print(""+curPath);
        if(currentPath.getText().toString() !=null) {
            folderPath = new File(currentPath.getText().toString() + "/" + folderName);
            if (folderPath != null) {
                if (folderPath.exists()) {
                    //Toast.makeText(mContext, "Folder exists already", Toast.LENGTH_SHORT).show();
                    Utility.dispToast(mContext, getResources().getString(R.string.folder_exist));
                    return false;
                } else {
                    //create folder
                    //   /storage/0000-8E01/images/photo0021.jpg
                    String sr = folderPath.getParent();
                    File file = new File(sr);
                    boolean st = file.canWrite();
                    System.out.print("" + st);

                    if (file.canWrite()) {
                        boolean dstatus = folderPath.mkdir();
                        System.out.print("" + dstatus);
                        return dstatus;
                    } else {
                        // create folder  insdcard
                        //here
                        File dummyFile = new File(sdCardPath);
                        boolean ss = UtilityStorage.isWritableNormalOrSaf(dummyFile, mContext);
                        System.out.print("" + ss);
                        if (UtilityStorage.isWritableNormalOrSaf(dummyFile, mContext)) {
                            boolean st1 = UtilityStorage.mkdir(folderPath, mContext);
                            return st1;
                        } else {
                            UtilityStorage.guideDialogForLEXA(mContext, dummyFile.getAbsolutePath(), Constants.FOLDER_CREATE_REQUEST_CODE);
                        }
                    }
                }
            }
        }
        return  false;
    }

    private boolean isSdcardPresent()
    {
        sdCardPath = UtilityStorage.getExternalStoragePath(mContext, true);
        // if sdcard is ejected the returned path will not exist;
        if (sdCardPath != null && Utility.isPathExist(sdCardPath,mContext))

            return true;
        else return false;

    }

    private   void loadFileList() {
        isUnseleAllEnabled=false;
        folderList.clear();
        fileList_root.clear();
        try {
            path.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "unable to write on the sd card ");
        }

        // Checks whether path exists
        if (path.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    // Filters based on whether the file is hidden or not
                    if(!isShowHiddenFiles(mContext))
                     return (sel.isFile() || sel.isDirectory()) && !sel.isHidden(); //   allow hidden files to be displayed ;
                    else
                    return (sel.isFile() || sel.isDirectory()) ;

                }
            };

            String[] fList = path.list(filter);
            if (fList !=null) {
                fileList = new Model_Storage[fList.length];
                for (int i = 0; i < fList.length; i++) {
                    fileList[i] = new Model_Storage(fList[i], R.mipmap.file_icon);
                    Model_Storage model = new Model_Storage(fList[i], R.mipmap.file_icon);
                    // Convert into file path
                    File sel = new File(path, fList[i]);

                    // Set drawables
                    if (sel.isDirectory()) {


                        model.setIcon(R.mipmap.directory_icon);
                        model.setFile(fList[i]);
                        model.setItemcount(sel.listFiles().length);
                        model.setFileModifiedDate(Utility.LongToDate(sel.lastModified()));
                        model.setFilePath(sel.getAbsolutePath()); // was  not set initially , done in 1.8 for delete purpose;
                        model.setIsDirecoty(true);
                        folderList.add(model);
                        Log.d("DIRECTORY", fileList[i].file);
                    } else {
                        Log.d("FILE", fileList[i].file);

                        model.setFile(fList[i]);
                        model.setFileModifiedDate(Utility.LongToDate(sel.lastModified()));
                        model.setFilesize(Utility.humanReadableByteCount(sel.length(), true));
                        model.setFilePath(sel.getAbsolutePath());
                        model.setIsDirecoty(false);
                        fileList_root.add(model);

                    }

                }

            }

            Collections.sort(fileList_root, new Comparator<Model_Storage>() {
                public int compare(Model_Storage o1, Model_Storage o2) {
                    return o1.getFile().compareToIgnoreCase(o2.getFile());

                }
            });

            Collections.sort(folderList, new Comparator<Model_Storage>() {
                public int compare(Model_Storage o1, Model_Storage o2) {
                    return o1.getFile().compareToIgnoreCase(o2.getFile());

                }
            });





            folderList.addAll(fileList_root);




            System.out.print(""+folderList);





        } else {
            Log.e(TAG, "path does not exist");
        }
    }

    public void finish_Action_Mode() {
        if (mActionMode != null) {
            mActionMode.finish();
            multiselect_list.clear();
            mActionMode=null;
        }
    }
    private void setCurrentDispPath() {

        String pathstr=path.getAbsolutePath();
        Constants.pastePath=pathstr;     // this variable is used to paste the file on the path assigned to  this variable in mainActivity
        currentPath.setText(pathstr);


    }

    private void updateVisibilityPasteMenu()
    {
        try {
          //will  call  oncreateOptions menu  and that  will  update pastemenuVisibility;
            invalidateOptionsMenu();

        }catch (Exception e)
        {

        }

    }

    private void selectAll()
    {
        if (mActionMode != null)
        {
            multiselect_list.clear();

            for(int i=0;i<folderList.size();i++)
            {
                if(!multiselect_list.contains(multiselect_list.contains(folderList.get(i))))
                {
                    multiselect_list.add(folderList.get(i));
                }
            }
            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            //to change  the selectAll  menu  to  unselectAll
            selectMenuChnage();
            //to change  the selectAll  menu  to  unselectAll


            refreshAdapter_ActionMode();



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

            refreshAdapter_ActionMode();

        }
    }

    private void  shareApkMultipleFilesWithNoughatAndAll()
    {



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
                    Uri uri = FileProvider.getUriForFile(mContext, getResources().getString(R.string.file_provider_authority), file);
                    files.add(uri);
                }
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(sharingIntent);

            }

        }
        else
        {
            //Toast.makeText(mContext, "No files to share", Toast.LENGTH_SHORT).show();
            Utility.dispToast(mContext, getResources().getString(R.string.nofile));
        }



    }

    private void DispDetailsDialog( Model_Storage fileProperty )
    {

        if(fileProperty.getFilePath() !=null)
        {
            String[] splitPath = fileProperty.getFilePath().split("/");
            String fName = splitPath[splitPath.length - 1];

            Dialog dialog = new Dialog(mContext);
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
            File f = new File(fileProperty.getFilePath());

            if(f.isDirectory())
                FileSize.setText(Utility.humanReadableByteCount(Utility.getFolderSize(f),true));
            else
                FileSize.setText(Utility.humanReadableByteCount(f.length(),true));


            FileDate.setText(fileProperty.getFileModifiedDate());



            dialog.show();
        }
    }


    public  String calcSelectFileSize(ArrayList<Model_Storage> fileList)
    {
        long totalSize=0;

        for(int i=0;i<fileList.size();i++)
        {
            Model_Storage m =  fileList.get(i);
            File  f= new File(m.getFilePath());
            if(f.isDirectory())
                totalSize+=Utility.getFolderSize(f);
            else
                totalSize+=f.length();
        }

        return  Utility.humanReadableByteCount(totalSize,true);
    }

    @Override
    public void onPositiveClick(int from) {


        if(from==1)
        {
            if(multiselect_list.size()>0)
            {

                File f =new File(multiselect_list.get(0).getFilePath());

                //delte internal file and folder;
                boolean st=f.canWrite();
                System.out.print(""+st);
                // delete InterNal Folder/file;
              if(f.canWrite())
              {
                  new DeleteFileTask(multiselect_list).execute();
                  return;
              }
                  // delete sdcard Folder/file;
                if(UtilityStorage.isWritableNormalOrSaf(f,mContext)) {
                    new DeleteFileTask(multiselect_list).execute();
                }
                else
                {
                    UtilityStorage.guideDialogForLEXA(mContext,f.getParent(),Constants.FILE_DELETE_REQUEST_CODE);
                }




            }
        }
        else if(from==2)
        {
//            if (mActionMode != null) {
//                mActionMode.finish();
//            }
            finish_Action_Mode();

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

    private class DeleteFileTask extends AsyncTask<Void,Void,Integer>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            CustomProgressDialog.show(mContext,getResources().getString(R.string.deleting_file));
        }

        ArrayList<Model_Storage> multiselect_list;
        DeleteFileTask( ArrayList<Model_Storage> multiselect_list)
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


            // remove  the  file from ApkList list if deleted;

            if(FileCount>0)
            {
                for (int i = 0; i < multiselect_list.size(); i++) {
                    folderList.remove(multiselect_list.get(i));
                    Utility.removeFileFromCopyList(multiselect_list.get(i).getFilePath());
                }

                refreshAdapter_ActionMode();

                if (mActionMode != null) {
                    mActionMode.finish();
                }
            }

            String msg=FileCount>1 ? FileCount+" "+getResources().getString(R.string.delmsg1) : FileCount+" "+getResources().getString(R.string.delmsg2);
            Utility.dispToast(mContext, msg);


            CustomProgressDialog.dismiss();
        }
    }
    public  boolean deleteNon_EmptyDir(File dir) {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteNon_EmptyDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // MediaMetadataRetriever obj = new MediaMetadataRetriever();


        boolean status= dir.delete();
        if(status)
            sendBroadcast(dir);

        return status;


    }
    private int deleteFile(ArrayList<Model_Storage> delete_list)
    {
        int count=0;

        for(int i=0;i<delete_list.size();i++) {

            if (delete_list.get(i).getisDirecoty()) {

                File f =  new File(delete_list.get(i).getFilePath());
                boolean status=deleteNon_EmptyDir(f);
                if(status) {
                    count++;
                    }
                    // to  delete external  storage  folder
                  else
                {
                    // try  with storage access fromework
                    boolean st = UtilityStorage.isWritableNormalOrSaf(f, mContext);
                    System.out.println("" + st);
                    if (st) {
                        boolean status1 = UtilityStorage.deleteWithAccesFramework(mContext, f);
                        if (status1) {
                            count++;
                            Utility.RunMediaScan(mContext, f);
                        }
                    } else {
                        // UtilityStorage.triggerStorageAccessFramework(mcontext);
                    }
                }

            } else {

                File f = new File(String.valueOf(delete_list.get(i).getFilePath()));
                if (f.exists()) {
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
                            // UtilityStorage.triggerStorageAccessFramework(mcontext);
                        }


                    }

                }

            }
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
    public void onItemSelected(Model_Storage modelStorage_model) {

        if(mActionMode==null) {
            if (modelStorage_model.getisDirecoty()) {
                previous_path = path;
                String chosenFile = modelStorage_model.getFile();
                File sel = new File(path + "/" + chosenFile);

                // Adds chosen directory to list
                str.add(chosenFile);
                fileList = null;
                folderList.clear();
                fileList_root.clear();
                path = new File(sel + "");
                if (!previous_path.equals(path))
                    pathList.add(previous_path);

                loadFileList();
                setCurrentDispPath();
                if (folderList.size() != 0) {

                    blankIndicator.setVisibility(View.GONE);
                    multiSelectAdapter.notifyDataSetChanged();

                } else {
                    blankIndicator.setVisibility(View.VISIBLE);

                    }

            } else {

                //Utility.OpenFile(getActivity(),modelStorage_model.getFilePath());
                      String type=Utility.getMimiTypefromPath(modelStorage_model.getFilePath());
                      System.out.print(""+type);
                 if(type !=null)
                 {
                     if (type.contains(Constants.mimeType_Img_Registries)) {
                         Grid_Model model = new Grid_Model();
                         model.setImgPath(modelStorage_model.getFilePath());
                         Constants.img_ArrayImgList.clear();
                         Constants.img_ArrayImgList.add(model);
                         Intent intentImageGallary = new Intent(mContext, Media_ImgActivity.class);
                         intentImageGallary.putExtra(Constants.CUR_POS_VIEW_PAGER, 0);
                         intentImageGallary.putExtra(Constants.MEDIA_DELETE_ACTIVITY_TRACKER, Constants.STORAGE);
                         startActivity(intentImageGallary);
                     } else if (type.contains(Constants.mimeType_Audio_Registries)) {
                         if (addprefs != null) {
                             addprefs.setIntValue("position", 0);
                         }
                         Intent intentAudioGallary = new Intent(mContext, Media_AdoActivity.class);
                         intentAudioGallary.putExtra(Constants.selectedAdo, modelStorage_model.getFilePath());
                         intentAudioGallary.putExtra(Constants.MEDIA_DELETE_ACTIVITY_TRACKER, Constants.STORAGE);
                         startActivity(intentAudioGallary);
                     } else if (type.contains(Constants.mimeType_Video_Registries)) {
                         if (addprefs != null) {
                             addprefs.setIntValue("position", 0);
                         }
                         Intent intentVideoGallary = new Intent(mContext, Media_VdoActivity.class);
                         intentVideoGallary.putExtra(Constants.selectedVdo, modelStorage_model.getFilePath());
                         intentVideoGallary.putExtra(Constants.MEDIA_DELETE_ACTIVITY_TRACKER, Constants.STORAGE);
                         startActivity(intentVideoGallary);
                     }
                     else if(modelStorage_model.getFilePath().endsWith(".apk")) {
                         glob_model = modelStorage_model;
                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                             if (!getPackageManager().canRequestPackageInstalls()) {
                                 startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), Constants.APK_INSTALL_REQUEST_CODE);
                             } else {
                                 Utility.OpenFileWithNoughtAndAll_Apk(modelStorage_model.getFilePath(), mContext, getResources().getString(R.string.file_provider_authority));
                             }
                         } else {
                             Utility.OpenFileWithNoughtAndAll_Apk(modelStorage_model.getFilePath(), mContext, getResources().getString(R.string.file_provider_authority));
                         }
                     }
                     else
                         Utility.OpenFileWithNoughtAndAll(modelStorage_model.getFilePath(), mContext, getResources().getString(R.string.file_provider_authority));

                 }
                else
                Utility.OpenFileWithNoughtAndAll(modelStorage_model.getFilePath(), mContext, getResources().getString(R.string.file_provider_authority));
            }
        }




    }
    private void setVisibleblanklayout(Context context)
    {
        Activity act = (Activity)context;
        act.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                recyclerView.setVisibility(View.INVISIBLE);
                blankIndicator.setVisibility(View.VISIBLE);
            } });
    }
    private void dispRecyclerView(Context context)
    {
        Activity act = (Activity)context;
        act.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                recyclerView.setVisibility(View.VISIBLE);
                blankIndicator.setVisibility(View.INVISIBLE);
            } });
    }

    @Override
    public void copyFinish() {
        loadFileList();
        if(folderList.size()!=0) {

            blankIndicator.setVisibility(View.GONE);
            multiSelectAdapter = new Adapter_Storage(mContext, folderList,multiselect_list, this);
            recyclerView.setAdapter(multiSelectAdapter);
        }
        else {
            blankIndicator.setVisibility(View.VISIBLE);
        }

        updateVisibilityPasteMenu();
        finish();


    }

    @Override
    public void onBackPressed() {



        int flag=  changePathOnBackPress();
        if(flag==0)
            super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        super.onRestart();


        if(mActionMode !=null)
        {
            mActionMode.finish();

        }

        // update  the  list  if a file is deleted from media_img,media_audio,media_vdo
        if(multiSelectAdapter!=null)
        {
            multiSelectAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utility.log_FirebaseActivity_Events(Activity_Stotrage.this,"Storage");
    }

    public int  changePathOnBackPress() {


        int listSize=pathList.size();

        if (pathList.size() ==0 )  // 1 to 0
        {
            path=initial_path;
            // pathList.clear();
            //pathList.add(initial_path);
            loadFileList();
            /*if null check  added to  handle attempt to invoke virtual method
            'void com.mojodigi.filehunt.Adapter.Adapter_Storage.notifyDataSetChanged()'
            on a null object reference  reported by  appemtrica  on 03-05-2019*/
            if(multiSelectAdapter!=null) {
           multiSelectAdapter.notifyDataSetChanged();
           setCurrentDispPath();
       }

            return 0;

        }
        else
        {

            path=pathList.get(pathList.size()-1);
            pathList.remove(pathList.size()-1);
            loadFileList();
            if(folderList.size()!=0) {
                blankIndicator.setVisibility(View.GONE);
                multiSelectAdapter.notifyDataSetChanged();
            }
            else
            {
                blankIndicator.setVisibility(View.VISIBLE);
            }
        }
        setCurrentDispPath();
        return listSize;


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_paste, menu);

        MenuItem item1 = menu.findItem(R.id.action_paste);

        if(item1!=null) {
           // if (Constants.filesToCopy != null && Constants.filesToCopy.size() >= 1 && storageType.equalsIgnoreCase(Constants.interNal)  ) {
            if (Constants.filesToCopy != null && Constants.filesToCopy.size() >= 1  ) {
                item1.setVisible(false); //no need to  make visible
                pastelayout.setVisibility(View.VISIBLE);
                }
            else
            {
                item1.setVisible(false);
                pastelayout.setVisibility(View.GONE);
            }
        }
        return true;

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        switch (id)
        {
            case R.id.action_create_folder:
                folderCreateDialog();
                break;

                case R.id.action_paste:
                pasteData();
                break;
        }



        return super.onOptionsItemSelected(item);
    }


    public void pasteData() {
        //fileList_root  is being passed to  check  file already exist in same location;
        long totalSize;
        if (isEnoghStorageAvalaiable()) {
            // new copyAsyncTask(mcontext,this, Constants.filesToCopy,fileList_root,currentPath.getText().toString()).execute();
            if(isPastingInInterNal)
            new copyAsyncTask(mContext, this, Constants.filesToCopy, folderList, currentPath.getText().toString(),isPastingInInterNal).execute();
            else {
                if(sdCardPath !=null)
                {
                    File dummyFile = new File(sdCardPath);
                    boolean ss = UtilityStorage.isWritableNormalOrSaf(dummyFile, mContext);
                    System.out.print("" + ss);
                    if (UtilityStorage.isWritableNormalOrSaf(dummyFile, mContext)) {
                        new copyAsyncTask(mContext, this, Constants.filesToCopy, folderList, currentPath.getText().toString(), isPastingInInterNal).execute();

                    } else {
                        UtilityStorage.guideDialogForLEXA(mContext, dummyFile.getAbsolutePath(), Constants.COPY_REQUEST_CODE);
                    }
                }

            }

        } else {
            if (isPastingInInterNal)
                Utility.dispToast(mContext, "" + Utility.humanReadableByteCount(Utility.listFileSize(Constants.filesToCopy) - Utility.getAvailableInternalMemorySize(), true) + " additional space is required");
            else
                Utility.dispToast(mContext, "" + Utility.humanReadableByteCount(Utility.listFileSize(Constants.filesToCopy) - Utility.getAvailableExternalMemorySize(UtilityStorage.getExternalStoragePath(mContext, true)), true) + " additional space is required on sdcard");

            // if paste operations does not take place remove  the data from list  to avoid user confusion if he/she select  other files to copy
            //as per project requirenments
             Constants.filesToCopy.clear();

        }
    }
    private boolean isEnoghStorageAvalaiable( ) {


        if(isPastingInInterNal) {
            if (Utility.listFileSize(Constants.filesToCopy) < Utility.getAvailableInternalMemorySize()) {
                return true;

            } else
                return false;
        }
        else {

            if (Utility.listFileSize(Constants.filesToCopy) < Utility.getAvailableExternalMemorySize(UtilityStorage.getExternalStoragePath(mContext, true))) {
                return true;

            } else
                return false;

        }

    }

        public void ShowHideMenu()
    {


        if(context_menu!=null)
        {


//            for (int i = 0; i < context_menu.size(); i++) {
//                MenuItem item = context_menu.getItem(i);
//                if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.menu_paste))) {
//                    if (Constants.filesToCopy.size() > 0 && page instanceof  TabFragment2) {
//                        item.setVisible(true);
//                    } else {
//                        item.setVisible(false);
//                    }
//                }

            }
            invalidateOptionsMenu();
        }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Constants.APK_INSTALL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (getPackageManager().canRequestPackageInstalls()) {
                if(glob_model !=null)
                    Utility.OpenFileWithNoughtAndAll(glob_model.getFilePath(), mContext, getResources().getString(R.string.file_provider_authority));
            }
        } else {
            //give the error
        }

        if(requestCode== Constants.COPY_REQUEST_CODE) {
            boolean isPersistUriSet = UtilityStorage.setUriForStorage(requestCode, resultCode, data);
            if (isPersistUriSet && Constants.filesToCopy.size() > 0)
              pasteData();
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

                Utility.renameFile(mContext,multiselect_list.get(0).getFilePath(), Constants.Global_File_Rename_NewName,TYPE_STORAGE);
            }

        }




        if(requestCode==Constants.FOLDER_CREATE_REQUEST_CODE) {

            if (sdCardPath != null && folderPath != null) {
                boolean isPersistUriSet = UtilityStorage.setUriForStorage(requestCode, resultCode, data);
                if(isPersistUriSet)
                {
                    File dummyFile = new File(sdCardPath);
                    boolean ss = UtilityStorage.isWritableNormalOrSaf(dummyFile, mContext);
                    System.out.print("" + ss);
                    if (UtilityStorage.isWritableNormalOrSaf(dummyFile, mContext)) {
                        boolean st1 = UtilityStorage.mkdir(folderPath, mContext);
                        if (st1) {
                            loadFileList();
                            refreshAdapter();
                            Utility.dispToast(mContext, getResources().getString(R.string.foldercreatedmsg));
                        }
                    } else {
                        Utility.dispToast(mContext, getResources().getString(R.string.error_folder_create));
                    }
                }

            else{
                    Utility.dispToast(mContext, getResources().getString(R.string.error_folder_create));
                }
            }
            else {
                Utility.dispToast(mContext, getResources().getString(R.string.error_folder_create));
            }
        }
    }
}



