package com.example.filehunt.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.example.filehunt.Model.Model_Anim;
import com.example.filehunt.Model.Model_Apk;
import com.example.filehunt.Model.Model_Docs;
import com.example.filehunt.Model.Model_Download;
import com.example.filehunt.Model.Model_Recent;
import com.example.filehunt.Model.Model_images;
import com.example.filehunt.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class AsynctaskUtility <T> extends AsyncTask<Void, Void, ArrayList<T>> {

 private Context mcontext;
    boolean boolean_folder;
 public  T model_type;
 int fileStorageType;


    public  AsynctaskUtility(Context mcontext,AsyncResponse delegate,int fileStorageType)
  {
      this.mcontext=mcontext;
      this.delegate=delegate;
      this.fileStorageType=fileStorageType;


  }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        CustomProgressDialog.show(mcontext,mcontext.getResources().getString(R.string.loading_msg));
    }

    // you may separate this or combined to caller class.
    public interface AsyncResponse<T>  {
        void processFinish(ArrayList<T> output);
    }
    public AsyncResponse delegate = null;


    @Override
    protected ArrayList<T> doInBackground(Void... voids) {

        switch (fileStorageType)
           {
               case 7:
                   return  (ArrayList<T>)listRecentFiles();
               case 4:
                   return  (ArrayList<T>) FetchDocuments();
               case 5:
                   return (ArrayList<T>)getDownLoads((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
               case 6:
                   return (ArrayList<T>)fetchAnimationFiles();
               case 8:
                   return  (ArrayList<T>) fetchApks();
               case 3:
                   return  (ArrayList<T>) FetchAudio();
               case 2:
                   return   (ArrayList<T>) FetchVideos();
               case 1:
                   return  (ArrayList<T>) Load_Images();



           }
           return null;


        }


    @Override
    protected void onPostExecute(ArrayList<T> list) {
        delegate.processFinish(list);

        CustomProgressDialog.dismiss();

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        System.out.println("task cancelled");


    }

    public ArrayList<Model_images>  Load_Images()
    {
        ArrayList<Model_images> al_images = new ArrayList<>();

        int int_position = 0;

        Cursor cursor;
        int column_index_data, column_index_folder_name,column_index_date_modified;

        String absolutePathOfImage = null;

          Uri  uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
          String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.MediaColumns.DATE_MODIFIED};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = mcontext.getContentResolver().query( uri, projection, null, null, orderBy + " DESC");
        if(cursor==null) {
            return al_images;
        }
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        column_index_date_modified= cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));

            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_path.add(absolutePathOfImage);
                al_images.get(int_position).setAl_imagepath(al_path);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setDate_modified(LongToDate(cursor.getString(column_index_date_modified)));
                obj_model.setAl_imagepath(al_path);

                al_images.add(obj_model);

                }


        }

        return al_images;
    }
    private  ArrayList<Model_images>   FetchVideos()
    {
        ArrayList<Model_images> al_images = new ArrayList<>();
        int int_position = 0;
        Cursor cursor;
        int column_index_data, column_index_folder_name,column_index_date_modified,thumb,column_index_duration;
        String absolutePathOfImage = null;
          Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.MediaColumns.DATE_MODIFIED,MediaStore.Video.Thumbnails.DATA,MediaStore.Video.VideoColumns.DURATION};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = mcontext.getContentResolver().query( uri, projection, null, null, orderBy + " DESC");
        if(cursor==null) {
            return al_images;
        }
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        column_index_date_modified= cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);
        thumb= cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
        column_index_duration=cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));
            String thumbstr=cursor.getString(thumb);
            long duration=cursor.getLong(column_index_duration);


            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                ArrayList<String> al_vdoThumb = new ArrayList<>();
                ArrayList<String> al_duration = new ArrayList<>();

                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_vdoThumb.addAll(al_images.get(int_position).getAl_vdoThumb());
                al_duration.addAll(al_images.get(int_position).getAlVdoDuration());

                al_path.add(absolutePathOfImage);
                al_vdoThumb.add(thumbstr);
                al_duration.add(Utility.convertDuration(duration));

                al_images.get(int_position).setAl_imagepath(al_path);
                al_images.get(int_position).setAl_vdoThumb(al_vdoThumb);
                al_images.get(int_position).setAlVdoDuration(al_duration);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                ArrayList<String> al_vdoThumb = new ArrayList<>();
                ArrayList<String> al_duration = new ArrayList<>();

                al_path.add(absolutePathOfImage);
                al_vdoThumb.add(thumbstr);
                al_duration.add(Utility.convertDuration(duration));

                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setDate_modified(LongToDate(cursor.getString(column_index_date_modified)));
                obj_model.setAl_imagepath(al_path);
                obj_model.setAl_vdoThumb(al_vdoThumb);
                obj_model.setAlVdoDuration(al_duration);

                al_images.add(obj_model);
            }



        }

        return al_images;
    }

    private ArrayList<Model_images>  FetchAudio()
    {
        ArrayList<Model_images> al_images = new ArrayList<>();
        int int_position = 0;
        Cursor cursor;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";  // only music file will be fetched
        int column_index_data, column_index_duration,column_index_folder_name,column_index_date_modified;
        String absolutePathOfImage = null;
        String fileDuration=null;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.DATE_MODIFIED,MediaStore.Audio.Media.DURATION};

        final String orderBy = MediaStore.Audio.Media.DATE_MODIFIED;
        cursor = mcontext.getContentResolver().query( uri, projection, selection, null, orderBy + " DESC");
            if(cursor==null) {
                return al_images;
            }
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
        column_index_date_modified= cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);
        column_index_duration=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            fileDuration= Utility.convertDuration(cursor.getLong(column_index_duration));

            Log.e("Duration",fileDuration);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));

            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                ArrayList<String> al_fileduration = new ArrayList<>();

                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_fileduration.addAll(al_images.get(int_position).getAl_FileDuration());

                al_path.add(absolutePathOfImage);
                al_fileduration.add(fileDuration);

                al_images.get(int_position).setAl_imagepath(al_path);
                al_images.get(int_position).setAl_FileDuration(al_fileduration);


            } else {
                ArrayList<String> al_path = new ArrayList<>();
                ArrayList<String> al_fileduration = new ArrayList<>();


                al_path.add(absolutePathOfImage);
                al_fileduration.add(fileDuration);
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setDate_modified(LongToDate(cursor.getString(column_index_date_modified)));
                obj_model.setAl_imagepath(al_path);
                obj_model.setAl_FileDuration(al_fileduration);
                al_images.add(obj_model);


            }


        }
        return al_images;


    }
    private String LongToDate(String longV)
    {
        long input=Long.parseLong(longV);
        Date date = new Date(input*1000); // *1000 gives accurate date otherwise returns 1970
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setCalendar(cal);
        cal.setTime(date);
        return sdf.format(date);

    }

    private ArrayList<Model_Apk>  fetchApks() {
        ArrayList<Model_Apk> ApkList = new ArrayList<>();

        final String[] projection = {MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.TITLE,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns.MEDIA_TYPE};
        final String orderBy = MediaStore.Files.FileColumns.DATE_ADDED;
        Cursor cursor = mcontext.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                projection, null, null, orderBy+" desc");
        if (cursor == null)
           return  ApkList;
        else if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String fileName=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                //long fileDateModified=cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                String fileDateModified=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
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
 return  ApkList;
    }

    private  ArrayList<Model_Recent> listRecentFiles()
    {
        //MediaStore.Files.FileColumns.DISPLYA_NAME returns null on some devices
        ArrayList<Model_Recent> RecentListLocal = new ArrayList<>();
        final String[] projection = {MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns.MIME_TYPE};
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - 7);
        Date d = c.getTime();
        final String orderBy = MediaStore.Files.FileColumns.DATE_ADDED;
        Cursor cursor = mcontext.getContentResolver().query(MediaStore.Files
                        .getContentUri("external"), projection,
                null,
                null, orderBy+" desc");
        String FileType="";
        if (cursor == null)
            return RecentListLocal;

        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                // if user click  back button while this  process  is running;
                if(isCancelled())
                {
                    System.out.println("process killed");
                    break;
                }



                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String fileName=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                long fileSize=cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                long mDate=cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
                // String type=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
              //sent from wahtsapp
                String[] types = new String[]{"pdf","png","jpeg","jpg","mp4","mp3","aac","amr","gif","doc", "docx", "txt", "wpd", "wps","xls","xlsx",
                        "pptx"
                };

                // if any file type needed add extension here and task is done

