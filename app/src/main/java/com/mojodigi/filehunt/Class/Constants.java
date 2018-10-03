package com.mojodigi.filehunt.Class;

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


    public  static  String POSITION="position";
    public  static int IMAGES=0;
    public  static int VIDEO=1;
    public  static int AUDIO=2;

    public static int DOCUMENT=3;
    public static int DOWNLOAD=4;
    public static int ANIMATION=5;
    public static int RECENT=6;
    public static int APK=7;



    //  on ActivityresultConstants

         public static final int FILE_DELETE_REQUEST_CODE=3;
         public static final int FILE_RENAME_REQUEST_CODE=4;
    //

    public static String Global_File_Rename_NewName;
    public  static  String WordMimeType="application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    public static String TAB_FRAGMENT_TAG="FragMent2";
    public static String PATH="ImgPath";
    public static String  FileProtocol="file://";

    public static String fileToBeRenamed;


    public static final String PREFERENCE_URI = "URI";
    public static ArrayList<String> filesToCopy=new ArrayList<String>();
    public static boolean redirectToStorage=false;
    public static String pastePath="";


    public static ArrayList<String> filesToCopy=new ArrayList<String>();
    public static boolean redirectToStorage=false;
    public static String pastePath="";

}
