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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Memo extends AppCompatActivity implements View.OnClickListener {


    private Button Insert_Btn;
    private Button Cancel_Btn;
    ImageView Spinner_Btn;


    private EditText Title_edit;
    private EditText Schedule_edit;


    private TextView textView;
    private Spinner Spinner_1;
    private Spinner Spinner_2;

    private String Title;
    private String Contents;
    private String userid;
    private String Previoustime;
    private String Aftertime;
    private String Savedate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        Insert_Btn = findViewById(R.id.memo_insert_button);
        Cancel_Btn = findViewById(R.id.memo_cancel_button);
        Spinner_Btn = findViewById(R.id.memo_date_imageView);
        Insert_Btn.setOnClickListener(this);
        Cancel_Btn.setOnClickListener(this);
        Spinner_Btn.setOnClickListener(this);


        Intent intent = getIntent();
        int year = intent.getIntExtra("year", 0);
        int month = intent.getIntExtra("month", 0);
        int day = intent.getIntExtra("day", 0);
        String Month = null;
        if (month < 10) {
            Month = "0" + String.valueOf(month);
        }
        Savedate = String.valueOf(year) + Month + String.valueOf(day);
        userid = intent.getStringExtra("id");
        textView = findViewById(R.id.memo_date);
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER);
        textView.setText(year + "년 " + month + "월 " + day + "일 ");


        Spinner_1 = findViewById(R.id.Clack_Spinner_1);
        Spinner_1.setSelection(NowTime() - 1);
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
        Spinner_2.setSelection(NowTime());
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

    public int NowTime() {
        long Now = System.currentTimeMillis();
        Date Now_time = new Date(Now);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");

        String Format = simpleDateFormat.format(Now_time);

        int Time = Integer.parseInt(Format);

        return Time;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.memo_insert_button:
                save();
                break;
            case R.id.memo_cancel_button:
                cancel();
                break;
            case R.id.memo_date_imageView:
                startActivity(new Intent(getApplicationContext(), SpinnerActivity.class));
                break;

        }
    }

    public void save() {
        Title_edit = findViewById(R.id.memo_title);
        Schedule_edit = findViewById(R.id.memo_contents);
        Title = Title_edit.getText().toString();
        Contents = Schedule_edit.getText().toString();
        if (Title.equals("")) {
            Toast.makeText(this, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
        } else if (Contents.equals("")) {
            Toast.makeText(this, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
        } else {
            InsertToSchedule();
        }
        Intent intent = new Intent(this, Schedule.class);
        intent.putExtra("id", userid);
        startActivity(intent);
        finish();
    }

    public void cancel() {
        Toast.makeText(this, "취소하셨습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Schedule.class);
        intent.putExtra("id", userid);
        startActivity(intent);
        finish();
    }

    public void InsertToSchedule() {
        class InsertData extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                Log.e(" Error : ", s);
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    Log.e("Savedata : ", Savedate);
                    String link = "http://pyg941007.dothome.co.kr/Insert_schedule.php";
                    String data = URLEncoder.encode("Userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
                    data += "&" + URLEncoder.encode("Title", "UTF-8") + "=" + URLEncoder.encode(Title, "UTF-8");
                    data += "&" + URLEncoder.encode("Contents", "UTF-8") + "=" + URLEncoder.encode(Contents, "UTF-8");
                    data += "&" + URLEncoder.encode("Previoustime", "UTF-8") + "=" + URLEncoder.encode(Previoustime, "UTF-8");
                    data += "&" + URLEncoder.encode("Aftertime", "UTF-8") + "=" + URLEncoder.encode(Aftertime, "UTF-8");
                    data += "&" + URLEncoder.encode("Savedate", "UTF-8") + "=" + URLEncoder.encode(Savedate, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    InputStream inputStream = conn.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    reader.close();
                    inputStream.close();
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception : " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Schedule.class);
        intent.putExtra("id", userid);
        startActivity(intent);
        super.onBackPressed();
    }
}