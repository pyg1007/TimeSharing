package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.List;

public class GroupShcedule extends AppCompatActivity implements View.OnClickListener{

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

    private TextView Memodate;

    private List<String> getUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_shcedule);

        GetData();
        UI();

        new GroupGetMember().execute();
        new GetUUID().execute();
    }

    public void GetData(){
        //넘어온 데이터 받은 것.
        Intent intent = getIntent();

        MenuName = intent.getStringExtra("Menu");
        TableName = intent.getStringExtra("GroupName");
        Userid = intent.getStringExtra("id");
        dates = intent.getStringExtra("Selectdates");
        Time = intent.getStringExtra("Time");

        init_Start = intent.getIntExtra("Starttime",0);
        init_End = intent.getIntExtra("Endtime",0);
        //

        String[] Splittime = Time.split(" ");
        Start = Integer.parseInt(Splittime[0]);
        End = Integer.parseInt(Splittime[2]);
    }

    public void UI(){
        Cancel = findViewById(R.id.memo_cancel_button);
        Make = findViewById(R.id.memo_insert_button);

        Cancel.setOnClickListener(this);
        Make.setOnClickListener(this);

        Startspinner = findViewById(R.id.Clack_Spinner_1);
        Endspinner = findViewById(R.id.Clack_Spinner_2);

        TitleEdit = findViewById(R.id.memo_title);
        ContentsEdit = findViewById(R.id.memo_contents);
        TitleEdit.setText(MenuName);
        Memodate = findViewById(R.id.memo_date);
        Memodate.setText(dates.substring(0,4) + "년" + dates.substring(4,6) + "월" + dates.substring(6) + "일");
        Memodate.setGravity(Gravity.CENTER);

        Spinner();
        EnterKey();
    }

    public void Spinner(){
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
    }

    public void EnterKey(){
        ContentsEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        ContentsEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
                if (keyCode == EditorInfo.IME_ACTION_DONE){
                    Make.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.memo_cancel_button:
                Intent intent = new Intent(GroupShcedule.this, EmptyTimeDate.class);
                intent.putExtra("id", Userid);
                intent.putExtra("GroupName", TableName);
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
                else if (set_Start>=set_End){
                    Toast.makeText(this, "시간을 다시 설정해 주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    for (int i = 0; i < members.size(); i++) {
                        new GroupMemberScheduleIn().execute(members.get(i), TitleEdit.getText().toString(), ContentsEdit.getText().toString());
                    }
                    sendPostFCM();
                    Intent group = new Intent(GroupShcedule.this, Group.class);
                    group.putExtra("id", Userid);
                    group.putExtra("GroupName", TableName);
                    startActivity(group);
                    finish();
                }
                break;
        }
    }

    public class GroupMemberScheduleIn extends AsyncTask<String, Void, String> {

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

    public class GroupGetMember extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                String link = "http://pyg941007.dothome.co.kr/Group_LoadMember.php";
                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(TableName, "UTF-8");
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
            try {
                members = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;

                String userID;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    userID = object.getString("id");

                    members.add(userID);
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void sendPostFCM(){
        for (int i = 0; i<getUUID.size(); i++) {
            new Send().execute(getUUID.get(i));
        }
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
                info.put("body", ""+Userid + "님이" + setting_Start + " ~ " + setting_End + "시간에 " + TitleEdit.getText().toString() + "으로 등록하셨습니다.");

                JSONObject json = new JSONObject();
                json.put("to", strings[0]);
                json.put("data", info);

                OutputStream os = urlConnection.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = urlConnection.getResponseCode();
                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + json.toString().getBytes("UTF-8"));
                System.out.println("Response Code : " + responseCode);
                Log.e("JSON : ", String.valueOf(json));

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Log.e("response : ", s);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

//    public class Getnotification_key extends AsyncTask<String, Void, String>{
//
//        private String APIKey = "AAAADqP_LKw:APA91bHYjTJOSQkbX9mz1Zgcfwu00meC8O-p-MwXyptZ57Xtwv1rIqWL-DBjmlYrwpxIjJOQp9WKq6NtF49OlOX4At4dRiyIEgrigmlgvL_BeC43BG91sYDd37Rwyh0oK41NR9s7S1e7";
//        private String ServerHttps = "https://fcm.googleapis.com/fcm/notification";
//        private String SenderID = "62880951468";
//
//        @Override
//        protected String doInBackground(String... strings) {
//            try{
//                URL url = new URL(ServerHttps);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setDoInput(true);
//                urlConnection.setDoOutput(true);
//                urlConnection.setRequestMethod("POST");
//                urlConnection.setRequestProperty("Content-Type", "application/json");
//                urlConnection.setRequestProperty("Authorization", "key=" + APIKey);
//                urlConnection.setRequestProperty("project_id", SenderID);
//
//                JSONObject json = new JSONObject();
//                json.put("operation", "create");
//                json.put("notification_key_name",TableName);
//                json.put("registration_ids", new JSONArray(getUUID));
//
//                OutputStream os = urlConnection.getOutputStream();
//                os.write(json.toString().getBytes("UTF-8"));
//                os.flush();
//                os.close();
//
//                int responseCode = urlConnection.getResponseCode();
//                System.out.println("\nSending 'POST' request to URL : " + url);
//                System.out.println("Post parameters : " + json.toString().getBytes("UTF-8"));
//                System.out.println("Response Code : " + responseCode);
//                Log.e("JSON : ", String.valueOf(json.toString()));
//
//                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                String inputLine;
//                StringBuffer response = new StringBuffer();
//
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//                in.close();
//
//                return response.toString();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            try {
//                Log.e("response : ", s);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    } // 이건지금안씀 해볼려고하는중
//
//    public class AddNotification extends AsyncTask<String, Void, String>{
//        private String APIKey = "AAAADqP_LKw:APA91bHYjTJOSQkbX9mz1Zgcfwu00meC8O-p-MwXyptZ57Xtwv1rIqWL-DBjmlYrwpxIjJOQp9WKq6NtF49OlOX4At4dRiyIEgrigmlgvL_BeC43BG91sYDd37Rwyh0oK41NR9s7S1e7";
//        private String ServerHttps = "https://fcm.googleapis.com/fcm/notification";
//        private String SenderID = "62880951468";
//
//        @Override
//        protected String doInBackground(String... strings) {
//            try{
//                URL url = new URL(ServerHttps);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setDoInput(true);
//                urlConnection.setDoOutput(true);
//                urlConnection.setRequestMethod("POST");
//                urlConnection.setRequestProperty("Content-Type", "application/json");
//                urlConnection.setRequestProperty("Authorization", "key=" + APIKey);
//                urlConnection.setRequestProperty("project_id", SenderID);
//
//                JSONObject json = new JSONObject();
//                json.put("operation", "add");
//                json.put("notification_key_name",TableName);
//                json.put("notification_key", "APA91bFom5THRyRpsaRb-YU8okRWd5LjNvZ3YixO6YRPKSJyJB7OY8LM3DcqfuF9kxVCsQ30lPAwyZ4Df4GmjGn9XbsS9_VzcCTUqs4K1YH9tWnYWx8tqi0");
//                json.put("registration_ids", new JSONArray(getUUID));
//
//                OutputStream os = urlConnection.getOutputStream();
//                os.write(json.toString().getBytes("UTF-8"));
//                os.flush();
//                os.close();
//
//                int responseCode = urlConnection.getResponseCode();
//                System.out.println("\nSending 'POST' request to URL : " + url);
//                System.out.println("Post parameters : " + json.toString().getBytes("UTF-8"));
//                System.out.println("Response Code : " + responseCode);
//                Log.e("JSON : ", String.valueOf(json.toString()));
//
//                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                String inputLine;
//                StringBuffer response = new StringBuffer();
//
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//                in.close();
//
//                return response.toString();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            try {
//                Log.e("response : ", s);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    }

    public class GetUUID extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                String link = "http://pyg941007.dothome.co.kr/JoinUUID.php";
                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(TableName, "UTF-8");
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
            try {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;

                String UUID = null;
                getUUID = new ArrayList<>();

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    UUID = object.getString("uuid");

                    getUUID.add(UUID);
                    count++;
                }
               // new Getnotification_key().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GroupShcedule.this, EmptyTimeDate.class);
        intent.putExtra("id", Userid);
        intent.putExtra("GroupName", TableName);
        intent.putExtra("Selectdates", dates);
        intent.putExtra("Starttime", init_Start);
        intent.putExtra("Endtime", init_End);
        startActivity(intent);
        super.onBackPressed();
    }
}
