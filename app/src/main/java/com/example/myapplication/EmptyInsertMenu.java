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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class EmptyInsertMenu extends AppCompatActivity implements View.OnClickListener {

    private EditText Title;
    private Button Add, Cancel;
    private Spinner FirstTime, EndTime;
    private int PreTime, AftTime;
    private boolean flag = false;

    private String Userid, TableName, Dates, MenuName;

    private String Start, End;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_insert_menu);

        GetData();
        UI();
    }

    public void GetData() {
        // 데이터 전달받음
        Intent intent = getIntent();
        Userid = intent.getStringExtra("id");
        TableName = intent.getStringExtra("GroupName");
        flag = intent.getBooleanExtra("Editcheck", false);
        if (flag == true) {
            MenuName = intent.getStringExtra("Menu");
            PreTime = intent.getIntExtra("Start", 0);
            AftTime = intent.getIntExtra("End", 0);
        }
        Dates = intent.getStringExtra("Selectdates");
    }

    public void UI() {
        Title = findViewById(R.id.MenuNameEdit);
        Add = findViewById(R.id.Add);
        Cancel = findViewById(R.id.Cancle);
        Add.setOnClickListener(this);
        Cancel.setOnClickListener(this);

        if (flag) {
            Title.setText(MenuName);
        }
        Spinner();
    }

    public void Spinner() {
        FirstTime = findViewById(R.id.Clack_Spinner_1);
        EndTime = findViewById(R.id.Clack_Spinner_2);

        if (flag) {
            FirstTime.setSelection(PreTime);
            EndTime.setSelection(AftTime);
        }

        FirstTime.setGravity(Gravity.CENTER);
        FirstTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Start = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        EndTime.setGravity(Gravity.CENTER);
        EndTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                End = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(EmptyInsertMenu.this, EmptyTime.class);
        String[] setstart = Start.split(" ");
        String[] setend = End.split(" ");
        int Set_start, Set_End;
        Set_start = Integer.parseInt(setstart[0]);
        Set_End = Integer.parseInt(setend[0]);
        switch (view.getId()) {
            case R.id.Add:
                if (Title.getText().toString().equals("")) {
                    Toast.makeText(this, "메뉴이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(setstart[0]) >= Integer.parseInt(setend[0])) {
                    Toast.makeText(this, "시간설정을 다시해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!flag)
                        new MenuInsert().execute(Title.getText().toString(), String.valueOf(Set_start), String.valueOf(Set_End));
                    else if (flag)
                        new MenuUpdate().execute(Title.getText().toString(), String.valueOf(Set_start), String.valueOf(Set_End));
                    intent.putExtra("id", Userid);
                    intent.putExtra("GroupName", TableName);
                    intent.putExtra("Selectdates", Dates);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.Cancle:
                intent.putExtra("id", Userid);
                intent.putExtra("GroupName", TableName);
                intent.putExtra("Selectdates", Dates);
                startActivity(intent);
                finish();
                break;
        }
    }

    public class MenuInsert extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String link = "http://pyg941007.dothome.co.kr/insert_menu.php";
                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(TableName, "UTF-8");
                data += "&" + URLEncoder.encode("menuname", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8");
                data += "&" + URLEncoder.encode("starttime", "UTF-8") + "=" + URLEncoder.encode(strings[1], "UTF-8");
                data += "&" + URLEncoder.encode("endtime", "UTF-8") + "=" + URLEncoder.encode(strings[2], "UTF-8");

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
                if (s.equals("fail")) {
                    Toast.makeText(EmptyInsertMenu.this, "이미 같은 카테고리가 존재합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EmptyInsertMenu.this, "생성 성공", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MenuUpdate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String link = "http://pyg941007.dothome.co.kr/UpdateMenu.php";
                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(TableName, "UTF-8");
                data += "&" + URLEncoder.encode("menuname", "UTF-8") + "=" + URLEncoder.encode(MenuName, "UTF-8");
                data += "&" + URLEncoder.encode("start", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(PreTime), "UTF-8");
                data += "&" + URLEncoder.encode("end", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(AftTime), "UTF-8");
                data += "&" + URLEncoder.encode("changemenuname", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8");
                data += "&" + URLEncoder.encode("starttime", "UTF-8") + "=" + URLEncoder.encode(strings[1], "UTF-8");
                data += "&" + URLEncoder.encode("endtime", "UTF-8") + "=" + URLEncoder.encode(strings[2], "UTF-8");

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
                if (s.equals("success")) {
                    Toast.makeText(EmptyInsertMenu.this, "변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EmptyInsertMenu.this, EmptyTime.class);
        intent.putExtra("id", Userid);
        intent.putExtra("GroupName", TableName);
        intent.putExtra("Selectdates", Dates);
        startActivity(intent);
        super.onBackPressed();
    }
}
