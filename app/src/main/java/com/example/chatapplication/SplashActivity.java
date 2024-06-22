package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.chatapplication.model.UserModel;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FirebaseUtil.isLogin()){
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    }
                    else {
                        startActivity(new Intent(SplashActivity.this,loginPhoneNumber.class));
                        finish();
                    }
                }
            },1000);

//        if (FirebaseUtil.isLogin() && getIntent().getExtras()!=null){
//
//            //notification
//            String userId =getIntent().getExtras().getString("userId");
//            FirebaseUtil.allUserCollectionReference().document(userId).get()
//                    .addOnCompleteListener(task -> {
//                       if(task.isSuccessful()){
//                           UserModel userModel=task.getResult().toObject(UserModel.class);
//
//                           Intent mainintent=new Intent(this,MainActivity.class);
//                           mainintent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                           startActivity(mainintent);
//
//                           Intent intent =new Intent(this, ChatActivity.class);
//                           AndroidUtil.passUserModelAsIntent(intent,userModel);
//                           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                           startActivity(intent);
//                           finish();
//                       }
//                    });
//
//        }else {
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if(FirebaseUtil.isLogin()){
//                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
//                    }
//                    else {
//                        startActivity(new Intent(SplashActivity.this,loginPhoneNumber.class));
//                        finish();
//                    }
//                }
//            },1000);
//
//        }


    }
}