package com.example.myapplication;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class EmptyTime extends AppCompatActivity implements View.OnClickListener{

    private Button button;

    private String Userid;
    private String Tablename;

    private ArrayList<String> members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_time);

        Intent intent = getIntent();
        Userid = intent.getStringExtra("id");
        Tablename = intent.getStringExtra("GroupName");
        members = intent.getStringArrayListExtra("memberlist");

        for(int i =0;  i<members.size(); i++){
            Log.e("Memberid :", members.get(i));
        }

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
                info.put("body", "TEST입니다.");

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