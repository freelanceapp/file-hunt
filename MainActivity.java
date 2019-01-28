package com.mojodigi.filehunt;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.mojodigi.filehunt.AddsUtility.AddConstants;
import com.mojodigi.filehunt.AddsUtility.AddMobUtils;
import com.mojodigi.filehunt.AddsUtility.JsonParser;
import com.mojodigi.filehunt.AddsUtility.OkhttpMethods;
import com.mojodigi.filehunt.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Model.category_Model;
import com.mojodigi.filehunt.R;
import com.mojodigi.filehunt.Utils.Utility;
import com.mojodigi.filehunt.Utils.UtilityStorage;
import com.smaato.soma.AdDownloaderInterface;
import com.smaato.soma.AdListenerInterface;
import com.smaato.soma.BannerView;
import com.smaato.soma.ErrorCode;
import com.smaato.soma.ReceivedBannerInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mojodigi.filehunt.Class.Constants.POSITION;

public class MainActivity extends AppCompatActivity implements AdListenerInterface {


    Toolbar toolbar;
    RecyclerView recyclerView;
    List<category_Model> catList = new ArrayList<category_Model>();
    Context mContext;

    ProgressBar progressBar, progressBar_Ext;
    TextView avlbMemory, totalMemmory, internalTxt, avlbTxt;
    TextView avlbMemory_Ext, totalMemmory_Ext, internalTxt_Ext, avlbTxt_Ext;
    RelativeLayout storage_section;
    LinearLayout ext_layout,internalLLayout;

    private Menu context_menu;

    String[] permissionsRequired = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;


    //add vars
    BroadcastReceiver internetChangerReceiver;
    SharedPreferenceUtil addprefs;
    private AdView mAdView;
    BannerView smaaTobannerView;
    RelativeLayout smaaToAddContainer;
    View adContainer;
    RelativeLayout addhoster;

    //add vars


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=MainActivity.this;
        permissionStatus =mContext.getSharedPreferences("permissionStatus", MODE_PRIVATE);


        askForPermission();



