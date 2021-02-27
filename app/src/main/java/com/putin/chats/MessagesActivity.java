package com.putin.chats;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends AppCompatActivity {

    CircleImageView dp;
    TextView uname;

    FirebaseUser firebaseUser;
    DatabaseReference ref;

    MessageAdapter messageAdapter;
    List<Chat> chats;

    RecyclerView recyclerview;

    LinearLayoutManager linearLayoutManager;

    MaterialEditText text_send;

    boolean first = true;

    boolean grp = false;
    String receiver_id = "";
    String reply = "none";

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

        recyclerview = findViewById(R.id.chats_rv);
        recyclerview.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(MessagesActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerview.setLayoutManager(linearLayoutManager);

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

                    read_grp_chats(firebaseUser.getUid(), intent.getStringExtra("grpid"), group.getImageURL());

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
                    read_chats(firebaseUser.getUid(), uid, user.getImageURL());
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
        hashmap.put("reply", reply);

        if (grp) {
            reference.child("Chats_Grp").child(receiver).push().setValue(hashmap);
        } else {
            reference.child("Chats").child(firebaseUser.getUid()).child(receiver_id).push().setValue(hashmap);
            reference.child("Chats").child(receiver_id).child(firebaseUser.getUid()).push().setValue(hashmap);
        }

    }

    public void read_chats(String user_id, String id, String imageURL) {

        chats = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(firebaseUser.getUid()).child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for (int n = 0; n < 20; n++) {
                    Chat chat = new Chat(user_id, id, "", "none");
                    chats.add(chat);
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    assert chat != null;
                    if ((chat.getReceiver().equals(id) && chat.getSender().equals(user_id)) ||
                            (chat.getReceiver().equals(user_id) && chat.getSender().equals(id))) {
                        chats.add(chat);
                    }
                }
                messageAdapter = new MessageAdapter(MessagesActivity.this, chats, imageURL, grp);
                recyclerview.setAdapter(messageAdapter);
                recyclerview.setVisibility(View.VISIBLE);
                findViewById(R.id.imageView).setVisibility(View.GONE);
                Log.d("auth", "error");
                if (first) {
                    int pos = messageAdapter.getItemCount() - 30;
                    if (pos < 0) {
                        pos = 0;
                    }
                    linearLayoutManager.scrollToPositionWithOffset(pos, 0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            linearLayoutManager.scrollToPositionWithOffset(messageAdapter.getItemCount() - 1, 0);
                        }
                    }, 100);
//                    new Refresh().execute();
                    first = false;
                }
                recyclerview.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    if (bottom < oldBottom) {
                        linearLayoutManager.scrollToPositionWithOffset(messageAdapter.getItemCount() - 1, 0);
                    }
                });
                recyclerview.setVisibility(View.VISIBLE);
                findViewById(R.id.imageView).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void read_grp_chats(String user_id, String id, String imageURL) {

        chats = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats_Grp").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for (int n = 0; n < 20; n++) {
                    Chat chat = new Chat(user_id, id, "", "none");
                    chats.add(chat);
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    assert chat != null;
                    if ((chat.getReceiver().equals(id))) {
                        chats.add(chat);
                    }
                }
                messageAdapter = new MessageAdapter(MessagesActivity.this, chats, imageURL, grp);
                recyclerview.setAdapter(messageAdapter);
                if (first) {
                    int pos = messageAdapter.getItemCount() - 30;
                    if (pos < 0) {
                        pos = 0;
                    }
                    linearLayoutManager.scrollToPositionWithOffset(pos, 0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            linearLayoutManager.scrollToPositionWithOffset(messageAdapter.getItemCount() - 1, 0);
                        }
                    }, 100);
//                    new Refresh().execute();
                    first = false;
                }
                recyclerview.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    if (bottom < oldBottom) {
                        linearLayoutManager.scrollToPositionWithOffset(messageAdapter.getItemCount() - 1, 0);
                    }
                });
                recyclerview.setVisibility(View.VISIBLE);
                findViewById(R.id.imageView).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}