package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class UserListAdapter extends BaseAdapter{

    private List<User> userList;
    private User user;



    public UserListAdapter(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public User getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final Context context = viewGroup.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.user, viewGroup, false);
        }

        user = getItem(i);

        TextView userID = (TextView)view.findViewById(R.id.userID);
        userID.setText(user.getUserID());

        return view;
    }


}
