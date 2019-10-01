package com.example.myapplication;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.myapplication.Empty.EmptyAdapter;
import com.example.myapplication.Empty.EmptyItem;
import com.example.myapplication.CustomAdapterItem.MyItem;
import com.example.myapplication.CustomAdapterItem.ShareItem;

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
import java.util.List;

public class EmptyTime extends AppCompatActivity implements View.OnClickListener{

    private Button button;
    private ListView listView;

    private String Userid;
    private String Tablename;

    private String dates;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_time);

        GetData();
        UI();
    }

    public void GetData(){
        Intent intent = getIntent();
        Userid = intent.getStringExtra("id");
        Tablename = intent.getStringExtra("GroupName");
        dates = intent.getStringExtra("Selectdates");

        new FirstMenu().execute();
        new MenuLoad().execute();
    }

    public void UI(){
        TextView textView = findViewById(R.id.Title);

        button = findViewById(R.id.AddMenu);
        listView = findViewById(R.id.list);
        button.setOnClickListener(this);

        textView.setGravity(Gravity.CENTER);
        textView.setText("카테고리를 정하세요.");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.AddMenu:
                Intent intent = new Intent(EmptyTime.this, EmptyInsertMenu.class);
                intent.putExtra("id",Userid);
                intent.putExtra("GroupName",Tablename);
                intent.putExtra("Selectdates", dates);
                startActivity(intent);
                finish();
                break;
        }
    }

    public class FirstMenu extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {

                String link = "http://pyg941007.dothome.co.kr/FirstRoomMenu.php";
                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(Tablename, "UTF-8");

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
        }
    }

    public class MenuLoad extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                String link = "http://pyg941007.dothome.co.kr/Load_menu.php";
                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(Tablename, "UTF-8");

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
            final List<EmptyItem> emptyItems = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;

                int start, end;
                String MenuName;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    MenuName = object.getString("MenuName");
                    start = object.getInt("StartTime");
                    end = object.getInt("EndTime");

                    emptyItems.add(new EmptyItem(MenuName,start,end));
                    count++;
                }
                final EmptyAdapter emptyAdapter = new EmptyAdapter(EmptyTime.this, emptyItems);
                listView.setAdapter(emptyAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(EmptyTime.this, EmptyTimeDate.class);
                        intent.putExtra("Menu", emptyItems.get(i).getMenu());
                        //되돌아올때
                        intent.putExtra("id",Userid);
                        intent.putExtra("GroupName", Tablename);
                        intent.putExtra("Selectdates",dates);
                        //
                        intent.putExtra("Starttime", emptyItems.get(i).getStart());
                        intent.putExtra("Endtime", emptyItems.get(i).getEnd());
                        startActivity(intent);
                        finish();
                    }
                });
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, final long id) {
                        PopupMenu popupMenu = new PopupMenu(EmptyTime.this, view, Gravity.RIGHT);
                        getMenuInflater().inflate(R.menu.list_menu, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.list_menu1: // 삭제
                                        new DeleteMenu().execute(emptyItems.get(position).getMenu(), String.valueOf(emptyItems.get(position).getStart()), String.valueOf(emptyItems.get(position).getEnd()));
                                        emptyItems.remove(position);
                                        emptyAdapter.notifyDataSetChanged();
                                        break;
                                    case R.id.list_menu2: // 편집
                                        flag = true;
                                        Intent intent = new Intent(EmptyTime.this, EmptyInsertMenu.class);
                                        intent.putExtra("id",Userid);
                                        intent.putExtra("Selectdates",dates);
                                        intent.putExtra("GroupName", Tablename);
                                        intent.putExtra("Menu",emptyItems.get(position).getMenu());
                                        intent.putExtra("Editcheck",flag);
                                        intent.putExtra("Start",emptyItems.get(position).getStart());
                                        intent.putExtra("End",emptyItems.get(position).getEnd());
                                        startActivity(intent);
                                        finish();
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                        return true;
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class DeleteMenu extends AsyncTask<String, Void, String>{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String link = "http://pyg941007.dothome.co.kr/DeleteMenu.php";
                String data = URLEncoder.encode("tablename", "UTF-8") + "=" + URLEncoder.encode(Tablename, "UTF-8");
                data += "&" + URLEncoder.encode("menuname", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8");
                data += "&" + URLEncoder.encode("start", "UTF-8") + "=" + URLEncoder.encode(strings[1], "UTF-8");
                data += "&" + URLEncoder.encode("end", "UTF-8") + "=" + URLEncoder.encode(strings[2], "UTF-8");

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
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EmptyTime.this, Group.class);
        intent.putExtra("id", Userid);
        intent.putExtra("GroupName", Tablename);
        startActivity(intent);
        super.onBackPressed();
    }
}