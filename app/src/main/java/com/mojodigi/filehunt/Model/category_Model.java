package com.mojodigi.filehunt.Model;

import android.graphics.drawable.Drawable;

public class category_Model {

    String catName;
    String iteCount;
    int cat_icon;

    public category_Model(String catName, String iteCount, int cat_icon)
    {
        this.cat_icon=cat_icon;
        this.catName=catName;
        this.iteCount=iteCount;
    }
    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getIteCount() {
        return iteCount;
    }

    public void setIteCount(String iteCount) {
        this.iteCount = iteCount;
    }

    public int getCat_icon() {
        return cat_icon;
    }

    public void setCat_icon(int cat_icon) {
        this.cat_icon = cat_icon;
    }
}
