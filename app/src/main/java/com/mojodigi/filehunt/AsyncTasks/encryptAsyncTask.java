package com.mojodigi.filehunt.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

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
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class encryptAsyncTask extends AsyncTask<Void, Void, Integer> {

    File[] fileList;
    Context mContext;
    int counter=0;
    String password;
    int cat_Type;
    EncryptListener listener;
    public encryptAsyncTask(Context pContext, File[] pfileList, String pPassword,int cat_Type,EncryptListener listener)
    {
        this.mContext=pContext;
        this.fileList=pfileList;
        this.password=pPassword;
        this.cat_Type=cat_Type;
        this.listener=listener;


    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        CustomProgressDialog.show(mContext,mContext.getResources().getString(R.string.encmsg));
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        for(int i=0;i<fileList.length;i++)
        {
            String outputFile =fileList[i].getPath();
            outputFile = Utility.getEncryptFileName(outputFile,cat_Type);    // remove  the file extension and add .des as extension;
            counter+= encrypt(fileList[i],new File(outputFile),password,mContext);
            }
        return counter;

    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

         CustomProgressDialog.dismiss();
         String msg= integer > 1 ? integer + " files encrypted successfully" : integer + " file encrypted successfully";
         Utility.dispToast(mContext,msg);
         if(integer>0)
             listener.onEncryptSuccessful();

    }
    public  interface EncryptListener
    {
        void onEncryptSuccessful();
    }
    private  int encrypt(File inputFile, File outputFile, String passwordKey, Context ctx)
    {
        try {

            // FileInputStream inFile = new FileInputStream("G:\\EncryptTest\\image\\Takendra.jpg");
            //FileOutputStream outFile = new FileOutputStream("G:\\EncryptTest\\image\\Takendra.des");



            FileInputStream inFile = new FileInputStream(inputFile);
            FileOutputStream outFile = new FileOutputStream(outputFile);

            String password = passwordKey;
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            // SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");  //in java
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");  //in android
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);


            //
//            SecretKey key = ...;
//            Cipher cipher = Cipher.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");
//            byte[] iv = new byte[16];
//            new SecureRandom().nextBytes(iv);
//            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

            //


            byte[] salt = new byte[8];

            Random random = new Random();
            random.nextBytes(salt);

            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
            //Cipher cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");  //in java
            Cipher cipher = Cipher.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");  // in android
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParameterSpec);
            outFile.write(salt);

           // byte[] input = new byte[64];  //takes time  to  encryt;
            byte[] input = new byte[1024*1024];
            int bytesRead;
            while ((bytesRead = inFile.read(input)) != -1) {
                byte[] output = cipher.update(input, 0, bytesRead);
                if (output != null)
                    outFile.write(output);
            }

            byte[] output = cipher.doFinal();
            if (output != null)
                outFile.write(output);

            inFile.close();
            outFile.flush();
            outFile.close();
            if(inputFile.delete()) {
                Utility.RunMediaScan(ctx, inputFile);
            }
            Utility.RunMediaScan(ctx,outputFile);
            return 1;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
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
