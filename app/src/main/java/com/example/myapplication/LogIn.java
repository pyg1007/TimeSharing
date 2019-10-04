package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class LogIn extends AppCompatActivity implements View.OnClickListener{
    //UI부분 - 전역변수 선언

    //private static int Success_Login = 3000; // startActivityForResult를 사용할때 사용할 예정 필요없으면 지워도됨

    private Button Sign_In;
    private Button Log_In;

    private EditText ID;
    private EditText PW;

    private final long FINISH_TIMER = 2000;
    private long backpresstimer = 0;

    /*
    화면 터치시 키보드 내려가는것을 위함.
     */
    private InputMethodManager imm;
    private RelativeLayout relativeLayout;

    private String userID;
    private String userPW;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        UI();
        HideKeyboard();
        EnterKey();
        getToken();
    }

    public void UI(){
        //UI연결
        Sign_In = findViewById(R.id.Sign_In);
        Sign_In.setOnClickListener(this);
        Log_In = findViewById(R.id.LogIn);
        Log_In.setOnClickListener(this);

        ID = findViewById(R.id.ID_Text);
        PW = findViewById(R.id.PW_Text);
    }
    //완료형태의 엔터키 누를시
    public void EnterKey(){
        PW.setImeOptions(EditorInfo.IME_ACTION_DONE);
        PW.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
                if(keyCode==EditorInfo.IME_ACTION_DONE){
                    Log_In.performClick();
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
                    imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ID.getWindowToken(),0);
                    imm.hideSoftInputFromWindow(PW.getWindowToken(),0);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.LogIn: // 로그인버튼
                userID = ID.getText().toString().trim();
                userPW = PW.getText().toString().trim();
                if(userID.equals("")){
                    Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(userPW.equals("")){
                    Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    new Login_task().execute();
                }
                break;
            case R.id.Sign_In: // 회원가입버튼
                startActivity(new Intent(getApplicationContext(), SignIn.class));
                finish();
                break;
        }
    }

    public void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()){
                    Log.w("TAG", "getInstanceId failed", task.getException());
                    return;
                }
                token = task.getResult().getToken();
            }
        });
    }

    public class Login_task extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                String link = "http://pyg941007.dothome.co.kr/Login.php";
                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                data += "&" + URLEncoder.encode("userpassword", "UTF-8") + "=" + URLEncoder.encode(userPW, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                wr.close();
                return sb.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (s.equals("1")) {
                    new CheckUUID().execute();
                    Intent intent = new Intent(LogIn.this, Schedule.class);
                    intent.putExtra("id", userID);
                    startActivity(intent);
                    finish();
                } else if (s.equals("0")) {
                    Toast.makeText(LogIn.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LogIn.this, "아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class CheckUUID extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                String link = "http://pyg941007.dothome.co.kr/CheckToken.php";
                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                wr.close();
                return sb.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Log.e("CHeck : ", s);
                JSONObject jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;

                String UUID = null;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    UUID = object.getString("uuid");

                    count++;
                }
                if (!UUID.equals(token)){
                    new UpdateUUID().execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class UpdateUUID extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                String link = "http://pyg941007.dothome.co.kr/UpdateToken.php";
                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                data += "&" + URLEncoder.encode("uuid", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                wr.close();
                return sb.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("UPDATE : ", s);
        }
    }


    //뒤로가기 두번누르면 종료
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backpresstimer;

        if (0 <= intervalTime && FINISH_TIMER >= intervalTime)
        {
            super.onBackPressed();
        }
        else
        {
            backpresstimer = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누르면 꺼집니다.", Toast.LENGTH_SHORT).show();
        }

    }

}

