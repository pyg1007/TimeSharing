package com.example.myapplication.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

public class NumKorEngFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
        if (charSequence.toString().matches("^[0-9ㄱ-ㅣ가-힣a-zA-Z]+$")){
            return charSequence;
        }else{
            return "";
        }
    }
}
