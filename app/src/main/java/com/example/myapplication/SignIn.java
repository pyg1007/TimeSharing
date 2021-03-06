package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
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

import com.example.myapplication.inputfilter.NumEngFilter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    //UI
    private ImageView Pw_chk;

    private EditText user_id;
    private EditText user_password;
    private EditText user_password_confirm;
    private EditText user_name;

    private Button Sign_in;
    private Button Cancle;
    private Button Id_duplicate;

    private boolean Id_Check = false;
    private String token;

    private RelativeLayout relativeLayout;
    private InputMethodManager imm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        UI();
        HideKeyboard();
        RealTimePWCheck();
        EnterKey();
        getToken();
    }

    public void UI(){
        Sign_in = findViewById(R.id.add_account_confirm);
        Cancle = findViewById(R.id.add_account_cancel);
        Id_duplicate = findViewById(R.id.ID_confirm_btn);

        Sign_in.setOnClickListener(this);
        Cancle.setOnClickListener(this);
        Id_duplicate.setOnClickListener(this);

        Pw_chk = findViewById(R.id.Pw_realtime_chk);
        user_id = findViewById(R.id.ID_Text);
        user_password = findViewById(R.id.PW_Text);
        user_password_confirm = findViewById(R.id.PW_check_Text);
        user_name = findViewById(R.id.nickname_Text);

        user_id.setFilters(new InputFilter[]{new NumEngFilter()});
        user_id.setHint("영어와 숫자만 사용가능합니다.");
        user_id.setTextSize(15);
        user_password.setHint("8자리 이상을 입력해주세요.");
        user_password.setTextSize(15);
        user_password_confirm.setHint("8자리 이상을 입력해주세요.");
        user_password_confirm.setTextSize(15);
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

    public void EnterKey(){
        user_name.setImeOptions(EditorInfo.IME_ACTION_DONE);
        user_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
                if(keyCode == EditorInfo.IME_ACTION_DONE){
                    Sign_in.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    public void RealTimePWCheck(){
        user_password_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (user_password.getText().toString().trim().equals(user_password_confirm.getText().toString().trim())) {
                    Pw_chk.setImageResource(R.drawable.circle);
                } else {
                    Pw_chk.setImageResource(android.R.drawable.ic_delete);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(user_id.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(user_name.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(user_password.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(user_password_confirm.getWindowToken(), 0);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        String string_user_id = user_id.getText().toString().trim();
        String string_user_password = user_password.getText().toString().trim();
        String string_user_password_check = user_password_confirm.getText().toString().trim();
        String string_user_name = user_name.getText().toString().trim();
        switch (v.getId()) {
            case R.id.add_account_confirm:
                if (string_user_id.equals("")) {
                    Toast.makeText(this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (string_user_password.equals("")) {
                    Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if(string_user_password.length() < 8){
                    Toast.makeText(this, "비밀번호 길이를 체크해주세요.", Toast.LENGTH_SHORT).show();
                } else if (string_user_name.equals("")) {
                    Toast.makeText(this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (!string_user_password.equals(string_user_password_check)) {
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else if (!Id_Check) {
                    Toast.makeText(this, "중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                } else if (user_id.getText().toString().length() < 5) {
                    Toast.makeText(this, "아이디는 5글자 이상 입력해야합니다.", Toast.LENGTH_SHORT).show();
                } else if(user_password.getText().toString().length()< 8){
                    Toast.makeText(this, "비밀번호는 8글자 이상 입력해야합니다.", Toast.LENGTH_SHORT).show();
                }else {
                    new InsertData().execute(string_user_id, string_user_password, string_user_name);
                    startActivity(new Intent(getApplicationContext(), LogIn.class));
                    finish();
                }
                break;
            case R.id.add_account_cancel:
                startActivity(new Intent(getApplicationContext(), LogIn.class));
                finish();
                break;
            case R.id.ID_confirm_btn:
                if (string_user_id.equals("")) {
                    Toast.makeText(this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (user_id.getText().toString().length() < 5) {
                    Toast.makeText(this, "아이디는 5글자 이상 입력해야합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    new Idcheck().execute(string_user_id);
                }

                break;
        }
    }

    public class InsertData extends AsyncTask<String, Void, String> {
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
            try {

                String link = "http://pyg941007.dothome.co.kr/Signin.php";
                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8");
                data += "&" + URLEncoder.encode("userpassword", "UTF-8") + "=" + URLEncoder.encode(strings[1], "UTF-8");
                data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(strings[2], "UTF-8");
                data += "&" + URLEncoder.encode("userintroduce", "UTF-8") + "=" + URLEncoder.encode(strings[2] + "입니다.", "UTF-8");
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
                    sb.append(line);
                }
                reader.close();
                wr.close();
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception : " + e.getMessage());
            }
        }
    }

    public class Idcheck extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String Id = strings[0];
                String link = "http://pyg941007.dothome.co.kr/id_chk.php";
                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");

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
                    sb.append(line);
                }
                reader.close();
                wr.close();
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("failure")) {
                Id_Check = false;
                Toast.makeText(SignIn.this, "이미 아이디가 존재합니다.", Toast.LENGTH_SHORT).show();
            } else if (s.equals("success")) {
                Id_Check = true;
                Toast.makeText(SignIn.this, "가입 가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignIn.this, LogIn.class));
        super.onBackPressed();
    }
}
