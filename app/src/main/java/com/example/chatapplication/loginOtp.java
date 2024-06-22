package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatapplication.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class loginOtp extends AppCompatActivity {

    String phoneNumber;
    Long timeoutSecond=60L;

    String  verificationCode;

    PhoneAuthProvider.ForceResendingToken resendingToken;

    EditText otpInput;
    ProgressBar progressBar;
    Button nextbtn;

    FirebaseAuth mAuth=FirebaseAuth.getInstance();

    TextView resendOTPtextview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        otpInput=findViewById(R.id.login_otp);
        progressBar=findViewById(R.id.login_otp_prograssbar);
        nextbtn=findViewById(R.id.login_next_btn);
        resendOTPtextview=findViewById(R.id.resend_otp_textview);

        phoneNumber=getIntent().getExtras().getString("phone");

//        Map<String,String> data= new HashMap<>();
//
//        FirebaseFirestore.getInstance().collection("test").add(data);

        sendotp(phoneNumber,false);

        nextbtn.setOnClickListener(v->{
            String enterOtp = otpInput.getText().toString();
           PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationCode,enterOtp);
           signIn(credential);
           setInProgress(true);

        });

        resendOTPtextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendotp(phoneNumber,true);
            }
        });
    }

    void sendotp(String phoneNumber, boolean isResend){
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(timeoutSecond, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);

                                verificationCode=s;
                               resendingToken=forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(),"OTP verification successfully");
                                setInProgress(false);
                            }
                        });

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }



    }

    void signIn(PhoneAuthCredential phoneAuthCredential)
    {
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(loginOtp.this,loginUserNameActivity.class);
                    intent.putExtra("phone",phoneNumber);
                    startActivity(intent);

                }else {
                    AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                    setInProgress(false);

                }
            }
        });
    }

    void setInProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            nextbtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            nextbtn.setVisibility(View.VISIBLE);
        }
    }


    void startResendTimer(){
        resendOTPtextview.setEnabled(false);
        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSecond--;
                resendOTPtextview.setText("Resend OTP in "+timeoutSecond+" second");
                if(timeoutSecond<=0){
                    timeoutSecond=60L;
                    timer.cancel();
                    runOnUiThread(()->{
                        resendOTPtextview.setEnabled(true);
                    });
                }
            }
        },0,1000);
    }
}