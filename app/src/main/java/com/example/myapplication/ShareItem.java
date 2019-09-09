package com.example.myapplication;

public class ShareItem {

    private String[] member;
    private int[] membercount;

    public ShareItem(){
        member = new String[24];
        membercount = new int[24];
        for(int i =0; i<member.length; i++){
            member[i] = "";
        }
    }

    public String getMember(int index) {
        return member[index];
    }

    public int getMembercount(int index) {
        return membercount[index];
    }

    public String getStartTime(int index){
        String startTime = "";
        if(index<10){
            startTime = "0" + index + " : 00";
        }else{
            startTime = index + " : 00";
        }
        return startTime;
    }

    public String getEndTime(int index){
        String endTime = "";
        if(index<10){
            endTime = "0" + (index+1) + " : 00";
        }else{
            endTime = (index+1) + " : 00";
        }
        return endTime;
    }

    public void addmember(String memberid, String previoustime, String aftertime){
        int startTime = Integer.parseInt(previoustime.substring(0,2));
        int endTime = Integer.parseInt(aftertime.substring(0,2));
        for(int i = startTime; i<endTime; i++) {
            member[i] += memberid;
            if(i<endTime-1){
                member[i] += ",";
            }
            membercount[i] ++;
        }
    }

    public void clear(){
        for(int i =0; i<24; i++){
            member[i] = "";
            membercount[i] = 0;
        }
    }
}