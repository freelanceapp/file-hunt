package com.mojodigi.filehunt.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Model.Model_Storage;
import com.mojodigi.filehunt.R;
import com.mojodigi.filehunt.Utils.CustomProgressDialog;
import com.mojodigi.filehunt.Utils.Utility;
import com.mojodigi.filehunt.Utils.UtilityStorage;

import java.util.ArrayList;


public class copyAsyncTask extends AsyncTask<Integer, Integer, Integer> {
    Context mcontext;
    String destPath;

    public ArrayList<String> filesCopied = new ArrayList<String>();
    public ArrayList<String> filesToCopyAsync = new ArrayList<String>();
    private ArrayList<Model_Storage> fileList_root;
    int already_Exist_FileCount;
    boolean isPastinginInternal;
    public interface AsyncResponse {
        void copyFinish();
    }

    public AsyncResponse delegate = null;


    public copyAsyncTask(Context ctx, AsyncResponse delegate, ArrayList<String> filesToCopy, ArrayList<Model_Storage> pFileList_Root, String destpath,boolean isPastinginInternal) {
        filesToCopyAsync = filesToCopy;
        this.mcontext = ctx;
        this.destPath = destpath;
        this.delegate = delegate;
        this.fileList_root = pFileList_Root;  // to  check whether file aleady exsist on the current path;
        already_Exist_FileCount=0;
        this.isPastinginInternal=isPastinginInternal;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        CustomProgressDialog.show(mcontext, mcontext.getResources().getString(R.string.copying_msg));
    }

    @Override
    protected Integer doInBackground(Integer... integers) {

        int counter = 0;
        Constants.totalfolderCopied=0;
        try {
            for (int i = 0; i < filesToCopyAsync.size(); i++) {
               // Boolean b = isfileExistonCurrentPath(Constants.filesToCopy.get(i).toString());
                //System.out.print("" + b);
                if (isfileExistonCurrentPath(Constants.filesToCopy.get(i).toString())) {
                    filesCopied.add(Constants.filesToCopy.get(i).toString()); // add the file already exist to  list so  that  it can be removed from
                    //copy operation in below for loop;
                    System.out.print("file already exist");
                    continue;
                }

                int cnt = UtilityStorage.copyFileOrDirectory(mcontext, Constants.filesToCopy.get(i).toString(), destPath + "/",isPastinginInternal);
                if (cnt > 0) {
                    filesCopied.add(Constants.filesToCopy.get(i).toString());
                }
                counter += cnt;///
            }


            //remove the copied file from list;
            int delCount = 0;
            for (int i = 0; i < filesCopied.size(); i++) {

                if (Constants.filesToCopy.contains(filesCopied.get(i).toString())) {
                    delCount += 1;
                    Constants.filesToCopy.remove(filesCopied.get(i).toString());
                }
                System.out.print("" + delCount);


            }
        }catch (Exception e)
        {

        }

        return counter;
    }


    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        try {
            CustomProgressDialog.dismiss();
           // String msg = integer > 1 ? integer + " Items pasted" : integer + " Item pasted";
            String msg ="Copied successfully";
            Utility.dispToast(mcontext , msg );

            // Toast.makeText(mcontext, msg, Toast.LENGTH_SHORT).show();    // comments as  incorrect msg is coming;

            if (already_Exist_FileCount > 0) {
                String msgexst = already_Exist_FileCount > 1 ? already_Exist_FileCount + " Items already exist" : already_Exist_FileCount + " Item already exist";
                Utility.dispToast(mcontext, msgexst);
            }

            delegate.copyFinish();
        }
        catch (Exception e)
        {

        }
    }




    private boolean isfileExistonCurrentPath(String fPath) {

        for (int i = 0; i < fileList_root.size(); i++) {
            Model_Storage model = fileList_root.get(i);
            System.out.print("" + fPath);
            System.out.print("" + model.getFilePath());
            boolean b = model.getFilePath().equalsIgnoreCase(fPath);
            System.out.print("" + b);
            if (b) {
                ++already_Exist_FileCount;
                return true;
            }
        }

        return false;
    }

}
