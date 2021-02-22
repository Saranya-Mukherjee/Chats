package com.putin.chats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rengwuxian.materialedittext.MaterialEditText;

public class Registration extends AppCompatActivity {

    MaterialEditText uname,email,password;

    FirebaseAuth auth;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        uname=findViewById(R.id.uname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.pass);

        auth=FirebaseAuth.getInstance();

    }

    public void register(String username,String email,String password) {

        

    }

}