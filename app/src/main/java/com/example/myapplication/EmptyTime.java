package com.example.myapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EmptyTime extends AppCompatActivity implements View.OnClickListener{

    private Button button;

    private String Userid;
    private String Tablename;

    private ArrayList<String> members;
    private ArrayList<String> Time;
    private List<MyItem> myItems;

    private ShareItem shareItem;
    private String dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_time);
        Time = new ArrayList<>();

        Intent intent = getIntent();
        Userid = intent.getStringExtra("id");
        Tablename = intent.getStringExtra("GroupName");
        members = intent.getStringArrayListExtra("memberlist");
        dates = intent.getStringExtra("Selectdates");

        new JoinSchedule().execute();
//
//        List<ShareData> temp = new ArrayList<>();
//        ShareItem shareItem = new ShareItem();
//        temp = shareItem.get_List();
//
//        Log.e("time : ", temp.get(0).getStartTime());

        button = findViewById(R.id.send);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send:
                sendPostFCM();
                break;
        }
    }

    public void sendPostFCM(){
        new Send().execute();
    }

    public class Send extends AsyncTask<String, Void, String>{

        private String ServerKey = "AAAADqP_LKw:APA91bHYjTJOSQkbX9mz1Zgcfwu00meC8O-p-MwXyptZ57Xtwv1rIqWL-DBjmlYrwpxIjJOQp9WKq6NtF49OlOX4At4dRiyIEgrigmlgvL_BeC43BG91sYDd37Rwyh0oK41NR9s7S1e7";
        private String ServerHttps = "https://fcm.googleapis.com/fcm/send";

        @Override
        protected String doInBackground(String... strings) {
            try{
                URL url = new URL(ServerHttps);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "key=" + ServerKey);

                JSONObject info = new JSONObject();
                info.put("title" , Userid + "님이 보낸 메세지");
                info.put("body", Tablename + "에서 온 메세지 입니다.");

                JSONObject json = new JSONObject();
                json.put("to", "/topics/" + Tablename);
                json.put("data", info);

                OutputStream os = urlConnection.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = urlConnection.getResponseCode();
                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + json.toString().getBytes("UTF-8"));
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            }catch (Exception e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public class AllmemberScheduleInsert extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {

                String link = "http://pyg941007.dothome.co.kr/load_member.php";
                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8");
                data += "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(Userid, "UTF-8");
                data += "&" + URLEncoder.encode("contents", "UTF-8") + "=" + URLEncoder.encode(Userid, "UTF-8");
                data += "&" + URLEncoder.encode("previoustime", "UTF-8") + "=" + URLEncoder.encode(Userid, "UTF-8");
                data += "&" + URLEncoder.encode("aftertime", "UTF-8") + "=" + URLEncoder.encode(Userid, "UTF-8");
                data += "&" + URLEncoder.encode("savedate", "UTF-8") + "=" + URLEncoder.encode(Userid, "UTF-8");

                URL url = new URL(link);

                URLConnection urlConnection = url.openConnection();

                urlConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());

                wr.write(data);
                wr.flush();

                InputStream inputStream = urlConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;

                StringBuilder stringBuilder = new StringBuilder();

                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }

                wr.close();

                bufferedReader.close();
                inputStream.close();
                return  stringBuilder.toString().trim();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public class JoinSchedule extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/join.php";

                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(Tablename, "UTF-8");
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
                myItems = new ArrayList<>();
                shareItem = new ShareItem();
                String ID,Title,Contents,Previoustime,Aftertime,savedate;
                int _ID;

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
                shareItem.clear();
                for (int i = 0; i < myItems.size(); i++) {
                    if (dates.equals(myItems.get(i).getSavedate())) {
                        shareItem.addmember(myItems.get(i));
                    }
                }
                Log.e("sum :", shareItem.EmptyTimesum());

                AlertDialog.Builder ad = new AlertDialog.Builder(EmptyTime.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialoglist, null);
                ad.setView(view).setCancelable(false);

                final ListView listView = view.findViewById(R.id.dialog_list);
                DialogAdapter dialogAdapter = new DialogAdapter(shareItem.get_List());
                listView.setAdapter(dialogAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EmptyTime.this, Group.class);
        intent.putExtra("id", Userid);
        intent.putExtra("GroupName", Tablename);
        intent.putStringArrayListExtra("memberid", members);
        startActivity(intent);
        super.onBackPressed();
    }
}