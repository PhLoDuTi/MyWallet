package com.tdtu.mywallet.about_menu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.tdtu.mywallet.R;

import java.util.ArrayList;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ArrayList<Member> members = new ArrayList<>();
        members.add(new Member("Version 1.1",
                ""));
        members.add(new Member("Contributors",
                "https://github.com/PhLoDuTi/MyWallet/graphs/contributors"));
        members.add(new Member("GitHub",
                "https://github.com/PhLoDuTi/MyWallet"));


        MemberAdapter adapter = new MemberAdapter(this, members);

        ListView listView = findViewById(R.id.aboutListView);
        listView.setAdapter(adapter);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("About");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button press
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}