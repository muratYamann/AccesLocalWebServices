package com.yamankod.accesws;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static String TAG ="_main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Log.d("_main", "onCreate: "+sendGet());
        Log.d(TAG, "onCreate: ws call");
        new LongRunningGetIO().execute();


    }

    public static String sendGet() {
        StringBuilder result = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {
            String apiUrl = "http://192.168.1.34:8080/home_info"; // concatenate uri with base url eg: localhost:8080/ + uri
            Log.d("main_", "sendGet: "+apiUrl);
            URL requestUrl = new URL(apiUrl);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.connect(); // no connection is made
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.d("main_", "sendGet line: "+line);
                result.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        Log.d("main_", "sendGet result: "+result.toString());
        return result.toString();
    }



    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];
                n =  in.read(b);
                if (n>0) out.append(new String(b, 0, n));
            }
            return out.toString();
        }


        @Override
        protected String doInBackground(Void... params) {
            Log.d(TAG, "onCreate: ws start");
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("http://192.168.1.34:8080/home_info");
            String text = null;
            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);
                Log.d(TAG, "onCreate: ws response"+response);
                Log.d(TAG, "doInBackground: "+response);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);
            } catch (Exception e) {
                return e.getLocalizedMessage();
            }
            return text;
        }


        protected void onPostExecute(String results) {
            if (results!=null) {
                Log.d(TAG, "onCreate: ws response"+results);
                Log.d(TAG, "onPostExecute: Result"+results);
            }
        }
    }




}
