package com.mojodigi.filehunt.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mojodigi.filehunt.Adapter.Adapter_Storage;
import com.mojodigi.filehunt.ApkActivityRe;
import com.mojodigi.filehunt.Application.MyApplication;
import com.mojodigi.filehunt.AsyncTasks.copyAsyncTask;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.MainActivity;
import com.mojodigi.filehunt.Model.Model_Storage;
//
import com.mojodigi.filehunt.Utils.AlertDialogHelper;
import com.mojodigi.filehunt.Utils.CustomProgressDialog;
import com.mojodigi.filehunt.Utils.RecyclerItemClickListener;
import com.mojodigi.filehunt.Utils.Utility;
import com.mojodigi.filehunt.Utils.UtilityStorage;

import java.io.FileReader;
import java.util.Collections;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Comparator;
import com.mojodigi.filehunt.R;
public class TabFragment2 extends Fragment  implements Adapter_Storage.ItemListener , copyAsyncTask.AsyncResponse , AlertDialogHelper.AlertDialogListener{


   private    File path = new File(Environment.getExternalStorageDirectory() + "");
    private   File initial_path = new File(Environment.getExternalStorageDirectory() + "");
    private   File previous_path = new File(Environment.getExternalStorageDirectory() + "");



//    // variables for sdcard
     // private   File path_sdcard;
//    private   File initial_path;
//    private   File previous_path ;
    // variables for sdcard


    static ArrayList<String> str = new ArrayList<String>();
    static ArrayList<File> pathList=new ArrayList<>();
    private static final String TAG = "F_PATH";
    private Model_Storage[] fileList;
    private ArrayList<Model_Storage> folderList;  // contains the final data
    private ArrayList<Model_Storage> fileList_root;  // constans all files and folder after sorting;
    private ArrayList<Model_Storage> multiselect_list=new ArrayList<>();

    ImageView blankIndicator;
    private Boolean firstLvl = true;
     RecyclerView recyclerView;
    Context mcontext;
     Adapter_Storage multiSelectAdapter;
    TextView currentPath;
    private String sdCardPath;
    TextView internal_txt,sdcard_txt;
    RelativeLayout sdcard_change,internal_change;
    CardView storage_Layout;
    static TabFragment2 instance;
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
    private Model_Storage fileTorename;
       //rename vars

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         mcontext = getActivity();
         instance=this;

        //
        fab = view.findViewById(R.id.fabSetting);
        fab.setVisibility(View.GONE);

        layoutFabSave = (LinearLayout) view.findViewById(R.id.layoutFabSave);

        //
        internal_txt = (TextView) view.findViewById(R.id.internal_txt);
        sdcard_txt = (TextView) view.findViewById(R.id.sdcard_txt);

        sdcard_txt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));

        internal_txt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));
        sdcard_txt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));

        // storage_Layout=(CardView)view.findViewById(R.id.storage_Layout);
        storage_Layout = (CardView) view.findViewById(R.id.storage_Layout);

        sdcard_change = (RelativeLayout) view.findViewById(R.id.sdcard_change);
        internal_change = (RelativeLayout) view.findViewById(R.id.internal_change);


         alertDialogHelper =new AlertDialogHelper(getActivity(),this);


        if (isSdcardPresent()) {
            storage_Layout.setVisibility(View.VISIBLE);
        } else {
            storage_Layout.setVisibility(View.GONE);
        }


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);


        blankIndicator = (ImageView) view.findViewById(R.id.blankIndicator);
        currentPath = (TextView) view.findViewById(R.id.currentPath);
        currentPath.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.addItemDecoration(new DividerItemDecoration(mcontext, DividerItemDecoration.VERTICAL));

        folderList = new ArrayList<>();
        fileList_root = new ArrayList<>();

        //default internal is selected
        //  internal_change.setBackground(getResources().getDrawable(R.drawable.st_tab_back));
        //sdcard_change.setBackgroundColor(getResources().getColor(R.color.gradation_03_dark));

        internal_change.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        sdcard_change.setBackgroundColor(getResources().getColor(R.color.grey_ebebeb));

        //default internal is selected

        pathList.clear();// new
        loadFileList();

        if (folderList.size() != 0) {
            multiSelectAdapter = new Adapter_Storage(mcontext, folderList, multiselect_list, this);
            recyclerView.setAdapter(multiSelectAdapter);
        } else {
            blankIndicator.setVisibility(View.VISIBLE);
        }


        sdcard_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                internal_change.setBackgroundColor(getResources().getColor(R.color.gradation_03_dark));
