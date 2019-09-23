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

public class EmptyInsertMenu extends AppCompatActivity implements View.OnClickListener{

    private EditText Title;
    private Button Add, Cancel;
    private Spinner FirstTime, EndTime;

    private String Userid, TableName, Dates;
    private ArrayList<String> members;

    private String Start, End;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_insert_menu);

        FirstTime = findViewById(R.id.Clack_Spinner_1);
        EndTime = findViewById(R.id.Clack_Spinner_2);

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

        Title = findViewById(R.id.MenuNameEdit);
        Add = findViewById(R.id.Add);
        Cancel = findViewById(R.id.Cancle);
        Add.setOnClickListener(this);
        Cancel.setOnClickListener(this);



        // 데이터 전달받음
        Intent intent = getIntent();
        Userid = intent.getStringExtra("id");
        TableName = intent.getStringExtra("GroupName");
        members = intent.getStringArrayListExtra("memberlist");
        Dates = intent.getStringExtra("Selectdates");
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(EmptyInsertMenu.this, EmptyTime.class);
        String[] setstart = Start.split(" ");
        String[] setend = End.split(" ");
        switch (view.getId()){
            case R.id.Add:
                if(Title.getText().toString().equals("")){
                    Toast.makeText(this, "메뉴이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                }else if(Integer.parseInt(setstart[0]) >= Integer.parseInt(setend[0])){
                    Toast.makeText(this, "시간설정을 다시해주세요.", Toast.LENGTH_SHORT).show();
                } else{
                    new MenuInsert().execute(Title.getText().toString(), setstart[0], setend[0]);
                    intent.putExtra("id",Userid);
                    intent.putExtra("GroupName",TableName);
                    intent.putExtra("memberlist", members);
                    intent.putExtra("Selectdates",Dates);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.Cancle:
                intent.putExtra("id",Userid);
                intent.putExtra("GroupName",TableName);
                intent.putExtra("memberlist", members);
                intent.putExtra("Selectdates",Dates);
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
            try{
                Log.e("TAG : ", s);
                if (s.equals("fail")){
                    Toast.makeText(EmptyInsertMenu.this, "이미 같은 카테고리가 존재합니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EmptyInsertMenu.this, "생성 성공", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EmptyInsertMenu.this, EmptyTime.class);
        intent.putExtra("id",Userid);
        intent.putExtra("GroupName",TableName);
        intent.putExtra("memberlist", members);
        intent.putExtra("Selectdates",Dates);
        startActivity(intent);
        super.onBackPressed();
    }
}
