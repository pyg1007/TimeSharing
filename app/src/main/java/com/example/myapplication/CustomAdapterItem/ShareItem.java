package com.example.myapplication.CustomAdapterItem;

import java.util.ArrayList;
import java.util.List;

public class ShareItem {

    private String[] member;
    private int[] membercount;
    private List<ShareData> shareDataList;

    public ShareItem(){
        shareDataList = new ArrayList<>();
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

    public String EmptyTimesum(int start, int end){
        String sum = "";
        int startIndex = start;
        boolean isTime = true;
        for(int i=start; i<end; i++){
            if(getMembercount(i) == 0)
                isTime = false;

            if(getMembercount(i) != 0)
            {
                if(!isTime){
                    sum += String.valueOf(startIndex + " ~ " + i + ",");
                }
                isTime = true;
                startIndex = i+1;
            }
        }
        if(!isTime)
            sum += String.valueOf(startIndex + " ~ " + end);
        return sum;
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
            membercount[i] ++;
        }
    }

    public void addmember(MyItem myItem){
        int startTime = Integer.parseInt(myItem.getPrevioustime().substring(0,2));
        int endTime = Integer.parseInt(myItem.getAftertime().substring(0,2));
        for(int i = startTime; i<endTime; i++) {
            member[i] += myItem.getUserid();
            membercount[i] ++;
            ShareData shareData = check(i);
            if(shareData == null)
                shareDataList.add(new ShareData(myItem,i));
            else{
                shareData.addmember(myItem.getUserid());
            }
        }
    }

    public ShareData check(int Nowtime){
        String time;
        if(Nowtime<10)
            time = "0" + Nowtime + " : 00";
        else{
            time = Nowtime + " : 00";
        }
        for(int i=0; i<shareDataList.size(); i++){
            if(time.equals(shareDataList.get(i).getStartTime())){
                return shareDataList.get(i);
            }
        }
        return null;
    }

    public List<ShareData> get_List(){
        return shareDataList;
    }

    public void clear(){
        shareDataList.clear();
    }
}