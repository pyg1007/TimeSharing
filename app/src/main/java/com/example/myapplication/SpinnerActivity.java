package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class SpinnerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_spinner);



        DatePicker date_spinner = findViewById(R.id.date_spinner);
        final Button btn_confirm = findViewById(R.id.btn_confirm);
        final TextView pick_date = findViewById(R.id.pick_date);

        date_spinner.init(date_spinner.getYear(), date_spinner.getMonth(), date_spinner.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    final public void onDateChanged(DatePicker view, final int year, int month, final int day) {
                        month = 1 + month;
                        pick_date.setText(year + "/" + month + "/" + day);
                        final int finalmonth = month;

                        btn_confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.putExtra("s_year",year);
                                intent.putExtra("s_month", finalmonth);
                                intent.putExtra("s_day",day);
                                finish();
                            }
                        });


                    }

                });


    }
}
