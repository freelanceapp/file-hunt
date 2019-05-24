package com.mojodigi.filehunt.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.mojodigi.filehunt.Class.Constants;
import com.mojodigi.filehunt.R;
import com.mojodigi.filehunt.Utils.CustomProgressDialog;
import com.mojodigi.filehunt.Utils.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class decryptAsynscTask extends AsyncTask<Void, Void, Integer> {

    File[] fileList;
    Context mContext;
    int counter=0;
    String password;
    File fileDecrypted;
    int mediaType;

    public interface decryptListener  {
        void OnDeCryptFinish(File fileDecrypted );

    }

    public decryptListener listener = null;

    public decryptAsynscTask(Context pContext, File[] pfileList, decryptListener listener, String pPassword ,int mediaType)
    {
        this.mContext=pContext;
        this.fileList=pfileList;
        this.password=pPassword;
        this.listener=listener;
        this.mediaType=mediaType;

        }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        System.out.println("task cancelled");

        }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(!((Activity) mContext).isFinishing()) {
            CustomProgressDialog.show(mContext, mContext.getResources().getString(R.string.decmsg));
        }


    }
    @Override
    protected Integer doInBackground(Void... voids) {

        for(int i=0;i<fileList.length;i++)
        {
            String decryptedFile =fileList[i].getPath();

            // skip the files that are already encrypted;
            if(!decryptedFile.contains(".des"))
            {
                continue;
            }
            boolean status=isCancelled();
            if(isCancelled() || Constants.isAsyncOperationStopped)
            {

                break;
            }

            String fileName=decryptedFile.substring(0, decryptedFile.lastIndexOf(".")); // remove  the  .des extension
            File f=  new File(fileName);
            String fname=f.getName();
            decryptedFile= Utility.setDecryptFilePath(mediaType)+fname;
            System.out.print(""+decryptedFile);
            counter+=decryptfile(fileList[i],new File(decryptedFile),password,mContext);
            publishProgress();

        }


        return counter;
    }
    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

      //  CustomProgressDialog.dismiss();

        //String msg= integer > 1 ? integer + " files decrypted successfully" : integer + " file decrypted successfully";

         // listener.OnDeCryptFinish();       // now update ui on every file decryption using progressupdate;
          if(!Constants.isAsyncOperationStopped && fileDecrypted!=null) {
            //Utility.dispToast(mContext, msg);
              listener.OnDeCryptFinish(fileDecrypted);
              CustomProgressDialog.dismiss();
            }


    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

       /* listener.OnDeCryptFinish();
        CustomProgressDialog.dismiss(); // dismiss  on the fiesr filr successful decryption while updating ui ;*/
    }

    public  int decryptfile(File inputFile, File outputFile, String passwordKey, Context ctx)
    {
        try {
            String password = passwordKey;
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            // SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");  //in java
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");  //in android
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

            FileInputStream fis = new FileInputStream(inputFile);
            // FileInputStream fis = new FileInputStream("G:\\EncryptTest\\image\\Takendra.des");
            byte[] salt = new byte[8];
            fis.read(salt);

            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);

            //Cipher cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");  //in java
            Cipher cipher = Cipher.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");  // in android


            cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParameterSpec);
            FileOutputStream fos = new FileOutputStream(outputFile);
            // FileOutputStream fos = new FileOutputStream("G:\\EncryptTest\\image\\Takendra_decrypted.jpg");
           // byte[] in = new byte[64];   takes time  to  decrypt
            byte[] in = new byte[1024*1024];
            int read;
            while ((read = fis.read(in)) != -1) {
                byte[] output = cipher.update(in, 0, read);
                if (output != null)
                    fos.write(output);
            }

            byte[] output = cipher.doFinal();
            if (output != null)
                fos.write(output);

            fis.close();
            fos.flush();
            fos.close();

//            if(inputFile.delete())
//            {
//                Utility.RunMediaScan(ctx,inputFile);
//            }

               Utility.RunMediaScan(ctx,outputFile);

            fileDecrypted=outputFile;


            return 1;
        }catch (InvalidKeyException e)
        {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();         //  in case of wrong password this exception is being raised
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return 0;

    }







}
