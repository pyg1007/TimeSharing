package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

public class DialogAdapter extends BaseAdapter {

    private List<ShareData> shareDataList;
    private ShareData shareData;


    public DialogAdapter(List<ShareData> shareData) {
        this.shareDataList = shareData;
    }

    @Override
    public int getCount() {
        return shareDataList.size();
    }

    @Override
    public ShareData getItem(int i) {
        return shareDataList.get(i);
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

        shareData = getItem(i);

        TextView Time = (TextView)view.findViewById(R.id.time);
        TextView Members = (TextView)view.findViewById(R.id.members);

        Time.setText(shareData.getStartTime() + " ~ " + shareData.getEndTime());
        Members.setText(shareData.getMemeberid());

        return view;
    }
}
