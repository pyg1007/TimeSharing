package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class Grouproom extends AppCompatActivity {

    List<String> tablelist;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouproom);

        Intent intent= getIntent();
        userid = intent.getStringExtra("id");

        tablelist = new ArrayList<>();


        Searchroom searchroom = new Searchroom();
        searchroom.execute();

    }

    public class Searchroom extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/alltableserach.php";
                URL url = new URL(link);

                URLConnection urlConnection = url.openConnection();

                urlConnection.setDoOutput(true);

                InputStream inputStream = urlConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;

                StringBuilder stringBuilder = new StringBuilder();

                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;

                String Tablename;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    Tablename = object.getString("tablename");

                    tablelist.add(Tablename);
                    count++;
                }

                ListView listView = findViewById(R.id.listView);
                ArrayAdapter arrayAdapter = new ArrayAdapter(Grouproom.this, android.R.layout.simple_list_item_1, tablelist);
                listView.setAdapter(arrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Grouproom.this, Group.class);
                        intent.putExtra("tablename", tablelist.get(position));
                        intent.putExtra("userid", userid);
                        intent.putExtra("memberid",userid);
                        startActivity(intent);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Schedule.class);
        intent.putExtra("id",userid);
        startActivity(intent);
        super.onBackPressed();
    }
}
