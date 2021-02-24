package com.putin.chats;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context context;
    List<User> users;

    public UserAdapter(Context context, List<User> users) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.uname.setText(user.getUsername());
        if (user.getImageURL().equals("default")) {
            holder.dp.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(user.getImageURL()).into(holder.dp);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessagesActivity.class);
                intent.putExtra("userid", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView uname;
        public CircleImageView dp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            uname = itemView.findViewById(R.id.uname);
            dp = itemView.findViewById(R.id.dp);

        }
    }

}