//                String[] filePath=   path.split("/");
//                String FileName=filePath[filePath.length-1];
//                String[] s1=FileName.split("\\.");
//                if(s1.length==1)
//                    FileType=s1[0];
//                else if(s1.length==2)
//                    FileType=s1[1];
                FileType=Utility.getFileExtensionfromPath(path);


               Log.d("recent files mimeType",""+FileType);
                File f = new File(path);
                if (d.compareTo(new Date(f.lastModified())) != 1 && !f.isDirectory()  && Arrays.asList(types).contains(FileType)) {
                    Model_Recent model=new Model_Recent();

                    model.setFileSize(Utility.humanReadableByteCount(fileSize,true));
                    model.setFileName(fileName);
                    model.setFileMdate(Utility.LongToDate(String.valueOf(mDate)));
                    model.setFilePath(path);
                    model.setFileType(Utility.getFileExtensionfromPath(path));
                    RecentListLocal.add(model);

                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        //Collections.sort(recentFiles, (lhs, rhs) -> -1 * Long.valueOf(lhs.date).compareTo(rhs.date));

//        if (RecentListLocal.size() > 20)
//            for (int i = RecentListLocal.size() - 1; i > 20; i--) {
//                RecentListLocal.remove(i);
//            }


        return RecentListLocal;


    }
    private  ArrayList<Model_Docs> FetchDocuments()
    {
        ArrayList<Model_Docs> docsList = new ArrayList<>();
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "="+mimeType;

         final String[] projection = {MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.TITLE,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns.MEDIA_TYPE};
         String orderby= MediaStore.Files.FileColumns.DATE_ADDED;
         Cursor cursor = mcontext.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                projection, null, null, orderby+" desc");

        String[] types = new String[]{"pdf","doc", "docx", "txt", "wpd", "wps","xls","xlsx","ppt",
                "pptx"
        };     // if any file type needed add extension here and task is done

        // if any file type needed add extension here and task is done

        if (cursor == null) {
          return   docsList;

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
                    String fileName=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                    //long fileDateModified=cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                    String fileDateModified=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
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

    return docsList;

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
                    model.setDateToSort(f.lastModified());
                    model.setFileSize(Utility.humanReadableByteCount(f.length(),true));
                    model.setFileName(f.getName());
                    model.setFiletype(Utility.getFileExtensionfromPath(listFile[i].toString()));
                    downLoadListLocal.add(model);
                }
            }
        }
        Collections.sort(downLoadListLocal, new Comparator<Model_Download>() {
            public int compare(Model_Download o1, Model_Download o2) {
                return String.valueOf(o1.getDateToSort()).compareTo(String.valueOf(o2.getDateToSort()));
            }
        });

        Collections.reverse(downLoadListLocal);
        return downLoadListLocal;

    }

    private  ArrayList<Model_Anim> fetchAnimationFiles() {

        ArrayList<Model_Anim> animList = new ArrayList<>();
        final String[] projection = {MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.TITLE,MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns.MEDIA_TYPE};
        final String orderBy = MediaStore.Files.FileColumns.DATE_ADDED;
        Cursor cursor = mcontext.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                projection, null, null, orderBy+" desc");

        if (cursor == null)
           return animList;
        else if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String fileName=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                //long fileDateModified=cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                String fileDateModified=cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
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
           return  animList;
    }




}
