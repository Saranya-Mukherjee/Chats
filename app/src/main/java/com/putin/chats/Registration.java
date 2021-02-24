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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Objects;

public class Registration extends AppCompatActivity {

    MaterialEditText uname, email, password, phone;

    FirebaseAuth auth;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Registration");
//        getSupportActionBar().setLogo(R.mipmap.ic_launcher);

        uname = findViewById(R.id.uname);
        email = findViewById(R.id.text_send);
        password = findViewById(R.id.pass);
        phone = findViewById(R.id.pno);

        auth = FirebaseAuth.getInstance();

    }

    public void doRegister(View view) {

        String un = Objects.requireNonNull(uname.getText()).toString();
        String em = Objects.requireNonNull(email.getText()).toString();
        String pass = Objects.requireNonNull(password.getText()).toString();
        String ph = Objects.requireNonNull(phone.getText()).toString();

        if ((TextUtils.isEmpty(em)) || (TextUtils.isEmpty(un)) || (TextUtils.isEmpty(ph)) || (TextUtils.isEmpty(pass))) {
            Toast.makeText(Registration.this, "All Fields need to be filled.", Toast.LENGTH_SHORT).show();
        } else if (pass.length() < 6) {
            Toast.makeText(Registration.this, "Password should be at least of 6 characters..", Toast.LENGTH_SHORT).show();
        } else {
            register(un, em, pass, ph);
        }

    }

    public void register(String username, String email, String password, String phone) {

        Toast.makeText(Registration.this, "Working on it...", Toast.LENGTH_LONG).show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            assert user != null;
                            String uid = user.getUid();
                            ref = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                            HashMap<String, String> hashmap = new HashMap<>();

                            hashmap.put("id", uid);
                            hashmap.put("username", username);
                            hashmap.put("imageURL", "default");
                            hashmap.put("phone", phone);

                            ref.setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Registration.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Registration.this, Login.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(Registration.this, "Registration Unsuccessful.\nPlease Try Again.", Toast.LENGTH_LONG).show();
                            uname.setText("");
                        }
                    }
                });

    }

    public void login(View view) {
        Intent intent = new Intent(Registration.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}