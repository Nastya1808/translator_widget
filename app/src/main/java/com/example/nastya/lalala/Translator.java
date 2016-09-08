package com.example.nastya.lalala;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by nastya on 14.05.16.
 */
public class Translator {

    private Context context;

    public Translator(Context context) {
        this.context = context;
    }

    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    public String translate(String text, String from, String to) {
        String translated = "";
        try {
            URL url = new URL("https://translate.yandex.net/api/v1.5/tr/translate?key=trnsl.1.1.20160328T171441Z.2d5e1574bb839d6c.6ef1d8ad22b67e69685aa31ef44ea339b17fa3ce" +
                    "&text="+text+"&lang="+from+"-"+to);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            InputStream in = conn.getInputStream();

            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            translated = stringBuilder.toString();

            in.close();
            conn.disconnect();
        }
        catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        translated = getPhrase(translated);
        return translated;
    }

    private String getPhrase(String str){
        int start = str.indexOf("<text>");
        int end = str.indexOf("</text>");

        return str.substring(start+6, end);
    }
}

