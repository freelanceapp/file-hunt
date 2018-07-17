package com.example.filehunt.Model;

import java.util.ArrayList;



public class Model_images {
    String str_folder;
    ArrayList<String> al_imagepath;



    public void setStr_folder(String str_folder) {
        this.str_folder = str_folder;
    }

    public ArrayList<String> getAl_imagepath() {
        return al_imagepath;
    }

    public void setAl_imagepath(ArrayList<String> al_imagepath) {
        this.al_imagepath = al_imagepath;
    }
    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    String date_modified;
    public String getStr_folder() {
        return str_folder;
    }
}
