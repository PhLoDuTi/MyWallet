package com.tdtu.mywallet.about_menu;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tdtu.mywallet.R;

import java.util.ArrayList;
public class MemberAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<Member> members;

    public MemberAdapter(Context context, ArrayList<Member> members) {
        this.context = context;
        this.members = members;
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Object getItem(int position) {
        return members.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.member_item, parent, false);
        }

        Member member = (Member) getItem(position);

        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        nameTextView.setText(member.getName());

        TextView linkTextView = convertView.findViewById(R.id.linkTextView);
        linkTextView.setText(member.getLink());

        return convertView;
    }
}
