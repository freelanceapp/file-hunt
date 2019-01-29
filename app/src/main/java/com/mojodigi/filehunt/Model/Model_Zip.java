package com.mojodigi.filehunt.Model;

public class Model_Zip {



    String fileName;
    String filePath;
    String fileSize;

    String fileMDate;



    long fileSizeCmpr;



    long dateCmpr;
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


}
