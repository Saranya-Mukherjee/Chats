package com.putin.chats;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_GRP_TYPE_LEFT = 0;
    public List<String> inits;
    Context context;
    List<Chat> chats;
    String img;
    String init;
    String imageURL = "default";
    int t = 2;
    boolean grp;
    boolean done = false;
    HashMap<String, String> people = new HashMap<>();
    SharedPreferences sharedpreferences;

    public MessageAdapter(Context context, List<Chat> chats, String img, boolean grp) {
        this.chats = chats;
        this.context = context;
        this.img = img;
        this.grp = grp;
//        this.init = init;
        inits = new ArrayList<>();
    }


    public boolean makePerson(String id) {
        String name = people.get(id);
        if (name == null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(id);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                    people.put(id, name.split(" ")[0]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return false;
        } else {
            return true;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
        t = 0;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            t = 1;
        } else if (viewType == MSG_GRP_TYPE_LEFT) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_left_grp, parent, false);
            t = 0;
        }

        return new MessageAdapter.ViewHolder(view);
    }

    public void addInits(String init) {
        inits.add(init);
    }

    public void make_inits() {
        done = inits.size() == 0;
//        Log.d("auth", Integer.toString(inits.size()));
        if (chats.size() > inits.size()) {
            final int[] cnt = {1};
            for (Chat c : chats) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(c.getSender());

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                        Log.d("auth", name);
                        Log.d("auth", c.getMessage());
                        Log.d("auth", Integer.toString(cnt[0]));
                        inits.add(name.split(" ")[0]);
                        cnt[0]++;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = chats.get(position);
        holder.msg.setText(chat.getMessage());
        boolean is = makePerson(chat.getSender());
        if (holder.sender != null) {
            //                if() {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d("auth","wrong one.");
//                            holder.sender.setText(inits.get(position));
//                        }
//                    }, 100);
//                }
//                else{
//                    holder.sender.setText(inits.get(position));
//                }
            if (is) {
                holder.sender.setText(people.get(chat.getSender()));
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("auth", "new one.");
                        holder.sender.setText(people.get(chat.getSender()));
                        Log.d("auth", people.toString());
                    }
                }, 100);
            }
//                Log.d("auth",inits.get(position));
        }
//        holder.msg.setText(chat.getMessage());
//        if (imageURL.equals("default")) {
//            holder.dp.setImageResource(R.mipmap.ic_launcher);
//        } else {
//            Glide.with(context).load(imageURL).into(holder.dp);
//        }

//        if((t==MSG_TYPE_LEFT)&&!grp){
//            holder.sender.setVisibility(View.GONE);
//        }

//        if(grp&&t==MSG_GRP_TYPE_LEFT){
//            holder.sender.setVisibility(View.GONE);
//        }

//        Log.d("pass",Integer.toString(t));

//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, Extras.class);
//                intent.putExtra("userid", chat.getMessage());
//                context.startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        if (chats.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else if (!chats.get(position).getSender().equals(firebaseUser.getUid()) && grp) {
            return MSG_GRP_TYPE_LEFT;
        }
        return MSG_TYPE_LEFT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView msg;
        public TextView sender;
        public CircleImageView dp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            msg = itemView.findViewById(R.id.message);
            sender = itemView.findViewById(R.id.sender);
        }
    }
}
