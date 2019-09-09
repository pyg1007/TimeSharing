package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Account extends AppCompatActivity implements View.OnClickListener{

    private String userid;
    private String userName;

    private TextView ID;
    private TextView Name;
    private EditText Pw;
    private EditText Pw_chk;
    private Button confirm;
    private Button cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Intent intent = getIntent();
        userid = intent.getStringExtra("id");

        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute();

        ID = findViewById(R.id.Id);
        ID.setText("아이디 : " + userid);
        ID.setTextSize(16);

        Name = findViewById(R.id.name);
        Name.setText(userName);
        Name.setTextSize(16);

        Pw = findViewById(R.id.pw);
        Pw_chk = findViewById(R.id.pw_chk);

        confirm = findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.confirm:
                if(Pw.getText().toString().trim().equals("")){
                    Toast.makeText(this, "바꾸실 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(!Pw.getText().toString().trim().equals(Pw_chk.getText().toString().trim())){
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    updateuser _update = new updateuser();
                    _update.execute();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.cancel:
                Intent intent = new Intent(getApplicationContext(), Schedule.class);
                intent.putExtra("id", userid);
                startActivity(intent);
                finish();
                break;
        }
    }

    public class BackgroundTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/user_info.php";
                String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
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

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);


                    userName = object.getString("name");

                    count++;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class updateuser extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/updateuser.php";

                String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(userid), "UTF-8");
                data += "&" + URLEncoder.encode("Pw","UTF-8") + "=" + URLEncoder.encode(Pw.getText().toString().trim(),"UTF-8");
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Schedule.class);
        intent.putExtra("id", userid);
        startActivity(intent);
        super.onBackPressed();
    }
}
