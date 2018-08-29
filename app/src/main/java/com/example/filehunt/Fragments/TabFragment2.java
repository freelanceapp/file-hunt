package com.example.filehunt.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filehunt.Adapter.Adapter_Storage;
import com.example.filehunt.Model.Model_Storage;
import com.example.filehunt.Model.Model_Download;
import com.example.filehunt.R;
import com.example.filehunt.Utils.Utility;
import com.example.filehunt.Utils.UtilityStorage;

import java.util.Collections;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TabFragment2 extends Fragment  implements  Adapter_Storage.ItemListener{


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
    private ArrayList<Model_Storage> folderList;
    private ArrayList<Model_Storage> fileList_root;

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



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         mcontext=getActivity();


        internal_txt=(TextView)view.findViewById(R.id.internal_txt);
        sdcard_txt=(TextView)view.findViewById(R.id.sdcard_txt);

        sdcard_txt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));

        internal_txt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));
        sdcard_txt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));

        storage_Layout=(CardView)view.findViewById(R.id.storage_Layout);

        sdcard_change=(RelativeLayout)view.findViewById(R.id.sdcard_change);
        internal_change=(RelativeLayout)view.findViewById(R.id.internal_change);



         if(isSdcardPresent())
        {
            storage_Layout.setVisibility(View.VISIBLE);
        }
        else
        {
            storage_Layout.setVisibility(View.GONE);
        }



        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);



        blankIndicator=(ImageView)view.findViewById(R.id.blankIndicator);
        currentPath=(TextView)view.findViewById(R.id.currentPath);
        currentPath.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.addItemDecoration(new DividerItemDecoration(mcontext, DividerItemDecoration.VERTICAL));

        folderList=new ArrayList<>();
        fileList_root=new ArrayList<>();

       //default internal is selected
      //  internal_change.setBackground(getResources().getDrawable(R.drawable.st_tab_back));
        //sdcard_change.setBackgroundColor(getResources().getColor(R.color.gradation_03_dark));

        internal_change.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        sdcard_change.setBackgroundColor(getResources().getColor(R.color.grey_ebebeb));

        //default internal is selected

        pathList.clear();// new
        loadFileList();

        if(folderList.size()!=0) {
            multiSelectAdapter = new Adapter_Storage(mcontext, folderList, this);
            recyclerView.setAdapter(multiSelectAdapter);
        }
        else {
            blankIndicator.setVisibility(View.VISIBLE);
        }



        sdcard_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                internal_change.setBackgroundColor(getResources().getColor(R.color.gradation_03_dark));
//                sdcard_change.setBackground(getResources().getDrawable(R.drawable.st_tab_back));

                internal_change.setBackgroundColor(getResources().getColor(R.color.grey_ebebeb));
                sdcard_change.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                     path = new File(sdCardPath);
                     initial_path = new File(sdCardPath);
                     previous_path = new File(sdCardPath);
                     pathList.clear();
                     setCurrentDispPath();
                     loadFileList();

                     multiSelectAdapter.notifyDataSetChanged();

                if(folderList.size()==0) {

                    blankIndicator.setVisibility(View.VISIBLE);
                }
                else {
                    blankIndicator.setVisibility(View.GONE);
                }



            }

        });
        internal_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                internal_change.setBackground(getResources().getDrawable(R.drawable.st_tab_back));
//                sdcard_change.setBackgroundColor(getResources().getColor(R.color.gradation_03_dark));

                internal_change.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                sdcard_change.setBackgroundColor(getResources().getColor(R.color.grey_ebebeb));

                path = new File(Environment.getExternalStorageDirectory() + "");
                initial_path = new File(Environment.getExternalStorageDirectory() + "");
                previous_path = new File(Environment.getExternalStorageDirectory() + "");
                pathList.clear();
                loadFileList();
                setCurrentDispPath();
                multiSelectAdapter.notifyDataSetChanged();



                if(folderList.size()==0) {

                    blankIndicator.setVisibility(View.VISIBLE);
                }
                else {
                    blankIndicator.setVisibility(View.GONE);
                }



            }
        });

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
               multiSelectAdapter = new Adapter_Storage(mcontext, folderList, this);
               recyclerView.setAdapter(multiSelectAdapter);
           }
           else
           {
               blankIndicator.setVisibility(View.GONE);
           }


           //loads the data only fisrt time when fragment is visible to user if this is not done then at the time of installation first time the
           //storage details will not be visible so  call is once when fragment is visible to user

       }
       else {
          // pathList.clear();  new
           System.out.print(""+pathList);
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
                        // model.setFilePath(sel.getAbsolutePath());
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

            } else
                {

                //Utility.OpenFile(getActivity(),modelStorage_model.getFilePath());
                Utility.OpenFileWithNoughtAndAll(modelStorage_model.getFilePath(), getActivity(), getActivity().getResources().getString(R.string.file_provider_authority));
            }




    }
}
