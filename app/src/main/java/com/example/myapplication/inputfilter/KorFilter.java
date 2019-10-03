package com.example.myapplication.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

public class KorFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
        Pattern pt = Pattern.compile("^[ㄱ-ㅣ가-힣]+$");
        if (!pt.matcher(charSequence).matches()){
            return "";
        }else {
            return charSequence;
        }
    }
}
