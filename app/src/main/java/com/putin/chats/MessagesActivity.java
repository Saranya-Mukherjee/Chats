package com.putin.chats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends AppCompatActivity {

    CircleImageView dp;
    TextView uname;

    FirebaseUser firebaseUser;
    DatabaseReference ref;

    MaterialEditText text_send;

    boolean grp = false;
    String receiver_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        text_send = findViewById(R.id.text_send);

        dp = findViewById(R.id.dp);
        uname = findViewById(R.id.uname);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        Intent intent = getIntent();
        String uid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert uid != null;
        if (uid.startsWith("GGG:")) {
            grp = true;
            ref = FirebaseDatabase.getInstance().getReference("Groups").child(Objects.requireNonNull(intent.getStringExtra("grpid")));
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Group group = snapshot.getValue(Group.class);
                    assert group != null;
                    uname.setText(group.getName());
                    receiver_id = group.getId();
                    if (group.getImageURL().equals("default")) {
                        dp.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(MessagesActivity.this).load(group.getImageURL()).into(dp);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            ref = FirebaseDatabase.getInstance().getReference("Users").child(uid);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    uname.setText(user.getUsername());
                    receiver_id = user.getId();
                    if (user.getImageURL().equals("default")) {
                        dp.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(MessagesActivity.this).load(user.getImageURL()).into(dp);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    public void send(View view) {
        String msg = Objects.requireNonNull(text_send.getText()).toString();
        if (!msg.equals("")) {
            send_msg(firebaseUser.getUid(), msg, receiver_id);
            text_send.setText("");
        }
    }

    public void send_msg(String sender, String message, String receiver) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, String> hashmap = new HashMap<>();
        hashmap.put("sender", sender);
        hashmap.put("message", message);
        hashmap.put("receiver", receiver);

        if (grp) {
            reference.child("Chats_Grp").push().setValue(hashmap);
        } else {
            reference.child("Chats").push().setValue(hashmap);
        }

    }

}