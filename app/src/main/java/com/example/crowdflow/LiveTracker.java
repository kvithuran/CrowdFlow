package com.example.crowdflow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LiveTracker extends AppCompatActivity {
    private Button buttonBack, buttonRefresh;
    private TextView textViewCount;// TextView to display the count value
    private TextView textViewTraffic;
    private static final String ARDUINO_IP = "172.20.10.5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tracker);

        buttonBack = findViewById(R.id.buttonBack);
        textViewCount = findViewById(R.id.count); // Assume a TextView with this ID exists in your XML layout
        textViewTraffic = findViewById(R.id.traffic);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMain();
            }
        });

        // Fetch the count value when the activity is created
        buttonRefresh = findViewById(R.id.buttonRefresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchPopulationData();
            }
        });

    }


    private void fetchPopulationData() {
        new FetchPopulationTask().execute(ARDUINO_IP);
    }

    private class FetchPopulationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection connection = null;
            try {
                // Prepend "http://" dynamically to the provided IP
                URL url = new URL("http://" + urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder content = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    response = content.toString();
                } else {
                    response = "Error: " + responseCode;
                }
            } catch (Exception e) {
                response = "Connection failed: " + e.getMessage();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Error") || result.startsWith("Connection failed")) {
                Toast.makeText(LiveTracker.this, result, Toast.LENGTH_SHORT).show();
            } else {
                textViewCount.setText("Live Count: " + result);
            }
            if(Integer.valueOf(result)>10){
                textViewTraffic.setText("Traffic: High");

            }
            else{
                textViewTraffic.setText("Traffic: Low");
            }
        }
    }

    public void openMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
