package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SignIn extends AppCompatActivity implements View.OnClickListener{

    //UI
    private EditText user_id;
    private EditText user_password;
    private EditText user_password_confirm;
    private EditText user_name;

    private Button Sign_in;
    private Button Cancle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Sign_in = findViewById(R.id.add_account_confirm);
        Cancle = findViewById(R.id.add_account_cancel);

        Sign_in.setOnClickListener(this);
        Cancle.setOnClickListener(this);

        user_id = findViewById(R.id.ID_Text);
        user_password = findViewById(R.id.PW_Text);
        user_password_confirm = findViewById(R.id.PW_check_Text);
        user_name = findViewById(R.id.nickname_Text);
    }

    @Override
    public void onClick(View v) {
        String string_user_id = user_id.getText().toString().trim();
        String string_user_password = user_password.getText().toString().trim();
        String string_user_password_check = user_password_confirm.getText().toString().trim();
        String string_user_name = user_name.getText().toString().trim();
        switch(v.getId()){
            case R.id.add_account_confirm:
                if(string_user_id.equals("")){
                    Toast.makeText(this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(string_user_password.equals("")){
                    Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(string_user_name.equals("")){
                    Toast.makeText(this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(!string_user_password.equals(string_user_password_check)){
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    InsertToDB(string_user_id, string_user_password, string_user_name);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                break;
            case R.id.add_account_cancel:
                //한번더확인하는 다이얼로그?
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
        }
    }

    public void InsertToDB(String Id, String Pw, String Name){
        class InsertData extends AsyncTask<String, Void, String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... strings) {
                try{
                    String Id = strings[0];
                    String Pw = strings[1];
                    String Name = strings[2];

                    String link = "http://pyg941007.dothome.co.kr/Signin.php";
                    String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(Id,"UTF-8");
                    data += "&" + URLEncoder.encode("Pw","UTF-8") + "=" + URLEncoder.encode(Pw,"UTF-8");
                    data += "&" + URLEncoder.encode("Name","UTF-8") + "=" + URLEncoder.encode(Name,"UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine())!=null){
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }catch (Exception e){
                    return new String("Exception : " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(Id,Pw,Name);
    }
}
