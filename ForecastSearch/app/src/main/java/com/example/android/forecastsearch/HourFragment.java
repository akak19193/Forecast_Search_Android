package com.example.android.forecastsearch;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Calendar;
import java.util.Date;


public class HourFragment extends Fragment {

    // Create a function to handle 2 decimal places
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    // Create a function to transfer timestamp to HH:MM AM/PM format
    public static String stamptohhmm(String stamp, int offset) {
        long timestamplong = Long.parseLong(stamp) * 1000 + (offset+8)*1000*3600;
        Date d = new Date(timestamplong);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        String amorpm;
        if (c.get(Calendar.AM_PM) == Calendar.PM) {
            amorpm = "PM";
        } else {
            amorpm = "AM";
        }
        int hour = c.get(Calendar.HOUR);
        int minu = c.get(Calendar.MINUTE);
        String thour = (hour >= 10) ? hour + "" : "0" + hour;
        String tminu = (minu >= 10) ? minu + "" : "0" + minu;
        return thour + ":" + tminu + " " + amorpm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String json = getArguments().getString("Data");
        String unit = getArguments().getString("Degree");
        final int offset = getArguments().getInt("Offset");
        final String degU, winU, visU, preU;
        if (unit.equals("Fahrenheit")) {
            degU = "℉";
            winU = "mph";
            visU = "mi";
            preU = "mb";
        } else {
            degU = "℃";
            winU = "m/s";
            visU = "km";
            preU = "hPa";
        }
        JsonElement jelement = new JsonParser().parse(json);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonObject hourly = jobject.getAsJsonObject("hourly");
        JsonArray hourlydata = hourly.getAsJsonArray("data");
        final JsonObject hour[] = new JsonObject[48];
        for (int j = 0; j < 48; j++) {
            hour[j] = hourlydata.get(j).getAsJsonObject();
        }

        View rootView = inflater.inflate(R.layout.fragment_24, container, false);
        TextView temp = (TextView) rootView.findViewById(R.id.hour_temp);
        temp.setText("Temp(" + degU + ")");

        final LinearLayout myTab = (LinearLayout) rootView.findViewById(R.id.myTab);
        myTab.setGravity(Gravity.CENTER_HORIZONTAL);
        String uri;
        // Click to show or hide next element
        class showOrHide implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                ViewGroup container = (ViewGroup) v.getParent();
                View nextView = container.getChildAt(container.indexOfChild(v) + 1);
                if (nextView.getVisibility() == View.VISIBLE) {
                    nextView.setVisibility(View.GONE);
                } else {
                    nextView.setVisibility(View.VISIBLE);
                }
            }
        }

        for (int i = 0; i < 24; i++) {
            LinearLayout myRow = new LinearLayout(getActivity());
            myRow.setOrientation(LinearLayout.HORIZONTAL);
            if (i % 2 == 0) {
                myRow.setBackgroundColor(Color.GRAY);
            }
            myRow.setGravity(Gravity.CENTER_VERTICAL);
            myRow.setClickable(true);
            myRow.setOnClickListener(new showOrHide());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            TextView myTime = new TextView(getActivity());
            myTime.setText(stamptohhmm(hour[i].get("time").toString(),offset));
            myTime.setLayoutParams(params);
            myTime.setTextSize(20);
            myRow.addView(myTime);

            ImageView myImg = new ImageView(getActivity());
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params2.setMargins(180, 0, 0, 0);
            myImg.setLayoutParams(params2);
            uri = "@drawable/" + hour[i].get("icon").toString().replace("\"", "");
            uri = uri.replace("-", "_");
            int imageResource = getResources().getIdentifier(uri, null, "com.example.android.forecastsearch");
            myImg.setImageResource(imageResource);
            myImg.getLayoutParams().height = 200;
            myImg.getLayoutParams().width = 200;
            myRow.addView(myImg);

            TextView myTem = new TextView(getActivity());
            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params3.setMargins(270, 0, 0, 0);
            myTem.setLayoutParams(params3);
            myTem.setText(Math.round(Double.parseDouble(hour[i].get("temperature").toString())) + "");
            myTem.setTextSize(20);
            myRow.addView(myTem);

            myTab.addView(myRow, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout myNextRow = new LinearLayout(getActivity());
            myNextRow.setGravity(Gravity.CENTER_VERTICAL);
            myNextRow.setOrientation(LinearLayout.HORIZONTAL);
            myNextRow.setBackgroundColor(getResources().getColor(R.color.blue2));
            myNextRow.setVisibility(View.GONE);

            TextView wind = new TextView(getActivity());
            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            wind.setLayoutParams(params4);
            wind.setText("Wind\n" + round(Double.parseDouble(hour[i].get("windSpeed").toString()), 2) + " " + winU);
            wind.setGravity(Gravity.CENTER_HORIZONTAL);
            wind.setTextSize(20);
            myNextRow.addView(wind);

            TextView humidity = new TextView(getActivity());
            LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            humidity.setLayoutParams(params5);
            humidity.setText("Humidity\n" + "  " + Math.round((Double.parseDouble(hour[i].get("humidity").toString())) * 100) + "%");
            humidity.setGravity(Gravity.CENTER_VERTICAL);
            humidity.setTextSize(20);
            myNextRow.addView(humidity);

            TextView visibility = new TextView(getActivity());
            LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            visibility.setLayoutParams(params6);
            visibility.setText("Visibility\n" + round(Double.parseDouble(hour[i].get("visibility").toString()), 2) + " " + visU);
            visibility.setGravity(Gravity.CENTER_VERTICAL);
            visibility.setTextSize(20);
            myNextRow.addView(visibility);

            TextView pressure = new TextView(getActivity());
            LinearLayout.LayoutParams params7 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            visibility.setLayoutParams(params7);
            pressure.setText("Pressure\n" + round(Double.parseDouble(hour[i].get("pressure").toString()), 2) + " " + preU);
            pressure.setGravity(Gravity.CENTER_VERTICAL);
            pressure.setTextSize(20);
            myNextRow.addView(pressure);

            myTab.addView(myNextRow, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        LinearLayout myRow2 = new LinearLayout(getActivity());
        myRow2.setOrientation(LinearLayout.HORIZONTAL);
        myRow2.setBackgroundColor(Color.GRAY);
        myRow2.setGravity(Gravity.CENTER_HORIZONTAL);

        class show48 implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                for (int i = 24; i < 48; i++) {
                    LinearLayout myRow = new LinearLayout(getActivity());
                    myRow.setOrientation(LinearLayout.HORIZONTAL);
                    if (i % 2 == 0) {
                        myRow.setBackgroundColor(Color.GRAY);
                    }
                    myRow.setGravity(Gravity.CENTER_VERTICAL);
                    myRow.setClickable(true);
                    myRow.setOnClickListener(new showOrHide());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

                    TextView myTime = new TextView(getActivity());
                    myTime.setText(stamptohhmm(hour[i].get("time").toString(),offset));
                    myTime.setLayoutParams(params);
                    myTime.setTextSize(20);
                    myRow.addView(myTime);

                    ImageView myImg = new ImageView(getActivity());
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params2.setMargins(180, 0, 0, 0);
                    myImg.setLayoutParams(params2);
                    String uri = "@drawable/" + hour[i].get("icon").toString().replace("\"", "");
                    uri = uri.replace("-", "_");
                    int imageResource = getResources().getIdentifier(uri, null, "com.example.android.forecastsearch");
                    myImg.setImageResource(imageResource);
                    myImg.getLayoutParams().height = 200;
                    myImg.getLayoutParams().width = 200;
                    myRow.addView(myImg);

                    TextView myTem = new TextView(getActivity());
                    LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params3.setMargins(270, 0, 0, 0);
                    myTem.setLayoutParams(params3);
                    myTem.setText(Math.round(Double.parseDouble(hour[i].get("temperature").toString())) + "");
                    myTem.setTextSize(20);
                    myRow.addView(myTem);

                    myTab.addView(myRow, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    LinearLayout myNextRow = new LinearLayout(getActivity());
                    myNextRow.setGravity(Gravity.CENTER_VERTICAL);
                    myNextRow.setOrientation(LinearLayout.HORIZONTAL);
                    myNextRow.setBackgroundColor(getResources().getColor(R.color.blue2));
                    myNextRow.setVisibility(View.GONE);

                    TextView wind = new TextView(getActivity());
                    LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    wind.setLayoutParams(params4);
                    wind.setText("Wind\n" + round(Double.parseDouble(hour[i].get("windSpeed").toString()), 2) + " " + winU);
                    wind.setGravity(Gravity.CENTER_HORIZONTAL);
                    wind.setTextSize(20);
                    myNextRow.addView(wind);

                    TextView humidity = new TextView(getActivity());
                    LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

                    humidity.setLayoutParams(params5);
                    humidity.setText("Humidity\n" + "  " + Math.round((Double.parseDouble(hour[i].get("humidity").toString())) * 100) + "%");
                    humidity.setGravity(Gravity.CENTER_VERTICAL);
                    humidity.setTextSize(20);
                    myNextRow.addView(humidity);

                    TextView visibility = new TextView(getActivity());
                    LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

                    visibility.setLayoutParams(params6);
                    visibility.setText("Visibility\n" + round(Double.parseDouble(hour[i].get("visibility").toString()), 2) + " " + visU);
                    visibility.setGravity(Gravity.CENTER_VERTICAL);
                    visibility.setTextSize(20);
                    myNextRow.addView(visibility);

                    TextView pressure = new TextView(getActivity());
                    LinearLayout.LayoutParams params7 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

                    visibility.setLayoutParams(params7);
                    pressure.setText("Pressure\n" + round(Double.parseDouble(hour[i].get("pressure").toString()), 2) + " " + preU);
                    pressure.setGravity(Gravity.CENTER_VERTICAL);
                    pressure.setTextSize(20);
                    myNextRow.addView(pressure);

                    myTab.addView(myNextRow, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                }
                v.setVisibility(View.GONE);
            }
        }
        Button btn = new Button(getActivity());
        btn.setBackgroundResource(R.drawable.btn);
        LinearLayout.LayoutParams params8 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btn.setLayoutParams(params8);
        btn.getLayoutParams().height = 200;
        btn.getLayoutParams().width = 200;
        btn.setOnClickListener(new show48());
        myRow2.addView(btn);
        myTab.addView(myRow2, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        return rootView;
    }


}
