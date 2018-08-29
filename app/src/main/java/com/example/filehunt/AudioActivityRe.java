package com.example.filehunt;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filehunt.Adapter.MultiSelectAdapter_Audio;
import com.example.filehunt.Adapter.MultiSelectAdapter_Video;
import com.example.filehunt.Class.Constants;
import com.example.filehunt.Model.Grid_Model;
import com.example.filehunt.Model.Model_Apk;
import com.example.filehunt.Model.Model_Audio;
import com.example.filehunt.Utils.AlertDialogHelper;
import com.example.filehunt.Utils.AutoFitGridLayoutManager;
import com.example.filehunt.Utils.RecyclerItemClickListener;
import com.example.filehunt.Utils.Utility;

import java.io.File;
import java.util.ArrayList;


public class AudioActivityRe extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener ,MultiSelectAdapter_Audio.AudioListener {

    ActionMode mActionMode;
    Menu context_menu;

   
    RecyclerView recyclerView;
    MultiSelectAdapter_Audio multiSelectAdapter;
    boolean isMultiSelect = false;

    ArrayList<Model_Audio> audioList = new ArrayList<>();
    ArrayList<Model_Audio> multiselect_list = new ArrayList<>();

    AlertDialogHelper alertDialogHelper;
    int int_position;
    ArrayList<String> Intent_Audio_List;
    ArrayList<String> Intent_duration_List;
    private SearchView searchView;
    ImageView blankIndicator;