        mContext=MainActivity.this;
        if(mContext!=null)
        {
            addprefs = new SharedPreferenceUtil(mContext);
            addhoster=findViewById(R.id.addhoster);
            mAdView = (AdView) findViewById(R.id.adView);
            adContainer = findViewById(R.id.adMobView);
            smaaToAddContainer = findViewById(R.id.smaaToAddContainer);

            //smaaTobannerView =  findViewById(R.id.smaaTobannerView);

            smaaTobannerView = new BannerView((this).getApplication());
            smaaTobannerView.addAdListener(this);


            }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utility.setstatusBarColorBelowM(MainActivity.this);
        }

        dispbannerAdd();

        // this broadcast  will  listen the  internet state change for sendig request  when internet becomes available
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        internetChangerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean isNetworkAvailable = AddConstants.checkIsOnline(mContext);

                //  Toast.makeText(context, "isNetworkAvailable-->" + isNetworkAvailable, Toast.LENGTH_SHORT).show();

                Log.d("isNetworkAvailable", "" + isNetworkAvailable);
                if (isNetworkAvailable) {
                    long ms1=System.currentTimeMillis();
                    System.out.print("Milliseconds-->>"+ms1);
                    Log.e("Milliseconds before-->>", ""+ms1);
                    new WebCall().execute();

                } else {
                    if (mAdView != null && addprefs != null) {
                        AddMobUtils util = new AddMobUtils();
                        util.displayLocalBannerAdd(mAdView);
                        //util.showInterstitial(addprefs,HomeActivity.this, null);
                        //util.displayRewaredVideoAdd(addprefs, mContext, null);
                    }
                }
            }

        };
        registerReceiver(internetChangerReceiver, intentFilter);
        // this broadcast  will  listen the  internet state change for sendig request  when internet becomes available

       }

    @Override
    protected void onResume() {
        super.onResume();

       // dispbannerAdd();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void dispbannerAdd()
{
    // displat add from preferences

    AddMobUtils adutil = new AddMobUtils();

    if(AddConstants.checkIsOnline(mContext) && adContainer !=null && addprefs !=null)
    {
        String AddPrioverId=addprefs.getStringValue(AddConstants.ADD_PROVIDER_ID, AddConstants.NOT_FOUND);
        if(AddPrioverId.equalsIgnoreCase(AddConstants.Adsense_Admob_GooglePrivideId)) {
          adutil.displayServerBannerAdd(addprefs, adContainer, mContext);

          }
        else if(AddPrioverId.equalsIgnoreCase(AddConstants.SmaatoProvideId))
        {
            try {
                int publisherId = Integer.parseInt(addprefs.getStringValue(AddConstants.APP_ID, AddConstants.NOT_FOUND));
                int addSpaceId = Integer.parseInt(addprefs.getStringValue(AddConstants.BANNER_ADD_ID, AddConstants.NOT_FOUND));
                adutil.displaySmaatoBannerAdd(smaaTobannerView, smaaToAddContainer, publisherId, addSpaceId);
            }catch (Exception e)
            {
                String string = e.getMessage();
                System.out.print(""+string);
            }
        }

    }
    else {
        adutil.displayLocalBannerAdd(mAdView);
    }

    //  banner add
    // displat add from preferences
}

    public void askForPermission()
    {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[0]) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permissionsRequired[1])) {
                //Show Information about why you need the permission

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Need Permissions");
                builder.setMessage(mContext.getString(R.string.app_name) + " needs to access your storage.");

                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions((Activity) mContext, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Need Permissions");
                builder.setMessage(mContext.getString(R.string.app_name) + " app need stoarge permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(mContext, "Go to Permissions to Grant storage access", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions((Activity) mContext, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], false);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
           initActivityComponents();
           iniVars();

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                initActivityComponents();
                iniVars();


            } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permissionsRequired[0]) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permissionsRequired[1])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Need Permissions");
                builder.setMessage(mContext.getString(R.string.app_name) + " app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions((Activity) mContext,permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(mContext, "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }


    }


    private void iniVars() {

        category_Model cat_Img=new category_Model("Image");
        category_Model cat_Apk=new category_Model("Apps");
        category_Model cat_Animation=new category_Model("Animation");
        category_Model cat_Audio=new category_Model("Audio");
        category_Model cat_Video=new category_Model("Video");
        category_Model cat_Download=new category_Model("Download");
        category_Model cat_Document=new category_Model("Document");
        category_Model cat_Recent=new category_Model("Recent");
        category_Model cat_zip=new category_Model("Zip");


        catList.add(cat_Img);
        catList.add(cat_Video);
        catList.add(cat_Audio);
        catList.add(cat_Document);
        catList.add(cat_Download);
        catList.add(cat_Animation);
        catList.add(cat_Recent);
        catList.add(cat_Apk);
        catList.add(cat_zip);




        categoryAdapter cat_adapter = new categoryAdapter(catList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL));
        // recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cat_adapter);



        if(mContext!=null) {

            fillProgressBar();

            try {
                String sdCardPath = UtilityStorage.getExternalStoragePath(mContext, true);
                // if sdcard is ejected the returned path will not exist;
                if (sdCardPath != null && Utility.isPathExist(sdCardPath, mContext)) {
                    ext_layout.setVisibility(View.VISIBLE);
                    fillProgressBar_Ext();

                }
            } catch (Exception e) {

            }


        }



    }

    private void initActivityComponents()
    {
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_view);

        storage_section=findViewById(R.id.storage_section);
        storage_section.setVisibility(View.VISIBLE);
        ext_layout =  findViewById(R.id.ext_layout);
        internalLLayout=findViewById(R.id.internalLLayout);
        progressBar = findViewById(R.id.progressBar);
        avlbMemory =  findViewById(R.id.avlbMemory);
        totalMemmory = findViewById(R.id.totalMemmory);
        internalTxt =  findViewById(R.id.internalTxt);
        avlbTxt =  findViewById(R.id.avlbTxt);

        progressBar_Ext =  findViewById(R.id.progressBar_ext);
        avlbMemory_Ext =  findViewById(R.id.avlbMemory_ext);
        totalMemmory_Ext =  findViewById(R.id.totalMemmory_ext);
        internalTxt_Ext = findViewById(R.id.externalTxt);
        avlbTxt_Ext =  findViewById(R.id.avlbTxt_ext);

        TextView toolbar_title=findViewById(R.id.toolbar_title);
        TextView strgTxt=findViewById(R.id.strgTxt);


        //setTypeFce
        //setTypeFce
        if(mContext!=null) {

            toolbar_title.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            avlbTxt_Ext.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            internalTxt_Ext.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            avlbMemory_Ext.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            avlbTxt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            internalTxt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            totalMemmory.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            avlbMemory.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            strgTxt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));


        }

        internalLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent intentImageGallary = new Intent(mContext, Activity_Stotrage.class);
                intentImageGallary.putExtra(Constants.storageType, Constants.interNal);
                startActivity(intentImageGallary);

            }
        });

        ext_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intentImageGallary = new Intent(mContext, Activity_Stotrage.class);
                intentImageGallary.putExtra(Constants.storageType, Constants.sdCard);
                startActivity(intentImageGallary);
            }
        });




    }

    private void fillProgressBar() {


        progressBar.setProgress(0);
        progressBar.setMax(100);
        long TotalInternalMemory = Utility.getTotalInternalMemorySize();
        long AvailableInternalMemory = Utility.getAvailableInternalMemorySize();


        float per = (float) AvailableInternalMemory / (float) (TotalInternalMemory / 100);
        System.out.print("Memory Stats--> Total " + TotalInternalMemory + " Avaailable" + AvailableInternalMemory + "" + Utility.setdecimalPoints(String.valueOf(per), 2));
        // avlbMemory.setText(Utility.formatSize(Utility.getAvailableInternalMemorySize()));
        avlbMemory.setText(Utility.humanReadableByteCount(Utility.getAvailableInternalMemorySize(), true) + "(" + Utility.setdecimalPoints(String.valueOf(per), 2) + "%)");
        // totalMemmory.setText("Total "+Utility.formatSize(Utility.getTotalInternalMemorySize()));
        totalMemmory.setText("Total " + Utility.humanReadableByteCount(Utility.getTotalInternalMemorySize(), true));
        int progress = 100 - (int) per;
        progressBar.setProgress(progress);


    }

    private void fillProgressBar_Ext() {


        progressBar_Ext.setProgress(0);
        progressBar_Ext.setMax(100);
        long TotalMemory_Ext = Utility.getTotalExternalMemorySize(UtilityStorage.getExternalStoragePath(mContext, true));
        long AvailableMemory_Ext = Utility.getAvailableExternalMemorySize(UtilityStorage.getExternalStoragePath(mContext, true));


        long per = AvailableMemory_Ext / (TotalMemory_Ext / 100);
        System.out.print("Memory Stats--> Total " + TotalMemory_Ext + " Avaailable" + AvailableMemory_Ext + "" + per);

        avlbMemory_Ext.setText(Utility.humanReadableByteCount(AvailableMemory_Ext, true) + "(" + per + "%)");

        totalMemmory_Ext.setText("Total " + Utility.humanReadableByteCount(TotalMemory_Ext, true));
        int progress = 100 - (int) per;
        progressBar_Ext.setProgress(progress);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
       //menuInflater.inflate(R.menu.menu, menu);

        //  this menu  is not required as per vipul  storage will always be visible

//        if(storage_section!=null)
//        {
//            if (storage_section.getVisibility() == View.GONE) {
//                MenuItem item1 = menu.findItem(R.id.showstorage);
//                MenuItem item2 = menu.findItem(R.id.hidestorage);
//                item1.setVisible(true);
//                item2.setVisible(false);
//            }
//            if (storage_section.getVisibility() == View.VISIBLE) {
//                MenuItem item1 = menu.findItem(R.id.showstorage);
//                MenuItem item1 = menu.findItem(R.id.showstorage);
//                MenuItem item2 = menu.findItem(R.id.hidestorage);
//                item1.setVisible(false);
//                item2.setVisible(true);
//
//                // will  work on it later for scrolling to  make stofrage visible when visible  is clicked
//                if(recyclerView !=null && catList !=null)
//                    recyclerView.scrollToPosition(catList.size() - 1);
//
//
//            }
//        }


        return true;
    }



   private void showHidestorage(int flag)
   {

       switch (flag)
       {

           case  1:

               storage_section.setVisibility(View.VISIBLE);
               invalidateOptionsMenu();// this calls oncreateOptiosMenu there we we show/hide storage
               break;
           case 2:
               storage_section.setVisibility(View.GONE);
               invalidateOptionsMenu();// this calls oncreateOptiosMenu there we we show/hide storage


               break;

               }


   }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

