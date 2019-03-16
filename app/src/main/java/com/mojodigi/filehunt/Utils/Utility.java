package com.mojodigi.filehunt.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.FaceDetector;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.media.ExifInterface;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.ServiceSpecificExtraArgs;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mojodigi.filehunt.Activity_Stotrage;
import com.mojodigi.filehunt.AddsUtility.AddConstants;
import com.mojodigi.filehunt.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.filehunt.AnimationActivityRe;
import com.mojodigi.filehunt.ApkActivityRe;
import com.mojodigi.filehunt.Application.MyApplication;
import com.mojodigi.filehunt.AudioActivityRe;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Class.CustomTypefaceSpan;
import com.mojodigi.filehunt.Class.Icons;
import com.mojodigi.filehunt.DocsActivityRe;
import com.mojodigi.filehunt.DownloadActivityRe;
import com.mojodigi.filehunt.PhotosActivityRe;
import com.mojodigi.filehunt.R;
import com.mojodigi.filehunt.RecentActivityRe;
import com.mojodigi.filehunt.VideoActivityRe;
import com.mojodigi.filehunt.ZipActivityRe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import static com.mojodigi.filehunt.Class.Constants.FileProtocol;

public class Utility extends Activity

{

    private static final String INTERNAL_VOLUME = "internal";
    public static final String EXTERNAL_VOLUME = "external";

    private static final String EMULATED_STORAGE_SOURCE = System.getenv("EMULATED_STORAGE_SOURCE");
    private static final String EMULATED_STORAGE_TARGET = System.getenv("EMULATED_STORAGE_TARGET");
    private static final String EXTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");

    static boolean fileStatus = false;
   private  static  SharedPreferenceUtil addprefs;
    public static long getAvailMemory(Context context) {

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);


