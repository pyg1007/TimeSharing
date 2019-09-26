package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

    /*
    화면 터치시 키보드 내려가는것을 위함.
     */
    private InputMethodManager imm;
    private RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        GetData();
        UI();
    }

    public void GetData(){

        Intent intent = getIntent();
        userid = intent.getStringExtra("id");

        new LoadUserinfo().execute();
    }

    public void UI(){
        ID = findViewById(R.id.Id);
        ID.setText(userid + "님의 계정정보");
        ID.setGravity(Gravity.CENTER);
        ID.setTextSize(16);

        Pw = findViewById(R.id.pw);
        Pw_chk = findViewById(R.id.pw_chk);
        PW_realtime_chk = findViewById(R.id.Pw_check);
        TextLengthChk = findViewById(R.id.length_check);
        Account_edit = findViewById(R.id.account);
        Pw.setHint("8자이상 입력하세요.");
        Pw_chk.setHint("8자이상 입력하세요.");

        confirm = findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);

        HideKeyboard();
        RealTimePWCheck();
        EnterKey();
    }

    public void EnterKey(){
        Account_edit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        Account_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
                if(keyCode == EditorInfo.IME_ACTION_DONE){
                    confirm.performClick();
                    return true;
                }
                return false;
            }
        });
    }


    /*
        화면 클릭시 키보드 내려가게 하는 부분
         */
    public void HideKeyboard(){
        relativeLayout = findViewById(R.id.FullScreen);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(Pw.getWindowToken(),0);
                    imm.hideSoftInputFromWindow(Pw_chk.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(Account_edit.getWindowToken(),0);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void RealTimePWCheck(){
        Pw_chk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!Pw.getText().toString().trim().equals(Pw_chk.getText().toString().trim())){
                    PW_realtime_chk.setImageResource(android.R.drawable.ic_delete);
                }else{
                    PW_realtime_chk.setImageResource(R.drawable.circle);
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
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.confirm:
                if(Pw.getText().toString().trim().equals("")){
                    Toast.makeText(this, "바꾸실 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(Pw.getText().toString().length() < 8){
                    Toast.makeText(this, "8자 이상으로 설정해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(!Pw.getText().toString().trim().equals(Pw_chk.getText().toString().trim())){
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    new UpdateUserinfo().execute();
                    Intent intent = new Intent(getApplicationContext(), LogIn.class);
                    Toast.makeText(this, "비밀번호 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.cancel:
                Toast.makeText(this, "계정정보 변경을 취소하셨습니다.", Toast.LENGTH_SHORT).show();
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

    public class UpdateUserinfo extends AsyncTask<Void, Void, String> {

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
