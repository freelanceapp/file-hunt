package com.mojodigi.filehunt.Model;

public class Model_Anim {



    String fileName;
    String filePath;
    String fileSize;
    String fileType;
    String fileMDate;
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
