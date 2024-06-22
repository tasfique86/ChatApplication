package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.chatapplication.adapter.SearchUserRecyclerAdaptar;
import com.example.chatapplication.model.UserModel;
import com.example.chatapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.Queue;

public class SearchUserActivity extends AppCompatActivity {


    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;

    SearchUserRecyclerAdaptar adaptar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput=findViewById(R.id.user_search_input);
        searchButton=findViewById(R.id.search_user_button);
        backButton=findViewById(R.id.back_btn);
        recyclerView=findViewById(R.id.search_user_recyckerView);

        searchInput.requestFocus();

        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        searchButton.setOnClickListener(v -> {
            String searchTerm= searchInput.getText().toString();
            if(searchTerm.isEmpty()||searchTerm.length()<3)
            {
                searchInput.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });

    }

    void setupSearchRecyclerView(String searchTerm){

        Query query= FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username",searchTerm)
                .whereLessThanOrEqualTo("username",searchTerm+'\uf8ff');

        FirestoreRecyclerOptions<UserModel> oprtion = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();


        adaptar =new SearchUserRecyclerAdaptar(oprtion,getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptar);
        adaptar.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adaptar!=null)
            adaptar.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adaptar!=null)
            adaptar.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adaptar!=null)
            adaptar.startListening();
    }
}