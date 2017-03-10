package com.example.lboelsma.lab6;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.System.in;

public class WeatherForecast extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        // make the progressBar View.Visible = true?

    }

    private class ForecastQuery extends AsyncTask<String, Integer, String>{

        private String tempMin;
        private String tempMax;
        private String tempCurrent;
        private Bitmap bitmap;

        @Override
        protected String doInBackground(String... params) {
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&" +
                    "APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();

            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(conn.getInputStream();, null);
                parser.nextTag();
                return readFeed(parser);
            } finally {
                in.close();
            }
        }
    }
}
