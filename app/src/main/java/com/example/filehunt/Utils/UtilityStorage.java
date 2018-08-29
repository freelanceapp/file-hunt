package com.example.filehunt.Utils;
import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public  class  UtilityStorage {

    private static boolean externalStorageReadable, externalStorageWritable;

    public static boolean isExternalStorageReadable() {
        checkStorage();
        return externalStorageReadable;
    }

    public static boolean isExternalStorageWritable() {
        checkStorage();
        return externalStorageWritable;
    }

    public static boolean isExternalStorageReadableAndWritable() {
        checkStorage();
        return externalStorageReadable && externalStorageWritable;
    }

    private static void checkStorage() {



        String state = Environment.getExternalStorageState();

        Boolean isExternalStorageRemovable = Environment.isExternalStorageRemovable() ;

        if (state.equals(Environment.MEDIA_MOUNTED) ) {   //Storage state if the media is present and mounted at its mount point with read/write access.
            externalStorageReadable = externalStorageWritable = true;
        } else if (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            externalStorageReadable = true;
            externalStorageWritable = false;
        } else {
            externalStorageReadable = externalStorageWritable = false;
        }
    }


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
public static boolean IsRemovableSdcard(Context mContext)
{

     StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
    Class<?> storageVolumeClazz = null;
    try {
        storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
        Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
        Method getPath = storageVolumeClazz.getMethod("getPath");
        Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
        Object result = getVolumeList.invoke(mStorageManager);
        final int length = Array.getLength(result);
        for (int i = 0; i < length; i++)
        {
            Object storageVolumeElement = Array.get(result, i);
            String path = (String) getPath.invoke(storageVolumeElement);
            boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
            return removable;
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
    return false;

}

}