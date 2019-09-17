package com.example.myapplication;

public class ShareData {

    private String Memeberid;
    private String StartTime;
    private String EndTime;
    private String SaveDate;

    public ShareData(MyItem myItem, int time){
        this.Memeberid = myItem.getUserid();
        this.StartTime = String.valueOf(time) + " : 00";
        this.EndTime = String.valueOf(time+1) + " : 00";
        this.SaveDate = myItem.getSavedate();
    }

    public void addmember(String memberid){
        if(!Memeberid.equals(memberid)){
            Memeberid += "," + memberid;
        }
    }

    public String getMemeberid() {
        return Memeberid;
    }

    public String getStartTime() {
        return StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public String getSaveDate() {
        return SaveDate;
    }
}
