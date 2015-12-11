package com.example.android.forecastsearch;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;


public class MapActivity extends Activity {
    Fragment fr;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        final String lat = intent.getExtras().getString("Lat");
        final String lng = intent.getExtras().getString("Lng");
        final Bundle bundle = new Bundle();
        bundle.putString("Lat", lat);
        bundle.putString("Lng", lng);
        fr = new com.example.android.forecastsearch.MapFragment();
        fr.setArguments(bundle);
        fm = getFragmentManager();
        FragmentTransaction tr = fm.beginTransaction();
        tr.add(R.id.mapField, fr);
        tr.commit();
    }
}