        return memoryInfo.availMem;
    }


    public static long getTotalMemory(Context context) {

        String file = "/proc/meminfo";
        String memInfo;
        String[] strs;
        long memory = 0;

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);

            memInfo = bufferedReader.readLine();
            strs = memInfo.split("\\s+");
            for (String str : strs) {
                // L.d(AppUtil.class, str + "\t");
            }

            memory = Integer.valueOf(strs[1]).intValue();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return memory * 1024;
    }


    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        long availableBlocks = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getBlockCountLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getBlockCount();
        }
        return availableBlocks * blockSize;
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        long availableBlocks = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }

        return availableBlocks * blockSize;
    }

    public static boolean isPathExist(String pathStr, Context ctx) {
        File path = new File(pathStr);

        boolean pathexist = path.exists();
        //checking available memory too  as on some devices after unmounting sdcard the  path.exists() returns true;
        if (pathexist) {
            long TotalMemory_Ext = Utility.getTotalExternalMemorySize(UtilityStorage.getExternalStoragePath(ctx, true));
            return pathexist && TotalMemory_Ext > 0;
        } else {
            return pathexist;
        }

    }

    public static long getTotalExternalMemorySize(String pathStr) {
        File path = new File(pathStr);

        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        long availableBlocks = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getBlockCountLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getBlockCount();
        }
        return availableBlocks * blockSize;
    }

    public static long getAvailableExternalMemorySize(String pathStr) {
        File path = new File(pathStr);
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        long availableBlocks = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }

        return availableBlocks * blockSize;
    }


    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = " KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = " MB";
                size /= 1024;

                if (size >= 1024) {
                    suffix = " GB";
                    size /= 1024;
                }

            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }
        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    // https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
    public static String humanReadableByteCount(long bytes, boolean si) {

        // read this function

        //int unit = si ? 1000 : 1024;
        int unit = 1024;
        if (bytes < unit) return bytes + " Byte";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "KMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);


    }

    public static float px2dip(Context context, float pxValue) {
        DisplayMetrics mDisplayMetrics = getDisplayMetrics(context);
        return pxValue / mDisplayMetrics.density;
    }

    public static float dip2px(Context context, float dipValue) {
        DisplayMetrics mDisplayMetrics = getDisplayMetrics(context);
        return applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
                mDisplayMetrics);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null) {
            mResources = Resources.getSystem();

        } else {
            mResources = context.getResources();
        }
        // DisplayMetrics{density=1.5, width=480, height=854, scaledDensity=1.5,
        // xdpi=160.421, ydpi=159.497}
        // DisplayMetrics{density=2.0, width=720, height=1280,
        // scaledDensity=2.0, xdpi=160.42105, ydpi=160.15764}
        DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
        return mDisplayMetrics;
    }

    public static float applyDimension(int unit, float value,
                                       DisplayMetrics metrics) {
        switch (unit) {
            case TypedValue.COMPLEX_UNIT_PX:
                return value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return value * metrics.density;
            case TypedValue.COMPLEX_UNIT_SP:
                return value * metrics.scaledDensity;
            case TypedValue.COMPLEX_UNIT_PT:
                return value * metrics.xdpi * (1.0f / 72);
            case TypedValue.COMPLEX_UNIT_IN:
                return value * metrics.xdpi;
            case TypedValue.COMPLEX_UNIT_MM:
                return value * metrics.xdpi * (1.0f / 25.4f);
        }
        return 0;
    }


    public static Bitmap pathToBitmap(String path) {
        URL url = null;
        try {
            url = new URL(FileProtocol + path);
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }
        return null;
    }

    public static String LongToDate(String longV) {

        try {
            long input = Long.parseLong(longV.trim());
            Date date = new Date(input * 1000); // *1000 gives accurate date otherwise returns 1970
            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setCalendar(cal);
            cal.setTime(date);
            return sdf.format(date);
        }catch (Exception e)
        {
            return "";
        }
    }

    public static String LongToDate(Long date) {
        Date Date = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(Date);
        return formattedDate;
    }

    public static int getBitmapOrientation(Uri uri) {
        try {
            ExifInterface exif = new ExifInterface(uri.getPath());

            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            return rotationInDegrees;


        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static String convertDuration(long duration) {


        //convert the song duration into string reading hours, mins seconds
        int dur = (int) duration;

        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        String songTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        return songTime;
    }

    public static String getMimiTypefromPath(String path) {
        File file = new File(path);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return mimetype;
    }

    public static String getFileExtensionfromPath(String path) {
        try {
            File file = new File(path);
            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            return extension;
        } catch (Exception e) {
            return " ";
        }
    }

    public static void OpenFile(Context ctx, String path) {

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        File file = new File(path);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (extension.equalsIgnoreCase("") || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            intent.setDataAndType(Uri.fromFile(file), "text/*");
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimetype);
        }
        // custom message for the intent
        ctx.startActivity(Intent.createChooser(intent, "Choose an Application:"));

    }

    public static void shareFile(Context mContext, String fPath) {

        if (fPath != null) {
            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            sharingIntent.setType("*/*");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                ArrayList<Uri> files = new ArrayList<Uri>();


                File file = new File(fPath);
                Uri uri = Uri.fromFile(file);
                files.add(uri);
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                mContext.startActivity(sharingIntent);
            } else {
                ArrayList<Uri> files = new ArrayList<Uri>();
                File file = new File(fPath);
                Uri uri = FileProvider.getUriForFile(mContext, mContext.getResources().getString(R.string.file_provider_authority), file);
                files.add(uri);
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mContext.startActivity(sharingIntent);

            }

        } else {
            Utility.dispToast(mContext, "No files to share");
        }

    }

    public static void ShareSingleFile(String name, Context ctx, String authority) {
        //share the file for  NoughtAndAll

        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        File file = new File(name);
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
            if (uri != null) {
                if (extension.equalsIgnoreCase("") || mimetype == null) {
                    // if there is no extension or there is no definite mimetype, still try to open the file
                    intent.setType("text/*");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                } else {
                    intent.setType(mimetype);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                }
                // custom message for the intent
                ctx.startActivity(Intent.createChooser(intent, "Choose an Application:"));
            }
        } else {

            // in case of Android N and above Uri will be  made through provider written in Manifest file;
            uri = FileProvider.getUriForFile(ctx, authority,
                    file);

            if (uri != null) {
                if (extension.equalsIgnoreCase("") || mimetype == null) {
                    // if there is no extension or there is no definite mimetype, still try to open the file
                    intent.setType("text/*");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    intent.setType(mimetype);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                // custom message for the intent
                ctx.startActivity(Intent.createChooser(intent, "Choose an Application:"));
            }
            //

        }


    }

    public static void installApkNoughtAndAll(String name, Context ctx, String authority) {
        try {
            Uri uri = null;

            File file = new File(name.toLowerCase());
            String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                //uri = Uri.fromFile(file);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);

                uri = fileToContentUri(ctx, file);
                if (uri == null) uri = Uri.fromFile(file);

                if (uri != null) {
                    if (extension.equalsIgnoreCase("") || mimetype == null) {
                        // if there is no extension or there is no definite mimetype, still try to open the file
                        intent.setDataAndType(uri, "text/*");
                    } else {
                        //intent.setDataAndType(uri, mimetype);
                        intent.setData(uri);
                        intent.setType(mimetype);

                    }
                    // custom message for the intent
                    //ctx.startActivity(Intent.createChooser(intent, "Choose an Application:"));  // does not show just one and always options
                    ctx.startActivity(intent);

                }
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                // in case of Android N and above Uri will be  made through provider written in Manifest file;
                //uri = FileProvider.getUriForFile(ctx, authority, file);
                uri = fileToContentUri(ctx, file);
                if (uri == null)
                    uri = FileProvider.getUriForFile(ctx, authority, file);
                if (uri != null) {
                    if (extension.equalsIgnoreCase("") || mimetype == null) {
                        // if there is no extension or there is no definite mimetype, still try to open the file
                        intent.setDataAndType(uri, "text/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    } else {
                        intent.setDataAndType(uri, mimetype);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    // custom message for the intent
                    //ctx. startActivity(Intent.createChooser(intent, "Choose an Application:")); // does not show just one and always options
                    ctx.startActivity(intent);
                }
                //

            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ctx, "Application Not Found ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            String str = e.getMessage();
            System.out.println("" + str);
        }


    }

    public static void OpenFileWithNoughtAndAll_Apk(String name, Context ctx, String authority) {
        try {
            File file = new File(name.toLowerCase());
            String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(ctx, authority, file);
                if (uri != null) {
                    Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    if (extension.trim().isEmpty() || mimetype == null) {
                        intent.setDataAndType(uri, "text/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    ctx.startActivity(intent);
                }
            } else {
                Uri uri = Uri.fromFile(file);
                if (uri != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (extension.trim().isEmpty() || mimetype == null) {
                        intent.setDataAndType(uri, "text/*");
                    } else {
                        intent.setDataAndType(uri, mimetype);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    ctx.startActivity(intent);
                }
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ctx, "Application Not Found ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            String str = e.getMessage();
            System.out.println("" + str);
        }
    }
    public  static boolean hasUserHideStorages(SharedPreferenceUtil sharedPref)
    {
        if(sharedPref!=null)
           return sharedPref.getBoolanValue(AddConstants.KEY_HIDE_EXTERNAL_STORAGE, false);
        else
            return false;

    }

    public static void OpenFileWithNoughtAndAll(String name, Context ctx, String authority) {
        try {
            Uri uri = null;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(name.toLowerCase());
            String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                //uri = Uri.fromFile(file);

                uri = fileToContentUri(ctx, file);
                if (uri == null) uri = Uri.fromFile(file);

                if (uri != null) {
                    if (extension.equalsIgnoreCase("") || mimetype == null) {
                        // if there is no extension or there is no definite mimetype, still try to open the file
                        intent.setDataAndType(uri, "text/*");
                    } else {
                        intent.setDataAndType(uri, mimetype);
                    }
                    // custom message for the intent
                    //ctx.startActivity(Intent.createChooser(intent, "Choose an Application:"));  // does not show just one and always options
                    ctx.startActivity(intent);

                }
            } else {

                // in case of Android N and above Uri will be  made through provider written in Manifest file;
                //uri = FileProvider.getUriForFile(ctx, authority, file);
                uri = fileToContentUri(ctx, file);
                if (uri == null)
                    uri = FileProvider.getUriForFile(ctx, authority, file);
                if (uri != null) {
                    if (extension.equalsIgnoreCase("") || mimetype == null) {
                        // if there is no extension or there is no definite mimetype, still try to open the file
                        intent.setDataAndType(uri, "text/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    } else {
                        intent.setDataAndType(uri, mimetype);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    // custom message for the intent
                    //ctx. startActivity(Intent.createChooser(intent, "Choose an Application:")); // does not show just one and always options
                    ctx.startActivity(intent);
                }
                //

            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ctx, "Application Not Found ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            String str = e.getMessage();
            System.out.println("" + str);
        }


    }

    public static Uri fileToContentUri(Context context, File file) {
        // Normalize the path to ensure media search
        final String normalizedPath = normalizeMediaPath(file.getAbsolutePath());

        // Check in external and internal storages
        Uri uri = fileToContentUri(context, normalizedPath, file.isDirectory(), EXTERNAL_VOLUME);
        if (uri != null) {
            return uri;
        }
        uri = fileToContentUri(context, normalizedPath, file.isDirectory(), INTERNAL_VOLUME);
        if (uri != null) {
            return uri;
        }
        return null;
    }

    private static Uri fileToContentUri(Context context, String path, boolean isDirectory, String volume) {
        final String where = MediaStore.MediaColumns.DATA + " = ?";
        Uri baseUri;
        String[] projection;
        int mimeType = Icons.getTypeOfFile(path, isDirectory);

        switch (mimeType) {
            case Icons.IMAGE:
                baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            case Icons.VIDEO:
                baseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            case Icons.AUDIO:
                baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            default:
                baseUri = MediaStore.Files.getContentUri(volume);
                projection = new String[]{BaseColumns._ID, MediaStore.Files.FileColumns.MEDIA_TYPE};
        }

        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(baseUri, projection, where, new String[]{path}, null);
        try {
            if (c != null && c.moveToNext()) {
                boolean isValid = false;
                if (mimeType == Icons.IMAGE || mimeType == Icons.VIDEO || mimeType == Icons.AUDIO || mimeType == Icons.PDF || mimeType == Icons.APK || mimeType == Icons.DOCUMENTS
                        || mimeType == Icons.GIF || mimeType == Icons.PRESENTATION || mimeType == Icons.SPREADSHEETS || mimeType == Icons.TEXT) {
                    isValid = true;
                } else {
                    int type = c.getInt(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
                    isValid = type != 0;
                }

                if (isValid) {
                    // Do not force to use content uri for no media files
                    long id = c.getLong(c.getColumnIndexOrThrow(BaseColumns._ID));
                    return Uri.withAppendedPath(baseUri, String.valueOf(id));
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public static String normalizeMediaPath(String path) {
        // Retrieve all the paths and check that we have this environment vars
        if (TextUtils.isEmpty(EMULATED_STORAGE_SOURCE) ||
                TextUtils.isEmpty(EMULATED_STORAGE_TARGET) ||
                TextUtils.isEmpty(EXTERNAL_STORAGE)) {
            return path;
        }

        // We need to convert EMULATED_STORAGE_SOURCE -> EMULATED_STORAGE_TARGET
        if (path.startsWith(EMULATED_STORAGE_SOURCE)) {
            path = path.replace(EMULATED_STORAGE_SOURCE, EMULATED_STORAGE_TARGET);
        }
        return path;
    }

    public static String getOrintatin(int w, int h) {
        if (w > h) {
            return "Landscape";
        } else if (h > w) {
            return "Portrait";
        } else {
            return "Portrait";
        }
    }

    public static int getOrintatin(File f) {
        try {
            Uri contentUri = Uri.fromFile(f);
            int orientation = Utility.getBitmapOrientation(contentUri);
            return orientation;
        } catch (Exception e) {
            return 0;
        }


    }
   public static void setActivityTitle2(final Context ctx, String title)
   {
       android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) ctx).getSupportActionBar();

       //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF4500")));

       //  custom Layout

       LayoutInflater inflator = (LayoutInflater) ctx .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View v = inflator.inflate(R.layout.actionbarlayout, null);

       TextView tileleTxt=v.findViewById(R.id.titleTxt);
       ImageView backButton=v.findViewById(R.id.backButton);

        tileleTxt.setText(title);
        tileleTxt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(ctx));
        tileleTxt.setTextSize(Utility.getFontSizeValueHeading(ctx));



       backButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               ((AppCompatActivity) ctx).finish();
           }
       });
       //  custom Layout

       // Set the ActionBar display option
       actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
       actionBar.setCustomView(v);
   }


    public static void setActivityTitle(Context ctx, String title) {

        // this function works fine but not being used  now because another function
        //setActivityTitle2 provides custom layout in action bar  which enable  high level  of customization in user Interface


        //((AppCompatActivity)ctx).getSupportActionBar().setTitle(title);
        // ((AppCompatActivity)ctx).getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>"+title+"</font>"));
        Typeface tf = typeFace_adobe_caslonpro_Regular(ctx);
        SpannableString s = new SpannableString(title);


        //s.setSpan(new RelativeSizeSpan(2f), 0,s.length(), 0); // set size
        s.setSpan(new ForegroundColorSpan(ctx.getResources().getColor(R.color.titleColor)), 0, s.length(), 0);// set color
        s.setSpan(new CustomTypefaceSpan("", tf), 0, s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) ctx).getSupportActionBar();

        actionBar.setTitle(s);


        actionBar.setDisplayHomeAsUpEnabled(true);

        try {
            final Drawable upArrow = ctx.getResources().getDrawable(R.drawable.abc_ic_ab_back_material);

            //upArrow.setColorFilterctx(ctx.getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
            upArrow.setColorFilter(ctx.getResources().getColor(R.color.titleColor), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);


        } catch (Exception e) {

        }

    }

    public static String putStrinBrckt(String str) {

        return "(" + str + ")";
    }


    public static Typeface typeFace_adobe_caslonpro_Regular(Context ctx) {

        return Typeface.createFromAsset(ctx.getAssets(), "LatoSemibold.ttf");
    }

    public static Typeface typeface_caviar_dreams_Regular(Context ctx) {
        //return Typeface.createFromAsset(ctx.getAssets(), "caviar_dreams_Regular.ttf");
        return null;
    }

    public static int dpToPx(int dp, Context ctx) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public int pxToDp(int px, Context ctx) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    public static  int getFontSizeValueHeading(Context mContext)
    {
        if(addprefs!=null) {
            Log.d("TagTest", ""+addprefs);
            int txtSize = addprefs.getIntValue(AddConstants.KEY_TEXT_SIZE, 16);
            Log.d("fontSize- sizeUtility", ""+txtSize);
            return txtSize;
        }
        else
        {
            Log.d("TagTest", ""+addprefs);
            addprefs=new SharedPreferenceUtil(mContext);
            int txtSize = addprefs.getIntValue(AddConstants.KEY_TEXT_SIZE, 16);
            Log.d("fontSize- sizeUtility", ""+txtSize);
            return txtSize;
        }

    }



    public static  int getFontSizeValueSubHead(Context mContext)
    {
        if(addprefs!=null) {
            Log.d("TagTest", ""+addprefs);
            int txtSize = addprefs.getIntValue(AddConstants.KEY_TEXT_SIZE, 16);
            txtSize=txtSize*80/100;
            Log.d("fontSize- sizeUtility", ""+txtSize);
            return txtSize;
        }
        else
        {
            Log.d("TagTest", ""+addprefs);
            addprefs=new SharedPreferenceUtil(mContext);
            int txtSize = addprefs.getIntValue(AddConstants.KEY_TEXT_SIZE, 16);
            txtSize=txtSize*80/100;
            Log.d("fontSize- sizeUtility", ""+txtSize);
            return txtSize;
        }

    }
    public static  int getFontSizeValueSubHead3(Context mContext)
    {
        // for very small  text
        if(addprefs!=null) {
            Log.d("TagTest", ""+addprefs);
            int txtSize = addprefs.getIntValue(AddConstants.KEY_TEXT_SIZE, 16);
            txtSize=txtSize*70/100;
            Log.d("fontSize- sizeUtility", ""+txtSize);
            return txtSize;
        }
        else
        {
            Log.d("TagTest", ""+addprefs);
            addprefs=new SharedPreferenceUtil(mContext);
            int txtSize = addprefs.getIntValue(AddConstants.KEY_TEXT_SIZE, 16);
            txtSize=txtSize*70/100;
            Log.d("fontSize- sizeUtility", ""+txtSize);
            return txtSize;
        }

    }
    public static  int getFontSizeValueSubHead2(Context mContext)
    {
        // for very small  text
        if(addprefs!=null) {
            Log.d("TagTest", ""+addprefs);
            int txtSize = addprefs.getIntValue(AddConstants.KEY_TEXT_SIZE, 16);
            txtSize=txtSize*60/100;
            Log.d("fontSize- sizeUtility", ""+txtSize);
            return txtSize;
        }
        else
        {
            Log.d("TagTest", ""+addprefs);
            addprefs=new SharedPreferenceUtil(mContext);
            int txtSize = addprefs.getIntValue(AddConstants.KEY_TEXT_SIZE, 16);
            txtSize=txtSize*60/100;
            Log.d("fontSize- sizeUtility", ""+txtSize);
            return txtSize;
        }

    }
    public  static boolean isShowHiddenFiles(Context mContext)
    {
        boolean st;
        if(addprefs!=null) {
            st =addprefs.getBoolanValue(AddConstants.KEY_SHOW_HIDDEN_FILE, false);
            return st;
        }
        else
        {
             addprefs=new SharedPreferenceUtil(mContext);
             st =addprefs.getBoolanValue(AddConstants.KEY_SHOW_HIDDEN_FILE, false);
             return st;
        }
    }


    public static int percentOfValue(int TotalValue, int percent) {
        return TotalValue * percent / 100;
    }

    public static void multiFileDetailsDlg(Context ctx, String totalSize, int fileCount) {

        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.multifiledetais_dialog);
        // Set dialog title

        TextView FileNum = dialog.findViewById(R.id.FileNum);
        TextView FileSize = dialog.findViewById(R.id.FileSizem);
        TextView close = dialog.findViewById(R.id.close);
        FileNum.setText(String.valueOf(fileCount));
        FileSize.setText(totalSize);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    public static BigDecimal setdecimalPoints(String str, int offset) {
        BigDecimal decimal = new BigDecimal(str);
        decimal = decimal.setScale(offset, BigDecimal.ROUND_HALF_UP);
        return decimal;
    }

    public static void setCursorColor(EditText view, @ColorInt int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }

    public static void setCustomizeSeachBar(Context mcontext, android.support.v7.widget.SearchView searchView) {
        ImageView searchIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        ImageView crossIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);

        searchIcon.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.search_black));
        crossIcon.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.cross_black));

        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        searchEditText.setTextColor(mcontext.getResources().getColor(R.color.black));

        searchEditText.setSelection(0);

        searchEditText.setHintTextColor(mcontext.getResources().getColor(R.color.black));
        searchEditText.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));
        Utility.setCursorColor(searchEditText, mcontext.getResources().getColor(R.color.black));


    }

    // encryption functions
    public static boolean isManualPasswordSet()
    {
        // boolean status=false;
        try {
            String path = Environment.getExternalStorageDirectory() + "/" + Constants.passDir+"/"+Constants.passwordFileDes;
            File f = new File(path);
            return f.exists();
        }catch (Exception e)
        {
            return  false;
        }

    }

    public static String getEncryptFileName(String filePath, int cat_Type)
    {
        switch (cat_Type) {
            case 1://IMG
            filePath = Environment.getExternalStorageDirectory() + "/" + Constants.encryptFilesFolder + "/" + Constants.encrypImagesFolder + "/" + new File(filePath).getName() + ".des";
            break;
            case 2://VDO
                filePath = Environment.getExternalStorageDirectory() + "/" + Constants.encryptFilesFolder + "/" + Constants.encrypVideosFolder + "/" + new File(filePath).getName() + ".des";
                break;
            case 3: //ADO
                filePath = Environment.getExternalStorageDirectory() + "/" + Constants.encryptFilesFolder + "/" + Constants.encrypAudioFolder + "/" + new File(filePath).getName() + ".des";
                break;
            case 4://DOCS
                filePath = Environment.getExternalStorageDirectory() + "/" + Constants.encryptFilesFolder + "/" + Constants.encrypDocsFolder + "/" + new File(filePath).getName() + ".des";
                break;

            //filePath =filePath.substring(0, filePath.lastIndexOf("."))+".des";
        }
        return  filePath;
    }
    private static boolean checkOrCreateParentDirectory()
    {
        String path = Environment.getExternalStorageDirectory() + "/" + Constants.encryptFilesFolder;
        File file=new File(path);
        if(file.exists())
            return file.exists();
        else return  file.mkdir();


    }
    public static boolean createOrFindAppDirectory(int mediaType)
    {
        boolean status = false;

        if(checkOrCreateParentDirectory())
        {
            String path = "";

            switch (mediaType) {
                case 1:  //IMG
                    path = Environment.getExternalStorageDirectory() + "/" + Constants.encryptFilesFolder + "/" + Constants.encrypImagesFolder;
                    break;
                case 2: //VDO
                    path = Environment.getExternalStorageDirectory() + "/" + Constants.encryptFilesFolder + "/" + Constants.encrypVideosFolder;
                    break;
                case 3:  //ADO
                    path = Environment.getExternalStorageDirectory() + "/" + Constants.encryptFilesFolder + "/" + Constants.encrypAudioFolder;
                    break;
                case 4:  //DOCS
                    path = Environment.getExternalStorageDirectory() + "/" + Constants.encryptFilesFolder + "/" + Constants.encrypDocsFolder;
                    break;


            }
            File f = new File(path);
            if (!f.exists()) {
                if (f.mkdir())
                    status = true;
                else
                    status = false;
            } else
                status = true;


        }
        return  status;

    }

    public static String setDecryptFilePath(int mediaType)
    {
        switch (mediaType)
        {
            case 1://img
                 return Environment.getExternalStorageDirectory()+"/"+Constants.encryptFilesFolder+"/"+Constants.encrypImagesFolder+"/";

            case 2: //vdo
                return Environment.getExternalStorageDirectory()+"/"+Constants.encryptFilesFolder+"/"+Constants.encrypVideosFolder+"/";


            case 3://img
                return Environment.getExternalStorageDirectory()+"/"+Constants.encryptFilesFolder+"/"+Constants.encrypAudioFolder+"/";

            case 4: //vdo
                return Environment.getExternalStorageDirectory()+"/"+Constants.encryptFilesFolder+"/"+Constants.encrypDocsFolder+"/";

        }
        return  "";
    }
    public static  String readPasswordFile()
    {
        String path = Environment.getExternalStorageDirectory() + "/" + Constants.passDir+"/"+Constants.passwordFileDes;
        try {
            String password = Constants.encryptionPassword;
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            // SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");  //in java
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");  //in android
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
            File inputFile=new File(path);
            if(inputFile.exists())
            {
                FileInputStream fis = new FileInputStream(inputFile);

                byte[] salt = new byte[8];
                fis.read(salt);

                PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);

                //Cipher cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");  //in java
                Cipher cipher = Cipher.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");  // in android
                cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParameterSpec);

                //FileOutputStream fos = new FileOutputStream(outputFile);
                // FileOutputStream fos = new FileOutputStream("G:\\EncryptTest\\image\\Takendra_decrypted.jpg");
                byte[] in = new byte[64];
                int read;
                while ((read = fis.read(in)) != -1) {
                    byte[] output = cipher.update(in, 0, read);
                    // if (output != null)
                    // fos.write(output);
                }

                byte[] output = cipher.doFinal();
                if (output != null) {
                    // fos.write(output);
                    String s = new String(output);
                    return s;
                }

                fis.close();
                // fos.flush();
                //fos.close();
                // Utility.RunMediaScan(ctx,outputFile);
            }

        }catch (InvalidKeyException e)
        {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }



        return  "";
    }
    public static  int  createPasswordFile(Context ctx,String userPassword) {
        try {

            String path = Environment.getExternalStorageDirectory() + "/" + Constants.passDir;

            File f = new File(path);
            if (!f.exists()) {
                if (f.mkdir()) {
                    String cPath=path+"/"+Constants.passwordFile;
                    String data = userPassword;
                    FileOutputStream out = new FileOutputStream(cPath);
                    out.write(data.getBytes());
                    out.close();
                    File file=new File(cPath);

                    if (file.exists())
                    {
                        try {
                            FileInputStream inFile = new FileInputStream(file);
                            FileOutputStream outFile = new FileOutputStream(path+"/"+Constants.passwordFileDes);

                            String password = Constants.encryptionPassword;
                            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
                            // SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");  //in java
                            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");  //in android
                            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);




                            byte[] salt = new byte[8];
                            Random random = new Random();
                            random.nextBytes(salt);

                            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
                            //Cipher cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");  //in java
                            Cipher cipher = Cipher.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");  // in android
                            cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParameterSpec);
                            outFile.write(salt);

                            byte[] input = new byte[64];
                            int bytesRead;
                            while ((bytesRead = inFile.read(input)) != -1) {
                                byte[] output = cipher.update(input, 0, bytesRead);
                                if (output != null)
                                    outFile.write(output);
                            }

                            byte[] output = cipher.doFinal();
                            if (output != null)
                                outFile.write(output);

                            inFile.close();
                            outFile.flush();
                            outFile.close();

                            // delete  the  temporary file;
                            if(file.exists()) {
                                file.delete();
                                Utility.RunMediaScan(ctx,file);
                            }

                        }
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (InvalidAlgorithmParameterException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        }

                        return 1;

                    }


                } else {
                    Utility.dispToast(ctx, "file  not created"); // remove this message
                    return 0;
                }
            } else {
                Utility.dispToast(ctx, "can't create password");
                return 0;
            }
        }
        catch (Exception e)
        {
            return  0;
        }
        return  0;
    }


    public static void redirect(Context ctx, Class<?> target) {

        Intent i = new Intent(ctx, target);
        ctx.startActivity(i);

    }

    public static Bitmap creteVdoBitmapFromPath(String pathStr) {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(pathStr, MediaStore.Images.Thumbnails.MINI_KIND);
        return thumb;
    }

    //check if a string contains only white spaces;
    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static String formatTimer(long timer) {
        final long min = TimeUnit.SECONDS.toMinutes(timer);
        final long sec = TimeUnit.SECONDS.toSeconds(timer - TimeUnit.MINUTES.toMillis(min));
        return String.format("%02d:%02d", min, sec);
    }

    public static boolean isReadable(final File file) {
        if (file == null)
            return false;
        if (!file.exists()) return false;

        boolean result;
        try {
            result = file.canRead();
        } catch (SecurityException e) {
            return false;
        }

        return result;
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

    public static boolean IsNotEmpty(EditText view) {
        if (view.getText().length() > 0)
            return true;
        else
            return false;

    }

    public static boolean fileRenameDialog(final Context ctx, final String fPath, final int MediaType, boolean showRename_Error) {
        //https://github.com/sang89vh/easyfilemanager/blob/master/AmazeFileManagerSang89vhAdmob/src/main/java/com/mybox/filemanager/services/httpservice/FileUtil.java
        File f = new File(fPath);
        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.dialog_file_rename);
        // Set dialog title

        TextView headertxt = dialog.findViewById(R.id.headertxt);
        final EditText Edit_Rename = dialog.findViewById(R.id.Edit_Rename);
        if (showRename_Error)
            Edit_Rename.setError("File already exist.Try new name");


        TextView View_save = dialog.findViewById(R.id.View_save);
        TextView View_cancel = dialog.findViewById(R.id.View_cancel);

        headertxt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(ctx));
        Edit_Rename.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(ctx));
        View_cancel.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(ctx));
        View_save.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(ctx));

        if (f != null && !f.isDirectory()) {    //!f.isDirectory() new lines
            String extension = Utility.getFileExtensionfromPath(fPath.toLowerCase());
            Edit_Rename.setText(f.getName());
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            if (extension != null)
                // set cusror position ahead of file  extension ;
                Edit_Rename.setSelection(f.getName().length() - (extension.length() + 1));
            else
                Edit_Rename.setSelection(f.getName().length());
        }
        // new  part
        else if (f != null && f.isDirectory()) {
            Edit_Rename.setText(f.getName());
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            Edit_Rename.setSelection(f.getName().length());
        }
        // new  part


        View_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        View_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isWhitespace(Edit_Rename.getText().toString()))
                {
                    Edit_Rename.setError(ctx.getResources().getString(R.string.namerequired));

                    return;
                }

                if (Utility.IsNotEmpty(Edit_Rename))
                {
                    // will  be used on activityresult  function of every activity in case of filerename first time when write permission is granted
                    Constants.Global_File_Rename_NewName = Edit_Rename.getText().toString();
                    fileStatus = renameFile(ctx, fPath, Edit_Rename.getText().toString(), MediaType);
                    dialog.dismiss();
                } else {
                    Edit_Rename.setError(ctx.getResources().getString(R.string.emty_error));
                }

            }
        });


        dialog.show();


        return fileStatus;
    }

    public static boolean renameFile(Context pctx, String oldfpath, String newName, int MediaType) {

        File oldFile = new File(oldfpath);
        int i = oldfpath.lastIndexOf(File.separator);
        String pathstr = (i > -1) ? oldfpath.substring(0, i) : oldfpath;

        // String extension = Utility.getFileExtensionfromPath(path);
        //File latestname = new File(pathstr + "/" + newName + "." + extension);
        String nPath = pathstr + "/" + newName;
        File latestname = new File(pathstr + "/" + newName);

        //check whether the file exist with same name;


        boolean status = checkForAlreadyExist(latestname.getAbsolutePath().toString(), MediaType);
        if (status) {
            //Utility.dispToast(pctx,"File already exist");
            Utility.fileRenameDialog(pctx, oldfpath, MediaType, true);
            return false;// status of file rename
        }


        boolean success = oldFile.renameTo(latestname);

        // if normal  way  could  not rename file than use AccessFramework
        if (!success) {
            if (UtilityStorage.isWritableNormalOrSaf(oldFile, pctx))
                success = UtilityStorage.reNameWithAccesFramework(pctx, oldFile, latestname);
            else {
                UtilityStorage.guideDialogForLEXA(pctx, oldFile.getParent(), Constants.FILE_RENAME_REQUEST_CODE);
                return false; // do  not  procced to  generate Toast of file rename faild in case of asking for permission;
            }

        }

        if (success) {

            Toast.makeText(pctx, pctx.getResources().getString(R.string.rename_success), Toast.LENGTH_SHORT).show();

            switch (MediaType) {

                case 0: //images
                    PhotosActivityRe imgs = PhotosActivityRe.getInstance();
                    imgs.refreshAdapterAfterRename(pathstr + "/" + newName, newName);
                    break;
                case 1: //videos
                    VideoActivityRe vdo = VideoActivityRe.getInstance();
                    vdo.refreshAdapterAfterRename(pathstr + "/" + newName, newName);
                    break;
                case 2:  //audio
                    AudioActivityRe audio = AudioActivityRe.getInstance();
                    audio.refreshAdapterAfterRename(pathstr + "/" + newName, newName);
                    break;
                case 3:   // document
                    DocsActivityRe docs = DocsActivityRe.getInstance();
                    docs.refreshAdapterAfterRename(pathstr + "/" + newName, newName);
                    break;
                case 4:  // download
                    DownloadActivityRe download = DownloadActivityRe.getInstance();
                    download.refreshAdapterAfterRename(pathstr + "/" + newName, newName);
                    break;
                case 5:  //animation
                    AnimationActivityRe anim = AnimationActivityRe.getInstance();
                    anim.refreshAdapterAfterRename(pathstr + "/" + newName, newName);
                    break;
                case 6: //recent
                    RecentActivityRe recent = RecentActivityRe.getInstance();
                    recent.refreshAdapterAfterRename(pathstr + "/" + newName, newName);
                    break;

                case 7: //apk
                    ApkActivityRe apk = ApkActivityRe.getInstance();
                    apk.refreshAdapterAfterRename(pathstr + "/" + newName, newName);
                    break;

                case 8: //storage
                    Activity_Stotrage storage = Activity_Stotrage.getInstance();
                    if (storage != null)
                        storage.refreshAdapterAfterRename(pathstr + "/" + newName, newName);
                    break;

                case 9:

                    ZipActivityRe zip = ZipActivityRe.getInstance();
                    zip.refreshAdapterAfterRename(pathstr + "/" + newName, newName);
                    break;


            }


            //
            Utility.RunMediaScan(pctx, latestname);
            Utility.RunMediaScan(pctx, oldFile);




        } else {
            Toast.makeText(pctx, pctx.getResources().getString(R.string.rename_failed), Toast.LENGTH_SHORT).show();
        }

        return success;

    }

    private static boolean checkForAlreadyExist(String newFilePath, int MediaType) {
        switch (MediaType) {

            case 0: //images
                PhotosActivityRe imgs = PhotosActivityRe.getInstance();
                return imgs.checkForFileExist(newFilePath);

            case 1: //videos
                VideoActivityRe vdo = VideoActivityRe.getInstance();
                return vdo.checkForFileExist(newFilePath);

            case 2:  //audio
                AudioActivityRe audio = AudioActivityRe.getInstance();
                return audio.checkForFileExist(newFilePath);

            case 3:   // document
                DocsActivityRe docs = DocsActivityRe.getInstance();
                return docs.checkForFileExist(newFilePath);

            case 4:  // download
                DownloadActivityRe download = DownloadActivityRe.getInstance();
                return download.checkForFileExist(newFilePath);

            case 5:  //animation
                AnimationActivityRe anim = AnimationActivityRe.getInstance();
                return anim.checkForFileExist(newFilePath);

            case 6: //recent
                RecentActivityRe recent = RecentActivityRe.getInstance();
                return recent.checkForFileExist(newFilePath);


            case 7: //apk
                ApkActivityRe apk = ApkActivityRe.getInstance();
                return apk.checkForFileExist(newFilePath);


            case 8: //storage
//               TabFragment2 storage = TabFragment2.getInstance();
//               if (storage != null)
//                  return storage.checkForFileExist(newFilePath);
                break;
        }


        return false;
    }


    public static long getFolderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += getFolderSize(file);
        }
        return length;
    }


    // scan  the  system  after  rename a file  so  that file  is immedaitely visible to  user

    // the scan usually  runs  when device is restarted or sdcard is  plugged in
    public static void RunMediaScan(Context context, File fileName) {
        MediaScannerConnection.scanFile(
                context, new String[]{fileName.getPath()}, null,
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        System.out.println("acn connected");
                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                        System.out.println("scan completed");
                    }
                });
    }


    public static void removeFileFromCopyList(String fPath) {
        // this  function will  remove the file from copy operation  if file is deleted before pasting;
        if (Constants.filesToCopy != null && Constants.filesToCopy.size() > 0) {
            System.out.print("" + fPath);

            if (Constants.filesToCopy.contains(fPath)) {
                Constants.filesToCopy.remove(fPath);
            }
        }
    }


    public static long listFileSize(ArrayList<String> fileList) {
        long totalSize = 0;

        for (int i = 0; i < fileList.size(); i++) {
            String str = fileList.get(i);
            File f = new File(str);
            totalSize += f.length();
        }

        return totalSize;
    }


    public static void shareTracker(String categoryName, String label) {
        try {
            MyApplication.getInstance().trackEvent(categoryName, "share", label);
        } catch (Exception e) {
            String str = e.getMessage();
            System.out.print("" + str);
        }


    }

    public static Date longToDate(Long l) {
        Date d = new Date(l);
        return d;
    }

    public static void dispToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static void fileEncryptPasswordDialog(final Context mcontext) {
        //https://github.com/sang89vh/easyfilemanager/blob/master/AmazeFileManagerSang89vhAdmob/src/main/java/com/mybox/filemanager/services/httpservice/FileUtil.java

        final Dialog dialog = new Dialog(mcontext);
        dialog.setContentView(R.layout.dialog_folder_create);
        // Set dialog title

        TextView headertxt = dialog.findViewById(R.id.headertxt);
        final EditText encrypt_password_box = dialog.findViewById(R.id.encrypt_password_box);

        TextView View_encrypt = dialog.findViewById(R.id.View_encrypt);
        TextView View_cancel = dialog.findViewById(R.id.View_cancel);

        headertxt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));
        encrypt_password_box.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));
        View_cancel.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));
        View_encrypt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));

        View_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        View_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.IsNotEmpty(encrypt_password_box)) {

                    dispToast(mcontext, "encrypt");
                    dialog.dismiss();
                } else {
                    encrypt_password_box.setError(mcontext.getResources().getString(R.string.emty_error));
                }


            }

        });


        dialog.show();


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setstatusBarColorBelowM(Activity mContext) {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                Window window = mContext.getWindow();

                window.setStatusBarColor(mContext.getResources().getColor(R.color.material_grey_300)); // set dark color, the icon will auto change light
            }
        } catch (Exception e) {
            String string = e.getMessage();
            System.out.print("" + string);
        } finally {

        }
    }




    public static void hideKeyboard(Activity activity) {
        try {
            View v = activity.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null && v != null;
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception e)
        {

        }
    }

    public static void showKeyboard(Activity activity) {
        try {

            View v = activity.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null && v != null;
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        }catch (Exception e)
        {

        }
    }

    /* if want both the available storage visible send 1 in layoutVisibilityFlag
     *
     *
     * if want only internal  send 2, and for external  send 3 */

    public static void  dispLocalStorages(final Context mContext , int layoutVisibilityFlag )
    {

        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(R.layout.dialog_storages);
        TextView internal_txt=dialog.findViewById(R.id.internal_txt);
        TextView external_txt=dialog.findViewById(R.id.external_txt);
        internal_txt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        external_txt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
        View divider=dialog.findViewById(R.id.divider);
        LinearLayout internaLayout=dialog.findViewById(R.id.internaLayout);

        LinearLayout externLayout=dialog.findViewById(R.id.externLayout);
        divider.setSystemUiVisibility(View.GONE);
        externLayout.setVisibility(View.GONE);

        if(layoutVisibilityFlag==1) {

            try {
                String sdCardPath = UtilityStorage.getExternalStoragePath(mContext, true);
                // if sdcard is ejected the returned path will not exist;
                if (sdCardPath != null && Utility.isPathExist(sdCardPath, mContext)) {
                    divider.setSystemUiVisibility(View.VISIBLE);
                    externLayout.setVisibility(View.VISIBLE);

                }
            } catch (Exception e) {

            }
        }
        else if(layoutVisibilityFlag==3)
        {
            internaLayout.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);

            try {
                String sdCardPath = UtilityStorage.getExternalStoragePath(mContext, true);
                // if sdcard is ejected the returned path will not exist;
                if (sdCardPath != null && Utility.isPathExist(sdCardPath, mContext)) {
                    divider.setSystemUiVisibility(View.VISIBLE);
                    externLayout.setVisibility(View.VISIBLE);

                }
            } catch (Exception e) {

            }

        }
        else if(layoutVisibilityFlag==2)
        {
            internaLayout.setVisibility(View.VISIBLE);

        }


        internaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentImageGallary = new Intent(mContext, Activity_Stotrage.class);
                intentImageGallary.putExtra(Constants.storageType, Constants.interNal);
                mContext.startActivity(intentImageGallary);
                dialog.dismiss();

            }
        });

        externLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentImageGallary = new Intent(mContext, Activity_Stotrage.class);
                intentImageGallary.putExtra(Constants.storageType, Constants.sdCard);
                mContext.startActivity(intentImageGallary);

                dialog.dismiss();

            }
        });





        dialog.show();


    }

    public  static  void log_FirebaseActivity_Events(Activity activity,String activityName)
    {
         FirebaseAnalytics mFirebaseAnalytics= FirebaseAnalytics.getInstance(activity);
         if(mFirebaseAnalytics !=null)
         {
             Bundle bundle = new Bundle();
             bundle.putString(FirebaseAnalytics.Param.ITEM_ID, activityName);
             bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, activityName);
             bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity");
             mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
         }
    }




}
