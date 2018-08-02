package com.example.filehunt.Model;

import java.util.ArrayList;



public class Model_images
{
    String str_folder;
    ArrayList<String> al_imagepath;

    ArrayList<String> FileDuaration;
    ArrayList<String> al_vdoThumb;



    ArrayList<String> alVdoDuration;

    public ArrayList<String> getAlVdoDuration() {
        return alVdoDuration;
    }

    public void setAlVdoDuration(ArrayList<String> alVdoDuration) {
        this.alVdoDuration = alVdoDuration;
    }

    public ArrayList<String> getAl_vdoThumb() {
        return al_vdoThumb;
    }

    public void setAl_vdoThumb(ArrayList<String> al_vdoThumb) {
        this.al_vdoThumb = al_vdoThumb;
    }

    public void setStr_folder(String str_folder) {
        this.str_folder = str_folder;
    }

    public ArrayList<String> getAl_imagepath() {
        return al_imagepath;
    }

    public void setAl_imagepath(ArrayList<String> al_imagepath) {
        this.al_imagepath = al_imagepath;
    }
    public void setAl_FileDuration(ArrayList<String> FileDuaration) {
        this.FileDuaration = FileDuaration;
    }
    public ArrayList<String>  getAl_FileDuration (){
      return FileDuaration;
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
