package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //UI부분 - 전역변수 선언

    //private static int Success_Login = 3000; // startActivityForResult를 사용할때 사용할 예정 필요없으면 지워도됨

    private Button Sign_In;
    private Button LogIn;

    private EditText ID;
    private EditText PW;

    private String server_data = null;

    private final long FINISH_TIMER = 2000;
    private long backpresstimer = 0;



    private String userID;
    private String userPW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI연결

        Sign_In = findViewById(R.id.Sign_In);
        Sign_In.setOnClickListener(this);
        LogIn = findViewById(R.id.LogIn);
        LogIn.setOnClickListener(this);


        ID = findViewById(R.id.ID_Text);
        PW = findViewById(R.id.PW_Text);



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
                    login();
                }
                break;
            case R.id.Sign_In: // 회원가입버튼
                startActivity(new Intent(getApplicationContext(), SignIn.class));
                finish();
                break;



        }
    }



    public void login() {
        class Login_task extends AsyncTask<Void, Integer, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
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
                        break;
                    }
                    server_data = sb.toString().trim();

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
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    if (server_data.equals("1")) {
                        Intent intent = new Intent(MainActivity.this, Schedule.class);
                        intent.putExtra("id", userID);
                        startActivity(intent);
                        finish();
                    } else if (server_data.equals("0")) {
                        Toast.makeText(MainActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        Login_task login_task = new Login_task();
        login_task.execute();
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
