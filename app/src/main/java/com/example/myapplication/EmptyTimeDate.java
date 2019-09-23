package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class EmptyTimeDate extends AppCompatActivity {

    private String Userid, Tablename, MenuName, Dates;
    private ArrayList<String> members;
    private int Starttime, Endtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_time_date);

        TextView textView = findViewById(R.id.Title);

        //넘어온 데이터 받은 것.
        Intent intent = getIntent();

        members = intent.getStringArrayListExtra("memberlist");

        MenuName = intent.getStringExtra("Menu");
        Tablename = intent.getStringExtra("GroupName");
        Userid = intent.getStringExtra("id");
        Dates = intent.getStringExtra("Selectdates");

        Starttime = intent.getIntExtra("Starttime", 0);
        Endtime = intent.getIntExtra("Endtime", 0);
        //

        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(20);
        textView.setText(""+Dates + "일의 빈 시간");

        new LoadEmptySchedule().execute();
    }

    public class LoadEmptySchedule extends AsyncTask<Void, Void, String> {

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
            List<MyItem> myItems;
            ShareItem shareItem;
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;
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
                    count++;
                }
                shareItem.clear();
                for (int i = 0; i < myItems.size(); i++) {
                    if (Dates.equals(myItems.get(i).getSavedate())) {
                        shareItem.addmember(myItems.get(i));
                    }
                }

                final String[] SplitTime = shareItem.EmptyTimesum(Starttime,Endtime).split(",");
                final ListView listView = findViewById(R.id.Emptylist);
                ArrayAdapter arrayAdapter = new ArrayAdapter(EmptyTimeDate.this, android.R.layout.simple_list_item_1, SplitTime);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(EmptyTimeDate.this, AllScheduleInsert.class);
                        intent.putExtra("Time", SplitTime[i]);
                        intent.putExtra("id",Userid);
                        intent.putStringArrayListExtra("memberlist",members);
                        intent.putExtra("GroupName",Tablename);
                        intent.putExtra("Selectdates", Dates);
                        intent.putExtra("Menu",MenuName);
                        intent.putExtra("Starttime", Starttime);
                        intent.putExtra("Endtime", Endtime);
                        startActivity(intent);
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EmptyTimeDate.this, EmptyTime.class);
        intent.putExtra("id",Userid);
        intent.putStringArrayListExtra("memberlist",members);
        intent.putExtra("GroupName",Tablename);
        intent.putExtra("Selectdates", Dates);
        startActivity(intent);
        super.onBackPressed();
    }
}
