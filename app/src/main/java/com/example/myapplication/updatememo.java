package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class updatememo extends AppCompatActivity implements View.OnClickListener{

    // 값 받아오기
    String userid, Title, Contents, Previoustime, Aftertime, Savedate;
    int _ID;

    // UI셋팅
    private Button Insert_Btn;
    private Button Cancle_Btn;
    ImageView Spinner_Btn;

    private EditText Title_edit;
    private EditText Schedule_edit;

    private TextView textView;
    private Spinner Spinner_1;
    private Spinner Spinner_2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatememo);
        data();
        UI();
        Spinner();

        Spinner_Btn = findViewById(R.id.memo_date_imageView);
        Spinner_Btn.setOnClickListener(this);
    }

    public void data(){
        Intent intent = getIntent();
        userid = intent.getStringExtra("id");
        _ID = intent.getIntExtra("index",0);
        Title = intent.getStringExtra("title");
        Contents = intent.getStringExtra("content");
        Previoustime = intent.getStringExtra("previoustime");
        Aftertime = intent.getStringExtra("aftertime");
        Savedate = intent.getStringExtra("savedate");
    }

    public void UI(){
        Insert_Btn = findViewById(R.id.memo_insert_button);
        Cancle_Btn = findViewById(R.id.memo_cancel_button);
        Insert_Btn.setOnClickListener(this);
        Cancle_Btn.setOnClickListener(this);

        textView = findViewById(R.id.memo_date);
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER);

        String date = Savedate.substring(0,4) + "년" + Savedate.substring(4,6) + "월" + Savedate.substring(6) + "일";
        textView.setText(date);

        Title_edit = findViewById(R.id.memo_title);
        Title_edit.setText(Title);
        Schedule_edit = findViewById(R.id.memo_contents);
        Schedule_edit.setText(Contents);
    }

    public int pretime(){
        int time;
        String[] firsttime = Previoustime.split(" ");
        time = Integer.parseInt(firsttime[0]);
        return time;
    }

    public void Spinner(){
        Spinner_1 = findViewById(R.id.Clack_Spinner_1);
        Spinner_1.setSelection(pretime() - 1 );
        Spinner_1.setGravity(Gravity.CENTER);
        Spinner_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Previoustime = Spinner_1.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Spinner_2 = findViewById(R.id.Clack_Spinner_2);
        Spinner_2.setSelection(pretime());
        Spinner_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Aftertime = Spinner_2.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.memo_insert_button:
                save();
                break;
            case R.id.memo_cancel_button:
                cancle();
                break;
            case R.id.memo_date_imageView:
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DATE);
                DatePickerDialog datePickerDialog = new DatePickerDialog(updatememo.this, listener, year,month,day);
                datePickerDialog.show();
                break;
        }
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            textView.setText(year + "년 " + (month+1) + "월 " + day + "일 ");
        }
    };

    public void save(){
        Title = Title_edit.getText().toString();
        Contents = Schedule_edit.getText().toString();
        int StartTime = Integer.parseInt(Previoustime.substring(0,2));
        int EndTime = Integer.parseInt(Aftertime.substring(0,2));
        if(Title.equals("")){
            Toast.makeText(this, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
        }else if(Contents.equals("")){
            Toast.makeText(this, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }else if(StartTime > EndTime){
            Toast.makeText(this, "시간을 다시 설정해 주세요.", Toast.LENGTH_SHORT).show();
        }
        else{
            updateschedule _updateschedule = new updateschedule();
            _updateschedule.execute();
        }
        Intent intent = new Intent(this, Schedule.class);
        intent.putExtra("id",userid);
        startActivity(intent);
        finish();
    }

    public void cancle(){
        Toast.makeText(this, "취소하셨습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Schedule.class);
        intent.putExtra("id",userid);
        startActivity(intent);
        finish();
    }


    public class updateschedule extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/updateschedule.php";

                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(_ID), "UTF-8");
                data += "&" + URLEncoder.encode("title","UTF-8") + "=" + URLEncoder.encode(Title,"UTF-8");
                data += "&" + URLEncoder.encode("contents","UTF-8") + "=" + URLEncoder.encode(Contents,"UTF-8");
                data += "&" + URLEncoder.encode("previoustime","UTF-8") + "=" + URLEncoder.encode(Previoustime,"UTF-8");
                data += "&" + URLEncoder.encode("aftertime","UTF-8") + "=" + URLEncoder.encode(Aftertime,"UTF-8");
                data += "&" + URLEncoder.encode("savedate","UTF-8") + "=" + URLEncoder.encode(Savedate,"UTF-8");
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
