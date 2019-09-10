package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
import java.util.List;

public class CheckboxList extends AppCompatActivity {

    private ListView checkboxlistView;
    private String userid;
    private List<String> checkboxListItems;

    private CheckboxListAdapter checkboxListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkbox_list);

        Intent intent = getIntent();
        userid = intent.getStringExtra("id");

        checkboxlistView = findViewById(R.id.checkbox_list);

        new GetMemeber().execute();
    }

    public class GetMemeber extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String link = "http://pyg941007.dothome.co.kr/load_member.php";
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            checkboxListItems = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(result);

                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                int count = 0;

                String userID;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    userID = object.getString("id");

                    checkboxListItems.add(userID);
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            checkboxListAdapter = new CheckboxListAdapter(CheckboxList.this,checkboxListItems);
            checkboxlistView.setAdapter(checkboxListAdapter);
            checkboxlistView.setOnItemClickListener(mItemClickListener);
        }
        private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("TAG : ", parent.getItemAtPosition(position).toString() + view.getId());
                checkboxListAdapter.setChecked(position);
                checkboxListAdapter.notifyDataSetChanged();
            }
        };
    }

    private String isCheckedOrNot(CheckBox checkbox) {
        if(checkbox.isChecked())
            return "is checked";
        else
            return "is not checked";
    }
}
