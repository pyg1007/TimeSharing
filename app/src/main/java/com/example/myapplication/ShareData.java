package com.example.myapplication;

import android.util.Log;

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
        Log.e("before member : ", Memeberid);
        Log.e("memberid : ", memberid);
        Log.e("왜이럴까", String.valueOf(Memeberid.contains(memberid)));
        if(!Memeberid.contains(memberid)){
            Memeberid += "," + memberid;
        }
        Log.e("after member : ", Memeberid);
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
