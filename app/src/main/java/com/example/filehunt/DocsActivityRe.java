package com.example.filehunt;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.filehunt.Adapter.MultiSelectAdapter_Docs;
import com.example.filehunt.Adapter.MultiSelectAdapter_Video;
import com.example.filehunt.Model.Grid_Model;
import com.example.filehunt.Model.Model_Docs;
import com.example.filehunt.Utils.AlertDialogHelper;
import com.example.filehunt.Utils.AutoFitGridLayoutManager;
import com.example.filehunt.Utils.RecyclerItemClickListener;
import com.example.filehunt.Utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class DocsActivityRe extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener,MultiSelectAdapter_Docs.DocsListener {

    ActionMode mActionMode;
    Menu context_menu;

   
    RecyclerView recyclerView;
    MultiSelectAdapter_Docs multiSelectAdapter;
    boolean isMultiSelect = false;

    ArrayList<Model_Docs> docsList = new ArrayList<>();
    ArrayList<Model_Docs> multiselect_list = new ArrayList<>();

    AlertDialogHelper alertDialogHelper;
    int int_position;
    ArrayList<Model_Docs> Intent_Docs_List;
    private SearchView searchView;
    Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity_re);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mcontext=DocsActivityRe.this;
       
//           int_position = getIntent().getIntExtra("value", 0);
            FetchDocuments();
        
          //data_load();

        alertDialogHelper =new AlertDialogHelper(this);
        multiSelectAdapter = new MultiSelectAdapter_Docs(this,docsList,multiselect_list,this);
        // AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, (int)Utility.px2dip(mcontext,150.0f));  // did not work on high resolution phones


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.addItemDecoration(new DividerItemDecoration(mcontext,
                DividerItemDecoration.VERTICAL));

        // recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(multiSelectAdapter);



        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multi_select(position);

                else {

                   // openDocument(docsList.get(position).getFilePath());
                }
            }




            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<Model_Docs>();
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

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                multiSelectAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
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

    public void data_load() {

        for (int i = 0; i < Intent_Docs_List.size(); i++)
        {
            Model_Docs model = new Model_Docs();

            model.setFileType(Intent_Docs_List.get(i).getFileType());
            model.setFileMDate(Intent_Docs_List.get(i).getFileMDate());
            model.setFileSize(Intent_Docs_List.get(i).getFileSize());
            model.setFileName(Intent_Docs_List.get(i).getFileName());
            docsList.add(model);
        }
    }



    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(docsList.get(position)))
                multiselect_list.remove(docsList.get(position));
            else
                multiselect_list.add(docsList.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }


    public void refreshAdapter()
    {
        multiSelectAdapter.selected_DocsList=multiselect_list;
        multiSelectAdapter.DocsList=docsList;
        multiSelectAdapter.notifyDataSetChanged();
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
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("","Delete Video","DELETE","CANCEL",1,false);
                    return true;
                case R.id.action_select:
                    selectAll();
                    return  true;
                case  R.id.action_Share:
                    shareMultipleDocs();
                    return  true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<Model_Docs>();
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
                    docsList.remove(multiselect_list.get(i));

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
//            docsList.add(mImg);
            multiSelectAdapter.notifyDataSetChanged();

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
        ArrayList<Model_Docs> multiselect_list;
        DeleteFileTask( ArrayList<Model_Docs> multiselect_list)
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

            for(int i=0;i<docsList.size();i++)
            {
               if(!multiselect_list.contains(multiselect_list.contains(docsList.get(i))))
               {
                    multiselect_list.add(docsList.get(i));
               }
            }
            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
        }
    private int deleteFile(ArrayList<Model_Docs> delete_list)
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
    private void shareMultipleDocs() {

         if(multiselect_list.size()>0)
         {
             ArrayList<Uri> uris = new ArrayList<>();
             Intent intent = new Intent();
             intent.setAction(android.content.Intent.ACTION_SEND_MULTIPLE);
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

    private void FetchDocuments()
    {
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "="+mimeType;

        final String[] projection = {MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.DISPLAY_NAME,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.MEDIA_TYPE};
        Cursor cursor = mcontext.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                projection, null, null, null);

        String[] types = new String[]{"pdf",  "doc", "docx", "rtf", "txt", "wpd", "wps","xls","xlsx","json","dot","dotx","docm","dotm",
                "xlt",
                "xla",

                "xltx",
                "xlsm",
                "xltm",
                "xlam",
                "xlsb",

                "ppt",
                "pot",
                "pps",
                "ppa",

                "pptx",
                "potx",
                "ppsx",
                "ppam",
                "pptm",
                "potm",
                "ppsm",
                "mdb "   };     // if any file type needed add extension here and task is done

        if (cursor == null) {
            System.out.println("docs data count" + 0);

        } else if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {

                String FileType="";
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));


                String[] filePath=   path.split("/");
                String FileName=filePath[filePath.length-1];
                String[] s1=FileName.split("\\.");
                if(s1.length==1)
                    FileType=s1[0];
                else if(s1.length==2)
                    FileType=s1[1];

                String MediaType=cursor.getString(cursor.getColumnIndex( MediaStore.Files.FileColumns.MEDIA_TYPE));
                System.out.println("MediaType"+MediaType);
                System.out.println("ArrayLength-> "+s1.length);
                if (path != null && Arrays.asList(types).contains(FileType))
                {
                    String fileName=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
                    //long fileDateModified=cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                    String fileDateModified=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                    long fileSize=cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));

                    Model_Docs model=new Model_Docs();
                    model.setFileName(fileName);
                    model.setFilePath(path);
                    model.setFileSize(Utility.humanReadableByteCount(fileSize,true));
                    model.setFileMDate(Utility.LongToDate(fileDateModified));
                    model.setFileType(FileType);

                    docsList.add(model);


                }
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        System.out.println("docs data count" + docsList.size());



    }

    @Override
    public void onDocsSelected(Model_Docs docs) {

        openDocument(docs.getFilePath());

    }
    public void openDocument(String name) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        File file = new File(name);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (extension.equalsIgnoreCase("") || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            intent.setDataAndType(Uri.fromFile(file), "text/*");
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimetype);
        }
        // custom message for the intent
        startActivity(Intent.createChooser(intent, "Choose an Application:"));
    }
}