//                sdcard_change.setBackground(getResources().getDrawable(R.drawable.st_tab_back));


                finish_Action_Mode();

                internal_change.setBackgroundColor(getResources().getColor(R.color.grey_ebebeb));
                sdcard_change.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                path = new File(sdCardPath);
                initial_path = new File(sdCardPath);
                previous_path = new File(sdCardPath);
                pathList.clear();
                setCurrentDispPath();
                loadFileList();

                multiSelectAdapter.notifyDataSetChanged();

                if (folderList.size() == 0) {

                    blankIndicator.setVisibility(View.VISIBLE);
                } else {
                    blankIndicator.setVisibility(View.GONE);
                }


            }

        });
        internal_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                internal_change.setBackground(getResources().getDrawable(R.drawable.st_tab_back));
//                sdcard_change.setBackgroundColor(getResources().getColor(R.color.gradation_03_dark));

                finish_Action_Mode();

                internal_change.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                sdcard_change.setBackgroundColor(getResources().getColor(R.color.grey_ebebeb));

                path = new File(Environment.getExternalStorageDirectory() + "");
                initial_path = new File(Environment.getExternalStorageDirectory() + "");
                previous_path = new File(Environment.getExternalStorageDirectory() + "");
                pathList.clear();
                loadFileList();
                setCurrentDispPath();
                multiSelectAdapter.notifyDataSetChanged();


                if (folderList.size() == 0) {

                    blankIndicator.setVisibility(View.VISIBLE);
                } else {
                    blankIndicator.setVisibility(View.GONE);
                }


            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true) {
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });


        layoutFabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                folderCreareDialog();
                closeSubMenusFab();

            }
        });

        // delete task

          recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mcontext, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
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
                          mActionMode = getActivity().startActionMode(mActionModeCallback);
                      }
                  }

                  multi_select(position);


              }
          }));

       //delete task


    }

    public void finish_Action_Mode() {
        if (mActionMode != null) {
            mActionMode.finish();
            multiselect_list.clear();
            mActionMode=null;
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;

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
                    Utility.dispToast(mcontext,"Move");
                    return true;
                case R.id.action_encrypt:
                    Utility.dispToast(mcontext,"encrypt");
                    return true;

                case R.id.action_copy:
                    if(multiselect_list.size()>0)
                    {
                        for (int i = 0; i < multiselect_list.size(); i++) {
                            String fPath = multiselect_list.get(i).getFilePath().toString();
                            System.out.println("" + fPath);
                            if (!Constants.filesToCopy.contains(multiselect_list.get(i).getFilePath())) {
                                Constants.filesToCopy.add(multiselect_list.get(i).getFilePath().toString());
                            }
                        }


                        if(Constants.filesToCopy.size()>0)
                        {
                            finish_Action_Mode();
                            updateVisibilityPasteMenu();
                        }
                    }


                    return true ;
                case R.id.action_rename:
                    Utility.fileRenameDialog(mcontext,multiselect_list.get(0).getFilePath(),TYPE_STORAGE);

                    return  true;
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("","Delete file","DELETE","CANCEL",1,false);
                    return true;
                case R.id.action_select:

                    if(folderList.size()==multiselect_list.size() || isUnseleAllEnabled==true)
                        unSelectAll();
                    else
                        selectAll();

                    return  true;
                case  R.id.action_Share:
                    shareApkMultipleFilesWithNoughatAndAll();
                    return  true;
                case R.id.action_details:

                    if(multiselect_list.size()==1)
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
            multiselect_list = new ArrayList<Model_Storage>();
            refreshAdapter();
        }
    };




    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(folderList.get(position)))
                multiselect_list.remove(folderList.get(position));
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

    @Override
    public void onPositiveClick(int from) {


        if(from==1)
        {
            if(multiselect_list.size()>0)
            {


//                File f =new File(multiselect_list.get(0).getFilePath());
//
//                if(UtilityStorage.isWritableNormalOrSaf(f,mcontext)) {
//                    new DeleteFileTask(multiselect_list).execute();
//                }
//                else
//                {
//                    UtilityStorage.guideDialogForLEXA(mcontext,f.getParent(),Constants.FILE_DELETE_REQUEST_CODE);
//                }

                     new DeleteFileTask(multiselect_list).execute();



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
                    Uri uri = FileProvider.getUriForFile(mcontext, getResources().getString(R.string.file_provider_authority), file);
                    files.add(uri);
                }
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(sharingIntent);

            }

        }
        else
        {
            Toast.makeText(mcontext, "No files to share", Toast.LENGTH_SHORT).show();
        }



    }



    private class DeleteFileTask extends AsyncTask<Void,Void,Integer>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            CustomProgressDialog.show(mcontext,getResources().getString(R.string.deleting_file));
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
                for (int i = 0; i < multiselect_list.size(); i++)
                    folderList.remove(multiselect_list.get(i));

                  refreshAdapter_ActionMode();

                if (mActionMode != null) {
                    mActionMode.finish();
                }
            }

            String msg= FileCount>1 ? " Items deleted ":" Item deleted ";

            Toast.makeText(mcontext, FileCount+msg, Toast.LENGTH_SHORT).show();

            CustomProgressDialog.dismiss();
        }
    }
    private void DispDetailsDialog( Model_Storage fileProperty )
    {

        if(fileProperty.getFilePath() !=null)
        {
            String[] splitPath = fileProperty.getFilePath().split("/");
            String fName = splitPath[splitPath.length - 1];

            Dialog dialog = new Dialog(getActivity());
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

            } else {

                File f = new File(String.valueOf(delete_list.get(i).getFilePath()));
                if (f.exists()) {
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
            getActivity().sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
            getActivity().sendBroadcast(intent);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        closeSubMenusFab();
        MyApplication.getInstance().trackScreenView("Home Screen"); //  to  track for mobile vendors;

    }
    public static TabFragment2 getInstance() {
        return instance;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
       if(isVisibleToUser) {

           setCurrentDispPath();
           if(isSdcardPresent())
           {
               storage_Layout.setVisibility(View.VISIBLE);
           }
           else
           {
               storage_Layout.setVisibility(View.GONE);
           }



        //  pathList.clear(); new

           //loads the data only fisrt time when fragment is visible to user if this is not done then at the time of installation first time the
           // storage details will not be visible so  call is once when fragment is visible to user

           if(folderList.size()==0) {
               loadFileList();
               multiSelectAdapter = new Adapter_Storage(mcontext, folderList,multiselect_list, this);
               recyclerView.setAdapter(multiSelectAdapter);
           }
           else
           {
               blankIndicator.setVisibility(View.GONE);
           }


           //loads the data only fisrt time when fragment is visible to user if this is not done then at the time of installation first time the
           //storage details will not be visible so  call is once when fragment is visible to user

           updateVisibilityPasteMenu();

          // AddMobUtils addutil=new AddMobUtils();
         //  addutil.showInterstitial(getActivity());



       }
       else {
          // pathList.clear();  new
           System.out.print(""+pathList);
                     // finish action mode when fragment becomes invisible  to user;
           finish_Action_Mode();
       }

    }

    private void updateVisibilityPasteMenu()
    {
        try {
            Constants.redirectToStorage=false; //  once we have been redirected to  storage then make it false
            //as it is set true on click of copy menu;
            ((MainActivity) getActivity()).ShowHideMenu();

        }catch (Exception e)
        {

        }

    }

    private void setCurrentDispPath() {

        String pathstr=path.getAbsolutePath();
        Constants.pastePath=pathstr;//  this variable is used to paste the file on the path assigned to  this variable in mainActivity
        currentPath.setText(pathstr);


    }

    @Override
    public void onPause() {


        super.onPause();


    }
    @Override
    public void onDestroy() {

        super.onDestroy();
    }
    public void pasteData()
    {
        //fileList_root  is being passed to  check  file already exist in same location;
        new copyAsyncTask(mcontext,this, Constants.filesToCopy,fileList_root,currentPath.getText().toString()).execute();

    }

    public   int onBackPressed() {

        return  changePathOnBackPress();
    }
    public int  changePathOnBackPress() {


        int listSize=pathList.size();

      if (pathList.size() ==0 )  // 1 to 0
        {
            path=initial_path;
            // pathList.clear();
            //pathList.add(initial_path);
            loadFileList();

            multiSelectAdapter.notifyDataSetChanged();
            setCurrentDispPath();
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


private boolean isSdcardPresent()
{
    sdCardPath = UtilityStorage.getExternalStoragePath(mcontext, true);
    // if sdcard is ejected the returned path will not exist;
    if (sdCardPath != null && Utility.isPathExist(sdCardPath,getActivity()))

        return true;
    else return false;

}


    private   void loadFileList() {
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
                   // return (sel.isFile() || sel.isDirectory()) && !sel.isHidden(); // does  not allow hidden files to be displayed ;
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
                  Utility.OpenFileWithNoughtAndAll(modelStorage_model.getFilePath(), getActivity(), getActivity().getResources().getString(R.string.file_provider_authority));
              }
          }




    }
    @Override
    public void copyFinish() {
        loadFileList();

        if(folderList.size()!=0) {

            blankIndicator.setVisibility(View.GONE);
            multiSelectAdapter = new Adapter_Storage(mcontext, folderList,multiselect_list, this);
            recyclerView.setAdapter(multiSelectAdapter);
        }
        else {
            blankIndicator.setVisibility(View.VISIBLE);
        }

        updateVisibilityPasteMenu();

    }

    public  void refreshAdapter()

    {
        if(folderList.size()!=0) {

            blankIndicator.setVisibility(View.GONE);
            multiSelectAdapter = new Adapter_Storage(mcontext, folderList,multiselect_list, this);
            recyclerView.setAdapter(multiSelectAdapter);
        }
        else {
            blankIndicator.setVisibility(View.VISIBLE);
        }

    }
    public void refreshAdapterAfterRename(String newPath,String newName)
    {
        fileTorename.setFilePath(newPath);
        fileTorename.setFile(newName);
        folderList.set(renamePosition,fileTorename);
        refreshAdapter_ActionMode();

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


        getActivity().invalidateOptionsMenu();
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

    private void closeSubMenusFab(){
        layoutFabSave.setVisibility(View.INVISIBLE);
        fab.setImageResource(R.drawable.ic_add_black_24dp);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutFabSave.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        fab.setImageResource(R.drawable.ic_close_black_24dp);
        fabExpanded = true;
    }
    public  void folderCreareDialog()
    {
        //https://github.com/sang89vh/easyfilemanager/blob/master/AmazeFileManagerSang89vhAdmob/src/main/java/com/mybox/filemanager/services/httpservice/FileUtil.java

        final Dialog dialog = new Dialog(mcontext);
        dialog.setContentView(R.layout.dialog_folder_create);
        // Set dialog title

        TextView headertxt = dialog.findViewById(R.id.headertxt);
        final EditText Edit_FolderName = dialog.findViewById(R.id.Edit_FolderName);

        TextView View_crete = dialog.findViewById(R.id.View_crete);
        TextView View_cancel = dialog.findViewById(R.id.View_cancel);

        headertxt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));
        Edit_FolderName.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));
        View_cancel.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));
        View_crete.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));

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

                    if(createFolder(Edit_FolderName.getText().toString())) {
                        loadFileList();
                        refreshAdapter();
                    }
                    else {
                        Toast.makeText(mcontext, "Error while creating folder", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                } else {
                    Edit_FolderName.setError(mcontext.getResources().getString(R.string.emty_error));
                }

                closeSubMenusFab();

            }
        });


        dialog.show();


    }

    private boolean createFolder(String folderName) {

        if(currentPath.getText().toString() !=null)
        {
            File f= new File(currentPath.getText().toString()+"/"+folderName);

                if (f.exists()) {
                    Toast.makeText(mcontext, "Folder exists already", Toast.LENGTH_SHORT).show();
                    return  false;
                }
                else
                {
                    //create folder
                  boolean dstatus = f.mkdir();
                  System.out.print(""+dstatus);
                  return  dstatus;
                }
            }
        return  false;
    }

}
