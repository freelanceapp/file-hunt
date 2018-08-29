package com.example.filehunt;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filehunt.Adapter.MultiSelectAdapter_Apk;
import com.example.filehunt.Adapter.MultiSelectAdapter_Docs;
import com.example.filehunt.Class.Constants;
import com.example.filehunt.Model.Model_Anim;
import com.example.filehunt.Model.Model_Apk;
import com.example.filehunt.Model.Model_Docs;
import com.example.filehunt.Model.Model_Recent;
import com.example.filehunt.Utils.AlertDialogHelper;
import com.example.filehunt.Utils.AsynctaskUtility;
import com.example.filehunt.Utils.RecyclerItemClickListener;
import com.example.filehunt.Utils.Utility;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class ApkActivityRe extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener,MultiSelectAdapter_Apk.ApkListener,AsynctaskUtility.AsyncResponse{

    ActionMode mActionMode;
    Menu context_menu;


    RecyclerView recyclerView;
    MultiSelectAdapter_Apk multiSelectAdapter;
    boolean isMultiSelect = false;

    ArrayList<Model_Apk> ApkList = new ArrayList<>();
    ArrayList<Model_Apk> multiselect_list = new ArrayList<>();

    AlertDialogHelper alertDialogHelper;
    int int_position;
    ArrayList<Model_Apk> Intent_Docs_List;
    private SearchView searchView;

    ImageView blankIndicator;
    Context mcontext;
     int APK=8;
     boolean isUnseleAllEnabled=false;
     static  ApkActivityRe instance;
     private  int renamePosition;
    private Model_Apk fileTorename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity_re);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        blankIndicator=(ImageView) findViewById(R.id.blankIndicator);
        instance=this;
        mcontext=ApkActivityRe.this;
        Constants.DELETED_APK_FILES=0;

        Utility.setActivityTitle(mcontext,getResources().getString(R.string.cat_Apk));
        //           int_position = getIntent().getIntExtra("value", 0);

        new AsynctaskUtility<Model_Recent>(mcontext,this,APK).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        alertDialogHelper =new AlertDialogHelper(this);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    if(position!=RecyclerView.NO_POSITION)
                    multi_select(position);
                }

                else {

                   // openDocument(ApkList.get(position).getFilePath());
                }
            }




            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<Model_Apk>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position);

            }
        }));


    }
    public static ApkActivityRe getInstance() {
        return instance;
    }
    public void refreshAdapterAfterRename(String newPath,String newName)
    {
        fileTorename.setFilePath(newPath);
        fileTorename.setFileName(newName);
        ApkList.set(renamePosition,fileTorename);
        refreshAdapter();

    }

    @Override
    protected void onResume() {
        super.onResume();

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

        return super.onOptionsItemSelected(item);
    }




    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(ApkList.get(position)))
                multiselect_list.remove(ApkList.get(position));
            else {
                multiselect_list.add(ApkList.get(position));
                if(multiselect_list.size()==1) {
                    fileTorename = ApkList.get(position);
                    renamePosition = position;
                }
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
        multiSelectAdapter.selected_ApkList=multiselect_list;
        multiSelectAdapter.ApkList=ApkList;
        multiSelectAdapter.notifyDataSetChanged();

        selectMenuChnage();

        //finish action mode when user deselect files one by one ;
        if(multiselect_list.size()==0) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
        }


    }
    private void DispDetailsDialog( Model_Apk fileProperty )
    {

        if(fileProperty.getFilePath() !=null)
        {
            String[] splitPath = fileProperty.getFilePath().split("/");
            String fName = splitPath[splitPath.length - 1];

            Dialog dialog = new Dialog(ApkActivityRe.this);
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
            FileDate.setText(fileProperty.getFileMDate());



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

                case R.id.action_rename:
                    if(multiselect_list.size()==1)
                    Utility.fileRenameDialog(mcontext,multiselect_list.get(0).getFilePath(),Constants.APK);
                    return  true;
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("","Delete Apk","DELETE","CANCEL",1,false);
                    return true;
                case R.id.action_select:
                    if(ApkList.size()==multiselect_list.size() || isUnseleAllEnabled==true)
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
            multiselect_list = new ArrayList<Model_Apk>();
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
                new  DeleteFileTask(multiselect_list).execute();
                for(int i=0;i<multiselect_list.size();i++)
                    ApkList.remove(multiselect_list.get(i));

                multiSelectAdapter.notifyDataSetChanged();

                if (mActionMode != null) {
                    mActionMode.finish();
                }

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
//            ApkList.add(mImg);
            multiSelectAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }
    // function from asyncTask;
    @Override
    public void processFinish(ArrayList output) {


        ApkList=output;
        multiSelectAdapter = new MultiSelectAdapter_Apk(this, ApkList, multiselect_list, this);
        if(ApkList.size()==0)
        {
            blankIndicator.setVisibility(View.VISIBLE);
        }
        else {

            // AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, (int)Utility.px2dip(mcontext,150.0f));  // did not work on high resolution phones
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
            recyclerView.addItemDecoration(new DividerItemDecoration(mcontext,
                    DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(multiSelectAdapter);

        };

    }

    private class DeleteFileTask extends AsyncTask<Void,Void,Integer>
    {
        ArrayList<Model_Apk> multiselect_list;
        DeleteFileTask( ArrayList<Model_Apk> multiselect_list)
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

            Toast.makeText(mcontext, FileCount+" file deleted", Toast.LENGTH_SHORT).show();


        }
    }
    private void selectAll()
    {
        if (mActionMode != null)
        {
            multiselect_list.clear();

            for(int i=0;i<ApkList.size();i++)
            {
               if(!multiselect_list.contains(multiselect_list.contains(ApkList.get(i))))
               {
                    multiselect_list.add(ApkList.get(i));
               }
            }
            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            //to change  the selectAll  menu  to  unselectAll
            selectMenuChnage();
            //to change  the selectAll  menu  to  unselectAll


            refreshAdapter();



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
            if(ApkList.size()==multiselect_list.size()) {
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

    private int deleteFile(ArrayList<Model_Apk> delete_list)
    {
        int count=0;

        for(int i=0;i<delete_list.size();i++)
        {
            File f=new File(String.valueOf(delete_list.get(i).getFilePath()));
            if(f.exists())
                if(f.delete()) {
                    count++;
                   sendBroadcast(f);
                }

        }
        Constants.DELETED_APK_FILES=count;
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
    private void  shareApk() {

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
    private void  shareApkMultipleFilesWithNoughatAndAll() {

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

    private void fetchApks() {


        final String[] projection = {MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.TITLE,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.MEDIA_TYPE};
        Cursor cursor = mcontext.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                projection, null, null, null);
        if (cursor == null)
            System.out.println("Apk data count" + 0);
        else if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String fileName=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                //long fileDateModified=cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                String fileDateModified=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                long fileSize=cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));


                if (path != null && path.endsWith(".apk"))
                {
                    Model_Apk model=new Model_Apk();
                    model.setFileName(fileName);
                    model.setFilePath(path);
                    model.setFileSize(Utility.humanReadableByteCount(fileSize,true));
                    model.setFileMDate(Utility.LongToDate(fileDateModified));
                    ApkList.add(model);

                    }
            } while (cursor.moveToNext());
        }
        cursor.close();

    }



    @Override
    public void onApkSelected (Model_Apk model_apk) {

        // Utility.OpenFile(mcontext,model_apk.getFilePath()); // open file below  Android N
        if(mActionMode==null)
        Utility.OpenFileWithNoughtAndAll(model_apk.getFilePath(),mcontext,getResources().getString(R.string.file_provider_authority));
    }
    public  String calcSelectFileSize(ArrayList<Model_Apk> fileList)
    {
        long totalSize=0;

        for(int i=0;i<fileList.size();i++)
        {
            Model_Apk m =  fileList.get(i);
            File  f= new File(m.getFilePath());
            totalSize+=f.length();
        }

        return  Utility.humanReadableByteCount(totalSize,true);
    }




}
