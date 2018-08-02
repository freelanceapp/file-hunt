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
import android.widget.TextView;
import android.widget.Toast;

import com.example.filehunt.Adapter.MultiSelectAdapter_Anim;
import com.example.filehunt.Adapter.MultiSelectAdapter_Docs;
import com.example.filehunt.Model.Model_Anim;
import com.example.filehunt.Model.Model_Download;
import com.example.filehunt.Utils.AlertDialogHelper;
import com.example.filehunt.Utils.AsynctaskUtility;
import com.example.filehunt.Utils.RecyclerItemClickListener;
import com.example.filehunt.Utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class AnimationActivityRe extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener,MultiSelectAdapter_Anim.AnimListener,AsynctaskUtility.AsyncResponse{

    ActionMode mActionMode;
    Menu context_menu;


    RecyclerView recyclerView;
    MultiSelectAdapter_Anim multiSelectAdapter;
    boolean isMultiSelect = false;

    ArrayList<Model_Anim> animList = new ArrayList<>();
    ArrayList<Model_Anim> multiselect_list = new ArrayList<>();

    AlertDialogHelper alertDialogHelper;
    int int_position;
    ArrayList<Model_Anim> Intent_Docs_List;
    private SearchView searchView;
    Context mcontext;
  int ANIMATION=6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity_re);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mcontext=AnimationActivityRe.this;
        Utility.setActivityTitle(mcontext,getResources().getString(R.string.animation));

        new AsynctaskUtility<Model_Anim>(mcontext,this,ANIMATION).execute();

//           int_position = getIntent().getIntExtra("value", 0);

          //data_load();

        alertDialogHelper =new AlertDialogHelper(this);




        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multi_select(position);

                else {

                   // openDocument(animList.get(position).getFilePath());
                }
            }




            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<Model_Anim>();
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





    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(animList.get(position)))
                multiselect_list.remove(animList.get(position));
            else
                multiselect_list.add(animList.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }


    public void refreshAdapter()
    {
        multiSelectAdapter.selected_AnimList=multiselect_list;
        multiSelectAdapter.AnimList=animList;
        multiSelectAdapter.notifyDataSetChanged();
    }
    private void DispDetailsDialog( Model_Anim fileProperty )
    {

        if(fileProperty.getFilePath() !=null)
        {
            String[] splitPath = fileProperty.getFilePath().split("/");
            String fName = splitPath[splitPath.length - 1];

            Dialog dialog = new Dialog(AnimationActivityRe.this);
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
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("","Delete Video","DELETE","CANCEL",1,false);
                    return true;
                case R.id.action_select:
                    selectAll();
                    return  true;
                case  R.id.action_Share:
                    shareMultipleFilesWithNoughatAndAll();
                    return  true;
                case R.id.action_details:
                    if(multiselect_list.size()==1)//diplay details only for one selected image for now
                        DispDetailsDialog(multiselect_list.get(0));
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<Model_Anim>();
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
                    animList.remove(multiselect_list.get(i));

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
//            animList.add(mImg);
            multiSelectAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }
   // function from asyncTask
    @Override
    public void processFinish(ArrayList output) {
        animList=output;
        multiSelectAdapter = new MultiSelectAdapter_Anim(this,animList,multiselect_list,this);
        // AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, (int)Utility.px2dip(mcontext,150.0f));  // did not work on high resolution phones
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.addItemDecoration(new DividerItemDecoration(mcontext,
                DividerItemDecoration.VERTICAL));

        recyclerView.setAdapter(multiSelectAdapter);
    }

    private class DeleteFileTask extends AsyncTask<Void,Void,Integer>
    {
        ArrayList<Model_Anim> multiselect_list;
        DeleteFileTask( ArrayList<Model_Anim> multiselect_list)
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

            for(int i=0;i<animList.size();i++)
            {
               if(!multiselect_list.contains(multiselect_list.contains(animList.get(i))))
               {
                    multiselect_list.add(animList.get(i));
               }
            }
            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
        }
    private int deleteFile(ArrayList<Model_Anim> delete_list)
    {
        int count=0;

        for(int i=0;i<delete_list.size();i++)
        {
            File f=new File(String.valueOf(delete_list.get(i).getFilePath()));
            if(f.exists())
                if(f.delete()) {
                    count++;
                   // sendBroadcast(f);
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
    private void shareMultipleFilesWithNoughatAndAll() {

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

    private void fetchAnimationFiles() {
        ArrayList<String> animation = new ArrayList<>();
        
        final String[] projection = {MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.TITLE,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.MEDIA_TYPE};
        Cursor cursor = mcontext.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                projection, null, null, null);
        
        if (cursor == null)
            System.out.println("Anim data count" + 0);
        else if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String fileName=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                //long fileDateModified=cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                String fileDateModified=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                long fileSize=cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));


                if (path != null && path.endsWith(".gif") ||  path.endsWith(".swf")|| path.endsWith(".ani")) {
                    Model_Anim model=new Model_Anim();
                    model.setFileName(fileName);
                    model.setFilePath(path);
                    model.setFileSize(Utility.humanReadableByteCount(fileSize,true));
                    model.setFileMDate(Utility.LongToDate(fileDateModified));
                   // model.setFileType(FileType);

                    animList.add(model);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
       
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

                    Model_Anim model=new Model_Anim();
                    model.setFileName(fileName);
                    model.setFilePath(path);
                    model.setFileSize(Utility.humanReadableByteCount(fileSize,true));
                    model.setFileMDate(Utility.LongToDate(fileDateModified));
                    model.setFileType(FileType);

                    animList.add(model);


                }
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        System.out.println("docs data count" + animList.size());



    }

    @Override
    public void onAnimSelected(Model_Anim docs) {

        // Utility.OpenFile(mcontext,model_apk.getFilePath()); // open file below  Android N
        Utility.OpenFileWithNoughtAndAll(docs.getFilePath(),mcontext,getResources().getString(R.string.file_provider_authority));   //new
    }

}
