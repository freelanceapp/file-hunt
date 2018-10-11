package com.mojodigi.filehunt;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
//
import com.mojodigi.filehunt.Utils.Utility;

import java.io.File;


import static com.mojodigi.filehunt.Class.Constants.PATH;

public class SingleViewMediaActivity extends AppCompatActivity {

   // ImageView singleImage;
    SubsamplingScaleImageView singleImage;
    String imgPath;
    ProgressDialog dialog;
    Uri shareUri;
    Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singleviewmedia);
        mcontext=SingleViewMediaActivity.this;
        singleImage=(SubsamplingScaleImageView)findViewById(R.id.singleImage);

        imgPath = getIntent().getStringExtra(PATH);
        File f =new File(imgPath);
        shareUri=Uri.fromFile(f);

           if(null!=imgPath) {
               File f1 = new File(imgPath);
               Utility.setActivityTitle(mcontext,f1.getName());
               singleImage.setImage(ImageSource.uri(imgPath));
               singleImage.setRotation(Utility.getOrintatin(f));
           }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item1: setAsWallpaper();
                // new SetWallpaer().execute();
                return true;
            case R.id.item2:

                 getImagedetails();
                return true;
            case R.id.item3:
               // shareImage(shareUri);
                if(imgPath!=null)
                {
                    Utility.ShareSingleFile(imgPath,mcontext,getResources().getString(R.string.file_provider_authority));
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void getImagedetails()
    {
          String morientation="";
          File f =new File(imgPath);


          Bitmap bitmap= Utility.pathToBitmap(imgPath);
          int w=bitmap.getWidth();
          int h=bitmap.getHeight();

          System.out.println("ImageDetails path-> "+imgPath+"size \n "+ Utility.formatSize(f.length())+"\n resolution->"+w+"*"+h+"\n  orientation"+morientation);
          String[] splitPath=imgPath.split("/");

          String fName=splitPath[splitPath.length-1];


        Dialog dialog = new Dialog(SingleViewMediaActivity.this);
        dialog.setContentView(R.layout.dialog_file_property);
        // Set dialog title

        TextView FileName=dialog.findViewById(R.id.FileName);
        TextView FilePath=dialog.findViewById(R.id.FilePath);
        TextView FileSize=dialog.findViewById(R.id.FileSize);
        TextView FileDate=dialog.findViewById(R.id.FileDate);
        TextView Resolution=dialog.findViewById(R.id.Resolution);
        TextView Oreintation=dialog.findViewById(R.id.ort);

        FileName.setText(fName);
        FilePath.setText(imgPath);
        FileSize.setText(Utility.formatSize(f.length()));
        FileDate.setText(Utility.LongToDate((f.lastModified())));
        Resolution.setText(w+"*"+h);
        Oreintation.setText(Utility.getOrintatin(w,h));

        dialog.show();

    }
    // Share image
    private void shareImage(Uri imagePath)
    {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
        startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
    }
    private void shareText(String text) {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");// Plain format text

        // You can add subject also
        /*
         * sharingIntent.putExtra( android.content.Intent.EXTRA_SUBJECT,
         * "Subject Here");
         */
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(sharingIntent, "Share Text Using"));
    }



private int getOrintatin(File f)
{
    try {
        Uri contentUri = Uri.fromFile(f);
        int orientation = Utility.getBitmapOrientation(contentUri);
        return orientation;
    }catch (Exception e)
    {
        return  0;
    }



}
    private void setAsWallpaper()
    {
        File f = new File(imgPath);
        Uri contentUri = Uri.fromFile(f);
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(contentUri, "image/*");
        intent.putExtra("mimeType", "image/*");
        this.startActivity(Intent.createChooser(intent, "Set as:"));
    }
}
