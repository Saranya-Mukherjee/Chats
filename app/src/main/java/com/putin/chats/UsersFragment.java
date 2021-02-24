package com.putin.chats;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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

public class UsersFragment extends Fragment {

    RecyclerView recyclerview;
    UserAdapter userAdapter;
    List<User> users;
    List<ContactsInfo> contactsInfoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerview = view.findViewById(R.id.users_rv);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        users = new ArrayList<User>();

        read_users();

        return view;
    }

    public boolean find(String s) {
        for (ContactsInfo contact : contactsInfoList) {
            if (contact.getPhoneNumber().equals(s))
                return true;
        }
        return false;
    }


    public void getContacts() {
        ContentResolver contentResolver = Objects.requireNonNull(getActivity()).getContentResolver();
        String contactId = null;
        String displayName = null;
        contactsInfoList = new ArrayList<ContactsInfo>();
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        assert cursor != null;
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    ContactsInfo contactsInfo = new ContactsInfo();
                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contactsInfo.setContactId(contactId);
                    contactsInfo.setDisplayName(displayName);

                    Cursor phoneCursor = getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);

                    assert phoneCursor != null;
                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        contactsInfo.setPhoneNumber(phoneNumber);
                    }

                    phoneCursor.close();

                    contactsInfoList.add(contactsInfo);
                }
            }
        }
        cursor.close();
    }

    public void read_users() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    User user = datasnapshot.getValue(User.class);
                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getId().equals(firebaseUser.getUid())) {
                        users.add(user);
                    }
                }
                userAdapter = new UserAdapter(getContext(), users);
                recyclerview.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    public class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            read_users();

            super.onPostExecute(aVoid);
        }
    }

}