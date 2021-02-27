package com.putin.chats;

import android.os.Bundle;
import android.util.Log;
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
import java.util.Objects;

public class GroupsFragment extends Fragment {

    RecyclerView recyclerview;
    GroupAdapter groupAdapter;
    List<Group> groups;
    String pno = "none";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        recyclerview = view.findViewById(R.id.users_rv);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        groups = new ArrayList<Group>();

        read_users();

        return view;
    }

    public boolean isPresent(String name, String id) {
        for (String person : name.split(";")) {
            if (person.equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void read_users() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        assert firebaseUser != null;
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pno = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("pass", pno);
                if (!pno.equals("none")) {
                    groups.clear();
                    Log.d("pass", pno);
                    for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                        Group group = datasnapshot.getValue(Group.class);
                        assert group != null;
                        Log.d("pass", group.getPeople());
                        if (isPresent(group.getPeople(), pno)) {
                            groups.add(group);
                        }
                    }
                    groupAdapter = new GroupAdapter(getContext(), groups);
                    recyclerview.setAdapter(groupAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}