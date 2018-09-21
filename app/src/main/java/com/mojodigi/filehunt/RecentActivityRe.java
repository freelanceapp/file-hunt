package com.mojodigi.filehunt;

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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.mojodigi.filehunt.Adapter.MultiSelectAdapter_Recent;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Model.Model_Recent;
//

import com.mojodigi.filehunt.Utils.AlertDialogHelper;
import com.mojodigi.filehunt.Utils.AsynctaskUtility;
import com.mojodigi.filehunt.Utils.CustomProgressDialog;
import com.mojodigi.filehunt.Utils.RecyclerItemClickListener;
import com.mojodigi.filehunt.Utils.Utility;
import com.mojodigi.filehunt.Utils.UtilityStorage;


import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class RecentActivityRe extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener,MultiSelectAdapter_Recent.RecentListener, AsynctaskUtility.AsyncResponse {

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

   public  static  RecentActivityRe instance;

    private boolean isUnseleAllEnabled=false;
    private Model_Recent fileTorename;
    private int renamePosition;
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity_re);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        blankIndicator=(ImageView) findViewById(R.id.blankIndicator);

        mcontext=RecentActivityRe.this;
        instance=this;
        UtilityStorage.InitilaizePrefs(mcontext);
        Utility.setActivityTitle(mcontext,getResources().getString(R.string.cat_Recent));
        //execute the async task
        new AsynctaskUtility<Model_Recent>(mcontext,this,RECENTFILES).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        alertDialogHelper =new AlertDialogHelper(this);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect && position!=RecyclerView.NO_POSITION)
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
                    }
                }

                multi_select(position);

            }
        }));


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Constants.FILE_DELETE_REQUEST_CODE) {
            boolean isPersistUriSet = UtilityStorage.setUriForStorage(requestCode, resultCode, data);
            if (isPersistUriSet && multiselect_list.size() > 0)
                new DeleteFileTask(multiselect_list).execute();
        }
        if(requestCode==Constants.FILE_RENAME_REQUEST_CODE)
        {
            boolean isPersistUriSet = UtilityStorage.setUriForStorage(requestCode, resultCode, data);
            if (isPersistUriSet && multiselect_list.size() > 0)
            {
                // call rename function  here in the case of premission granted first  time;

                Utility.renameFile(mcontext,multiselect_list.get(0).getFilePath(),Constants.Global_File_Rename_NewName,6);
            }

        }


    }

    public static RecentActivityRe getInstance() {
        return instance;
    }
    public void refreshAdapterAfterRename(String newPath,String newName)
    {
        fileTorename.setFilePath(newPath);
        fileTorename.setFileName(newName);
        RecentList.set(renamePosition,fileTorename);
        refreshAdapter();

    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    protected void onStart() {
        super.onStart();


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        AsyncTask.Status st=new AsynctaskUtility<Model_Recent>(mcontext,this,RECENTFILES).getStatus();
        String str=st.name();
        System.out.println(""+str);
        if(new AsynctaskUtility<Model_Recent>(mcontext,this,RECENTFILES).getStatus()== AsyncTask.Status.PENDING || new AsynctaskUtility<Model_Recent>(mcontext,this,RECENTFILES).getStatus()== AsyncTask.Status.RUNNING)
        {
           boolean taskStopped=  new AsynctaskUtility<Model_Recent>(mcontext,this,RECENTFILES).cancel(true);
           System.out.println(""+taskStopped);
           }

    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.menu_common_activity, menu);

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
                    multiSelectAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                   if(multiSelectAdapter!=null)
                    multiSelectAdapter.getFilter().filter(query);
                    return false;

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

        if(id==R.id.action_sort)
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

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }


    public void refreshAdapter()
    {
        multiSelectAdapter.selected_RecentList=multiselect_list;
        multiSelectAdapter.RecentList=RecentList;
        multiSelectAdapter.notifyDataSetChanged();
        selectMenuChnage();

        //finish action mode when user deselect files one by one ;
        if(multiselect_list.size()==0) {
            if (mActionMode != null) {
                mActionMode.finish();
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
            dialog.setContentView(R.layout.file_property_dialog);
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

                case R.id.action_copy:
                    if(multiselect_list.size()>0)
                    {
                        for(int i=0;i<multiselect_list.size();i++)
                        {
                            String fPath=multiselect_list.get(0).getFilePath().toString();
                            System.out.println(""+fPath);
                            Constants.filesToCopy.add(multiselect_list.get(i).getFilePath().toString());
                        }
                        // redirect to  storage fragment;
                        Constants.redirectToStorage=true;

                        finish();


                    }
                    return true ;
                case R.id.action_rename:
                    if(multiselect_list.size()==1)
                        Utility.fileRenameDialog(mcontext,multiselect_list.get(0).getFilePath(),Constants.RECENT);
                    return  true;
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("","Delete file","DELETE","CANCEL",1,false);
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
                    UtilityStorage.guideDialogForLEXA(mcontext,f.getParent(),Constants.FILE_DELETE_REQUEST_CODE);
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
           recyclerView.addItemDecoration(new DividerItemDecoration(mcontext,
                   DividerItemDecoration.VERTICAL));
           recyclerView.setAdapter(multiSelectAdapter);


       }else
       {
           blankIndicator.setVisibility(View.VISIBLE);
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
                for (int i = 0; i < multiselect_list.size(); i++)
                    RecentList.remove(multiselect_list.get(i));

                multiSelectAdapter.notifyDataSetChanged();

                if (mActionMode != null) {
                    mActionMode.finish();
                }
            }
            Toast.makeText(mcontext, FileCount+" file deleted", Toast.LENGTH_SHORT).show();
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
             Toast.makeText(mcontext, "No files to share", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(mcontext, "No files to share", Toast.LENGTH_SHORT).show();
        }

    }

    private  ArrayList<Model_Recent> listRecentFiles()
    {
        //MediaStore.Files.FileColumns.DISPLYA_NAME returns null on some devices
          ArrayList<Model_Recent> RecentListLocal = new ArrayList<>();
        final String[] projection = {MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.MIME_TYPE};
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
        if(mActionMode==null)
        Utility.OpenFileWithNoughtAndAll(recent_Model.getFilePath() ,mcontext,getResources().getString(R.string.file_provider_authority));
    }
    public  String calcSelectFileSize(ArrayList<Model_Recent> fileList)
    {
        long totalSize=0;

        for(int i=0;i<fileList.size();i++)
        {
            Model_Recent m =  fileList.get(i);
            File  f= new File(m.getFilePath());
            totalSize+=f.length();
        }

        return  Utility.humanReadableByteCount(totalSize,true);
    }

    String action="";
    public  void sortDialog(final Context ctx)
    {
        action="Name";
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



                }

                System.out.print(""+RecentList);
                refreshAdapter();

            }
        });
        dialog.setNegativeButton(ctx.getResources().getString(R.string.ascending), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();



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

                }


                System.out.print(""+RecentList);
                refreshAdapter();


            }
        });


        dialog.show();



    }


}
