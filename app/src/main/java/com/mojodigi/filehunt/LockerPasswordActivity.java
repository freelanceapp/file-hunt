package com.mojodigi.filehunt;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mojodigi.filehunt.Utils.Utility;

public class LockerPasswordActivity extends AppCompatActivity {

   private EditText passwordTxt,cpasswordTxt;
   private Button submit;
   private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_locker_password);


        mContext = LockerPasswordActivity.this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utility.setstatusBarColorBelowM(LockerPasswordActivity.this);
        }


        submit=findViewById(R.id.done_button);
        passwordTxt=findViewById(R.id.password);
        cpasswordTxt=findViewById(R.id.cpassword);


        submit.setTypeface(Utility.typeFace_adobe_caslonpro_Regular(mContext));

        passwordTxt.setTextSize(Utility.getFontSizeValueHeading(mContext));
        cpasswordTxt.setTextSize(Utility.getFontSizeValueHeading(mContext));
        submit.setTextSize(Utility.getFontSizeValueHeading(mContext));


        Utility.setActivityTitle2(LockerPasswordActivity.this,getResources().getString(R.string.setpassword));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordTxt.getText().toString().length()>=1 && cpasswordTxt.getText().toString().length()>=1) {
                    if (passwordTxt.getText().toString().equalsIgnoreCase(cpasswordTxt.getText().toString())) {
                        int status = Utility.createPasswordFile(LockerPasswordActivity.this, passwordTxt.getText().toString());
                        if (status == 1)
                            finish();
                    } else {
                        Utility.dispToast(LockerPasswordActivity.this, getResources().getString(R.string.passwordnotmatch));
                    }
                }
                else
                {
                    Utility.dispToast(LockerPasswordActivity.this,getResources().getString(R.string.type_password));
                }
            }
        });
    }

}
