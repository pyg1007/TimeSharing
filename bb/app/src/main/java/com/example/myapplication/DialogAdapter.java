package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

public class DialogAdapter extends BaseAdapter {

    private List<MyItem> myItem;
    private MyItem item;


    public DialogAdapter(List<MyItem> myItems) {
        this.myItem = myItems;
    }

    @Override
    public int getCount() {
        return myItem.size();
    }

    @Override
    public MyItem getItem(int i) {
        return myItem.get(i);
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
            view = inflater.inflate(R.layout.dialogitem, viewGroup, false);
        }

        item = getItem(i);

        TextView Time = (TextView)view.findViewById(R.id.time);
        TextView Members = (TextView)view.findViewById(R.id.members);

        Time.setText(item.getPrevioustime() + " ~ " + item.getAftertime());
        Members.setText(item.getUserid());

        return view;
    }
}
