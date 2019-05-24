package com.mojodigi.filehunt.Utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mojodigi.filehunt.R;

public class EncryptDialogUtility
{


    public interface EncryptDialogListener  {
        void onCancelClick();
        int onEncryptClick(String password);

    }
    public EncryptDialogUtility(  EncryptDialogListener delegate )
    {
        this.delegate=delegate;
    }
    public EncryptDialogListener delegate = null;

    public   void fileEncryptPasswordDialog(final Context mcontext)
    {

        //https://github.com/sang89vh/easyfilemanager/blob/master/AmazeFileManagerSang89vhAdmob/src/main/java/com/mybox/filemanager/services/httpservice/FileUtil.java

        final Dialog dialog = new Dialog(mcontext);
        dialog.setContentView(R.layout.dialog_encrypt_file);
        // Set dialog title

        TextView headertxt = dialog.findViewById(R.id.headertxt);
        final EditText encrypt_password_box = dialog.findViewById(R.id.encrypt_password_box);

        TextView View_encrypt = dialog.findViewById(R.id.View_encrypt);
        TextView View_cancel = dialog.findViewById(R.id.View_cancel);

        headertxt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));
        encrypt_password_box.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));
        View_cancel.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));
        View_encrypt.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mcontext));

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        View_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                delegate.onCancelClick();
                dialog.dismiss();
            }
        });
        View_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.IsNotEmpty(encrypt_password_box)) {


                    int status= delegate.onEncryptClick(encrypt_password_box.getText().toString());
                    if(status==1)
                        dialog.dismiss();
                    else
                        encrypt_password_box.setError(mcontext.getResources().getString(R.string.Incrct_pswd));

                }


                else
                {
                    encrypt_password_box.setError(mcontext.getResources().getString(R.string.emty_error));
                }


            }

        });


        dialog.show();


    }

}
