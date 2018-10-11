package com.mojodigi.filehunt;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.ads.AdView;
import com.mojodigi.filehunt.Adapter.pagerAdapter2;
import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.Fragments.TabFragment1;
import com.mojodigi.filehunt.Fragments.TabFragment2;
//
import com.mojodigi.filehunt.Utils.AddMobUtils;
import com.mojodigi.filehunt.Utils.AlertDialogHelper;
import com.mojodigi.filehunt.Utils.Utility;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity  {


    int check = 0;
    ViewPager viewPager;
    //PagerAdapter adapter;
    pagerAdapter2 adapter;
    TabLayout tabLayout;
    CardView cardLayout;
    Menu context_menu;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        centerTitle();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
//        tabLayout.addTab(tabLayout.newTab().setText("CATEGORIES"));
//        tabLayout.addTab(tabLayout.newTab().setText("STORAGE"));

    //    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
         mAdView = (AdView) findViewById(R.id.adView);
         viewPager = (ViewPager) findViewById(R.id.pager);

        cardLayout=(CardView)findViewById(R.id.cardLayout);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        setupTabLayout();
      //  setSpace();
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        //

        //

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                check = position;

                }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        AddMobUtils addutil=new AddMobUtils();
        addutil.displayBannerAdd(mAdView);
    }
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_paste, menu);
        context_menu=menu;
        return super.onCreateOptionsMenu(menu);

    }


    private void setSpace() {
        for(int i=0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(20, 0, 20, 0);
            tab.requestLayout();
        }
    }

    private void setupViewPager(ViewPager viewPager) {

        adapter = new pagerAdapter2 (getSupportFragmentManager());
        adapter.addFragment(new TabFragment1(), "Categories");
        adapter.addFragment(new TabFragment2(), "Storage");
        viewPager.setAdapter(adapter);
    }

    private void setupTabLayout() {

        int ScreenWidth= (getScreenWidth()/2);
        System.out.print(""+ScreenWidth);

        TextView customTab1 = (TextView) LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.custom_tab_layout, null);



        TextView customTab2 = (TextView) LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.custom_tab_layout, null);


//        LinearLayout.LayoutParams params1=    new LinearLayout.LayoutParams(ScreenWidth, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
//
//        params1.rightMargin=-40;
//        LinearLayout.LayoutParams params2=    new LinearLayout.LayoutParams(ScreenWidth, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
//        params1.leftMargin=-40;

       // customTab1.setLayoutParams(params1);
     //   customTab2.setLayoutParams(params2);


          customTab1.setLayoutParams(new LinearLayout.LayoutParams(ScreenWidth, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
         customTab2.setLayoutParams(new LinearLayout.LayoutParams(ScreenWidth, LinearLayout.LayoutParams.WRAP_CONTENT, 1));


        customTab1.setText("Categories");
        tabLayout.getTabAt(0).setCustomView(customTab1);
        customTab2.setText("Storage");
        tabLayout.getTabAt(1).setCustomView(customTab2);
        customTab1.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(MainActivity.this));
        customTab2.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(MainActivity.this));

    }
    private  int getScreenWidth() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        System.out.println(width);
        return  width;
    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        int flag=0;
        switch (check) {
            case 0:
                finish();
                break;
            case 1:

                //Fragment page = adapter.getItem(viewPager.getCurrentItem());  //does not work

                Fragment page = (Fragment) adapter.instantiateItem(viewPager,viewPager.getCurrentItem());
                if(page!=null)
                    flag=((TabFragment2)page).onBackPressed();
                if(flag==0)
                    viewPager.setCurrentItem(0);

                break;
        }
        ShowHideMenu();
    }
    private void centerTitle() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                appCompatTextView.setTextColor(getResources().getColor(R.color.black));
                appCompatTextView.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(MainActivity.this));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Constants.redirectToStorage) {
            viewPager.setCurrentItem(1);
            ShowHideMenu();
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        ShowHideMenu();
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.action_paste)
        {

            Fragment page = (Fragment) adapter.instantiateItem(viewPager,viewPager.getCurrentItem());
            if(page!=null )
                if(page instanceof TabFragment2) {
                    ((TabFragment2) page).pasteData();
                }

        }
        return super.onOptionsItemSelected(item);
    }
    public void ShowHideMenu()
    {


        if(context_menu!=null)
        {
            Fragment page = (Fragment) adapter.instantiateItem(viewPager,viewPager.getCurrentItem());

            for (int i = 0; i < context_menu.size(); i++) {
                MenuItem item = context_menu.getItem(i);
                if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.menu_paste))) {
                    if (Constants.filesToCopy.size() > 0 && page instanceof  TabFragment2) {
                        item.setVisible(true);
                    } else {
                        item.setVisible(false);
                    }
                }

            }
            invalidateOptionsMenu();
        }

    }

    @Override
    protected void onDestroy() {

        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
        Constants.filesToCopy.clear();// clear  the array list  that  contains the files to  be copied;
        Constants.redirectToStorage=false; // set variable false because if it is true it redirect to  Tabfragmnet2 on load of MainActivity (it may crash app if remained to  true)

        clearApplicationData(MainActivity.this);
    }
    public void clearApplicationData(Context context) {
        File cache = context.getCacheDir();

        File appDir = new File(cache.getParent());

       long bytes=dirSize(appDir);

        System.out.println(""+bytes);
        String str=Utility.humanReadableByteCount(bytes,true);
        System.out.println(""+str);
        if(bytes>26214400)    //1024*1024 *25  25 mb;

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

//shared_prefs

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