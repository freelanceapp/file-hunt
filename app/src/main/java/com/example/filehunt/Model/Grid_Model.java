package com.example.filehunt.Model;

import android.graphics.Bitmap;

public class Grid_Model {



    public String getImgPath() {
        return ImgPath;
    }

    public void setImgPath(String imgPath) {
        ImgPath = imgPath;
    }

    private String ImgPath;

    public String getImgBitmapStr() {
        return imgBitmap;
    }

    public void setImgBitmap(String imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    private String imgBitmap;
}
