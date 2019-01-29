package com.mojodigi.filehunt.AddsUtility;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import org.json.JSONException;
import org.json.JSONObject;

public class AddConstants
{



    public static final String API_RESPONSE_CODE="apiResponseCode";


    public static final String APP_NAME_POSTFIX= "_JMM";  // will  identify the client like  lava ,carbon etc ;
    public static final String VENDOR_ID="JMMFH001";
   // public static final String APP_NAME_POSTFIX= "_KAB";
   //public static final String VENDOR_ID="KABFH001";




    public static final String API_URL="http://development.bdigimedia.com/riccha_dev/App-Ad-Mgmt/getAdDetailsByAppName.php";

    public static final String ADD_PROVIDER_ID="addProvId";
    //sharedPrefKeys

    // if this value comes false from  the server end we will not diaplay adds in app, means will not display adds even using the addIds from local sring.xml file;
      public static final String SHOW_ADD="show_Add";

    // this will contain mainId if  assigned for addproject  from  dashboard of add providerNetWork ;
    public static  final String APP_ID="appID";
    //this will contain bannerId incase of google /PlaceMentId in case of Inmobi /bannerId incase of SMAATO
    public static  final String BANNER_ADD_ID="bannerId";
    //this will contain interestialAddId in case of google/InMobi  and AddSpaceId in case of SMAATOO
    //it shows fullscreenAdss
     public static  final String INTERESTIAL_ADD_ID="interestialId";

     //this will contain videoAddId in case of every add providerNetWork;
    public static  final String VIDEO_ADD_ID="videoAddId";


    public static final String NOT_FOUND="0";
    public static final String NO_ADDS="No ad is currently available matching the requesting parameter.";

    //sharedPrefKeys



    //InMobiVars
    public static final int BANNER_WIDTH_INMOBI = 320;
    public static final int BANNER_HEIGHT_INMOBI = 50;

    //for banner
      public static long YOUR_PLACEMENT_ID = 1473189489298L;  //sdk test
    // long YOUR_PLACEMENT_ID = 1542777410386L;  // mine created


    // placementId for Interstitial full screen
     public static long YOUR_PLACEMENT_ID_INTERESTIAL = 1475973082314L; //sdk  //testId


    //InMobiVars


    //JsonRequestkeys

    public static final String appName="appName";
    public static final String packageName="packageName";
    public static final String appVendorId="appVendorId";
    public static final String appManufacturer="appManufacturer";
    public static final String deviceModelKey="deviceModel";
    public  static final String deviceIdkey="deviceId";


  //AddProviderskeys    will  decide  which type of adds  need to  be displayed in mobile app;
  // this values will  be compared with  the server key addProvId and on the basis od that adds will be displayed;


    public static final String Adsense_Admob_GooglePrivideId="1";
    public static final String InMobiProvideId="2";
    public static final String SmaatoProvideId="3";




    public static JSONObject prepareAddJsonRequest(Context mContext, String valueVendorId)
    {

        String deviceModel="";
        String deviceManufacturer="";
        String deviceId="";
        try
        {
            deviceModel = android.os.Build.MODEL;
            deviceManufacturer = android.os.Build.MANUFACTURER;
            deviceId= Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            System.out.print(""+deviceId);

        }
        catch (Exception e)
        {

        }
        JSONObject object  =  new JSONObject();
        try {

            object.put(appName, mContext.getString(com.mojodigi.filehunt.R.string.app_name)+APP_NAME_POSTFIX);
            object.put(packageName,mContext.getPackageName());
            object.put(appVendorId,valueVendorId );
            object.put(appManufacturer, deviceManufacturer);
            object.put(deviceModelKey, deviceModel);
            object.put(deviceIdkey, deviceId);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return object;
    }


    public static boolean checkIsOnline(Context mContext)
    {
        ConnectivityManager ConnectionManager=(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true )
        {
            return true;

        }
        else
        {
          return false;

        }
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }



}
