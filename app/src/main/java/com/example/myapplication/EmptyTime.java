package com.example.myapplication;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmptyTime extends AppCompatActivity implements View.OnClickListener{

    private Button button;

    private String Userid;
    private String Tablename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_time);

        Intent intent = getIntent();
        Userid = intent.getStringExtra("id");
        Tablename = intent.getStringExtra("GroupName");

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
}