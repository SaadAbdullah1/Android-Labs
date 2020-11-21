package com.example.androidlabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WeatherForecast extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        ForecastQuery forecast = new ForecastQuery();
        forecast.execute("https://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric");
    }

    class ForecastQuery extends AsyncTask<String, Integer, String> {

        private String UV, minTemp, maxTemp, currentTemp;
        private Bitmap weatherPic;

        @Override
        protected String doInBackground(String... strings) {
            try {

                //create a URL object of what server to contact:
                URL url = new URL(strings[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                //wait for data:
                InputStream response = urlConnection.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(response, "UTF-8");
                int eventType = parser.getEventType();
                while (eventType
                        != XmlPullParser.END_DOCUMENT) { //LOOPING THROUGH FIRST WEBSITE FOR TEMP AND WEATHER
                    String tagName = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (tagName.equalsIgnoreCase("temperature")) { //GET TEMPERATURE
                                currentTemp = parser.getAttributeValue(null, "value");
                                publishProgress(25);
                                minTemp = parser.getAttributeValue(null, "min");
                                publishProgress(50);
                                maxTemp = parser.getAttributeValue(null, "max");
                                publishProgress(75);
                            } else if (tagName.equalsIgnoreCase("weather")) { //GET WEATHER
                                String iconName = parser.getAttributeValue(null, "icon");
                                Log.i("weather", "Checking for bitmap file: " + iconName + ".png");
                                if (fileExistance(iconName)) {
                                    Log.i("weather", "Found file");
                                    FileInputStream fis = null;
                                    try {
                                        fis = openFileInput(iconName + ".png");
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    weatherPic = BitmapFactory.decodeStream(fis);
                                } else {
                                    Log.i("weather", "Did not find file, downloading it");
                                    weatherPic = null;
                                    URL urlWeatherPic = new URL("https://openweathermap.org/img/wn/" + iconName + ".png");
                                    urlConnection = (HttpURLConnection) urlWeatherPic.openConnection();
                                    urlConnection.connect();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == 200) {
                                        weatherPic = BitmapFactory.decodeStream(urlConnection.getInputStream());
                                        publishProgress(100);
                                        FileOutputStream outputStream = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
                                        weatherPic.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                        outputStream.flush();
                                        outputStream.close();
                                    }

                                }
                            }
                            break;
                        default:
                            break;
                    }
                    eventType = parser.next();
                }

                //GET UV
                URL urlUV = new URL(
                        "https://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389");

                //open the connection
                urlConnection = (HttpURLConnection) urlUV.openConnection();
                urlConnection.connect();
                //wait for data:
                response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONObject jObject = new JSONObject(result);
                double value = jObject.getDouble("value");
                Log.e("weather", "" + value);
                UV = "" + value;

            } catch (Exception e) {
                Log.e("weather", "something went wrong");
                e.printStackTrace();
            }

            return "Done";
        }

        public boolean fileExistance(String fname) {
            File file = getBaseContext().getFileStreamPath(fname + ".png");
            return file.exists();
        }

        @Override
        protected void onProgressUpdate(Integer... args) {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(args[0]);
        }

        @Override
        protected void onPostExecute(String fromDoInBackground) {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);

            TextView currentTempObject = findViewById(R.id.currentTemp);
            String text = getResources().getString(R.string.currtemp) + " " + currentTemp + " celsius";
            currentTempObject.setText(text);

            TextView minTempObject = findViewById(R.id.minTemp);
            text = getResources().getString(R.string.mintemp) + " " + minTemp + " celsius";
            minTempObject.setText(text);

            TextView maxTempObject = findViewById(R.id.maxTemp);
            text = getResources().getString(R.string.maxtemp) + " " + maxTemp + " celsius";
            maxTempObject.setText(text);

            TextView uvRatingObject = findViewById(R.id.UV);
            text = getResources().getString(R.string.UV) + " " + UV;
            uvRatingObject.setText(text);

            ImageView weather = findViewById(R.id.currentWeather);
            weather.setImageBitmap(weatherPic);
        }
    }
}