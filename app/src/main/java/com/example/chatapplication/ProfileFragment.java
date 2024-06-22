package com.example.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatapplication.model.UserModel;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class ProfileFragment extends Fragment {


    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;

    UserModel currentUserModel;

    ActivityResultLauncher<Intent> imagePickerLauncher;

    Uri selectedImageUri;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result ->{
                   if(result.getResultCode()==Activity.RESULT_OK){
                       Intent data= result.getData();
                       if(data!=null && data.getData()!= null){
                           selectedImageUri=data.getData();
                           AndroidUtil.setProfilePic(getContext(),selectedImageUri,profilePic);
                       }
                   }
                }
                );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        profilePic=view.findViewById(R.id.profile_image_view);
        usernameInput=view.findViewById(R.id.profile_username);
        phoneInput=view.findViewById(R.id.profile_phone);
        progressBar=view.findViewById(R.id.profile_progressbar);
        logoutBtn=view.findViewById(R.id.logout_btn);
        updateProfileBtn=view.findViewById(R.id.profile_update_btn);

        getUserData();

       updateProfileBtn.setOnClickListener(v -> updateBtnClick());

        logoutBtn.setOnClickListener(v -> {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        FirebaseUtil.logout();
                        Intent intent= new Intent(getContext(),SplashActivity.class);
                        startActivity(intent);
                    }
                }
            });


        });

        profilePic.setOnClickListener(v -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickerLauncher.launch(intent);
                            return null;
                        }
                    });
        });
        return view;
    }

    void  updateBtnClick(){
        String newUsername=usernameInput.getText().toString();
        if(newUsername.isEmpty()|| newUsername.length()<3){
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setUsername(newUsername);

    setInProgress(true);


    if(selectedImageUri!=null)
    {
        FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> updateToFirestore());
    }
    else {
        updateToFirestore();
    }

        updateToFirestore();
    }

    void updateToFirestore(){
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if(task.isSuccessful()){
                        AndroidUtil.showToast(getContext(),"Updated Successfully");
                    }else {
                        AndroidUtil.showToast(getContext(),"Updated failed");
                    }

                });

    }
    void getUserData(){


       setInProgress(true);

       FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                       .addOnCompleteListener(new OnCompleteListener<Uri>() {
                           @Override
                           public void onComplete(@NonNull Task<Uri> task) {
                               if(task.isSuccessful()){
                                   Uri uri=task.getResult();
                                   AndroidUtil.setProfilePic(getContext(),uri,profilePic);
                               }
                           }
                       });

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {

           setInProgress(false);



            currentUserModel=task.getResult().toObject(UserModel.class);
            usernameInput.setText(currentUserModel.getUsername());
            phoneInput.setText(currentUserModel.getPhone());


        });

    }
    void setInProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }

}