//            case R.id.showstorage:
//
//                showHidestorage(1);
//
//                break;
//            case R.id.hidestorage:
//
//                showHidestorage(2);
//
//
//                break;



        }
        return true;
    }

    private void redirectToActivity(Class targetClass)
    {

        Intent i =  new Intent(MainActivity.this,targetClass);
        startActivity(i);
    }

    @Override
    public void onReceiveAd(AdDownloaderInterface adDownloaderInterface, ReceivedBannerInterface receivedBanner) {

        if(receivedBanner.getErrorCode() != ErrorCode.NO_ERROR){
            // Toast.makeText(getBaseContext(), receivedBanner.getErrorMessage(), Toast.LENGTH_SHORT).show();
            Log.d("SmaatoErrorMsg", ""+receivedBanner.getErrorMessage());

            if(receivedBanner.getErrorMessage().equalsIgnoreCase(AddConstants.NO_ADDS))
            {
                smaaToAddContainer.setVisibility(View.GONE);
            }
        }


    }


    public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.categoryViewHolder> {

        List<category_Model> catList;
        @Override
        public categoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_item_layout, parent, false);

            return new categoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(categoryViewHolder holder, final int position) {
            holder.catName.setText(catList.get(position).getCatName());


            switch (position) {
                case 0:
                    holder.catIcon.setImageResource(R.drawable.cat_ic_image);
                    //holder.catIcon.setImageResource(R.drawable.ic_image);
                    break;
                case 1:
                    holder.catIcon.setImageResource(R.drawable.cat_ic_vdo);
                    break;
                case 2:
                    holder.catIcon.setImageResource(R.drawable.cat_ic_music);
                    break;
                case 3:
                    holder.catIcon.setImageResource(R.drawable.cat_ic_docs);
                    break;
                case 4:
                    holder.catIcon.setImageResource(R.drawable.cat_ic_download);
                    break;
                case 5:
                    holder.catIcon.setImageResource(R.drawable.cat_ic_animation);
                    break;
                case 6:
                    holder.catIcon.setImageResource(R.drawable.cat_ic_recent);
                    break;
                case 7:
                    holder.catIcon.setImageResource(R.drawable.cat_ic_apk);
                    break;
                case 8:
                    holder.catIcon.setImageResource(R.drawable.cat_ic_zip);
                    break;
                    default:
                   holder.catIcon.setImageResource(R.drawable.cat_ic_zip);
                        break;
            }

            holder.catName.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));
            //holder.itemCount.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(getActivity()));



            holder.container_Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    switch (position)
                    {
                        case 3:
                            redirectToActivity(DocsActivityRe.class);
                            break;
                        case 4:
                            redirectToActivity(DownloadActivityRe.class);
                                break;
                        case 5:
                            redirectToActivity(AnimationActivityRe.class);
                            break;
                        case 6:
                            redirectToActivity(RecentActivityRe.class);
                            break;
                        case 7:
                            redirectToActivity(ApkActivityRe.class);
                            break;
                        case 8:
                            redirectToActivity(ZipActivityRe.class);
                            break;
                            default:
                                Intent i = new Intent(mContext, Category_Explore_Activity.class);
                                i.putExtra(POSITION, position);
                                startActivity(i);
                                break;
                    }




                }
            });


        }

        @Override
        public int getItemCount() {
            return catList.size();
        }

        public class categoryViewHolder extends RecyclerView.ViewHolder {
            public TextView catName;
            RelativeLayout container_Layout;
            ImageView catIcon;



            public categoryViewHolder(View view) {
                super(view);

                catName =  view.findViewById(R.id.catName);
                catIcon =  view.findViewById(R.id.cat_Icon);
                container_Layout=view.findViewById(R.id.container_Layout);


            }
        }

        public categoryAdapter(List<category_Model> catList) {

            this.catList=catList;
        }

    }
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press again to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public class WebCall extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... strings) {

            try {
                JSONObject requestObj= AddConstants.prepareAddJsonRequest(mContext, AddConstants.VENDOR_ID);
                return OkhttpMethods.CallApi(mContext,AddConstants.API_URL,requestObj.toString());
            } catch (IOException e) {
                e.printStackTrace();
                return ""+e.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("JsonResponse", s);
            long ms2=System.currentTimeMillis();
            System.out.print("Milliseconds after-->>"+ms2);
            Log.e("Milliseconds after-->>", ""+ms2);
            if (addprefs != null)
            {
                int responseCode=addprefs.getIntValue(AddConstants.API_RESPONSE_CODE, 0);

                if (s != null  && responseCode==200 ) {
                    try {
                        JSONObject mainJson = new JSONObject(s);
                        if (mainJson.has("status")) {
                            String status = JsonParser.getkeyValue_Str(mainJson, "status");

                            if (status.equalsIgnoreCase("true")) {

                                String adShow = JsonParser.getkeyValue_Str(mainJson, "AdShow");

                                if (adShow.equalsIgnoreCase("true")) {
                                    if (mainJson.has("data")) {
                                        JSONObject dataJson = mainJson.getJSONObject("data");

                                        String show_Add = JsonParser.getkeyValue_Str(mainJson, "AdShow");

                                        String adProviderId =JsonParser.getkeyValue_Str(dataJson, "adProviderId");
                                        String adProviderName = JsonParser.getkeyValue_Str(dataJson, "adProviderName");

//                                         String appId_PublisherId = JsonParser.getkeyValue_Str(dataJson, "appId_PublisherId");
//                                        String bannerAdId = JsonParser.getkeyValue_Str(dataJson, "bannerAdId");
//                                        String interstitialAdId = JsonParser.getkeyValue_Str(dataJson, "interstitialAdId");
//                                       String videoAdId = JsonParser.getkeyValue_Str(dataJson, "videoAdId");


                                        String appId_PublisherId = "ca-app-pub-3940256099942544~3347511713";//testID
                                        String bannerAdId = "ca-app-pub-3940256099942544/6300978111"; //testId
                                        String interstitialAdId = "ca-app-pub-3940256099942544/1033173712";//testId
                                        String videoAdId = "ca-app-pub-3940256099942544/5224354917";//testId


                                        Log.d("AddiDs", adProviderName + " ==" + appId_PublisherId + "==" + bannerAdId + "==" + interstitialAdId + "==" + videoAdId);


                                        //check for true value above in code so  can put true directly;
                                        try {
                                            addprefs.setValue(AddConstants.SHOW_ADD, Boolean.parseBoolean(show_Add));
                                        }catch (Exception e)
                                        {
                                            // IN CASE OF EXCEPTION CONSIDER  FALSE AS THE VALUE WILL NOT BE TRUE,FALSE.
                                            addprefs.setValue(AddConstants.SHOW_ADD, false);
                                        }

                                        addprefs.setValue(AddConstants.ADD_PROVIDER_ID, adProviderId);
                                        addprefs.setValue(AddConstants.APP_ID, appId_PublisherId);
                                        addprefs.setValue(AddConstants.BANNER_ADD_ID, bannerAdId);
                                        addprefs.setValue(AddConstants.INTERESTIAL_ADD_ID, interstitialAdId);
                                        addprefs.setValue(AddConstants.VIDEO_ADD_ID, videoAdId);

                                        if (adContainer != null  && adProviderId.equalsIgnoreCase(AddConstants.Adsense_Admob_GooglePrivideId))

                                        {
                                            // requst googleAdd
                                            AddMobUtils util = new AddMobUtils();
                                            util.displayServerBannerAdd(addprefs, adContainer, mContext);
                                            // util.showInterstitial(addprefs,HomeActivity.this, interstitialAdId);
                                            //util.displayRewaredVideoAdd(addprefs,mContext, videoAdId);


                                        }
                                        else if (adProviderId.equalsIgnoreCase(AddConstants.InMobiProvideId))
                                        {

                                            // inmobi adds not being implemented in this version
                                            // inmobi adds not being implemented in this version

                                        }
                                        else if( smaaTobannerView !=null && adProviderId.equalsIgnoreCase(AddConstants.SmaatoProvideId))
                                        {
                                            //requestSmaatoBanerAdds

                                            AddMobUtils util=new AddMobUtils();
                                            try {
                                                int publisherId = Integer.parseInt(addprefs.getStringValue(AddConstants.APP_ID, AddConstants.NOT_FOUND));
                                                int addSpaceId = Integer.parseInt(addprefs.getStringValue(AddConstants.BANNER_ADD_ID, AddConstants.NOT_FOUND));
                                                util.displaySmaatoBannerAdd(smaaTobannerView, smaaToAddContainer, publisherId, addSpaceId);
                                            }catch (Exception e)
                                            {
                                                String string = e.getMessage();
                                                System.out.print(""+string);
                                            }


//                                            smaaTobannerView.getAdSettings().setPublisherId(0);
//                                            smaaTobannerView.getAdSettings().setAdspaceId(0);
//                                            smaaTobannerView.isLocationUpdateEnabled();
//                                            smaaTobannerView.setAutoReloadEnabled(true);
//                                            smaaTobannerView.setAutoReloadFrequency(20);//seconds
//                                            smaaTobannerView.asyncLoadNewBanner();
//                                            smaaToAddContainer.addView(smaaTobannerView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, AddConstants.dpToPx(50) ));
                                            //requestSmaatoBanerAdds
                                        }



                                    } else {
                                        String message = JsonParser.getkeyValue_Str(mainJson, "message");
                                        Log.d("message", "" + message);
                                    }
                                } else {
                                    String message = JsonParser.getkeyValue_Str(mainJson, "message");

                                    Log.d("message", "" + message);

                                }


                            }
                        }

                    } catch (JSONException e) {
                        Log.d("jsonParse", "error while parsing json -->" + e.getMessage());
                        e.printStackTrace();
                    }


                } else {
                    // display loccal AddiDs Adds;
                    if (mAdView != null) {
                        AddMobUtils util = new AddMobUtils();
                        util.displayLocalBannerAdd(mAdView);
                        //util.showInterstitial(addprefs,HomeActivity.this, null);
                        // util.displayRewaredVideoAdd(addprefs,mContext, null);
                    }
                }


            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(internetChangerReceiver !=null)
            unregisterReceiver(internetChangerReceiver);

        clearApplicationData(mContext);
    }

    public void clearApplicationData(Context context) {
        File cache = context.getCacheDir();

        File appDir = new File(cache.getParent());

        long bytes=dirSize(appDir);

        System.out.println(""+bytes);
        String str=Utility.humanReadableByteCount(bytes,true);
        System.out.println(""+str);
        long size=1024*1024 *50;  //1024*1024 *50  50 mb;
        System.out.print(""+size);
        if(bytes>size)

            if (appDir.exists()) {
                String[] children = appDir.list();
                for (String s : children) {
                    if (!s.equals("lib") || !s.equalsIgnoreCase("shared_prefs")) {
                        deleteDir(new File(appDir, s));
                        Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                    }
                }
            }
    }
    public static boolean deleteDir(File dir) {
        long bytes=dir.length();
        System.out.println(""+bytes);

        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        else if(dir!= null && dir.isFile())
        {
            return dir.delete();
        } else {
            return false;
        }
    }

    private static long dirSize(File dir) {

        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for(int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if(fileList[i].isDirectory()) {
                    result += dirSize(fileList [i]);
                } else {
                    // Sum the file size in bytes
                    result += fileList[i].length();
                }
            }
            return result; // return the file size
        }
        return 0;
    }
}
