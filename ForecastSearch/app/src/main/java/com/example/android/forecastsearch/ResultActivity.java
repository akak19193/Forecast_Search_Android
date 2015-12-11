package com.example.android.forecastsearch;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.*;
import com.facebook.FacebookSdk;

import java.util.Calendar;
import java.util.Date;


public class ResultActivity extends Activity {

    private CallbackManager callbackManager;

    // Create a function to handle 2 decimal places
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    // Create a function to transfer timestamp to HH:MM AM/PM format
    // Timezone is handled by offset
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
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        final String json = intent.getExtras().getString("Result");

        final String unit = intent.getExtras().getString("Degree");
        String degU, winU, visU;
        if (unit.equals("Fahrenheit")) {
            degU = "℉";
            winU = "mph";
            visU = "mi";
        } else {
            degU = "℃";
            winU = "m/s";
            visU = "km";
        }

        final String city = intent.getExtras().getString("City");
        final String state = intent.getExtras().getString("State");

        // Get views
        Button morebtn = (Button) findViewById(R.id.more);
        Button viewmapbtn = (Button) findViewById(R.id.mapbtn);
        ImageView img = (ImageView) findViewById(R.id.result_img);
        TextView title = (TextView) findViewById(R.id.result_title);
        TextView degree = (TextView) findViewById(R.id.result_degree);
        TextView temp = (TextView) findViewById(R.id.result_temp);
        TextView lh = (TextView) findViewById(R.id.result_lh);
        TextView prec = (TextView) findViewById(R.id.result_prec);
        TextView chan = (TextView) findViewById(R.id.result_chan);
        TextView wind = (TextView) findViewById(R.id.result_wind);
        TextView dewp = (TextView) findViewById(R.id.result_dewp);
        TextView humi = (TextView) findViewById(R.id.result_humi);
        TextView visi = (TextView) findViewById(R.id.result_visi);
        TextView sunr = (TextView) findViewById(R.id.result_sunr);
        TextView suns = (TextView) findViewById(R.id.result_suns);

        // Parse Json. Some names of imgs are changed to be identical with icon value ('-' is '_' now)
        JsonElement jelement = new JsonParser().parse(json);
        JsonObject jobject = jelement.getAsJsonObject();
        final String lat = jobject.get("latitude").toString();
        final String lng = jobject.get("longitude").toString();
        final int offset = Integer.parseInt(jobject.get("offset").toString());
        JsonObject currently = jobject.getAsJsonObject("currently");
        JsonObject daily = jobject.getAsJsonObject("daily");
        JsonArray dailydata = daily.getAsJsonArray("data");
        JsonObject day[] = new JsonObject[8];
        for (int i = 0; i < 8; i++) {
            day[i] = dailydata.get(i).getAsJsonObject();
        }

        // Set img sources
        String uri = "@drawable/" + currently.get("icon").toString().replace("\"", "");
        uri = uri.replace("-", "_");
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        img.setImageResource(imageResource);
        // Set text
        title.setText(currently.get("summary").toString().replace("\"", "") + " in " + city + ", " + state);
        temp.setText(Math.round(Double.parseDouble(currently.get("temperature").toString())) + "");
        degree.setText(degU);
        lh.setText("L: " + Math.round(Double.parseDouble(day[0].get("temperatureMin").toString())) + "° | H: " + Math.round(Double.parseDouble(day[0].get("temperatureMax").toString())) + "°");
        String predisplay;
        double preval = Double.parseDouble(currently.get("precipIntensity").toString());
        if (unit.equals("Fahrenheit")) {
            if (preval < 0.002) {
                predisplay = "None";
            } else if (preval < 0.017) {
                predisplay = "Very Light";
            } else if (preval < 0.1) {
                predisplay = "Light";
            } else if (preval < 0.4) {
                predisplay = "Moderate";
            } else {
                predisplay = "Heavy";
            }
        } else {
            if (preval < 0.0508) {
                predisplay = "None";
            } else if (preval < 0.4318) {
                predisplay = "Very Light";
            } else if (preval < 2.54) {
                predisplay = "Light";
            } else if (preval < 10.16) {
                predisplay = "Moderate";
            } else {
                predisplay = "Heavy";
            }
        }
        prec.setText(predisplay);
        chan.setText(Math.round((Double.parseDouble(currently.get("precipProbability").toString())) * 100) + "%");
        wind.setText(round(Double.parseDouble(currently.get("windSpeed").toString()), 2) + " " + winU);
        dewp.setText(round(Double.parseDouble(currently.get("dewPoint").toString()), 2) + " " + degU);
        humi.setText(Math.round((Double.parseDouble(currently.get("humidity").toString())) * 100) + "%");
        visi.setText(round(Double.parseDouble(currently.get("visibility").toString()), 2) + " " + visU);

