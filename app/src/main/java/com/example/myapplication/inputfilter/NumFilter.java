package com.example.myapplication.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

public class NumFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
        Pattern pt = Pattern.compile("^[0-9]+$");
        if (!pt.matcher(charSequence).matches()){
            return "";
        }else{
            return charSequence;
        }
    }
}
