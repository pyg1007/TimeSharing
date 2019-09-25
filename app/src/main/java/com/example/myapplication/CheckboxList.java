package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.CustomAdapter.CheckboxListAdapter;

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

public class CheckboxList extends AppCompatActivity implements View.OnClickListener{

    private ListView checkboxlistView;
    private String userid;
    private List<String> checkboxListItems;
    private ArrayList<String> members;
    private ArrayList<String> members_copy;

    String Tablename;

    private boolean flag = false;

    private CheckboxListAdapter checkboxListAdapter;

    private Button Invite_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkbox_list);

        GetData();
        UI();
    }

    public void GetData(){
        members = new ArrayList<>();
        members_copy = new ArrayList<>();

        Intent intent = getIntent();
        userid = intent.getStringExtra("id");
        flag = intent.getBooleanExtra("Invate_check",false);
        if (flag){
            members = intent.getStringArrayListExtra("memberid");
            Tablename = intent.getStringExtra("GroupName");
            for (int i =0; i<members.size();i++){
                members_copy.add(members.get(i));
            }
        }else{
            members.add(userid);
        }

        new GetMemeber().execute(userid);
    }

    public void UI(){
        checkboxlistView = findViewById(R.id.checkbox_list);
        Invite_btn = findViewById(R.id.Invite);
        Invite_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Invite:
                if(flag == true) {
                    if(members.size()>members_copy.size()) {
                        Intent intent = new Intent(CheckboxList.this, Group.class);
                        for (int i = 0; i < members.size(); i++) {
                            new Insertdata().execute(members.get(i));
                        }
                        intent.putExtra("id", userid);
                        intent.putStringArrayListExtra("memberid", members);
                        intent.putExtra("GroupName", Tablename);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(this, "초대할 사람을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if (members.size()>1) {
                        final EditText editText = new EditText(CheckboxList.this);
                        editText.setHint("영어로만 가능합니다. 숫자와 특수문자는 자동제거됩니다.");
                        AlertDialog.Builder ad = new AlertDialog.Builder(CheckboxList.this);
                        ad.setTitle("그룹명")
                                .setMessage("그룹명을 입력해 주세요.")
                                .setView(editText)
                                .setCancelable(false)
                                .setPositiveButton("생성", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(CheckboxList.this, "취소하셨습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        final AlertDialog dialog = ad.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Tablename = editText.getText().toString().trim();
                                if (Tablename.equals("")){
                                    Toast.makeText(CheckboxList.this, "방 이름을 설정해주세요.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Tablename = Tablename.replaceAll("\\p{Digit}|\\p{Punct}",""); // 숫자와 특수문자 제거
                                    if(Tablename.getBytes().length == Tablename.length()) {
                                        new Tablecreate().execute();
                                        dialog.dismiss();
                                    }
                                    else if(Tablename.getBytes().length == 3 * Tablename.length()){
                                        Toast.makeText(CheckboxList.this, "한글로는 방을 만들수 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }else{
                        Toast.makeText(this, "초대할 사람을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }

    public class GetMemeber extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {

                String link = "http://pyg941007.dothome.co.kr/load_member.php";
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
            checkboxListItems = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(result);

                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;

                String userID;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    userID = object.getString("id");

                    checkboxListItems.add(userID);
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(flag == true) {
                for (int i = 0; i < members_copy.size(); i++) {
                    for (int j = 0; j < checkboxListItems.size(); j++) {
                        if (members_copy.get(i).equals(checkboxListItems.get(j))) {
                            checkboxListItems.remove(j);
                        }
                    }
                }
            }

            checkboxListAdapter = new CheckboxListAdapter(CheckboxList.this,checkboxListItems);
            checkboxlistView.setAdapter(checkboxListAdapter);
            checkboxlistView.setOnItemClickListener(mItemClickListener);
        }
        private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkboxListAdapter.setChecked(position);
                checkboxListAdapter.notifyDataSetChanged();
                if(checkboxListAdapter.ischeck(position)){
                    members.add(parent.getItemAtPosition(position).toString());
                    Log.e("size : ", String.valueOf(members.size()));
                    if(flag)
                        Log.e("copy size : ", String.valueOf(members_copy.size()));
                }else{
                    members.remove(parent.getItemAtPosition(position).toString());
                }
            }
        };
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
                    Toast.makeText(CheckboxList.this, "이미 동일한 그룹명이 존재합니다.", Toast.LENGTH_SHORT).show();
                } else if (s.equals("success")) {
                    Toast.makeText(CheckboxList.this, "그룹생성 성공", Toast.LENGTH_SHORT).show();
                    for (int count = 0; count < members.size(); count++) {
                        new Insertdata().execute(members.get(count));
                    }
                    Intent intent = new Intent(CheckboxList.this, Group.class);
                    intent.putExtra("GroupName", Tablename);
                    intent.putExtra("id", userid);
                    intent.putStringArrayListExtra("memberid", members);
                    startActivity(intent);
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!flag) {
            Intent intent = new Intent(this, Schedule.class);
            intent.putExtra("id", userid);
            startActivity(intent);
            super.onBackPressed();
        }else{
            Intent intent = new Intent(this, Group.class);
            intent.putExtra("id", userid);
            intent.putExtra("GroupName", Tablename);
            intent.putExtra("memberid",members);
            startActivity(intent);
            super.onBackPressed();
        }
    }
}