        sunr.setText(stamptohhmm(day[0].get("sunriseTime").toString(), offset));
        suns.setText(stamptohhmm(day[0].get("sunsetTime").toString(), offset));

        class viewDetail implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(ResultActivity.this, DetailActivity.class);
                intent1.putExtra("Data", json);
                intent1.putExtra("Degree", unit);
                intent1.putExtra("City", city);
                intent1.putExtra("State", state);
                intent1.putExtra("Offset", offset);
                startActivity(intent1);
            }
        }
        morebtn.setOnClickListener(new viewDetail());

        class viewMap implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(ResultActivity.this, MapActivity.class);
                intent2.putExtra("Lat", lat);
                intent2.putExtra("Lng", lng);
                startActivity(intent2);
            }
        }
        viewmapbtn.setOnClickListener(new viewMap());

        Button fbbtn = (Button) findViewById(R.id.fb);


        final String fbtitle = "Current Weather in " + city + ", " + state;
        final String fbimg;
        String icon = currently.get("icon").toString().replace("\"", "");
        switch (icon) {
            case "clear-day":
                fbimg = "http://cs-server.usc.edu:45678/hw/hw8/images/clear.png";
                break;
            case "clear-night":
                fbimg = "http://cs-server.usc.edu:45678/hw/hw8/images/clear_night.png";
                break;
            case "partly-cloudy-day":
                fbimg = "http://cs-server.usc.edu:45678/hw/hw8/images/cloud_day.png";
                break;
            case "partly-cloudy-night":
                fbimg = "http://cs-server.usc.edu:45678/hw/hw8/images/cloud_night.png";
                break;
            default:
                fbimg = "http://cs-server.usc.edu:45678/hw/hw8/images/" + icon + ".png";
        }
        final String fbdes = currently.get("summary").toString().replace("\"","") + ", " + Math.round(Double.parseDouble(currently.get("temperature").toString())) + degU;
        // Below is a simpler way to post, but achieving callback is not possible in that way
        /*
        final ShareButton share = (ShareButton)findViewById(R.id.share);
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://forecast.io"))
                .setContentTitle(fbtitle)
                .setImageUrl(Uri.parse(fbimg))
                .setRef("forecast.io")
                .setContentDescription(fbdes)
                .build();
        share.setShareContent(content);*/

        // FB function
        class shareContent implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                FacebookDialog shareDialog = new ShareDialog(ResultActivity.this);
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("https://forecast.io"))
                            .setContentTitle(fbtitle)
                            .setImageUrl(Uri.parse(fbimg))
                            .setRef("forecast.io")
                            .setContentDescription(fbdes)
                            .build();
                    shareDialog.show(linkContent);
                    callbackManager = CallbackManager.Factory.create();
                    shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                        @Override
                        public void onSuccess(Sharer.Result result) {
                            Toast.makeText(ResultActivity.this, "Facebook Post Successful", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(ResultActivity.this, "Post Cancelled", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(FacebookException error) {
                            // TODO Auto-generated method stub
                            System.out.println("onError");
                        }

                    });
                }
            }
        }
        fbbtn.setOnClickListener(new shareContent());
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
