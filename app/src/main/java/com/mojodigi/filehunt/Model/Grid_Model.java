package com.mojodigi.filehunt.Model;

import android.graphics.Bitmap;

public class Grid_Model {


    private String ImgPath;
    String fileName;
    long dateToSort;
    long fileSizeCmpr;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getDateToSort() {
        return dateToSort;
    }

    public void setDateToSort(long dateToSort) {
        this.dateToSort = dateToSort;
    }

    public long getFileSizeCmpr() {
        return fileSizeCmpr;
    }

    public void setFileSizeCmpr(long fileSizeCmpr) {
        this.fileSizeCmpr = fileSizeCmpr;
    }

    public String getImgPath() {
        return ImgPath;
    }

    public void setImgPath(String imgPath) {
        ImgPath = imgPath;
    }



    public String getImgBitmapStr() {
        return imgBitmap;
    }

    public void setImgBitmap(String imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    private String imgBitmap;

    public String getVdoDuration() {
        return VdoDuration;
    }

    public void setVdoDuration(String vdoDuration) {
        VdoDuration = vdoDuration;
    }

    private String VdoDuration;
}
