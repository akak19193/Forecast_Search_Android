package com.example.android.forecastsearch;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Calendar;
import java.util.Date;


public class DayFragment extends Fragment {
    // Create a function to transfer timestamp to Week Month, Day format
    public static String stamptowmd(String stamp, int offset) {
        long timestamplong = Long.parseLong(stamp) * 1000 + (offset+8)*1000*3600;
        Date d = new Date(timestamplong);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        String[] weekarray = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] montharray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int week = c.get(Calendar.DAY_OF_WEEK);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String days = (day < 10) ? "0" + day : day + "";
        return weekarray[week - 1] + ", " + montharray[month] + " " + days;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String unit = getArguments().getString("Degree");
        String json = getArguments().getString("Data");
        final int offset = getArguments().getInt("Offset");
        final String degU;
        if (unit.equals("Fahrenheit")) {
            degU = "℉";
        } else {
            degU = "℃";
        }
        JsonElement jelement = new JsonParser().parse(json);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonObject daily = jobject.getAsJsonObject("daily");
        JsonArray dailydata = daily.getAsJsonArray("data");
        final JsonObject day[] = new JsonObject[7];
        for (int j = 0; j < 7; j++) {
            day[j] = dailydata.get(j + 1).getAsJsonObject();
        }
        View rootview = inflater.inflate(R.layout.fragment_7, container, false);
        LinearLayout cont = (LinearLayout) rootview.findViewById(R.id.days);
        String uri;
        for (int i = 0; i < 7; i++) {
            RelativeLayout myRow = new RelativeLayout(getActivity());
            myRow.setGravity(Gravity.CENTER_VERTICAL);
            switch (i) {
                case 0:
                    myRow.setBackgroundColor(getResources().getColor(R.color.day0));
                    break;
                case 1:
                    myRow.setBackgroundColor(getResources().getColor(R.color.day1));
                    break;
                case 2:
                    myRow.setBackgroundColor(getResources().getColor(R.color.day2));
                    break;
                case 3:
                    myRow.setBackgroundColor(getResources().getColor(R.color.day3));
                    break;
                case 4:
                    myRow.setBackgroundColor(getResources().getColor(R.color.day4));
                    break;
                case 5:
                    myRow.setBackgroundColor(getResources().getColor(R.color.day5));
                    break;
                case 6:
                    myRow.setBackgroundColor(getResources().getColor(R.color.day6));
                    break;
                default:
                    ;
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            TextView myTime = new TextView(getActivity());
            myTime.setText(stamptowmd(day[i].get("time").toString(), offset));
            myTime.setLayoutParams(params);
            myTime.setTextSize(20);
            myTime.setTypeface(null, Typeface.BOLD);
            myRow.addView(myTime);

            ImageView myImg = new ImageView(getActivity());
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            myImg.setLayoutParams(params2);
            uri = "@drawable/" + day[i].get("icon").toString().replace("\"", "");
            uri = uri.replace("-", "_");
            int imageResource = getResources().getIdentifier(uri, null, "com.example.android.forecastsearch");
            myImg.setImageResource(imageResource);
            myImg.getLayoutParams().height = 100;
            myImg.getLayoutParams().width = 100;
            myRow.addView(myImg);

            cont.addView(myRow, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout myNextRow = new LinearLayout(getActivity());
            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params4.setMargins(0, 0, 0, 20);
            myNextRow.setLayoutParams(params4);
            myNextRow.setGravity(Gravity.CENTER_VERTICAL);
            myNextRow.setOrientation(LinearLayout.HORIZONTAL);
            switch (i) {
                case 0:
                    myNextRow.setBackgroundColor(getResources().getColor(R.color.day0));
                    break;
                case 1:
                    myNextRow.setBackgroundColor(getResources().getColor(R.color.day1));
                    break;
                case 2:
                    myNextRow.setBackgroundColor(getResources().getColor(R.color.day2));
                    break;
                case 3:
                    myNextRow.setBackgroundColor(getResources().getColor(R.color.day3));
                    break;
                case 4:
                    myNextRow.setBackgroundColor(getResources().getColor(R.color.day4));
                    break;
                case 5:
                    myNextRow.setBackgroundColor(getResources().getColor(R.color.day5));
                    break;
                case 6:
                    myNextRow.setBackgroundColor(getResources().getColor(R.color.day6));
                    break;
                default:
                    ;
            }

            TextView lh = new TextView(getActivity());
            lh.setText("Min: " + Math.round(Double.parseDouble(day[i].get("temperatureMin").toString())) + degU + " | Max: " + Math.round(Double.parseDouble(day[i].get("temperatureMax").toString())) + degU);
            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lh.setLayoutParams(params3);
            lh.setTextSize(20);
            myNextRow.addView(lh);
            cont.addView(myNextRow);


        }
        return rootview;
    }
}
