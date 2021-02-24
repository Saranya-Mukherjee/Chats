package com.putin.chats;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Loged extends AppCompatActivity {

    CircleImageView dp;
    TextView uname;

    FirebaseUser firebaseUser;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loged);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        dp = findViewById(R.id.dp);
        uname = findViewById(R.id.uname);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                uname.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    dp.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(Loged.this).load(user.getImageURL()).into(dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        TabLayout tabLayout = findViewById(R.id.tab);
        ViewPager viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
        viewPagerAdapter.addFragment(new GroupsFragment(), "Groups");
        viewPagerAdapter.addFragment(new UsersFragment(), "Users");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);


    }

    public boolean contactExists(Context context, String number) {
        if (number != null) {
            ContentResolver cr = context.getContentResolver();
            Cursor curContacts = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

            while (curContacts.moveToNext()) {
                String contactNumber = curContacts.getString(curContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (number.equals(contactNumber)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Loged.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(Loged.this, "Logged Out.", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
                return true;
        }
        return false;
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragments;
        ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);


            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {

            fragments.add(fragment);
            titles.add(title);

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}