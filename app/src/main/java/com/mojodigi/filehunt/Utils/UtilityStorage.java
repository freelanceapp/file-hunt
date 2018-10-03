package com.mojodigi.filehunt.Utils;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Class.MediaStoreHack;
//

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import com.mojodigi.filehunt.R;
public  class  UtilityStorage {







    private  static SharedPreferences sharedPrefs;

     //https://gist.github.com/PauloLuan/4bcecc086095bce28e22
    // returns the path of android sd card using reflection
    public static String getExternalStoragePath(Context mContext, boolean is_removable) {


        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removable == removable) {

                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void guideDialogForLEXA(final Context ctx, String path, final int requestCode) {

        android.support.v7.app.AlertDialog.Builder  dialog=new android.support.v7.app.AlertDialog.Builder(ctx) ;
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.storage_perm_dialog, null);
        TextView textView = view.findViewById(R.id.description);
        TextView alerttext=view.findViewById(R.id.alerttext);

        textView.setText(ctx.getString(R.string.needsaccesssummary) + path + ctx.getString(R.string.needsaccesssummary1));
        textView.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(ctx));
        alerttext.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(ctx));
        ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.mipmap.sd_operate_step);

        dialog.setView(view);
       dialog.setPositiveButton(ctx.getResources().getString(R.string.open), new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

                UtilityStorage.triggerStorageAccessFramework(ctx,requestCode);
           }
       });
       dialog.setNegativeButton(ctx.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

               dialog.dismiss();
               Toast.makeText(ctx, ctx.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

           }
       });
               dialog.show();




    }



    public static  void InitilaizePrefs(Context ctx)
    {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }
    public static boolean setUriForStorage(int requestCode, int resultCode, Intent data)
    {
      // function will  be called onActivityResult of every activity

        if (requestCode == Constants.FILE_DELETE_REQUEST_CODE) {
            Uri treeUri;
            if (resultCode == Activity.RESULT_OK) {
                // Get Uri from Storage Access Framework.
                if(data!=null) {
                    treeUri = data.getData();
                    // Persist URI - this is required for verification of writability.
                    if (treeUri != null) {
                        getPrefs().edit().putString(Constants.PREFERENCE_URI, treeUri.toString()).commit();
                        return  true; // retutn  true if uri set successfully;
                    }
                }
            }
        }

        if (requestCode == Constants.FILE_RENAME_REQUEST_CODE) {
            Uri treeUri;
            if (resultCode == Activity.RESULT_OK) {
                // Get Uri from Storage Access Framework.
                if(data!=null) {
                    treeUri = data.getData();
                    // Persist URI - this is required for verification of writability.
                    if (treeUri != null) {
                        getPrefs().edit().putString(Constants.PREFERENCE_URI, treeUri.toString()).commit();
                        return  true; // retutn  true if uri set successfully;
                    }
                }
            }
        }

        return  false;
    }
    public static SharedPreferences getPrefs() {
        return sharedPrefs;
    }



    public static boolean isWritable(final File file) {
        if (file == null)
            return false;
        boolean isExisting = file.exists();

        try {
            FileOutputStream output = new FileOutputStream(file, true);
            try {
                output.close();
            } catch (IOException e) {
                // do nothing.
            }
        } catch (FileNotFoundException e) {
            return false;
        }
        boolean result = file.canWrite();

        // Ensure that file is not created during this process.
        if (!isExisting) {
            file.delete();
        }

        return result;
    }

