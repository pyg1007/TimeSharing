package com.example.myapplication.Empty;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.List;

public class EmptyAdapter extends BaseAdapter {

    private List<EmptyItem> emptyItemList;
    private LayoutInflater layoutInflater;

    public EmptyAdapter(Context context, List<EmptyItem> emptyItems){
        layoutInflater = LayoutInflater.from(context);
        this.emptyItemList = emptyItems;
    }

    @Override
    public int getCount() {
        return emptyItemList.size();
    }

    @Override
    public EmptyItem getItem(int i) {
        return emptyItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    static class ViewHolder {
        protected TextView MenuNametext;
        protected TextView Timetext;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.emptylist, null);
            viewHolder = new ViewHolder();
            viewHolder.MenuNametext = (TextView) view.findViewById(R.id.Menu);
            viewHolder.Timetext = (TextView) view.findViewById(R.id.Time);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.MenuNametext.setText(emptyItemList.get(i).getMenu());
        viewHolder.Timetext.setGravity(Gravity.RIGHT);
        viewHolder.Timetext.setText(String.valueOf(emptyItemList.get(i).getStart()) +" ~ " + String.valueOf(emptyItemList.get(i).getEnd()));
        return view;

    }

}
