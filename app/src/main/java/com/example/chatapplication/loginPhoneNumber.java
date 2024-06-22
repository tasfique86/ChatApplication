package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class loginPhoneNumber extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText editTextphone;
    Button buttonsendOTP;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        countryCodePicker=findViewById(R.id.login_countrycode);
        editTextphone=findViewById(R.id.login_modile_number);
        buttonsendOTP=findViewById(R.id.send_otp_btn);
        progressBar=findViewById(R.id.login_progressbar);

        progressBar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(editTextphone);
        buttonsendOTP.setOnClickListener((v)->{
            if(!countryCodePicker.isValidFullNumber()){
                editTextphone.setError("Phone number not valid");
                return;
            }
            Intent intent=new Intent(loginPhoneNumber.this,loginOtp.class);
           intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);

        });
    }
}