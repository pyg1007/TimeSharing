package com.example.myapplication.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

public class NonSpecialCharactersFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            if (!Character.isLetterOrDigit(source.charAt(i))) {
                return "";
            }
        }
        return null;
    }
}
