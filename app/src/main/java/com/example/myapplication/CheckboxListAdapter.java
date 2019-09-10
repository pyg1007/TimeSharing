package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class CheckboxListAdapter extends BaseAdapter {

    private List<String> checkboxList;
    private boolean[] isCheckedConfrim;
    private LayoutInflater layoutInflater;

    public CheckboxListAdapter(Context context, List<String> checkboxListItemList){
        layoutInflater = LayoutInflater.from(context);
        this.checkboxList = checkboxListItemList;
        this.isCheckedConfrim = new boolean[checkboxList.size()];
    }

    @Override
    public int getCount() {
        return checkboxList.size();
    }

    @Override
    public String getItem(int i) {
        return checkboxList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        protected CheckBox checkBox;
    }

    public void setChecked(int position){
        isCheckedConfrim[position] = !isCheckedConfrim[position];
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.multiple_add, null);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.check);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.checkBox.setClickable(false);
        viewHolder.checkBox.setFocusable(false);

        viewHolder.checkBox.setChecked(isCheckedConfrim[i]);
        return view;
    }
}
