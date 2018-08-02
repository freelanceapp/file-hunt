package com.example.filehunt.Fragments;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filehunt.Adapter.Adapter_Storage;
import com.example.filehunt.Model.Item;
import com.example.filehunt.R;
import com.example.filehunt.Utils.Utility;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class TabFragment2 extends Fragment  implements  Adapter_Storage.ItemListener{


    private   File path = new File(Environment.getExternalStorageDirectory() + "");
    private   File initial_path = new File(Environment.getExternalStorageDirectory() + "");
    static ArrayList<String> str = new ArrayList<String>();
    static ArrayList<File> pathList=new ArrayList<>();
    private static final String TAG = "F_PATH";
    private Item[] fileList;
    private ArrayList<Item> fileList1;

    private Boolean firstLvl = true;
     RecyclerView recyclerView;
    Context mcontext;
     Adapter_Storage multiSelectAdapter;
    TextView currentPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         mcontext=getActivity();
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        currentPath=(TextView)view.findViewById(R.id.currentPath);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.addItemDecoration(new DividerItemDecoration(mcontext, DividerItemDecoration.VERTICAL));
        fileList1=new ArrayList<>();
//        setCurrentDispPath();
//        pathList.add(path);
        loadFileList();


        multiSelectAdapter=new Adapter_Storage(mcontext,fileList1,this);
        recyclerView.setAdapter(multiSelectAdapter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
       if(isVisibleToUser) {

           System.out.println("VISIBLE");
           setCurrentDispPath();
           pathList.clear();
           pathList.add(path);
       }
       else {
           pathList.clear();
       }

    }

    private void setCurrentDispPath() {

        String pathstr=path.getAbsolutePath();
        currentPath.setText(pathstr);


    }

    public   int onBackPressed() {

        return  changePathOnBackPress();
    }
    public int  changePathOnBackPress() {


        int listSize=pathList.size();

      if (pathList.size() ==1 )
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

                path=pathList.get(pathList.size()-2);
                pathList.remove(pathList.size()-2);
                loadFileList();
                multiSelectAdapter.notifyDataSetChanged();
        }
        setCurrentDispPath();
        return listSize;


        }



    private   void loadFileList() {
        fileList1.clear();
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
                fileList = new Item[fList.length];
                for (int i = 0; i < fList.length; i++) {
                    fileList[i] = new Item(fList[i], R.mipmap.file_icon);
                    Item model = new Item(fList[i], R.mipmap.file_icon);
                    // Convert into file path
                    File sel = new File(path, fList[i]);

                    // Set drawables
                    if (sel.isDirectory()) {


                        model.setIcon(R.mipmap.directory_icon);
                        model.setFile(fList[i]);
                        model.setItemcount(sel.listFiles().length);
                        model.setFileModifiedDate(Utility.LongToDate(sel.lastModified()));
                        // model.setFilePath(sel.getAbsolutePath());
                        model.setIsDirecoty(true);

                        Log.d("DIRECTORY", fileList[i].file);
                    } else {
                        Log.d("FILE", fileList[i].file);

                        model.setFile(fList[i]);
                        model.setFileModifiedDate(Utility.LongToDate(sel.lastModified()));
                        model.setFilesize(Utility.humanReadableByteCount(sel.length(), true));
                        model.setFilePath(sel.getAbsolutePath());
                        model.setIsDirecoty(false);

                    }
                    fileList1.add(model);
                }
            }

            System.out.print(""+fileList);


//            if (!firstLvl) {
//                Item temp[] = new Item[fileList.length + 1];
//                for (int i = 0; i < fileList.length; i++) {
//                    temp[i + 1] = fileList[i];
//                }
//                temp[0] = new Item("Up", R.mipmap.directory_up);
//                fileList = temp;
            //  }


        } else {
            Log.e(TAG, "path does not exist");
        }
    }

    @Override
    public void onItemSelected(Item Item_model) {

        if (Item_model.getisDirecoty())
        {
            String chosenFile = Item_model.getFile();
            File sel = new File(path + "/" + chosenFile);
            //firstLvl = false;
            // Adds chosen directory to list
             str.add(chosenFile);
             fileList = null;
             fileList1.clear();
             path = new File(sel + "");

             pathList.add(path);
            loadFileList();
            setCurrentDispPath();

            multiSelectAdapter.notifyDataSetChanged();

        }
        else{

            //Utility.OpenFile(getActivity(),Item_model.getFilePath());
            Utility.OpenFileWithNoughtAndAll(Item_model.getFilePath(),getActivity(),getActivity().getResources().getString(R.string.file_provider_authority));
        }

    }
}
