package com.putin.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    List<Group> groups;
    List<User> users;

    FirebaseUser firebaseuser;
    DatabaseReference ref, ref2;

    RecyclerView rv1, rv2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        rv1 = view.findViewById(R.id.chats_grp);
        rv1.setHasFixedSize(true);
        rv1.setLayoutManager(new LinearLayoutManager(getContext()));

        rv2 = view.findViewById(R.id.chats_user);
        rv2.setHasFixedSize(true);
        rv2.setLayoutManager(new LinearLayoutManager(getContext()));

        groups = new ArrayList<Group>();
        users = new ArrayList<User>();

        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();

//        ref=FirebaseDatabase.getInstance().getReference("Chats");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String list=" ;";
//                users.clear();
//                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
//                    Chat chat=dataSnapshot.getValue(Chat.class);
//                    assert chat != null;
//                    Log.d("pass",chat.getMessage()+chat.getSender()+chat.getReceiver());
//                    if(chat.getSender().equals(firebaseuser.getUid())){
//                        Log.d("pass",chat.getMessage());
//                        if(!isPresent(list,chat.getReceiver())){
//                            Log.d("pass",list);
//                            getUser(chat.getReceiver());
//                            list+=chat.getReceiver()+";";
//                        }
//                    }
//                    else if(chat.getReceiver().equals(firebaseuser.getUid())){
//                        Log.d("pass",chat.getMessage());
//                        if(!isPresent(list,chat.getSender())){
//                            Log.d("pass",list);
//                            getUser(chat.getSender());
//                            list+=chat.getSender()+";";
//                        }
//                    }
//                }
//                UserAdapter userAdapter = new UserAdapter(getContext(), users);
//                rv2.setAdapter(userAdapter);
//                for(User user : users) {
//                    Log.d("pass",user.getUsername());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        ref2=FirebaseDatabase.getInstance().getReference("Chats_Grp");
//        ref2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String list=";";
//                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
//                    Chat chat=snapshot.getValue(Chat.class);
//                    assert chat != null;
//                    if(chat.getSender().equals(firebaseuser.getUid())){
//                        if(!isPresent(list,chat.getReceiver())){
//                            getGroup(chat.getReceiver());
//                            list+=chat.getReceiver()+";";
//                        }
//                    }
//                    else if(chat.getReceiver().equals(firebaseuser.getUid())){
//                        if(!isPresent(list,chat.getReceiver())){
//
//                            list+=chat.getReceiver()+";";
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        return view;
    }

    public boolean isPresent(String list, String value) {
        for (String s : list.split(";")) {
            if (s.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public void getUser(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                users.add(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean isInGroup(String id, String people, String grp_id, String list) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                String pno = user.getPno();
                if (isPresent(list, grp_id)) {
                    if (isPresent(people, pno)) {
                        getGroup(grp_id);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return false;
    }

    public void getGroup(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                groups.add(group);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}