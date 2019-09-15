package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    private TextView ID;
    private TextView TextLengthChk;
    private ImageView PW_realtime_chk;
    private EditText Pw;
    private EditText Pw_chk;
    private EditText Account_edit;
    private Button confirm;
    private Button cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Intent intent = getIntent();
        userid = intent.getStringExtra("id");

        new LoadUserinfo().execute();

        ID = findViewById(R.id.Id);
        ID.setText(userid + "님의 계정정보");
        ID.setTextSize(16);

        Pw = findViewById(R.id.pw);
        Pw_chk = findViewById(R.id.pw_chk);
        PW_realtime_chk = findViewById(R.id.Pw_check);
        TextLengthChk = findViewById(R.id.length_check);
        Account_edit = findViewById(R.id.account);

        Pw_chk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!Pw.getText().toString().trim().equals(Pw_chk.getText().toString().trim())){
                    PW_realtime_chk.setImageResource(R.drawable.circle);
                }else{
                    PW_realtime_chk.setImageResource(android.R.drawable.ic_delete);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Account_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String introduction = Account_edit.getText().toString();
                TextLengthChk.setText(introduction.length() + " / 200글자 수");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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

    public class LoadUserinfo extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/user_info.php";
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

            try {
                JSONObject jsonObject = new JSONObject(result);

                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;

                String userintroduce = null;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);


                    userintroduce = object.getString("introduce");

                    count++;
                }

                Account_edit.setText(userintroduce);

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

                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
                data += "&" + URLEncoder.encode("userpassword","UTF-8") + "=" + URLEncoder.encode(Pw.getText().toString().trim(),"UTF-8");
                data += "&" + URLEncoder.encode("userintroduce","UTF-8") + "=" + URLEncoder.encode(Account_edit.getText().toString(),"UTF-8");
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
