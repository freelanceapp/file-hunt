package com.mojodigi.filehunt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mojodigi.filehunt.Utils.Utility;

public class LockerPasswordActivity extends AppCompatActivity {

    EditText passwordTxt,cpasswordTxt;
    Button submit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         setContentView(R.layout.activity_locker_password);

          submit=findViewById(R.id.done_button);
          passwordTxt=findViewById(R.id.password);
          cpasswordTxt=findViewById(R.id.cpassword);

        Utility.setActivityTitle(LockerPasswordActivity.this,getResources().getString(R.string.setpassword));



          submit.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {



                  if(passwordTxt.getText().toString().length()>=1 && cpasswordTxt.getText().toString().length()>=1) {
                      if (passwordTxt.getText().toString().equalsIgnoreCase(cpasswordTxt.getText().toString())) {
                          int status = Utility.createPasswordFile(LockerPasswordActivity.this, passwordTxt.getText().toString());
                          if (status == 1)
                              finish();
                      } else {
                          Utility.dispToast(LockerPasswordActivity.this, "Password does not match");
                      }
                  }
                  else
                  {
                      Utility.dispToast(LockerPasswordActivity.this,"type a password");
                  }


              }
          });



    }




}
