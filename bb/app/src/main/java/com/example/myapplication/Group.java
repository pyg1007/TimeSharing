package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Group extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMonthChangedListener {

    private String tablename;
    private String userid;
    private String memberid;
    private TextView DateTextView;

    private MyItemManager myItemManager;

    private int _ID;
    private String Title, ID, Contents, Previoustime, Aftertime, savedate;

    private List<MyItem> myItems; // 전체 스케쥴 리스트
    private List<MyItem> myItemList; // 동일날짜 스케쥴 저장용 리스트
    private List<MyItem> showitems; // 공유화면에 보여줄 리스트

    private MaterialCalendarView materialCalendarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        myItemManager = new MyItemManager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myItems = new ArrayList<>();
        myItemList = new ArrayList<>();
        showitems = new ArrayList<>();

        Intent intent = getIntent();
        tablename = intent.getStringExtra("GroupName");
        userid = intent.getStringExtra("userid");
        memberid = intent.getStringExtra("memberid");

        Calandar_init();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        Groupadd groupadd =  new Groupadd();
        groupadd.execute();

        JoinSchedule joinSchedule = new JoinSchedule();
        joinSchedule.execute();

    }

    public void Calandar_init(){
        materialCalendarView = findViewById(R.id.calendarView);

        materialCalendarView.setOnMonthChangedListener(Group.this);
        materialCalendarView.setTopbarVisible(false);

        materialCalendarView.state().edit().setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2019,0,1))
                .setMaximumDate(CalendarDay.from(2030,12,31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator());

        materialCalendarView.setShowOtherDates(MaterialCalendarView.SHOW_OUT_OF_RANGE);
        materialCalendarView.setDynamicHeightEnabled(true);
        materialCalendarView.setCurrentDate(new Date(System.currentTimeMillis()));
        materialCalendarView.setDateSelected(new Date(System.currentTimeMillis()),true);
        materialCalendarView.setSelectedDate(new Date(System.currentTimeMillis()));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        DateTextView = findViewById(R.id.calendarDate);
        DateTextView.setGravity(Gravity.CENTER);
        DateTextView.setTextSize(20);
        DateTextView.setText(date.getYear() + "년" + (date.getMonth()+1) + "월");
    }


    public class Groupadd extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/singlegroupadd.php";

                String data = URLEncoder.encode("Userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
                data += "&" + URLEncoder.encode("Tablename","UTF-8") + "=" + URLEncoder.encode(tablename,"UTF-8");
                //for(int i =0; i<memberid.length; i++){
                    data += "&" + URLEncoder.encode("Memberid","UTF-8") + "=" + URLEncoder.encode(memberid,"UTF-8");
               // }
                URL url = new URL(link);

                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                InputStream inputStream = conn.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;

                StringBuilder stringBuilder = new StringBuilder();

                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                wr.close();
                return  stringBuilder.toString().trim();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    public class JoinSchedule extends  AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/join.php";

                String data = URLEncoder.encode("Tablename", "UTF-8") + "=" + URLEncoder.encode(tablename, "UTF-8");
                URL url = new URL(link);

                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                InputStream inputStream = conn.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;

                StringBuilder stringBuilder = new StringBuilder();

                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                wr.close();
                return  stringBuilder.toString().trim();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;
                ArrayList<CalendarDay> days = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    _ID = object.getInt("_id");
                    ID = object.getString("userid");
                    Title = object.getString("title");
                    Contents = object.getString("contents");
                    Previoustime = object.getString("previoustime");
                    Aftertime = object.getString("aftertime");
                    savedate = object.getString("savedate");

                    myItems.add(new MyItem(_ID, ID, Title, Contents, Previoustime, Aftertime, savedate));
                    calendar.set(Integer.parseInt(savedate.substring(0, 4)), Integer.parseInt(savedate.substring(4, 6)) - 1, Integer.parseInt(savedate.substring(6)));
                    CalendarDay day = CalendarDay.from(calendar);
                    days.add(day);
                    count++;
                }
                materialCalendarView.addDecorator(new EventDecorator(Color.RED, days));
                materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) { // 날자번경시 리스트보이게
                        myItemList.clear();
                        myItemManager.clear();
                        int year = date.getYear();
                        int month = date.getMonth();
                        int day = date.getDay();

                        String dates = null;
                        if (month < 10) {
                            dates = String.valueOf(year) + "0" + String.valueOf(month + 1) + String.valueOf(day);
                        } else {
                            dates = String.valueOf(year) + String.valueOf(month + 1) + String.valueOf(day);
                        }

                        for(int i=0; i<myItems.size(); i++){
                            if(dates.equals(myItems.get(i).getSavedate())){
                                myItemManager.addmember(myItems.get(i).getUserid(), myItems.get(i).getPrevioustime(),myItems.get(i).getAftertime());
                            }
                        }

                        int index = 0;
                        for(int i = 0; i< 24; i++){
                            if(myItemManager.getMembercount(i)>0){
                                myItemList.add(new MyItem(myItems.get(index).getIndex(), myItemManager.getMember(i),myItems.get(index).getTitle(),myItems.get(index).getContents(),myItemManager.getStartTime(i),myItemManager.getEndTime(i),myItems.get(index).getSavedate()));
                                index++;
                            }
                        }

                        AlertDialog.Builder ad = new AlertDialog.Builder(Group.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.dialoglist, null);
                        ad.setView(view).setCancelable(false);

                        final ListView listView = view.findViewById(R.id.dialog_list);
                        DialogAdapter dialogAdapter = new DialogAdapter(myItemList);
                        listView.setAdapter(dialogAdapter);

                        ad.setTitle(dates + " "+ "공유일정");
                        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        ad.show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void CheckDuplicate(){
        List<String> time;
    }
}

