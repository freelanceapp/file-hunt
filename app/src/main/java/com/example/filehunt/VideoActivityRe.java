package com.example.filehunt;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.filehunt.Adapter.MultiSelectAdapter;
import com.example.filehunt.Adapter.MultiSelectAdapter_Video;
import com.example.filehunt.Class.Constants;
import com.example.filehunt.Model.Grid_Model;
import com.example.filehunt.Utils.AlertDialogHelper;
import com.example.filehunt.Utils.AutoFitGridLayoutManager;
import com.example.filehunt.Utils.CustomProgressDialog;
import com.example.filehunt.Utils.RecyclerItemClickListener;
import com.example.filehunt.Utils.Utility;
import com.example.filehunt.Utils.UtilityStorage;

import java.io.File;
import java.util.ArrayList;


import static com.example.filehunt.Class.Constants.PATH;
import static com.example.filehunt.Utils.Utility.pathToBitmap;


public class VideoActivityRe extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener ,MultiSelectAdapter_Video.VdoListener {

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
    private boolean isUnseleAllEnabled=false;
    private Grid_Model fileTorename;
    private int renamePosition;
    public static  VideoActivityRe instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity_re);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        blankIndicator=(ImageView) findViewById(R.id.blankIndicator);
        mcontext=VideoActivityRe.this;
        instance=this;
        UtilityStorage.InitilaizePrefs(mcontext);

        int_position = getIntent().getIntExtra("value", 0);
        String tittle=Category_Explore_Activity.al_images.get(int_position).getStr_folder();
        Utility.setActivityTitle(mcontext,tittle);
          Intent_Video_List = Category_Explore_Activity.al_images.get(int_position).getAl_imagepath();
          thumbList= Category_Explore_Activity.al_images.get(int_position).getAl_vdoThumb();// this list  will be part  of  Intent_Video_List using setter getter
          durationList=Category_Explore_Activity.al_images.get(int_position).getAlVdoDuration();


          data_load();



        alertDialogHelper =new AlertDialogHelper(this);



        if(Intent_Video_List.size()!=0) {

            multiSelectAdapter = new MultiSelectAdapter_Video(this, vidioList, multiselect_list, this);
            // AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, (int)Utility.px2dip(mcontext,150.0f));  // did not work on high resolution phones


            //set the width of column 20 %  of width of screen
            int columnWidthPercent = (getScreenWidth() * 20) / 100;
            AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, columnWidthPercent);
            recyclerView.setLayoutManager(layoutManager);
            //set the width of column 20 %  of width of screen
