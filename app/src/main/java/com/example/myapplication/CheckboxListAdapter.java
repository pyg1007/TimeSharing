package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class CheckboxListAdapter extends BaseAdapter {

    private List<CheckboxListItem> checkboxListItems;
    private boolean CheckItem_flag = false;
    private Activity contexts;

    public CheckboxListAdapter(Activity context, List<CheckboxListItem> checkboxListItemList){
        this.checkboxListItems = checkboxListItemList;
        this.contexts = context;
    }

    @Override
    public int getCount() {
        return checkboxListItems.size();
    }

    @Override
    public CheckboxListItem getItem(int i) {
        return checkboxListItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        protected TextView textView;
        protected CheckBox checkBox;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;

        if (view == null) {
            LayoutInflater inflator = contexts.getLayoutInflater();
            view = inflator.inflate(R.layout.multiple_add, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.label);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.check);
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    checkboxListItems.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            view.setTag(viewHolder);
            view.setTag(R.id.label, viewHolder.textView);
            view.setTag(R.id.check, viewHolder.checkBox);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.checkBox.setTag(i); // This line is important.

        viewHolder.textView.setText(checkboxListItems.get(i).getUserid());
        viewHolder.checkBox.setChecked(checkboxListItems.get(i).isSelected());
        return view;
    }
}
