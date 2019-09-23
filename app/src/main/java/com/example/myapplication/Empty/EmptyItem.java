package com.example.myapplication.Empty;

public class EmptyItem {

    private String Menu;
    private int start, end;

    public EmptyItem(String MenuName, int Start, int End){
        this.Menu = MenuName;
        this.start = Start;
        this.end = End;
    }

    public String getMenu() {
        return Menu;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