    Context mcontext;
    Model_Audio fileTorename;
    int renamePosition;
    static  AudioActivityRe instance;
    private boolean isUnseleAllEnabled=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity_re);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        blankIndicator=(ImageView) findViewById(R.id.blankIndicator);
        mcontext=AudioActivityRe.this;
        Utility.setActivityTitle(mcontext,getResources().getString(R.string.cat_Audio));
        int_position = getIntent().getIntExtra("value", 0);

        String tittle=Category_Explore_Activity.al_images.get(int_position).getStr_folder();
        Utility.setActivityTitle(mcontext,tittle);

         Intent_Audio_List = Category_Explore_Activity.al_images.get(int_position).getAl_imagepath();
         Intent_duration_List=Category_Explore_Activity.al_images.get(int_position).getAl_FileDuration();
         data_load();
         instance=this;
        alertDialogHelper =new AlertDialogHelper(this);


        if(audioList.size()!=0) {
            multiSelectAdapter = new MultiSelectAdapter_Audio(this, audioList, multiselect_list, this);
            //  AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, (int)Utility.px2dip(mcontext,150.0f));

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
            recyclerView.addItemDecoration(new DividerItemDecoration(mcontext,
                    DividerItemDecoration.VERTICAL));
            // recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(multiSelectAdapter);
        }
        else
        {
            blankIndicator.setVisibility(View.VISIBLE);
        }



        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    int pos = position;
                     if(pos!=RecyclerView.NO_POSITION)
                    multi_select(position);
                }

                else {


                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<Model_Audio>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position);

            }
        }));


    }
    public static AudioActivityRe getInstance() {
        return instance;
    }
    public void refreshAdapterAfterRename(String newPath,String newName)
    {
        fileTorename.setAudioPath(newPath);
        fileTorename.setAudiFileName(newName);
        audioList.set(renamePosition,fileTorename);
        refreshAdapter();

    }


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


    private void DispDetailsDialog( Model_Audio fileProperty )
    {

        if(fileProperty.getAudioPath() !=null)
        {
            File f = new File(fileProperty.getAudioPath());
            String[] splitPath = fileProperty.getAudioPath().split("/");
            String fName = splitPath[splitPath.length - 1];

            Dialog dialog = new Dialog(AudioActivityRe.this);
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
            resltxt.setText("Duration");


            FileName.setText(fName);
            FilePath.setText(fileProperty.getAudioPath());
            FileSize.setText(Utility.humanReadableByteCount(f.length(),true));
            FileDate.setText(Utility.LongToDate((f.lastModified())));
            Resolution.setText(fileProperty.getAudioFileDuration());



            dialog.show();
        }
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

        for (int i = 0; i < Intent_Audio_List.size(); i++)
        {
            Model_Audio model = new Model_Audio();
            File f=new File(Intent_Audio_List.get(i));
            model.setAudioPath(Intent_Audio_List.get(i));
            model.setAudiofileMDate(Utility.LongToDate(f.lastModified()));
            model.setAudioFileSize(Utility.humanReadableByteCount(f.length(),true));
            model.setAudiFileName(f.getName());
            if(i<Intent_duration_List.size())
            model.setAudioFileDuration(Intent_duration_List.get(i));
            audioList.add(model);
        }
    }


    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(audioList.get(position)))
                multiselect_list.remove(audioList.get(position));
            else {
                multiselect_list.add(audioList.get(position));

                // to  rename file contain old file;
                if(multiselect_list.size()==1) {
                     fileTorename = audioList.get(position);
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
        multiSelectAdapter.selected_AudioList=multiselect_list;
        multiSelectAdapter.AudioList=audioList;
        multiSelectAdapter.notifyDataSetChanged();
        selectMenuChnage();

       //finish action mode when user deselect files one by one ;
        if(multiselect_list.size()==0) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
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
                    if(multiselect_list.size()==1) {

                        Utility.fileRenameDialog(mcontext,multiselect_list.get(0).getAudioPath(),Constants.AUDIO);

                        }
                    return true;
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("","Delete Audio","DELETE","CANCEL",1,false);
                    return true;
                case R.id.action_select:
                    if(audioList.size()==multiselect_list.size() || isUnseleAllEnabled==true)
                        unSelectAll();
                    else
                        selectAll();
                    return  true;
                case  R.id.action_Share:
                    shareMultipleAudioWithNoughatAndAll();
                    return  true;
                case R.id.action_details:
                    if(multiselect_list.size()==1)//diplay details only for one selected image for now
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
            multiselect_list = new ArrayList<Model_Audio>();
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
                    audioList.remove(multiselect_list.get(i));

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
//            Model_Audio mImg = new Model_Audio();
//            mImg.setImgPath("");
//            audioList.add(mImg);
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
        ArrayList<Model_Audio> multiselect_list;
        DeleteFileTask( ArrayList<Model_Audio> multiselect_list)
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

            for(int i=0;i<audioList.size();i++)
            {
               if(!multiselect_list.contains(multiselect_list.contains(audioList.get(i))))
               {
                    multiselect_list.add(audioList.get(i));
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
            if(audioList.size()==multiselect_list.size()) {
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

    private int deleteFile(ArrayList<Model_Audio> delete_list)
    {
        int count=0;

        for(int i=0;i<delete_list.size();i++)
        {
            File f=new File(String.valueOf(delete_list.get(i).getAudioPath()));
            if(f.exists())
                if(f.delete()) {
                    count++;
                    sendBroadcast(f);
                }

        }
        Constants.DELETED_AUDIO_FILES=count;
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
    private void shareMultipleAudio() {

         if(multiselect_list.size()>0)
         {
        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
             // intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");

        sharingIntent.setType("audio/*"); /* Audio. */

        ArrayList<Uri> files = new ArrayList<Uri>();

        for(int i=0;i<multiselect_list.size();i++)
        {

            File file = new File(multiselect_list.get(i).getAudioPath());
            Uri uri = Uri.fromFile(file);
            files.add(uri);
        }
        sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(sharingIntent);
             }
             else
         {
             Toast.makeText(mcontext, "No files to share", Toast.LENGTH_SHORT).show();
         }

    }
    private void shareMultipleAudioWithNoughatAndAll() {

        if(multiselect_list.size()>0)
        {
            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            sharingIntent.setType("audio/*"); /* Audio. */
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            {
                ArrayList<Uri> files = new ArrayList<Uri>();

                for (int i = 0; i < multiselect_list.size(); i++) {
                    File file = new File(multiselect_list.get(i).getAudioPath());
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
                    File file = new File(multiselect_list.get(i).getAudioPath());
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

    @Override
    public void onAudioSelected(Model_Audio audioModel) {

           // Utility.OpenFile(mcontext,model_apk.getFilePath()); // open file below  Android N

         if(mActionMode==null)   // only open file when action mode  is not enabled;
        {
            Utility.OpenFileWithNoughtAndAll(audioModel.getAudioPath(),mcontext,getResources().getString(R.string.file_provider_authority));
        }






    }
    public  String calcSelectFileSize(ArrayList<Model_Audio> fileList)
    {
        long totalSize=0;

        for(int i=0;i<fileList.size();i++)
        {
            Model_Audio m =  fileList.get(i);
            File  f= new File(m.getAudioPath());
            totalSize+=f.length();
        }

        return  Utility.humanReadableByteCount(totalSize,true);
    }



}
