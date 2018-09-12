package com.mojodigi.filehunt.Model;
 public class Model_Storage {
    public String file;
    public String fileModifiedDate;
    public int  Itemcount;
    public int icon;
     String filePath;
     boolean isDirecoty;


     boolean isSdcCardPath;

     public String getFilePath() {
         return filePath;
     }

     public void setFilePath(String filePath) {
         this.filePath = filePath;
     }


    public boolean getisDirecoty() {
       return isDirecoty;
    }

    public void setIsDirecoty(boolean direcoty) {
       isDirecoty = direcoty;
    }



    public String filesize;

    public String getFile() {
       return file;
    }

    public void setFile(String file) {
       this.file = file;
    }

    public String getFileModifiedDate() {
       return fileModifiedDate;
    }

    public void setFileModifiedDate(String fileModifiedDate) {
       this.fileModifiedDate = fileModifiedDate;
    }

    public int getItemcount() {
       return Itemcount;
    }

    public void setItemcount(int itemcount) {
       Itemcount = itemcount;
    }

    public int getIcon() {
       return icon;
    }

    public void setIcon(int icon) {
       this.icon = icon;
    }

    public String getFilesize() {
       return filesize;
    }

    public void setFilesize(String filesize) {
       this.filesize = filesize;
    }

    public Model_Storage(String file, Integer icon, String fileModifiedDate, int  Itemcount, String filesize) {
        this.file = file;
        this.icon = icon;
        this.fileModifiedDate=fileModifiedDate;
        this.Itemcount=Itemcount;
        this.filesize=filesize;
    }
    public Model_Storage()
    {

    }
    public Model_Storage(String file, Integer icon) {
       this.file = file;
       this.icon = icon;
    }


    @Override
    public String toString() {
        return file;
    }
}