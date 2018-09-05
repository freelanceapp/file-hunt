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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filehunt.Adapter.MultiSelectAdapter_Apk;
import com.example.filehunt.Adapter.MultiSelectAdapter_Download;
import com.example.filehunt.Class.Constants;
import com.example.filehunt.Model.Model_Apk;
import com.example.filehunt.Model.Model_Docs;
import com.example.filehunt.Model.Model_Download;
import com.example.filehunt.Utils.AlertDialogHelper;
import com.example.filehunt.Utils.AsynctaskUtility;
import com.example.filehunt.Utils.CustomProgressDialog;
import com.example.filehunt.Utils.RecyclerItemClickListener;
import com.example.filehunt.Utils.Utility;
import com.example.filehunt.Utils.UtilityStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DownloadActivityRe extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener,MultiSelectAdapter_Download.DownloadListener, AsynctaskUtility.AsyncResponse  {

    ActionMode mActionMode;
    Menu context_menu;


    RecyclerView recyclerView;
    MultiSelectAdapter_Download multiSelectAdapter;
    boolean isMultiSelect = false;

    ArrayList<Model_Download> DownloadList = new ArrayList<>();
    ArrayList<Model_Download> multiselect_list = new ArrayList<>();

    AlertDialogHelper alertDialogHelper;
    int int_position;
    ArrayList<Model_Download> Intent_Docs_List;
    private SearchView searchView;
    Context mcontext;
    ImageView blankIndicator;
  int DOWNLOADS=5;
    private boolean isUnseleAllEnabled=false;

    public static  DownloadActivityRe instance;
    private Model_Download fileTorename;
    private int renamePosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity_re);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        blankIndicator=(ImageView) findViewById(R.id.blankIndicator);
        mcontext=DownloadActivityRe.this;
        instance=this;
        UtilityStorage.InitilaizePrefs(mcontext);
        Utility.setActivityTitle(mcontext,getResources().getString(R.string.cat_Download));

//           int_position = getIntent().getIntExtra("value", 0);
        new AsynctaskUtility<Model_Download>(mcontext,this,DOWNLOADS).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        alertDialogHelper =new AlertDialogHelper(this);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect && position!=RecyclerView.NO_POSITION)
                    multi_select(position);

                else {

                   // openDocument(DownloadList.get(position).getFilePath());
                }
            }




            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<Model_Download>();
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

                Utility.renameFile(mcontext,multiselect_list.get(0).getFilePath(),Constants.Global_File_Rename_NewName,4);
            }

        }


    }

    public static DownloadActivityRe getInstance() {
        return instance;
    }
    public void refreshAdapterAfterRename(String newPath,String newName)
    {
        fileTorename.setFilePath(newPath);
        fileTorename.setFileName(newName);
        DownloadList.set(renamePosition,fileTorename);
        refreshAdapter();

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
            if (multiselect_list.contains(DownloadList.get(position)))
                multiselect_list.remove(DownloadList.get(position));
            else {
                multiselect_list.add(DownloadList.get(position));
                // to  rename file contain old file;
                if(multiselect_list.size()==1) {
                    fileTorename =DownloadList.get(position);
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
        multiSelectAdapter.selected_DownloadList=multiselect_list;
        multiSelectAdapter.DownloadList=DownloadList;
        multiSelectAdapter.notifyDataSetChanged();
        selectMenuChnage();
        //finish action mode when user deselect files one by one ;
        if(multiselect_list.size()==0) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
        }
    }
    private void DispDetailsDialog( Model_Download fileProperty )
    {

        if(fileProperty.getFilePath() !=null)
        {
            String[] splitPath = fileProperty.getFilePath().split("/");
            String fName = splitPath[splitPath.length - 1];

            Dialog dialog = new Dialog(DownloadActivityRe.this);
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
            FileDate.setText(fileProperty.getFileDateModified());



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
                        Utility.fileRenameDialog(mcontext,multiselect_list.get(0).getFilePath(),Constants.DOWNLOAD);
                    return  true;

                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("","Delete file","DELETE","CANCEL",1,false);
                    return true;
                case R.id.action_select:
                    if(DownloadList.size()==multiselect_list.size() || isUnseleAllEnabled==true)
                        unSelectAll();
                    else
                        selectAll();
                    return  true;
                case  R.id.action_Share:
                    shareFileWithNoughatAndAll();
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
            multiselect_list = new ArrayList<Model_Download>();
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
//                    DownloadList.remove(multiselect_list.get(i));
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
//            DownloadList.add(mImg);
            multiSelectAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }

    //function from asyncTask class
    @Override
    public void processFinish(ArrayList output) {

        DownloadList=output;
        multiSelectAdapter = new MultiSelectAdapter_Download(this, DownloadList, multiselect_list, this);
        if(DownloadList.size()!=0) {

            // AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, (int)Utility.px2dip(mcontext,150.0f));  // did not work on high resolution phones
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

        ArrayList<Model_Download> multiselect_list;
        DeleteFileTask( ArrayList<Model_Download> multiselect_list)
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
                    DownloadList.remove(multiselect_list.get(i));

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

            for(int i=0;i<DownloadList.size();i++)
            {
               if(!multiselect_list.contains(multiselect_list.contains(DownloadList.get(i))))
               {
                    multiselect_list.add(DownloadList.get(i));
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
            if(DownloadList.size()==multiselect_list.size()) {
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

    private int deleteFile(ArrayList<Model_Download> delete_list)
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
                        //UtilityStorage.triggerStorageAccessFramework(mcontext);
                    }


                }
                //new
            }

        }
        Constants.DELETED_DOWNLOAD_FILES=count;
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
    private void shareFileWithNoughatAndAll() {

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


    private ArrayList<Model_Download> getDownLoads(File dir)
    {
        ArrayList<Model_Download>downLoadListLocal=new ArrayList<>();
        File[] listFile;
        listFile = dir.listFiles();
        System.out.print(""+listFile.toString());

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getDownLoads(listFile[i]);
                } else
                {
                    File f=new File(listFile[i].toString());
                    Model_Download model=new Model_Download();
                    model.setFilePath(listFile[i].toString());
                    model.setFileDateModified(Utility.LongToDate(f.lastModified()));
                    model.setFileSize(Utility.humanReadableByteCount(f.length(),true));
                    model.setFileName(f.getName());
                    model.setFiletype(Utility.getFileExtensionfromPath(listFile[i].toString()));
                    downLoadListLocal.add(model);
                }
            }
        }
        return downLoadListLocal;

    }


    @Override
    public void onDownloadSelected (Model_Download download_model) {


        // Utility.OpenFile(mcontext,model_apk.getFilePath()); // open file below  Android N
        if(mActionMode==null)
        Utility.OpenFileWithNoughtAndAll(download_model.getFilePath() ,mcontext,getResources().getString(R.string.file_provider_authority));
    }


    //function not being used can be deleted as  this code  is a part  of utility  now
    public void openFile(String name) {


        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(name);
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (extension.equalsIgnoreCase("") || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            intent.setDataAndType(Uri.fromFile(file), "text/*");
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimetype);
        }
        // custom message for the intent
        startActivity(Intent.createChooser(intent, "Choose an Application:"));
    }
    public  String calcSelectFileSize(ArrayList<Model_Download> fileList)
    {
        long totalSize=0;

        for(int i=0;i<fileList.size();i++)
        {
            Model_Download m =  fileList.get(i);
            File  f= new File(m.getFilePath());
            totalSize+=f.length();
        }

        return  Utility.humanReadableByteCount(totalSize,true);
    }
}