//    public static boolean isWritableTarget(final File file)
//    {
//        if(file==null)
//            return  false;
//
//
//        if(file.isDirectory() && file.canWrite())
//        {
//            return file.canWrite();
//        }
//        return false;
//
//    }


     // check  whether  the  write permission  is allowed  on external sdcard;
    public static boolean isWritableNormalOrSaf(final File folder, Context c) {

        // Verify that this is a directory.
        if (folder == null || !folder.exists())
            return false;

//        if (!folder.exists() || !folder.isDirectory()) {
//            return false;
//        }

        // Find a non-existing file in this directory.
//        int i = 0;
//        File file =folder;
////        do {
////           // String fileName = "AugendiagnoseDummyFile" + (++i);
////            //file = new File(folder, fileName);
////            file = folder;
////
////        } while (file.exists());

        // First check regular writability
        if (isWritable(folder)) {
            return true;
        }

        // Next check SAF writability.
        DocumentFile document = getDocumentFile(folder, false, c);

        if (document == null) {
            return false;
        }

        // This should have created the file - otherwise something is wrong with access URL.
        boolean result = document.canWrite() && folder.exists();

        // Ensure that the dummy file is not remaining.
       // deleteFile(folder, c);   // new do not delete the file

        return result;
    }
    private static boolean rmdir(@NonNull final File file, Context context) {
        if (!file.exists()) return true;

        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for(File child : files) {
                rmdir(child, context);
            }
        }

        // Try the normal way
        if (file.delete()) {
            return true;
        }

        // Try with Storage Access Framework.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DocumentFile document = getDocumentFile(file, true, context);
            if(document != null && document.delete()) {
                return true;
            }
        }

        // Try the Kitkat workaround.
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // Delete the created entry, such that content provider will delete the file.
            resolver.delete(MediaStore.Files.getContentUri("external"), MediaStore.MediaColumns.DATA + "=?",
                    new String[]{file.getAbsolutePath()});
        }

        return !file.exists();
    }
    static boolean deleteFile(@NonNull final File file, Context context) {
        // First try the normal deletion.
        if (file == null) return true;
        boolean fileDelete = rmdir(file, context);
        if (file.delete() || fileDelete)
            return true;

        // Try with Storage Access Framework.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isOnExtSdCard(file, context)) {

            DocumentFile document = getDocumentFile(file, false, context);
            return document.delete();
        }

        // Try the Kitkat workaround.
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            ContentResolver resolver = context.getContentResolver();

            try {
                Uri uri = MediaStoreHack.getUriFromFile(file.getAbsolutePath(), context);
                resolver.delete(uri, null, null);
                return !file.exists();
            } catch (Exception e) {
                Log.e("LOg", "Error when deleting file " + file.getAbsolutePath(), e);
                return false;
            }
        }

        return !file.exists();
    }

    // from  audioActivity

    //new
    public static   boolean deleteWithAccesFramework(Context context, File file)
    {
        // Try with Storage Access Framework.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isOnExtSdCard(file, context)) {

            DocumentFile document = getDocumentFile(file, false, context);
            return document.delete();

        }
        else
        {
            return  false;
        }

    }

    public static   boolean reNameWithAccesFramework(Context context, File file,File newFile)
    {
        // Try with Storage Access Framework.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isOnExtSdCard(file, context)) {

            DocumentFile document = getDocumentFile(file, false, context);
            String str=newFile.getName();
            System.out.print(""+str);
            return document.renameTo(newFile.getName());

        }
        else
        {
            return  false;
        }

    }
    public static boolean isOnExtSdCard(final File file, Context c) {
        return getExtSdCardFolder(file, c) != null;
    }
    public static DocumentFile getDocumentFile(final File file, final boolean isDirectory, Context context) {

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
            return DocumentFile.fromFile(file);

        String baseFolder = getExtSdCardFolder(file, context);
        boolean originalDirectory = false;
        if (baseFolder == null) {
            return null;
        }

        String relativePath = null;
        try {
            String fullPath = file.getCanonicalPath();
            if (!baseFolder.equals(fullPath))
                relativePath = fullPath.substring(baseFolder.length() + 1);
            else originalDirectory = true;
        } catch (IOException e) {
            return null;
        } catch (Exception f) {
            originalDirectory = true;
            //continue
        }
        String as = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREFERENCE_URI,
                null);

        Uri treeUri = null;
        if (as != null) treeUri = Uri.parse(as);
        if (treeUri == null) {
            return null;
        }

        // start with root of SD card and then parse through document tree.
        DocumentFile document = DocumentFile.fromTreeUri(context, treeUri);
        if (originalDirectory) return document;
        String[] parts = relativePath.split("\\/");
        for (int i = 0; i < parts.length; i++) {
            DocumentFile nextDocument = document.findFile(parts[i]);

            if (nextDocument == null) {
                if ((i < parts.length - 1) || isDirectory) {
                    nextDocument = document.createDirectory(parts[i]);
                } else {
                    nextDocument = document.createFile("image", parts[i]);
                }
            }
            document = nextDocument;
        }

        return document;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getExtSdCardFolder(final File file, Context context) {
        String[] extSdPaths = getExtSdCardPaths(context);
        try {
            for (int i = 0; i < extSdPaths.length; i++) {
                if (file.getCanonicalPath().startsWith(extSdPaths[i])) {
                    return extSdPaths[i];
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String[] getExtSdCardPaths(Context context) {
        List<String> paths = new ArrayList<>();
        for (File file : context.getExternalFilesDirs("external")) {
            if (file != null && !file.equals(context.getExternalFilesDir("external"))) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w("Log", "Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        if (paths.isEmpty()) paths.add("/storage/sdcard1");
        return paths.toArray(new String[0]);
    }



    public static void triggerStorageAccessFramework(Context  ctx,int requestcode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        ((Activity)ctx).startActivityForResult(intent, requestcode);
    }


    public static int copyFileOrDirectory(Context  ctx,String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(ctx,src1, dst1);

                }
            } else {
                return copyFile(ctx,src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static int copyFile(Context ctx,File sourceFile, File destFile) throws IOException {

        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());

            Utility.RunMediaScan(ctx,destFile);
            return  1;

        }
        catch (Exception e)
        {
            String str=e.getMessage();
            System.out.print(""+str);
            return  0;
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }



//    public static boolean copyFile(final File source, final File target, Context context) {
//        FileInputStream inStream = null;
//        OutputStream outStream = null;
//        FileChannel inChannel = null;
//        FileChannel outChannel = null;
//
//        try {
//            inStream = new FileInputStream(source);
//
//            // First try the normal way
//            if (isWritableTarget(target)) {
//                // standard way
//                outStream = new FileOutputStream(target);
//                inChannel = inStream.getChannel();
//                outChannel = ((FileOutputStream) outStream).getChannel();
//                inChannel.transferTo(0, inChannel.size(), outChannel);
//            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    // Storage Access Framework
//                    DocumentFile targetDocument = getDocumentFile(target, false, context);
//                    outStream =
//                            context.getContentResolver().openOutputStream(targetDocument.getUri());
//                }
//
//                else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//                    // Workaround for Kitkat ext SD card
//                    Uri uri = MediaStoreHack.getUriFromFile(target.getAbsolutePath(), context);
//                    outStream = context.getContentResolver().openOutputStream(uri);
//                } else {
//                    return false;
//                }
//
//                if (outStream != null) {
//                    // Both for SAF and for Kitkat, write to output stream.
//                    byte[] buffer = new byte[16384]; // MAGIC_NUMBER
//                    int bytesRead;
//                    while ((bytesRead = inStream.read(buffer)) != -1) {
//                        outStream.write(buffer, 0, bytesRead);
//                    }
//                }
//
//            }
//        } catch (Exception e) {
//            Log.e("Error",
//                    "Error when copying file from " + source.getAbsolutePath() + " to " + target.getAbsolutePath(), e);
//            return false;
//        } finally {
//            try {
//                inStream.close();
//            } catch (Exception e) {
//                // ignore exception
//            }
//
//            try {
//                outStream.close();
//            } catch (Exception e) {
//                // ignore exception
//            }
//
//            try {
//                inChannel.close();
//            } catch (Exception e) {
//                // ignore exception
//            }
//
//            try {
//                outChannel.close();
//            } catch (Exception e) {
//                // ignore exception
//            }
//        }
//        return true;
//    }
//
//
}