package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.CalendarDecorator.EventDecorator;
import com.example.myapplication.CalendarDecorator.OneDayDecorator;
import com.example.myapplication.CalendarDecorator.SaturdayDecorator;
import com.example.myapplication.CalendarDecorator.SundayDecorator;
import com.example.myapplication.CustomAdapter.DialogAdapter;
import com.example.myapplication.Loading.LodingProgress;
import com.example.myapplication.CustomAdapterItem.MyItem;
import com.example.myapplication.CustomAdapterItem.ShareData;
import com.example.myapplication.CustomAdapterItem.ShareItem;
import com.google.firebase.messaging.FirebaseMessaging;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Group extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMonthChangedListener, View.OnClickListener{

    private String tablename;
    private String userid;
    private TextView DateTextView;
    private TextView Group_Explanation;
    private ActionBarDrawerToggle drawerToggle;
    private View view;

    private ShareItem shareItem;

    private int _ID;
    private String Title, ID, Contents, Previoustime, Aftertime, savedate;

    private List<MyItem> myItems; // 전체 스케쥴 리스트
    private List<MyItem> myItemList; // 동일날짜 보여주는 리스트
    private ArrayList<String> memberlist;

    private MaterialCalendarView materialCalendarView;

    private FloatingActionButton fab;
    private Button Exit;
    private Button Invite;

    private boolean Invite_check = false; // 초대버튼으로 왔는지 아닌지 체크용


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        GetData();
        Calandar_init();
        UI();
    }

    public void GetData(){
        shareItem = new ShareItem();

        myItems = new ArrayList<>();
        myItemList = new ArrayList<>();

        Intent intent = getIntent();
        tablename = intent.getStringExtra("GroupName");
        userid = intent.getStringExtra("id");

        new LookupMemeber().execute();
        new LookupRoomComment().execute();
        new JoinSchedule().execute();
    }

    public void UI(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        view = navigationView.getHeaderView(0);

        Exit = findViewById(R.id.exit);
        Invite = findViewById(R.id.Invite_list);
        Exit.setOnClickListener(this);
        Invite.setOnClickListener(this);
        Group_Explanation = view.findViewById(R.id.group_explanation);
        Group_Explanation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Group.this);
                final EditText editText = new EditText(Group.this);
                ad.setView(editText).setCancelable(false).setTitle("그룹 소개").setMessage("그룹 소개글을 작성해주세요.");
                ad.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new UpdateRoomComment().execute(editText.getText().toString().trim());
                        Group_Explanation.setText(editText.getText().toString().trim());
                    }
                });
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Group.this, "취소하셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                ad.show();
            }
        });
        fab = findViewById(R.id.fab);
    }

    Comparator<String> comAsc = new Comparator<String>() {
        @Override
        public int compare(String s, String t1) {
            return s.compareTo(t1);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Invite_list:
                Intent intent = new Intent(Group.this, CheckboxList.class);
                Invite_check = true;
                intent.putExtra("GroupName", tablename);
                intent.putExtra("id", userid);
                intent.putExtra("Invate_check", Invite_check);
                startActivity(intent);
                finish();
                break;
            case R.id.exit:
                new ExitRoom().execute();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_deco, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
//        if (drawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
            switch (item.getItemId()) {
                case R.id.back_icon:
                    Intent intent = new Intent(Group.this, GroupList.class);
                    intent.putExtra("id", userid);
                    startActivity(intent);
                    finish();
                    break;
            }
            // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
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

    public class JoinSchedule extends AsyncTask<Void, Void, String>{

        LodingProgress lodingProgress = new LodingProgress(Group.this);
        @Override
        protected void onPreExecute() {
            lodingProgress.loading();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/join.php";

                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(tablename, "UTF-8");
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
                final ArrayList<CalendarDay> days = new ArrayList<>();
                final Calendar calendar = Calendar.getInstance();

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
                        shareItem.clear();
                        myItemList.clear();
                        boolean checkingday = false;
                        int year = date.getYear();
                        int month = date.getMonth();
                        int day = date.getDay();

                        String dates = null;
                        if (month < 9) {
                            dates = String.valueOf(year) + "0" + String.valueOf(month + 1) + String.valueOf(day);
                        } else {
                            dates = String.valueOf(year) + String.valueOf(month + 1) + String.valueOf(day);
                        }

                        calendar.set(year,month,day);
                        CalendarDay calendarDay = CalendarDay.from(calendar);
                        for(int i=0; i<days.size(); i++){
                            if(calendarDay.equals(days.get(i))){
                                checkingday = true;
                            }
                        }

                        if(checkingday) {
                            for (int i = 0; i < myItems.size(); i++) {
                                if (dates.equals(myItems.get(i).getSavedate())) {
                                    shareItem.addmember(myItems.get(i));
                                }
                            }
                            AlertDialog.Builder ad = new AlertDialog.Builder(Group.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.dialoglist, null);
                            ad.setView(view).setCancelable(false);



                            final ListView listView = view.findViewById(R.id.dialog_list);
                            Collections.sort(shareItem.get_List(),shareDataComparator);
                            DialogAdapter dialogAdapter = new DialogAdapter(shareItem.get_List());
                            listView.setAdapter(dialogAdapter);

                            ad.setTitle(dates + " " + "공유일정");
                            ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            ad.show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            lodingProgress.loadingEnd();
        }
    }

    /*
    리스트 정렬
     */
    Comparator<ShareData> shareDataComparator = new Comparator<ShareData>() {
        @Override
        public int compare(ShareData shareData, ShareData t1) {
            return shareData.getStartTime().compareTo(t1.getStartTime());
        }
    };

    public class LookupMemeber extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/Member_search.php";

                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(tablename, "UTF-8");
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
                memberlist = new ArrayList<>();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;
                String Memberid;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    Memberid = object.getString("userid");

                    memberlist.add(Memberid);
                    count++;
                }
                if(memberlist.size()>0) {
                    ListView listView = findViewById(R.id.list);
                    Collections.sort(memberlist, comAsc);
                    ArrayAdapter arrayAdapter = new ArrayAdapter(Group.this, android.R.layout.simple_list_item_1, memberlist);
                    listView.setAdapter(arrayAdapter);
                }else{
                    new DropRoom().execute();
                }
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Group.this, EmptyTime.class);
                        CalendarDay c = materialCalendarView.getSelectedDate();
                        int month = c.getMonth()+1;
                        String zeromonth = null;
                        if(month<10){
                            zeromonth = "0"+month;
                        }else{
                            zeromonth = String.valueOf(month);
                        }
                        String dates = String.valueOf(c.getYear()) + zeromonth + c.getDay();
                        Log.e("dates :", dates);
                        intent.putExtra("id", userid);
                        intent.putExtra("GroupName", tablename);
                        intent.putExtra("Selectdates",dates);
                        startActivity(intent);
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class UpdateRoomComment extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {

                String link = "http://pyg941007.dothome.co.kr/UpdateRoomComment.php";

                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(tablename, "UTF-8");
                data += "&" + URLEncoder.encode("tableexplanation", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8");
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

    public class LookupRoomComment extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {

                String link = "http://pyg941007.dothome.co.kr/LookRoomComment.php";

                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(tablename, "UTF-8");
                data += "&" + URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
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

                String tableexplanation = "";
                while(count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    tableexplanation = object.getString("tableexplanation");
                    count++;
                }

                Group_Explanation = view.findViewById(R.id.group_explanation);
                if(tableexplanation.equals("null")){
                    Group_Explanation.setText(tablename + "방 입니다.");
                }else{
                    Group_Explanation.setText(tableexplanation);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ExitRoom extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try{
                String link = "http://pyg941007.dothome.co.kr/deleteRoom.php";

                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(tablename, "UTF-8");
                data += "&" + URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
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
            new LookupMemeber().execute();
            Intent intent = new Intent(Group.this, GroupList.class);
            intent.putExtra("id", userid);
            startActivity(intent);
            finish();

            FirebaseMessaging.getInstance().unsubscribeFromTopic(tablename);
        }
    }

    public class DropRoom extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                String link = "http://pyg941007.dothome.co.kr/droproom.php";

                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(tablename, "UTF-8");
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            Intent intent = new Intent(getApplicationContext(), GroupList.class);
            intent.putExtra("id", userid);
            startActivity(intent);
            super.onBackPressed();
        }
    }
}

