package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatapplication.adapter.ChatRecyclerAdapter;
import com.example.chatapplication.adapter.SearchUserRecyclerAdaptar;
import com.example.chatapplication.model.ChatMessageModel;
import com.example.chatapplication.model.ChatRoomModel;
import com.example.chatapplication.model.UserModel;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.net.MediaType;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;

    ChatRecyclerAdapter adapter;

    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;

    String chatroomId;
    ChatRoomModel chatroomModel;

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId= FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUserId());

        messageInput =findViewById(R.id.chat_message_input);
        sendMessageBtn=findViewById(R.id.message_send_btn);
        backBtn=findViewById(R.id.back_btn);
        otherUsername=findViewById(R.id.other_username);
        imageView=findViewById(R.id.profile_pic_image_view);
        recyclerView=findViewById(R.id.chat_recyclerView);

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> t) {
                        if(t.isSuccessful()){
                            Uri uri=t.getResult();
                            AndroidUtil.setProfilePic(ChatActivity.this,uri,imageView);
                        }
                    }
                });


        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());

        getOrCreateChatroomModel();

        setupChatRecyclerView();

        sendMessageBtn.setOnClickListener(v -> {
         //   AndroidUtil.showToast(getApplicationContext(),"Send F");
            String message= messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        });


    }

    void setupChatRecyclerView(){

        Query query= FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> option = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();


        adapter =new ChatRecyclerAdapter(option,getApplicationContext());
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });

    }

    void sendMessageToUser(String message){
      //  AndroidUtil.showToast(getApplicationContext(),"ToUser");
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);

        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel=new ChatMessageModel(message,FirebaseUtil.currentUserId(),Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
                         //   sendNotification(message);
                        }
                    }
                });
    }



    void getOrCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               chatroomModel =task.getResult().toObject(ChatRoomModel.class);
               if(chatroomModel==null){
                   chatroomModel = new ChatRoomModel(
                     chatroomId,
                           Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()) ,
                           Timestamp.now(),
                           ""
                   );
                   FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
               }
           }
        });
    }
//    void sendNotification(String message){
//
//        //current username,messagea,currentuserId,otherUserToken
//
//        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                UserModel currentUser=task.getResult().toObject(UserModel.class);
//                try {
//                        JSONObject jsonObject =new JSONObject();
//
//                        JSONObject notificationObj= new JSONObject();
//                        notificationObj.put("title",currentUser.getUsername());
//                        notificationObj.put("boby",message);
//
//                        JSONObject dataObj =new JSONObject();
//                        dataObj.put("userId",currentUser.getUserId());
//
//                        jsonObject.put("notification",notificationObj);
//                        jsonObject.put("data",dataObj);
//                        jsonObject.put("to",otherUser.getFcmToken());
//
//                        callApi(jsonObject);
//                }catch (Exception e){
//
//                }
//            }
//        });
//
//    }
//    void callApi(JSONObject jsonObject){
//
//        MediaType JSON = MediaType.get("application/json");
//
//
//        OkHttpClient client = new OkHttpClient();
//        String url="http://fcm.googleapis.com/fcm/send";
//        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
//        Request request=new Request.Builder()
//                .url(url)
//                .post(body)
//                .header("Authorization","Bearer AAAAuUEf4uI:APA91bG3UahSxU4qfD3_OFAy8UVMKnCsHGmKByHCczSx814PpkPLUkr1WYly5stG_LHH55j9FbznnLiBPvTNxd4OZgTQKzQRPfPLjwt8KomgqNCMefWGCJZdDUKFASILqQ2gYsj7DB3d")
//                .build();
//        client.newCall(request);
//
//
//    }

//    void sendNotification(String message){
//
//        //current username, message, current userId, otherUserToken
//
//        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                UserModel currentUser=task.getResult().toObject(UserModel.class);
//                try {
//                    JSONObject jsonObject = new JSONObject();
//
//                    JSONObject notificationObj = new JSONObject();
//                    notificationObj.put("title", currentUser.getUsername());
//                    notificationObj.put("body", message); // Fixed the typo in "body"
//
//                    JSONObject dataObj = new JSONObject();
//                    dataObj.put("userId", currentUser.getUserId());
//
//                    jsonObject.put("notification", notificationObj);
//                    jsonObject.put("data", dataObj);
//                    jsonObject.put("to", otherUser.getFcmToken());
//
//                    sendNotificationRequest(jsonObject);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

//    void sendNotificationRequest(JSONObject jsonObject) {
//        String url = "YOUR_FCM_API_URL";
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // Handle success response
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // Handle error response
//            }
//        });
//
//        // Add the request to the RequestQueue.
//        RequestQueue requestQueue = Volley.newRequestQueue(ChatActivity.this); // Pass your context here
//        requestQueue.add(jsonObjectRequest);
//    }

}