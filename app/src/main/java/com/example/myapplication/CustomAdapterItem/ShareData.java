package com.example.myapplication.CustomAdapterItem;

import com.example.myapplication.CustomAdapterItem.MyItem;

public class ShareData {

    private String Memeberid;
    private String StartTime;
    private String EndTime;
    private String SaveDate;

    public ShareData(MyItem myItem, int time){
        this.Memeberid = myItem.getUserid();
        int end = time+1;
        if(time<10) {
            this.StartTime = "0" + String.valueOf(time) + " : 00";
        }if(end<10) {
            this.EndTime = "0" + String.valueOf(end) + " : 00";
        }if(time>=10) {
            this.StartTime = String.valueOf(time) + " : 00";
        }if(end>=10) {
            this.EndTime = String.valueOf(time + 1) + " : 00";
        }
        this.SaveDate = myItem.getSavedate();
    }

    public void addmember(String memberid){
        if(!Memeberid.contains(memberid)){
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