//        //set the number of columns as  per  width of screen
//         int columnCount=getScreenWidth()/100;
//        System.out.println(""+columnCount);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, columnCount));
//        //set the number of columns as  per  width of screen


            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(multiSelectAdapter);
        }
        else
        {
            blankIndicator.setVisibility(View.VISIBLE);
        }


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect && position!=RecyclerView.NO_POSITION)
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
            if (isPersistUriSet && multiselect_list.size() ==1)
            {
                       // call rename function  here in the case of premission granted first  time;

                Utility.renameFile(mcontext,multiselect_list.get(0).getImgPath(),Constants.Global_File_Rename_NewName,1);

            }

        }



    }

    public static VideoActivityRe getInstance() {
        return instance;
    }
    public void refreshAdapterAfterRename(String newPath,String newName)
    {
        fileTorename.setImgPath(newPath);
        vidioList.set(renamePosition,fileTorename);
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

        for (int i = 0; i < Intent_Video_List.size(); i++)
        {
            Grid_Model gridImg = new Grid_Model();
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
                if(multiselect_list.size()==1) {
                    fileTorename = vidioList.get(position);
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
        multiSelectAdapter.selected_VdoList=multiselect_list;
        multiSelectAdapter.VdoList=vidioList;
        multiSelectAdapter.notifyDataSetChanged();
        selectMenuChnage();

        //finish action mode when user deselect files one by one ;
        if(multiselect_list.size()==0) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
        }
    }
    private void DispDetailsDialog( Grid_Model fileProperty )
    {

        if(fileProperty.getImgPath() !=null)
        {
            File f = new File(fileProperty.getImgPath());
            String[] splitPath = fileProperty.getImgPath().split("/");
            String fName = splitPath[splitPath.length - 1];

            Dialog dialog = new Dialog(VideoActivityRe.this);
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
            FilePath.setText(fileProperty.getImgPath());
            FileSize.setText(Utility.humanReadableByteCount(f.length(),true));
            FileDate.setText(Utility.LongToDate((f.lastModified())));
            Resolution.setText(fileProperty.getVdoDuration());

             // Oreintation.setText("06:00");
            // Oreintation.setText(String.valueOf(Utility.getOrintatin(f))+"");

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
                        Utility.fileRenameDialog(mcontext,multiselect_list.get(0).getImgPath(),Constants.VIDEO);
                    return  true;
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("","Delete Video","DELETE","CANCEL",1,false);
                    return true;
                case R.id.action_select:
                    if(vidioList.size()==multiselect_list.size() || isUnseleAllEnabled==true)
                        unSelectAll();
                    else
                        selectAll();
                    return  true;
                case  R.id.action_Share:
                    shareMultipleVideo();
                    return  true;
                case R.id.action_details:
                    if(multiselect_list.size()==1)
                    {
                        DispDetailsDialog(multiselect_list.get(0));
                    }
                    else {
                            String size =calcSelectFileSize(multiselect_list);
                            System.out.println("" + size);
                            if(size!=null)
                                Utility.multiFileDetailsDlg(mcontext,size,multiselect_list.size());
                        }


                    return  true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<Grid_Model>();
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
                File f =new File(multiselect_list.get(0).getImgPath());

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
//                    vidioList.remove(multiselect_list.get(i));
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
    private class DeleteFileTask extends AsyncTask<Void,Void,Integer>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomProgressDialog.show(mcontext,getResources().getString(R.string.deleting_file));
        }

        ArrayList<Grid_Model> multiselect_list;
        DeleteFileTask( ArrayList<Grid_Model> multiselect_list)
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
                    vidioList.remove(multiselect_list.get(i));

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

            for(int i=0;i<vidioList.size();i++)
            {
               if(!multiselect_list.contains(multiselect_list.contains(vidioList.get(i))))
               {
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
            if(vidioList.size()==multiselect_list.size()) {
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

    private int deleteFile(ArrayList<Grid_Model> delete_list)
    {
        int count=0;

        for(int i=0;i<delete_list.size();i++)
        {
           // File f=new File(String.valueOf(delete_list.get(i).getImgPath().toLowerCase()));
            File f=new File(String.valueOf(delete_list.get(i).getImgPath()));
            if(f.exists()) {
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
        Constants.DELETED_VDO_FILES=count;

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
    private void shareMultipleVideo() {

         if(multiselect_list.size()>0)
         {
             Intent sharingIntent = new Intent();
             sharingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
             sharingIntent.setType("video/*");
             if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
             {
                 ArrayList<Uri> files = new ArrayList<Uri>();

                 for (int i = 0; i < multiselect_list.size(); i++) {
                     File file = new File(multiselect_list.get(i).getImgPath());
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
                     File file = new File(multiselect_list.get(i).getImgPath());
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
     private  int getScreenWidth() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        System.out.println(width);
        return  width;
    }

    @Override
    public void onVdoSelected(Grid_Model vdoModel) {


        // //function not being used can be deleted as  this code  is a part  of utility  now


                    // send the file to player
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(vdoModel.getImgBitmapStr()));
//                    intent.setDataAndType(Uri.parse(vdoModel.getImgBitmapStr()), "video/*");
//                    startActivity(intent);


        // Utility.OpenFile(mcontext,model_apk.getFilePath()); // open file below  Android N
        if(mActionMode==null)
        Utility.OpenFileWithNoughtAndAll(vdoModel.getImgBitmapStr(),mcontext,getResources().getString(R.string.file_provider_authority));

    }
    public  String calcSelectFileSize(ArrayList<Grid_Model> fileList)
    {
        long totalSize=0;

        for(int i=0;i<fileList.size();i++)
        {
            Grid_Model m =  fileList.get(i);
            File  f= new File(m.getImgPath());
            totalSize+=f.length();
        }

        return  Utility.humanReadableByteCount(totalSize,true);
    }

}
