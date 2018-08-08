package com.example.filehunt;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.example.filehunt.Adapter.MultiSelectAdapter;
import com.example.filehunt.Model.Grid_Model;
import com.example.filehunt.Utils.AlertDialogHelper;
import com.example.filehunt.Utils.AutoFitGridLayoutManager;
import com.example.filehunt.Utils.GridSpacingItemDecoration;
import com.example.filehunt.Utils.RecyclerItemClickListener;
import com.example.filehunt.Utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.filehunt.Class.Constants.PATH;
import static com.example.filehunt.Utils.Utility.pathToBitmap;


public class PhotosActivityRe extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener ,MultiSelectAdapter.ImgListener {

    ActionMode mActionMode;
    Menu context_menu;

   
    RecyclerView recyclerView;
    MultiSelectAdapter multiSelectAdapter;
    boolean isMultiSelect = false;

    ArrayList<Grid_Model> img_ImgList = new ArrayList<>();
    ArrayList<Grid_Model> multiselect_list = new ArrayList<>();
    private SearchView searchView;
    AlertDialogHelper alertDialogHelper;
    int int_position;
    ArrayList<String> Intent_Images_List;
    Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity_re);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mcontext=PhotosActivityRe.this;

        int_position = getIntent().getIntExtra("value", 0);

        Intent_Images_List = Category_Explore_Activity.al_images.get(int_position).getAl_imagepath();
        String tittle=Category_Explore_Activity.al_images.get(int_position).getStr_folder();
        Utility.setActivityTitle(mcontext,tittle);
        data_load();

        alertDialogHelper =new AlertDialogHelper(this);
        multiSelectAdapter = new MultiSelectAdapter(this,img_ImgList,multiselect_list,this);
        //  AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, (int)Utility.px2dip(mcontext,150.0f));   // did not work on high resolution phones


        //set the width of column 20 %  of width of screen
            int columnWidthPercent=(getScreenWidth()*20)/100;
            AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this,columnWidthPercent);
            recyclerView.setLayoutManager(layoutManager);
        //set the width of column 20 %  of width of screen


//        //set the number of columns as  per  width of screen
//         int columnCount=getScreenWidth()/100;
//        System.out.println(""+columnCount);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, columnCount));
//        //set the number of columns as  per  width of screen




        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.setAdapter(multiSelectAdapter);



        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multi_select(position);

                else {
//                    Intent intent = new Intent(getApplicationContext(), SingleViewMediaActivity.class);
//                    intent.putExtra(PATH, Category_Explore_Activity.al_images.get(int_position).getAl_imagepath().get(position));
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

        for (int i = 0; i < Intent_Images_List.size(); i++) {
            Grid_Model gridImg = new Grid_Model();
            gridImg.setImgPath(Intent_Images_List.get(i));
            img_ImgList.add(gridImg);
        }
    }


    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(img_ImgList.get(position)))
                multiselect_list.remove(img_ImgList.get(position));
            else
                multiselect_list.add(img_ImgList.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }


    public void refreshAdapter()
    {
        multiSelectAdapter.selected_ImgList=multiselect_list;
        multiSelectAdapter.ImgList=img_ImgList;
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
                    alertDialogHelper.showAlertDialog("","Delete Image","DELETE","CANCEL",1,false);
                    return true;
                case R.id.action_select:
                    selectAll();
                    return true;
                case  R.id.action_Share:
                    shareMultipleImagesWithNoughatAndAll();
                    return true;
                case R.id.action_details:
                      if(multiselect_list.size()==1)
                      {
                          DispDetailsDialog(multiselect_list.get(0).getImgPath());
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

    private void DispDetailsDialog(String imgPath )
    {

        String morientation="";
        File f =new File(imgPath);
        Bitmap bitmap=pathToBitmap(imgPath);
        int w=bitmap.getWidth();
        int h=bitmap.getHeight();

        System.out.println("ImageDetails path-> "+imgPath+"size \n "+ Utility.formatSize(f.length())+"\n resolution->"+w+"*"+h+"\n  orientation"+morientation);
        String[] splitPath=imgPath.split("/");

        String fName=splitPath[splitPath.length-1];


        Dialog dialog = new Dialog(PhotosActivityRe.this);
        dialog.setContentView(R.layout.file_property_dialog);
        // Set dialog title

        TextView FileName=dialog.findViewById(R.id.FileName);
        TextView FilePath=dialog.findViewById(R.id.FilePath);
        TextView FileSize=dialog.findViewById(R.id.FileSize);
        TextView FileDate=dialog.findViewById(R.id.FileDate);
        TextView Resolution=dialog.findViewById(R.id.Resolution);
        TextView Oreintation=dialog.findViewById(R.id.ort);

        FileName.setText(fName);
        FilePath.setText(imgPath);
        FileSize.setText(Utility.formatSize(f.length()));
        FileDate.setText(Utility.LongToDate((f.lastModified())));
        Resolution.setText(w+"*"+h);
        Oreintation.setText(Utility.getOrintatin(w,h));

        dialog.show();
    }






    // AlertDialog Callback Functions

    @Override
    public void onPositiveClick(int from) {
        if(from==1)
        {
            if(multiselect_list.size()>0)
            {
                new  DeleteFileTask(multiselect_list).execute();
                for(int i=0;i<multiselect_list.size();i++)
                    img_ImgList.remove(multiselect_list.get(i));

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
//            img_ImgList.add(mImg);
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

            Toast.makeText(mcontext, FileCount+" file deleted", Toast.LENGTH_SHORT).show();


        }
    }
    private void selectAll()
    {
        if (mActionMode != null)
        {
            multiselect_list.clear();

            for(int i=0;i<img_ImgList.size();i++)
            {
               if(!multiselect_list.contains(multiselect_list.contains(img_ImgList.get(i))))
               {
                    multiselect_list.add(img_ImgList.get(i));
               }
            }
            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
        }
    private int deleteFile( ArrayList<Grid_Model> delete_list)
    {
        int count=0;

        for(int i=0;i<delete_list.size();i++)
        {
            File f=new File(String.valueOf(delete_list.get(i).getImgPath()));
            if(f.exists())
                if(f.delete()) {
                    count++;
                    sendBroadcast(f);
                }

        }

        // clear  existing cache of glide library
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(mcontext).clearDiskCache();
               // Glide.getPhotoCacheDir(mcontext).delete();
            }
        }).start();

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
    private void shareMultipleImages() {

         if(multiselect_list.size()>0)
         {
        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
             // intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
             // //sharingIntent.setType("image/jpeg");
        sharingIntent.setType("image/*"); /* images. */

        ArrayList<Uri> files = new ArrayList<Uri>();

        for(int i=0;i<multiselect_list.size();i++)
        {

            File file = new File(multiselect_list.get(i).getImgPath());
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
    private void  shareMultipleImagesWithNoughatAndAll() {

        if(multiselect_list.size()>0)
        {
            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            sharingIntent.setType("*/*");
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
       // int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        System.out.println(width);
        return  width;
    }

    @Override
    public void onImageSelected(Grid_Model imgModel) {

        Intent intent = new Intent(getApplicationContext(), SingleViewMediaActivity.class);
       // intent.putExtra(PATH, Category_Explore_Activity.al_images.get(int_position).getAl_imagepath().get(position));
        intent.putExtra(PATH, imgModel.getImgPath());
        startActivity(intent);
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
