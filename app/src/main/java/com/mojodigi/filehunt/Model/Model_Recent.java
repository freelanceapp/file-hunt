package com.mojodigi.filehunt.Model;

public class Model_Recent  {


    private String FileName;
    private String FilePath;
    private String FileMdate;
    private String FileSize;

    long dateToSort;
    long fileSizeCmpr;



    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        FileType = fileType;
    }

    private String FileType;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getFileMdate() {
        return FileMdate;
    }

    public void setFileMdate(String fileMdate) {
        FileMdate = fileMdate;
    }

    public String getFileSize() {
        return FileSize;
    }

    public void setFileSize(String fileSize) {
        FileSize = fileSize;
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
}
