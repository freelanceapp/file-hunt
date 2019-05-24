package com.mojodigi.filehunt.Class;

import com.mojodigi.filehunt.Model.Grid_Model;
import com.mojodigi.filehunt.Model.Model_images;

import java.util.ArrayList;

public class Constants {


    public  static int DELETED_APK_FILES=0;       // to update the delete count of apk ;
    public  static int DELETED_IMG_FILES=0;       // to update the delete count of apk ;
    public  static int DELETED_VDO_FILES=0;       // to update the delete count of apk ;
    public  static int DELETED_AUDIO_FILES=0;     // to update the delete count of apk ;
    public  static int DELETED_DOCUMENT_FILES=0;  // to update the delete count of apk ;
    public  static int DELETED_DOWNLOAD_FILES=0;  // to update the delete count of apk ;
    public  static int DELETED_ANIMATION_FILES=0; // to update the delete count of apk ;
    public  static int DELETED_RECENT_FILES=0;    // to update the delete count of apk ;


    public  static String POSITION="position";
    public  static int IMAGES=0;
    public  static int VIDEO=1;
    public  static int AUDIO=2;

    public static int DOCUMENT=3;
    public static int DOWNLOAD=4;
    public static int ANIMATION=5;
    public static int RECENT=6;
    public static int APK=7;
    public static int ZIP=9;
    public static int STORAGE=10;

    public static String MEDIA_DELETE_ACTIVITY_TRACKER="tracker";



    //  on ActivityresultConstants

         public static final int FILE_DELETE_REQUEST_CODE=3;
         public static final int FILE_RENAME_REQUEST_CODE=4;
         public static final int FOLDER_CREATE_REQUEST_CODE=5;
         public static final int COPY_REQUEST_CODE=6;
         public static final int APK_INSTALL_REQUEST_CODE=7;
    //

    public static String Global_File_Rename_NewName;
    public  static String WordMimeType="application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    public static String TAB_FRAGMENT_TAG="FragMent2";
    public static String PATH="ImgPath";
    public static String FileProtocol="file://";
    public static boolean isTextSizeChanged=false;
    public static String fileToBeRenamed;

  public static int  totalfolderCopied;


    public static final String PREFERENCE_URI = "URI";
    public static ArrayList<String> filesToCopy=new ArrayList<String>();
    public static boolean redirectToStorage=false;
    public static String pastePath="";


   public static ACTIVITY_ENUM ACTIVITY_TRACKER ;


    public static  enum ACTIVITY_ENUM
    {
        ACTIVITY_IMAGES, ACTIVTY_ZIP,ACTIVITY_VIDEO, ACTIVITY_AUDIO,ACTIVITY_ANIM,ACTIVITY_DOCS,ACTIVITY_DOWNLOAD,ACTIVITY_RECENT,ACTIVITY_APK,ACTIIVTY_STORAGE,ACTIVITY_NO_ACTIVITY;
    }


    // mimeTypes

    public static  final  String mimeType_excl="'application/vnd.ms-excel (.xls)'";
    public static  final  String mimeType_exclNew="'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet (.xlsx)'";
    public static  final  String mimeType_word="'application/msword (.doc)'";
    public static final   String mimeType_wordMime="'application/vnd.openxmlformats-officedocument.wordprocessingml.document'";
    public static final   String mimeType_NotePad="'text/plain'";
    public static final   String mimeType_Pdf="'application/pdf'";


    //https://www.iana.org/assignments/media-types/media-types.xhtml
    //we would decide  on the basis if Registries  (the first part in mimetype like audio/mp4    here audio is Registries )

   public static final  String mimeType_Audio_Registries="audio";
   public static final  String mimeType_Video_Registries="video";
   public static final  String mimeType_Img_Registries="image";


    public static final String selectedVdo="URI_SELECTED_VIDEO";
    public static final String selectedAdo="PATH_SELECTED_AUDIO";
    public static String fileInfoPath="FILE_PATH";
    public static String storageType="storage";
    public static String interNal="internal";
    public static String sdCard="sdcard";

    public static  ArrayList<Grid_Model> img_ArrayImgList=new ArrayList<>();
    public static final String CUR_POS_VIEW_PAGER="position";

    public  static final String mediaType="mediaType";

    public static Model_images model;

    public static  long fileSizeFilter=    1024*30; // 30  kb




    public static  String encryptFilesFolder=".FileHunt";
    public static String encrypImagesFolder="Images";
    public static String encrypVideosFolder="Videos";
    public static String encrypAudioFolder="Audios";
    public static String encrypDocsFolder="Documents";


    public static  String passDir=".mojoDigi";
    public static String passwordFile="filehunt.txt";
    public static  String passwordFileDes="filehunt.des";
    public static String encryptionPassword="x!23ghjt@#";
    public static String encryptedFilePassword=".des";


    public static int MEDIA_TYPE_IMG=1;
    public static int MEDIA_TYPE_VDO=2;
    public static int MEDIA_TYPE_ADO=3;
    public static int MEDIA_TYPE_DOC=4;
    public static final String isFcmRegistered="fcm";

    public static boolean isAsyncOperationStopped=false;


    //suggetsedApps packageNames;
    public static  final String screenLock="com.mojodigi.screenlock";
    public static  final String videoPlayer="com.mojodigi.videoplayer";
    public static  final String privacyUrl="http://mojodigitech.com/privacy-policy-for-file-hunt/";

}
