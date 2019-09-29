package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Memo extends AppCompatActivity implements View.OnClickListener {


    private Button Insert_Btn;
    private Button Cancel_Btn;
    private ImageView Spinner_Btn;

    private EditText Title_edit;
    private EditText Schedule_edit;

    private TextView textView;
    private Spinner Spinner_1;
    private Spinner Spinner_2;
    private boolean flag = false;

    private String Title;
    private String Contents;
    private String userid;
    private String Previoustime;
    private String Aftertime;
    private String Savedate;
    private String Zero_Add_Month;
    private int year, month, day, _ID, Start, End;

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        GetData();
        UI();
        HideKeyboard();
        EnterKey();
    }

    public void GetData(){
        Intent intent = getIntent();
        year = intent.getIntExtra("year", 0);
        month = intent.getIntExtra("month", 0);
        day = intent.getIntExtra("day", 0);
        Zero_Add_Month = null;
        flag = intent.getBooleanExtra("flag",false);
        if (month < 10) {
            Zero_Add_Month = "0" + String.valueOf(month);
        }
        Savedate = String.valueOf(year) + Zero_Add_Month + String.valueOf(day);
        userid = intent.getStringExtra("id");
        if (flag){
            _ID = intent.getIntExtra("index",0);
            Title = intent.getStringExtra("title");
            Contents = intent.getStringExtra("content");
            Previoustime = intent.getStringExtra("previoustime");
            Aftertime = intent.getStringExtra("aftertime");
            Savedate = intent.getStringExtra("savedate");
            String[] ConvertStart = Previoustime.split(" ");
            String[] ConvertEnd = Aftertime.split(" ");

            Start = Integer.parseInt(ConvertStart[0]);
            End = Integer.parseInt(ConvertEnd[0]);
        }
    }

    public void UI(){
        Insert_Btn = findViewById(R.id.memo_insert_button);
        Cancel_Btn = findViewById(R.id.memo_cancel_button);
        Spinner_Btn = findViewById(R.id.memo_date_imageView);
        Insert_Btn.setOnClickListener(this);
        Cancel_Btn.setOnClickListener(this);
        Spinner_Btn.setOnClickListener(this);

        textView = findViewById(R.id.memo_date);
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER);
        textView.setText(year + "년 " + Zero_Add_Month + "월 " + day + "일 ");
        Title_edit = findViewById(R.id.memo_title);
        Schedule_edit = findViewById(R.id.memo_contents);
        if (flag){
            Title_edit.setText(Title);
            Schedule_edit.setText(Contents);
            String date = Savedate.substring(0,4) + "년" + Savedate.substring(4,6) + "월" + Savedate.substring(6) + "일";
            textView.setText(date);
        }
        Spinner();
    }

    public void EnterKey(){
        Schedule_edit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        Schedule_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
                if (keyCode == EditorInfo.IME_ACTION_DONE){
                    Insert_Btn.performClick();
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

        linearLayout = findViewById(R.id.FullScreen);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(Title_edit.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(Schedule_edit.getWindowToken(), 0);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void Spinner(){
        Spinner_1 = findViewById(R.id.Clack_Spinner_1);
        Spinner_2 = findViewById(R.id.Clack_Spinner_2);
        if (!flag) {
            Spinner_1.setSelection(NowTime());
            Spinner_2.setSelection(NowTime()+1);
        }
        else {
            Spinner_1.setSelection(Start);
            Spinner_2.setSelection(End);
        }
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
                if (flag){
                    new ScheduleUpdate().execute();
                    Intent intent = new Intent(this, Schedule.class);
                    intent.putExtra("id", userid);
                    startActivity(intent);
                    finish();
                }else {
                    save();
                }
                break;
            case R.id.memo_cancel_button:
                cancel();
                break;
            case R.id.memo_date_imageView:
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DATE);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Memo.this, listener, year,month,day);
                datePickerDialog.show();
                break;
        }
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            textView.setText(year + "년 " + (month+1) + "월 " + day + "일 ");
            String Month = null;
            if(month < 9){
                Month = "0" + (month+1);
            }
            Savedate = ""+ year + Month + day;
        }
    };

    public void save() {
        int StartTime = Integer.parseInt(Previoustime.substring(0,2));
        int EndTime = Integer.parseInt(Aftertime.substring(0,2));
        Title = Title_edit.getText().toString();
        Contents = Schedule_edit.getText().toString();
        if (Title.equals("")) {
            Toast.makeText(this, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
        } else if (Contents.equals("")) {
            Toast.makeText(this, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
        } else if(StartTime > EndTime){
            Toast.makeText(this, "시간을 다시 설정해 주세요.", Toast.LENGTH_SHORT).show();
        } else{
            new InsertToSchedule().execute();
            Intent intent = new Intent(this, Schedule.class);
            intent.putExtra("id", userid);
            startActivity(intent);
            finish();
        }
    }

    public void cancel() {
        Toast.makeText(this, "취소하셨습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Schedule.class);
        intent.putExtra("id", userid);
        startActivity(intent);
        finish();
    }

    public class InsertToSchedule extends AsyncTask<String, Void, String> {
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
                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
                data += "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(Title, "UTF-8");
                data += "&" + URLEncoder.encode("contents", "UTF-8") + "=" + URLEncoder.encode(Contents, "UTF-8");
                data += "&" + URLEncoder.encode("previoustime", "UTF-8") + "=" + URLEncoder.encode(Previoustime, "UTF-8");
                data += "&" + URLEncoder.encode("aftertime", "UTF-8") + "=" + URLEncoder.encode(Aftertime, "UTF-8");
                data += "&" + URLEncoder.encode("savedate", "UTF-8") + "=" + URLEncoder.encode(Savedate, "UTF-8");

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

    public class ScheduleUpdate extends AsyncTask<Void, Void, String> {

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