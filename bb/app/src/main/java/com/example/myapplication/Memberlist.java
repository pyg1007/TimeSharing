package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import java.util.List;

public class Memberlist extends AppCompatActivity{

    private String userid;
    private String Tablename;
    private String memberid;

    private ListView listView;
    private UserListAdapter adapter;
    private List<User> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberlist);

        Intent intent = getIntent();
        userid = intent.getStringExtra("id");

        addlist();
    }

    public void addlist(){
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute();

    }


    public class BackgroundTask extends AsyncTask<Void, Void, String> implements View.OnClickListener{

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/load_member.php";
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

            listView = (ListView) findViewById(R.id.listView);
            userList = new ArrayList<User>();

            adapter = new UserListAdapter(userList);
            listView.setAdapter(adapter);

            try {
                JSONObject jsonObject = new JSONObject(result);

                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;

                String userID;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    userID = object.getString("id");


                    User user = new User(userID, this);

                    userList.add(user);
                    count++;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View view) {
            View parentview = (View)view.getParent();
            TextView useridtext = parentview.findViewById(R.id.userID);
            String position = (String) parentview.getTag();
            memberid = useridtext.getText().toString().trim();
            final EditText et = new EditText(Memberlist.this);
            AlertDialog.Builder ad = new AlertDialog.Builder(Memberlist.this);
                ad.setTitle("그룹명")
                        .setMessage("그룹명을 입력해 주세요.")
                        .setView(et)
                .setCancelable(false)
                .setPositiveButton("생성", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Tablename = et.getText().toString().trim();
                        dialogInterface.dismiss();
                        Tablecreate tablecreate = new Tablecreate();
                        tablecreate.execute();
                    }
                });
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Memberlist.this, "취소하셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                ad.show();

                Log.e("TAG : ", position + " "+ useridtext.getText().toString());
            }
    }

        public class Tablecreate extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {

                String link = "http://pyg941007.dothome.co.kr/group_room.php";
                String data = URLEncoder.encode("TableName", "UTF-8") + "=" + URLEncoder.encode(Tablename, "UTF-8");
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
                Log.e("result : ", s);
                if(s.equals("failure")){
                    Toast.makeText(Memberlist.this, "이미 동일한 그룹명이 존재합니다.", Toast.LENGTH_SHORT).show();
                } else if (s.equals("success")) {
                    Toast.makeText(Memberlist.this, "그룹생성 성공", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Memberlist.this, Group.class);
                    intent.putExtra("GroupName", Tablename);
                    intent.putExtra("userid", userid);
                    intent.putExtra("memberid", memberid);
                    startActivity(intent);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
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

