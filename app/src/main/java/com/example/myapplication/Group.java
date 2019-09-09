package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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

public class Group extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMonthChangedListener, View.OnClickListener{

    private String tablename;
    private String userid;
    private TextView DateTextView;
    private ActionBarDrawerToggle drawerToggle;
    private View view;

    private ShareItem shareItem;

    private int _ID;
    private String Title, ID, Contents, Previoustime, Aftertime, savedate;

    private List<String> members; // 넘어온 아이디 명단
    private List<MyItem> myItems; // 전체 스케쥴 리스트
    private List<MyItem> myItemList; // 동일날짜 보여주는 리스트

    private MaterialCalendarView materialCalendarView;

    private Button Exit;
    private Button Invite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        shareItem = new ShareItem();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar();

        myItems = new ArrayList<>();
        myItemList = new ArrayList<>();
        members = new ArrayList<>();

        Intent intent = getIntent();
        tablename = intent.getStringExtra("GroupName");
        userid = intent.getStringExtra("userid");
        members = intent.getStringArrayListExtra("memberid");

        if(members == null){
            new LookupMemeber().execute();
        }else{
            addmember();
        }

        Calandar_init();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        view = navigationView.getHeaderView(0);
        new LookupRoomComment().execute();

        Exit = view.findViewById(R.id.exit);
        Invite = view.findViewById(R.id.Invite_list);

        final TextView Group_Explanation = view.findViewById(R.id.group_explanation);
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
                ad.show();
            }
        });

        JoinSchedule joinSchedule = new JoinSchedule();
        joinSchedule.execute();

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
                    Intent intent = new Intent(Group.this, Grouproom.class);
                    intent.putExtra("id", userid);
                    startActivity(intent);
                    finish();
                    break;
            }
            // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    public void addmember(){
        try {
            for (int i = 0; i < members.size(); i++) {
                new Groupadd().execute(members.get(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Invite_list:
                new InviteRoom().execute();
                break;
            case R.id.exit:
                new ExitRoom().execute();
                break;
        }
    }


    public class Groupadd extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... Strings) {
            try {

                String link = "http://pyg941007.dothome.co.kr/groupadd.php";
                String data = URLEncoder.encode("Tablename", "UTF-8") + "=" + URLEncoder.encode(tablename, "UTF-8");
                data += "&" + URLEncoder.encode("Memberid", "UTF-8") + "=" + URLEncoder.encode(Strings[0], "UTF-8");

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
            ListView listView = findViewById(R.id.list);
            ArrayAdapter arrayAdapter = new ArrayAdapter(Group.this, android.R.layout.simple_list_item_1, members);
            listView.setAdapter(arrayAdapter);
        }
    }

    public class JoinSchedule extends AsyncTask<Void, Void, String>{

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
                        myItemList.clear();
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
                                shareItem.addmember(myItems.get(i).getUserid(), myItems.get(i).getPrevioustime(),myItems.get(i).getAftertime());
                            }
                        }
//
                        try {
                            int index = 0;
                            for (int i = 0; i < 24; i++) {
                                if (shareItem.getMembercount(i) > 0) {
                                    Log.e("index : " , String.valueOf(index));
                                    myItemList.add(new MyItem(myItems.get(index).getIndex(), shareItem.getMember(i), myItems.get(index).getTitle(), myItems.get(index).getContents(), shareItem.getStartTime(i), shareItem.getEndTime(i), myItems.get(index).getSavedate()));

                                    index++;
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
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

    public class LookupMemeber extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/Member_search.php";

                String data = URLEncoder.encode("Tn", "UTF-8") + "=" + URLEncoder.encode(tablename, "UTF-8");
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

                List<String> memberlist = new ArrayList<>();
                String Memberid;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    Memberid = object.getString("userid");

                    memberlist.add(Memberid);
                    count++;
                }
                ListView listView = findViewById(R.id.list);
                ArrayAdapter arrayAdapter = new ArrayAdapter(Group.this,android.R.layout.simple_list_item_1,memberlist);
                listView.setAdapter(arrayAdapter);
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

                String data = URLEncoder.encode("Tn", "UTF-8") + "=" + URLEncoder.encode(tablename, "UTF-8");
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
            try{
                Log.e("re : ", result);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class LookupRoomComment extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {

                String link = "http://pyg941007.dothome.co.kr/LookRoomComment.php";

                String data = URLEncoder.encode("Tn", "UTF-8") + "=" + URLEncoder.encode(tablename, "UTF-8");
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
                Log.e("Room :", result);
                JSONObject jsonObject = new JSONObject(result);

                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;

                String tableexplanation = "";
                while(count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    tableexplanation = object.getString("tableexplanation");
                    count++;
                }

                final TextView Group_Explanation = view.findViewById(R.id.group_explanation);
                if(tableexplanation == null){
                    Group_Explanation.setText(tablename + "방 입니다.");
                }else{
                    Group_Explanation.setText(tableexplanation);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class InviteRoom extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    public class ExitRoom extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            Intent intent = new Intent(getApplicationContext(), Grouproom.class);
            intent.putExtra("id", userid);
            startActivity(intent);
            super.onBackPressed();
        }
    }
}

