package com.example.android.forecastsearch;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Code to set url img as background
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Bitmap myImage = getBitmapFromURL("https://s-media-cache-ak0.pinimg.com/736x/33/a9/f1/33a9f1dc9bbed779f610e6f0da830a05.jpg");
        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.matincontent);
        Drawable dr = new BitmapDrawable(myImage);
        rLayout.setBackgroundDrawable(dr);
        // Code for spinner content display
        Spinner spinner = (Spinner) findViewById(R.id.state);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.state_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Add link to logo img
        ImageView img = (ImageView) findViewById(R.id.logo);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://forecast.io"));
                startActivity(intent);
            }
        });

        // Validation
        Button searchbutton = (Button) findViewById(R.id.button1);
        class validation implements View.OnClickListener {
            @Override
            public void onClick(View v) {

                EditText streetval = (EditText) findViewById(R.id.street);
                EditText cityval = (EditText) findViewById(R.id.city);
                Spinner stateval = (Spinner) findViewById(R.id.state);
                TextView errorval = (TextView) findViewById(R.id.errors);
                RadioButton fff = (RadioButton) findViewById(R.id.radio1);
                String degreeV = "";
                if (fff.isChecked()) {
                    degreeV = "Fahrenheit";
                } else {
                    degreeV = "Celsius";
                }
                String streetV = streetval.getText().toString();
                String cityV = cityval.getText().toString();
                String stateV = stateval.getSelectedItem().toString();
                String errorInfo = "";
                Log.v("tag", streetV);
                if (streetV.trim().length() == 0) {
                    errorInfo = "Please enter a Street.";
                } else if (cityV.trim().length() == 0) {
                    errorInfo = "Please enter a City.";
                } else if (stateV.equals("Select")) {
                    errorInfo = "Please select a State.";
                } else {
                    String quest = "http://liu966sapp-env.elasticbeanstalk.com/?address=" + streetV + "&city=" + cityV + "&state=" + stateV + "&degree=" + degreeV;
                    quest = quest.replace(" ", "+");
                    new awsjson().execute(quest);
                }
                errorval.setText(errorInfo);
            }
        }
        searchbutton.setOnClickListener(new validation());

        // Clear function
        Button clr = (Button) findViewById(R.id.button2);
        class clearInfo implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                EditText streetval = (EditText) findViewById(R.id.street);
                EditText cityval = (EditText) findViewById(R.id.city);
                Spinner stateval = (Spinner) findViewById(R.id.state);
                TextView errorval = (TextView) findViewById(R.id.errors);
                RadioButton radio1 = (RadioButton) findViewById(R.id.radio1);
                RadioButton radio2 = (RadioButton) findViewById(R.id.radio2);
                streetval.setText("");
                cityval.setText("");
                stateval.setSelection(0);
                errorval.setText("");
                radio1.setChecked(true);
                radio2.setChecked(false);
            }
        }
        clr.setOnClickListener(new clearInfo());

    }

    // Function for About button
    public void checkInfo(View v) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    // Async task to make request and fetch json data
    class awsjson extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {
            try {
                URL aURL = new URL(uri[0]);
                HttpURLConnection conn = (HttpURLConnection) aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String webPage = "", data;

                while ((data = reader.readLine()) != null) {
                    webPage += data;
                }
                return webPage;
            } catch (IOException e) {
                Log.d("ERROR!!!", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Intent i = new Intent(MainActivity.this, ResultActivity.class);
                i.putExtra("Result", s);
                RadioButton fff = (RadioButton) findViewById(R.id.radio1);
                String degreeV = "";
                if (fff.isChecked()) {
                    degreeV = "Fahrenheit";
                } else {
                    degreeV = "Celsius";
                }
                i.putExtra("Degree", degreeV);
                EditText streetval = (EditText) findViewById(R.id.street);
                EditText cityval = (EditText) findViewById(R.id.city);
                Spinner stateval = (Spinner) findViewById(R.id.state);
                String streetV = streetval.getText().toString().trim();
                String cityV = cityval.getText().toString().trim();
                cityV = toTitleCase(cityV);
                String stateV = stateval.getSelectedItem().toString();
                i.putExtra("Street", streetV);
                i.putExtra("City", cityV);
                i.putExtra("State", stateV);
                startActivity(i);
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
