package com.putin.chats;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

public class Login extends AppCompatActivity {

    MaterialEditText email, password;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        getSupportActionBar().setLogo(R.drawable.ic_launcher_foreground);

        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);

        auth = FirebaseAuth.getInstance();


    }

    public void signup(View view) {
        Intent intent = new Intent(Login.this, Registration.class);
        startActivity(intent);
    }

    public void authorise(View view) {

        String em = Objects.requireNonNull(email.getText()).toString();
        String pass = Objects.requireNonNull(password.getText()).toString();

        if ((TextUtils.isEmpty(em)) || (TextUtils.isEmpty(pass))) {
            Toast.makeText(Login.this, "All Fields need to be filled.", Toast.LENGTH_SHORT).show();
        } else {
            auth.signInWithEmailAndPassword(em, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Logged in.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Incorrect Credentials.\nPlease Retry.", Toast.LENGTH_SHORT).show();
                        email.setText("");
                        password.setText("");
                    }
                }
            });
        }

    }

}