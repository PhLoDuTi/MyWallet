package com.tdtu.mywallet;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.tdtu.mywallet.Member;
import java.util.ArrayList;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ArrayList<Member> members = new ArrayList<>();
        members.add(new Member("Pham Long Duy Tiến", "https://github.com/PhLoDuTi"));
        members.add(new Member("Phạm Tấn Duy", "https://github.com/GHTDuy"));
        members.add(new Member("Trần Trung Dũng", "https://github.com/YuInH"));
        members.add(new Member("Vi Nguyễn Thành Đạt", "https://github.com/thanhviD"));
        members.add(new Member("Nguyễn Ngọc Tú", "https://github.com/tunguyen2207"));
        members.add(new Member("Project GitHub", "https://github.com/PhLoDuTi/MyWallet"));


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