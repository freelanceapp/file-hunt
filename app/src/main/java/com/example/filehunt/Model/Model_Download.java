package com.example.filehunt.Model;

import android.support.annotation.NonNull;

import java.util.Date;

public class Model_Download
{
    public String FilePath;
    public String FileName;
    public String FileSize;
    public String FileDateModified;
    public String Filetype;
    public long dateToSort;
    public long getDateToSort() {
        return dateToSort;
    }

    public void setDateToSort(long dateToSort) {
        this.dateToSort = dateToSort;
    }


    public String getFiletype() {
        return Filetype;
    }

    public void setFiletype(String filetype) {
        Filetype = filetype;
    }


    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileSize() {
        return FileSize;
    }

    public void setFileSize(String fileSize) {
        FileSize = fileSize;
    }

    public String getFileDateModified() {
        return FileDateModified;
    }

    public void setFileDateModified(String fileDateModified) {
        FileDateModified = fileDateModified;
    }




}
