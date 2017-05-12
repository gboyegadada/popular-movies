package com.example.android.popularmovies.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Gboyega.Dada on 5/2/2017.
 */

public class Fetch {
    public static String get(String stringUrl) {
        String stringResponse = "UNDEFINED";
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();

            String inputString;
            while ((inputString = bufferedReader.readLine()) != null) {
                builder.append(inputString);
            }
            stringResponse = builder.toString();
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringResponse;
    }
}
