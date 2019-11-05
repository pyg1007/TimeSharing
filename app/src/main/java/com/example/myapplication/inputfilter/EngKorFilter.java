package com.example.myapplication.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

public class EngKorFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
        if (charSequence.toString().matches("^[a-zA-Zㄱ-ㅣ가-힣]+$")){
            return charSequence;
        }else{
            return "";
        }
    }
}
