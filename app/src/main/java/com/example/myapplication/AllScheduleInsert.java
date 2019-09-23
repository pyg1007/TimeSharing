package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

public class AllScheduleInsert extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<String> members;
    private String MenuName, TableName, Userid, dates, Time, setting_Start, setting_End;
    private int Start, End;
    private int init_Start, init_End;


    private Button Cancel;
    private Button Make;

    private Spinner Startspinner;
    private Spinner Endspinner;

    private EditText TitleEdit;
    private EditText ContentsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_schedule_insert);


        //넘어온 데이터 받은 것.
        Intent intent = getIntent();

        members = intent.getStringArrayListExtra("memberlist");

        MenuName = intent.getStringExtra("Menu");
        TableName = intent.getStringExtra("GroupName");
        Userid = intent.getStringExtra("id");
        dates = intent.getStringExtra("Selectdates");
        Time = intent.getStringExtra("Time");

        init_Start = intent.getIntExtra("Starttime",0);
        init_End = intent.getIntExtra("Endtime",0);
        //

        Cancel = findViewById(R.id.memo_cancel_button);
        Make = findViewById(R.id.memo_insert_button);

        Cancel.setOnClickListener(this);
        Make.setOnClickListener(this);

        Startspinner = findViewById(R.id.Clack_Spinner_1);
        Endspinner = findViewById(R.id.Clack_Spinner_2);

        String[] Splittime = Time.split(" ");
        Start = Integer.parseInt(Splittime[0]);
        End = Integer.parseInt(Splittime[2]);

        Startspinner.setSelection(Start);
        Startspinner.setGravity(Gravity.CENTER);
        Startspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setting_Start = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Endspinner.setSelection(End);
        Endspinner.setGravity(Gravity.CENTER);
        Endspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setting_End = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        TitleEdit = findViewById(R.id.memo_title);
        ContentsEdit = findViewById(R.id.memo_contents);

        TitleEdit.setText(MenuName);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.memo_cancel_button:
                Intent intent = new Intent(AllScheduleInsert.this, EmptyTimeDate.class);
                intent.putExtra("id", Userid);
                intent.putExtra("GroupName", TableName);
                intent.putStringArrayListExtra("memberlist", members);
                intent.putExtra("Selectdates", dates);
                intent.putExtra("Starttime", init_Start);
                intent.putExtra("Endtime", init_End);
                startActivity(intent);
                finish();
                break;
            case R.id.memo_insert_button:
                String[] setStart = setting_Start.split(" ");
                String[] setEnd = setting_End.split(" ");
                int set_Start = Integer.parseInt(setStart[0]);
                int set_End = Integer.parseInt(setEnd[0]);
                if (TitleEdit.getText().toString().equals("")){
                    Toast.makeText(this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if (ContentsEdit.getText().toString().equals("")){
                    Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if (set_Start>set_End){
                    Toast.makeText(this, "시작시간이 마지막시간보다 더 큽니다.", Toast.LENGTH_SHORT).show();
                }else {
                    for (int i = 0; i < members.size(); i++) {
                        new AllmemberScheduleInsert().execute(members.get(i), TitleEdit.getText().toString(), ContentsEdit.getText().toString());
                    }
                    sendPostFCM();
                    Intent group = new Intent(AllScheduleInsert.this, Group.class);
                    group.putStringArrayListExtra("memberid", members);
                    group.putExtra("id", Userid);
                    group.putExtra("GroupName", TableName);
                    startActivity(group);
                    finish();
                }
                break;
        }
    }

    public class AllmemberScheduleInsert extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {

                String link = "http://pyg941007.dothome.co.kr/Insert_schedule.php";
                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8");
                data += "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(strings[1], "UTF-8");
                data += "&" + URLEncoder.encode("contents", "UTF-8") + "=" + URLEncoder.encode(strings[2], "UTF-8");
                data += "&" + URLEncoder.encode("previoustime", "UTF-8") + "=" + URLEncoder.encode(setting_Start, "UTF-8");
                data += "&" + URLEncoder.encode("aftertime", "UTF-8") + "=" + URLEncoder.encode(setting_End, "UTF-8");
                data += "&" + URLEncoder.encode("savedate", "UTF-8") + "=" + URLEncoder.encode(dates, "UTF-8");

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
                info.put("title" , TableName + "에서 온 메세지");
                info.put("body", ""+Userid + "님이"+setting_Start + " ~ " + setting_End + "시간에" + MenuName + "으로 등록하셨습니다.");

                JSONObject json = new JSONObject();
                json.put("to", "/topics/" + TableName);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllScheduleInsert.this, EmptyTimeDate.class);
        intent.putExtra("id", Userid);
        intent.putExtra("GroupName", TableName);
        intent.putStringArrayListExtra("memberlist", members);
        intent.putExtra("Selectdates", dates);
        intent.putExtra("Starttime", init_Start);
        intent.putExtra("Endtime", init_End);
        startActivity(intent);
        super.onBackPressed();
    }
}
