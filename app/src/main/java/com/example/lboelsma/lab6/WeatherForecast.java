package com.example.lboelsma.lab6;

import android.app.Activity;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

        AsyncTask task = new AsyncTask() {

            private String tempMin;
            private String tempMax;
            private String tempCurrent;
            private String weatherIcon;
            private Bitmap weatherIconBitmap;

            @Override
            protected Object doInBackground(Object ...args) {

                try {
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&" +
                            "APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric");

                    // Create connection object
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // Settings for connection
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setRequestMethod("GET");
                    connection.connect();

                    // Actually downloads the data into "in"
                    InputStream in = connection.getInputStream();

                    // Create the Xml parser object
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

                    // Put the connection that has been downloaded into the xml parser
                    parser.setInput(in, null);

                    // Tell the parser to go to the next tag, maybe if statement here?
                    parser.nextTag();

                    int eventType = parser.getEventType();

                    while (eventType != XmlPullParser.END_DOCUMENT) {

                        if (eventType == XmlPullParser.START_TAG) {

                            String name = parser.getName();

                            // <temperature value="-7.02" min="-8" max="-6" unit="metric"/>
                            if (name.equalsIgnoreCase("temperature")) {

                                int n = parser.getAttributeCount();
                                for (int i = 0; i < n; i++) {

                                    String attr = parser.getAttributeName(i);
                                    if (attr.equalsIgnoreCase("value")) {
                                        this.tempCurrent = parser.getAttributeValue(i);
                                        publishProgress(25);
                                        Log.i("testing", "Temperature current:" + this.tempCurrent);
                                    } else if (attr.equalsIgnoreCase("min")) {
                                        this.tempMin = parser.getAttributeValue(i);
                                        publishProgress(50);
                                        Log.i("testing", "Temperature min:" + this.tempMin);
                                    } else if (attr.equalsIgnoreCase("max")) {
                                        this.tempMax = parser.getAttributeValue(i);
                                        publishProgress(75);
                                        Log.i("testing", "Temperature max:" + this.tempMax);
                                    }
                                }
                            }
                            //<weather number="800" value="clear sky" icon="02d"/>
                            else if (name.equalsIgnoreCase("weather")) {
                                int n = parser.getAttributeCount();
                                for (int i = 0; i < n; i++) {

                                    String attr = parser.getAttributeName(i);
                                    if (attr.equalsIgnoreCase("icon")) {
                                        this.weatherIcon = parser.getAttributeValue(i);
                                        Log.i("testing", "icon type:" + this.weatherIcon);
                                    }
                                }
                            }
                        }
                        eventType = parser.next();
                    }
                } catch (Exception e) {
                    return e;
                }

                try {
                    URL url = new URL("http://openweathermap.org/img/w/" + this.weatherIcon + ".png");

                    // Create connection object
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // Settings for connection
                    connection.connect();

                    // Downloads the picture
                    this.weatherIconBitmap = BitmapFactory.decodeStream(connection.getInputStream());

                    publishProgress(100);

                } catch (Exception e) {
                    return e;
                }

                // I'm a noob
                return null;
            }

            @Override
            protected void onProgressUpdate(Object... value){

                //set the visibility of the progress bar to visible
                ProgressBar fetchProgress = (ProgressBar) (findViewById(R.id.progressBar));
                fetchProgress.setVisibility(View.VISIBLE);

                Log.i("testing", "Inside onProgressUpdate.");

                //set the progressBar progress from the "value" being passed to this
                int v = (Integer) value[0];
                fetchProgress.setProgress(v);

            }

            @Override
            protected void onPostExecute(Object result) {

                // Set min text
                TextView textTempMin = (TextView) (findViewById(R.id.textView_minTemp));
                textTempMin.setText("Min: " + this.tempMin);
                Log.i("testing", "setting the min temp as: " + this.tempMin);

                // Set max text
                TextView textTempMax = (TextView) (findViewById(R.id.textView_maxTemp));
                textTempMax.setText("Max: " + this.tempMax);
                Log.i("testing", "setting the max temp as: " + this.tempMax);

                // Set curr text
                TextView textTempCurr = (TextView) (findViewById(R.id.textView_currTemp));
                textTempCurr.setText("Curr: " + this.tempCurrent);
                Log.i("testing", "setting the current temp as: " + this.tempCurrent);

                //update picture for the weather
                ImageView weatherImage = (ImageView) (findViewById(R.id.imageView));
                weatherImage.setImageBitmap(this.weatherIconBitmap);
                Log.i("testing", "setting the weather icon");

                //set visibility of the progress bar to invisible
                ProgressBar fetchProgress = (ProgressBar) (findViewById(R.id.progressBar));
                fetchProgress.setVisibility(View.INVISIBLE);
            }

        };

        // execute launches a new thread
        task.execute();
    }
}
