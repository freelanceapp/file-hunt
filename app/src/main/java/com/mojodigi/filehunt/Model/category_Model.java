package com.mojodigi.filehunt.Model;

import android.graphics.drawable.Drawable;

public class category_Model {

    String catName;


    public category_Model(String catName)
    {

        this.catName=catName;

    }
    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }


}
