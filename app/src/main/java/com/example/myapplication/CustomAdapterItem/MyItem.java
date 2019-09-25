package com.example.myapplication.CustomAdapterItem;

public class MyItem {
    private int index;
    private String Title;
    private String Userid;
    private String Contents;
    private String Previoustime;
    private String Aftertime;
    private String Savedate;

    public MyItem(int Index, String userid, String title, String contents, String previoustime, String aftertime, String savedate){
        this.index = Index;
        this.Userid = userid;
        this.Title = title;
        this.Contents = contents;
        this.Previoustime = previoustime;
        this.Aftertime = aftertime;
        this.Savedate = savedate;
    }

    public String getContents() {
        return Contents;
    }

    public void setContents(String contents) {
        Contents = contents;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSavedate() {
        return Savedate;
    }

    public void setSavedate(String savedate) {
        Savedate = savedate;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getPrevioustime() {
        return Previoustime;
    }

    public void setPrevioustime(String previoustime) {
        this.Previoustime = previoustime;
    }

    public String getAftertime() {
        return Aftertime;
    }

    public void setAftertime(String aftertime) {
        this.Aftertime = aftertime;
    }

}
