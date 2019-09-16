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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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

public class Schedule extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnMonthChangedListener{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView mListView;
    private EventDecorator eventDecorator;
    private MaterialCalendarView materialCalendarView;
    private FloatingActionButton fab;
    private TextView DateTextView;
    private MyAdapter myAdapter;
    private List<MyItem> myItems;
    private List<MyItem> showitem;


    private final long FINISH_TIMER = 2000;
    private long backpresstimer = 0;



    private String userid, userpassword;

    ////// 스케쥴
    private String Title, ID, Contents, Previoustime, Aftertime, savedate;
    int _ID;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = findViewById(R.id.schedule_list);
        myItems = new ArrayList<MyItem>();
        showitem = new ArrayList<MyItem>();
        myAdapter = new MyAdapter(showitem);
        mListView.setAdapter(myAdapter);

        //동그란 버튼 +
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        userid = intent.getStringExtra("id");
        View header = navigationView.getHeaderView(0);
        TextView id = header.findViewById(R.id.textView);
        id.setTextSize(25);
        id.setText(userid + "님 환영합니다.");

        Calendar_init();
        scheduleTask schedule_Task = new scheduleTask();
        schedule_Task.execute();
    }

    public void Calendar_init(){
        //달력관련
        materialCalendarView = findViewById(R.id.calendarView);

        materialCalendarView.setOnMonthChangedListener(this);
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
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fab:
                CalendarDay date = materialCalendarView.getSelectedDate();
                Intent memo = new Intent(getApplicationContext(), Memo.class);

                int year = date.getYear();
                int month =  date.getMonth()+1;
                int day = date.getDay();

                memo.putExtra("year",year);
                memo.putExtra("month",month);
                memo.putExtra("day",day);
                memo.putExtra("id",userid);
                startActivity(memo);
                finish();
        }
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backpresstimer;


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (0 <= intervalTime && FINISH_TIMER >= intervalTime) {
                super.onBackPressed();
            }
            else {
                backpresstimer = tempTime;
                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누르면 꺼집니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        drawerToggle.syncState();
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
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if(id == R.id.account){ //계정버튼눌렀을때 이동할 곳
            final EditText et = new EditText(Schedule.this);
            AlertDialog.Builder ad = new AlertDialog.Builder(Schedule.this);
            ad.setTitle("본인 확인").setMessage("비밀번호를 입력해 주세요.").setView(et).setCancelable(false);
            ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userpassword = et.getText().toString().trim();
                    Confirm confirm = new Confirm();
                    confirm.execute();
                    dialog.dismiss();
                }
            });
            ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(Schedule.this, "취소하셨습니다.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            ad.show();
        }else if(id == R.id.people){
            Intent intent = new Intent(Schedule.this, Memberlist.class);
            intent.putExtra("id",userid);
            startActivity(intent);
            finish();
        }else if(id == R.id.checkGroup){
            Intent intent = new Intent(Schedule.this, Grouproom.class);
            intent.putExtra("id",userid);
            startActivity(intent);
            finish();
        }else if(id == R.id.logout){ //로그아웃 버튼 눌렀을 때
            Intent logout_intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(logout_intent);
            finish();
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        DateTextView = findViewById(R.id.calendarDate);
        DateTextView.setGravity(Gravity.CENTER);
        DateTextView.setTextSize(20);
        DateTextView.setText(date.getYear() + "년" + (date.getMonth()+1) + "월");
    }

    /// 일정
    public void schedule_list(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), updatememo.class);
                intent.putExtra("index", showitem.get(i).getIndex());
                intent.putExtra("title",showitem.get(i).getTitle());
                intent.putExtra("id", userid);
                intent.putExtra("content", showitem.get(i).getContents());
                intent.putExtra("previoustime", showitem.get(i).getPrevioustime());
                intent.putExtra("aftertime", showitem.get(i).getAftertime());
                intent.putExtra("savedate", showitem.get(i).getSavedate());
                myAdapter.notifyDataSetChanged();
                startActivity(intent);
                finish();
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view, Gravity.RIGHT);
                getMenuInflater().inflate(R.menu.list_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.list_menu1: // 삭제
                                int select = 0,j;
                                for(j =0; j<myItems.size(); j++){
                                    if(showitem.get(i).getTitle().equals(myItems.get(j).getTitle()) && showitem.get(i).getSavedate().equals(myItems.get(j).getSavedate()) && showitem.get(i).getIndex() == myItems.get(j).getIndex()){
                                        select = j;
                                    }
                                }
                                new DeleteSchedule().execute(showitem.get(i).getTitle(), showitem.get(i).getSavedate(), String.valueOf(showitem.get(i).getIndex()));
                                showitem.remove(i);
                                myItems.remove(select);
                                if (showitem.size() ==0){
                                    materialCalendarView.removeDecorators();
                                    Calendar_init();
                                    new scheduleTask().execute();
                                }
                                myAdapter.notifyDataSetChanged();
                                break;
                            case R.id.list_menu2:// 편집
                                Intent intent = new Intent(getApplicationContext(), updatememo.class);
                                intent.putExtra("index", showitem.get(i).getIndex());
                                intent.putExtra("title",showitem.get(i).getTitle());
                                intent.putExtra("id", userid);
                                intent.putExtra("content", showitem.get(i).getContents());
                                intent.putExtra("previoustime", showitem.get(i).getPrevioustime());
                                intent.putExtra("aftertime", showitem.get(i).getAftertime());
                                intent.putExtra("savedate", showitem.get(i).getSavedate());
                                myAdapter.notifyDataSetChanged();
                                startActivity(intent);
                                finish();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }

    public class scheduleTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/load_schedule.php";

                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
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
                days.clear();
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
                    calendar.set(Integer.parseInt(savedate.substring(0,4)), Integer.parseInt(savedate.substring(4,6))-1, Integer.parseInt(savedate.substring(6)));
                    CalendarDay day = CalendarDay.from(calendar);
                    days.add(day);
                    count++;
                }
                eventDecorator = new EventDecorator(Color.RED, days);
                materialCalendarView.addDecorator(eventDecorator);
                materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) { // 날자번경시 리스트보이게
                        showitem.clear();
                        int year = date.getYear();
                        int month = date.getMonth();
                        int day = date.getDay();
                        int i = 0;

                        String dates = null;
                        if(month < 10){
                            dates = String.valueOf(year)+ "0" + String.valueOf(month+1) + String.valueOf(day);
                        }else{
                            dates = String.valueOf(year)+ String.valueOf(month+1) + String.valueOf(day);
                        }
                        for(i=0; i<myItems.size(); i++){
                            if(dates.equals(myItems.get(i).getSavedate())){
                                showitem.add(new MyItem(myItems.get(i).getIndex(), myItems.get(i).getUserid(), myItems.get(i).getTitle(), myItems.get(i).getContents(), myItems.get(i).getPrevioustime(), myItems.get(i).getAftertime(), myItems.get(i).getSavedate()));
                            }
                        }
                        myAdapter.notifyDataSetChanged();
                        schedule_list();
                    }
                });
                //최초실행시 리스트 보이게
                CalendarDay date = materialCalendarView.getSelectedDate();
                int year = date.getYear();
                int month = date.getMonth();
                int day = date.getDay();
                int i = 0;

                String dates = null;
                if(month < 10){
                    dates = String.valueOf(year)+ "0" + String.valueOf(month+1) + String.valueOf(day);
                }else{
                    dates = String.valueOf(year)+ String.valueOf(month+1) + String.valueOf(day);
                }
                for(i=0; i<myItems.size(); i++){
                    if(dates.equals(myItems.get(i).getSavedate())){
                        showitem.add(new MyItem(myItems.get(i).getIndex(), myItems.get(i).getUserid(), myItems.get(i).getTitle(), myItems.get(i).getContents(), myItems.get(i).getPrevioustime(), myItems.get(i).getAftertime(), myItems.get(i).getSavedate()));
                    }
                }
                myAdapter.notifyDataSetChanged();
                schedule_list();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class DeleteSchedule extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try{
                String link = "http://pyg941007.dothome.co.kr/deleteschedule.php";

                String data = URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8");
                data += "&" + URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
                data += "&" + URLEncoder.encode("savedate", "UTF-8") + "=" + URLEncoder.encode(strings[1], "UTF-8");
                data += "&" + URLEncoder.encode("index", "UTF-8") + "=" + URLEncoder.encode(strings[2], "UTF-8");
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
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.e("Delete : ", result);
        }
    }

    public class Confirm extends AsyncTask<Void, Integer, Void>{
        String server_data;
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (server_data.equals("1")) {
                    Intent intent = new Intent(getApplicationContext(), Account.class);
                    intent.putExtra("id", userid);
                    startActivity(intent);
                    finish();
                } else if (server_data.equals("0")) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
            String link = "http://pyg941007.dothome.co.kr/Login.php";
            String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
            data += "&" + URLEncoder.encode("userpassword", "UTF-8") + "=" + URLEncoder.encode(userpassword, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                break;
            }
            server_data = sb.toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
        }
            return null;
        }
    }
}