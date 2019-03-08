package com.mojodigi.filehunt.Model;

public class Model_Audio {

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    private String audioPath;
    private String audiFileName;
    private String audioFileSize;
    private String audiofileMDate;
    private String audioFileDuration;


    private String fileType;
    long dateToSort;
    long fileSizeCmpr;


    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
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

    public String getAudiFileName() {
        return audiFileName;
    }

    public void setAudiFileName(String audiFileName) {
        this.audiFileName = audiFileName;
    }

    public String getAudioFileSize() {
        return audioFileSize;
    }

    public void setAudioFileSize(String audioFileSize) {
        this.audioFileSize = audioFileSize;
    }

    public String getAudiofileMDate() {
        return audiofileMDate;
    }

    public void setAudiofileMDate(String audiofileMDate) {
        this.audiofileMDate = audiofileMDate;
    }

    public String getAudioFileDuration() {
        return audioFileDuration;
    }

    public void setAudioFileDuration(String audioFileDuration) {
        this.audioFileDuration = audioFileDuration;
    }
}
