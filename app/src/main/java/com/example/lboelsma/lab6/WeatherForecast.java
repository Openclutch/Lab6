package com.example.lboelsma.lab6;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.in;

public class WeatherForecast extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        // make the progress bar visible onCreate()
        ProgressBar fetchProgress = (ProgressBar) (findViewById(R.id.progressBar));
        fetchProgress.setVisibility(View.VISIBLE);


    }

    private class ForecastQuery extends AsyncTask<String, Integer, String> {

        private String tempMin;
        private String tempMax;
        private String tempCurrent;
        private Bitmap bitmap;

        @Override
        protected String doInBackground(String ...args) {
            String url = ("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&" +
                    "APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric");

            try {
                parse(downloadUrl(url));
            } catch(XmlPullParserException xml) {
                xml.printStackTrace();
            } catch(IOException io) {
                io.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        // Given a string representation of a URL, sets up a connection and gets
        // an input stream.
        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            return conn.getInputStream();
        }

        // parse the downloadUrl
        public List parse(InputStream in) throws XmlPullParserException, IOException {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
                return readFeed(parser);
            } finally {
                in.close();
            }
        }

        private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
            List entries = new ArrayList();

            parser.require(XmlPullParser.START_TAG, null, "feed");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                // Starts by looking for the entry tag
                if (name.equals("entry")) {
                    entries.add(readEntry(parser));
                } else {
                    skip(parser);
                }
            }
            return entries;
        }


    }
}
