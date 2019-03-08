package com.mojodigi.filehunt.Model;

import android.os.Parcel;
import android.os.Parcelable;


public class Model_Locker implements Parcelable {



    String fileName;
    String filePath;
    String fileSize;

    String fileMDate;



    long fileSizeCmpr;



    long dateCmpr;


    protected Model_Locker(Parcel in) {
        fileName = in.readString();
        filePath = in.readString();
        fileSize = in.readString();
        fileMDate = in.readString();
        fileSizeCmpr = in.readLong();
        dateCmpr = in.readLong();
    }
    public Model_Locker()
    {

    }

    public static final Creator<Model_Locker> CREATOR = new Creator<Model_Locker>() {
        @Override
        public Model_Locker createFromParcel(Parcel in) {
            return new Model_Locker(in);
        }

        @Override
        public Model_Locker[] newArray(int size) {
            return new Model_Locker[size];
        }
    };

    public String getFileMDate() {
        return fileMDate;
    }

    public void setFileMDate(String fileMDate) {
        this.fileMDate = fileMDate;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    public long getFileSizeCmpr() {
        return fileSizeCmpr;
    }

    public void setFileSizeCmpr(long fileSizeCmpr) {
        this.fileSizeCmpr = fileSizeCmpr;
    }

    public long getDateCmpr() {
        return dateCmpr;
    }

    public void setDateCmpr(long dateCmpr) {
        this.dateCmpr = dateCmpr;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(filePath);
        dest.writeString(fileSize);
        dest.writeString(fileMDate);
        dest.writeLong(fileSizeCmpr);
        dest.writeLong(dateCmpr);
    }
}
