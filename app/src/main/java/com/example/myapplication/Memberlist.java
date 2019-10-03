package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.CustomAdapterItem.User;
import com.example.myapplication.CustomAdapter.UserListAdapter;
import com.google.firebase.messaging.FirebaseMessaging;
import com.prolificinteractive.materialcalendarview.CalendarDay;

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
import java.util.Collections;
import java.util.List;

public class Memberlist extends AppCompatActivity implements View.OnClickListener{

    private String userid;
    private String Tablename;

    private ListView listView;
    private UserListAdapter adapter;
    private List<User> userList;
    private FloatingActionButton floatingActionButton;
    private ArrayList<String> Members;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberlist);

        GetData();
        UI();
        showlist();
    }

    public void GetData(){
        Members = new ArrayList<>();

        Intent intent = getIntent();
        userid = intent.getStringExtra("id");
        Members.add(userid);
    }

    public void UI(){
        listView = (ListView) findViewById(R.id.listView);
        floatingActionButton = findViewById(R.id.multipleadd);
        floatingActionButton.setOnClickListener(this);
    }

    public void showlist(){
        new ShowMemberList().execute();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.multipleadd:
                Intent intent = new Intent(getApplicationContext(), CheckboxList.class);
                intent.putExtra("id",userid);
                startActivity(intent);
                finish();
                break;
        }
    }

    public void makedialog(View view){
        final EditText et = new EditText(Memberlist.this);
        et.setHint("영어로만 가능합니다.");
        et.setFilters(new InputFilter[]{RoomNameFilter});
        final TextView Idtext = view.findViewById(R.id.Id);
        Members.add(Idtext.getText().toString());
        AlertDialog.Builder ad = new AlertDialog.Builder(Memberlist.this);
        ad.setTitle("그룹명")
                .setMessage("그룹명을 입력해 주세요.")
                .setView(et)
                .setCancelable(false)
                .setPositiveButton("생성", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Memberlist.this, "취소하셨습니다.", Toast.LENGTH_SHORT).show();
                Members.remove(Idtext.getText().toString());
            }
        });
        final AlertDialog dialog = ad.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tablename = et.getText().toString().trim();
                if (Tablename.equals("")){
                    Toast.makeText(Memberlist.this, "방제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    new Tablecreate().execute();
                    dialog.dismiss();
                }
            }
        });
    }

    /*
    영어만 사용가능하게 제한 걸기
     */
    InputFilter RoomNameFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.toString().matches("^[a-zA-Z]+$")) {
                return source;
            } else {
                return "";
            }
        }
    };

    public class ShowMemberList extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/load_member.php";
                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
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
            userList = new ArrayList<User>();

            try {
                JSONObject jsonObject = new JSONObject(result);

                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;

                String userID;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    userID = object.getString("id");


                    User user = new User(userID);

                    userList.add(user);
                    count++;
                }
                adapter = new UserListAdapter(userList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        new UserIntrudece().execute(userList.get(position).getUserID());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class Tablecreate extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                String link = "http://pyg941007.dothome.co.kr/group_room.php";
                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(Tablename, "UTF-8");

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
                if(s.equals("failure")){
                    Toast.makeText(Memberlist.this, "이미 동일한 그룹명이 존재합니다.", Toast.LENGTH_SHORT).show();
                } else if (s.equals("success")) {
                    Toast.makeText(Memberlist.this, "그룹생성 성공", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Memberlist.this, Group.class);
                    for (int count = 0; count < Members.size(); count++) {
                        new Insertdata().execute(Members.get(count));
                        new Groupadd().execute(Members.get(count));
                    }
                    intent.putExtra("GroupName", Tablename);
                    intent.putExtra("id", userid);
                    startActivity(intent);
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class UserIntrudece extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                String link = "http://pyg941007.dothome.co.kr/user_info.php";
                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8");

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

                return stringBuilder.toString().trim();
            } catch (Exception e) {
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

                String userID = null, userName = null, userIntroduce = null;

                JSONObject object = jsonArray.getJSONObject(count);
                userID = object.getString("id");
                userName = object.getString("name");
                userIntroduce = object.getString("introduce");

                final View Dialogview = getLayoutInflater().inflate(R.layout.dialog, null);
                TextView Idtext = Dialogview.findViewById(R.id.Id);
                TextView Nametext = Dialogview.findViewById(R.id.Name);
                TextView Introduce = Dialogview.findViewById(R.id.Introduce);
                Idtext.setText(userID);
                Nametext.setText(userName);
                Introduce.setText(userIntroduce);
                AlertDialog.Builder ad = new AlertDialog.Builder(Memberlist.this);
                ad.setView(Dialogview).setTitle(userID + "님의 회원정보");
                ad.setPositiveButton("초대하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        makedialog(Dialogview);
                        dialog.dismiss();
                    }
                });
                ad.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class Insertdata extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                String link = "http://pyg941007.dothome.co.kr/Insert_room(person).php";


                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8");
                data += "&" + URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(Tablename, "UTF-8");

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

    public class Groupadd extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... Strings) {
            try {

                String link = "http://pyg941007.dothome.co.kr/groupadd.php";
                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(Tablename, "UTF-8");
                data += "&" + URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(Strings[0], "UTF-8");

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

