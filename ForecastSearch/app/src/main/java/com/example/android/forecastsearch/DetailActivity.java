package com.example.android.forecastsearch;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    Fragment fr;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        final String json = intent.getExtras().getString("Data");
        final String unit = intent.getExtras().getString("Degree");
        final String city = intent.getExtras().getString("City");
        final String state = intent.getExtras().getString("State");
        final int offset = intent.getExtras().getInt("Offset");

        final Bundle bundle = new Bundle();
        bundle.putString("Data", json);
        bundle.putString("Degree", unit);
        bundle.putString("City", city);
        bundle.putString("State", state);
        bundle.putInt("Offset", offset);

        TextView title = (TextView) findViewById(R.id.more_title);
        title.setText("More Details in " + city + ", " + state);

        final Button btn24 = (Button) findViewById(R.id.btn24);
        final Button btn7 = (Button) findViewById(R.id.btn7);


        class btn24action implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                btn24.setBackgroundColor(getResources().getColor(R.color.yellow2));
                btn7.setBackgroundColor(Color.WHITE);
                fr = new HourFragment();
                fr.setArguments(bundle);
                fm = getFragmentManager();
                FragmentTransaction tr = fm.beginTransaction();
                tr.replace(R.id.fragment_empty, fr);
                tr.commit();
            }
        }
        class btn7action implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                btn7.setBackgroundColor(getResources().getColor(R.color.yellow2));
                btn24.setBackgroundColor(Color.WHITE);
                fr = new DayFragment();
                fr.setArguments(bundle);
                fm = getFragmentManager();
                FragmentTransaction tr = fm.beginTransaction();
                tr.replace(R.id.fragment_empty, fr);
                tr.commit();

            }
        }
        btn24.setOnClickListener(new btn24action());
        btn7.setOnClickListener(new btn7action());
        btn24.performClick();
    }
}
