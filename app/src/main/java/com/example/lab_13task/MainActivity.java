package com.example.lab_13task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    private Button button1;
    private TextView view1, view2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        button1=(Button)findViewById(R.id.button1);
        view1=(TextView) findViewById(R.id.textView);
        view2=(TextView) findViewById(R.id.textView2);




        load_file();//to initiate the program



        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
fetch_web();//ignore what's loaded and go fetch from web
            }
        });












    }//onceate






    public void store_file() {
        String filename = "myfile";

        String data = view1.getText().toString();
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
            Log.v("storage app", "data saved..");
            view2.setText("Data stored");
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("storage app", "Error: data is not saved");
            view2.setText("Data not stored seccfully");
        }
    }



    public void load_file() {
        String filename = "myfile";

        try {
            FileInputStream inputStream =  openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            inputStream.close();
            view1.setText(sb);
            Log.v("storage app", "data loaded..");
            view2.setText("Data loaded");
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("storage app", "Error: data is not loaded.." + e.getMessage());
            view2.setText("unable to load data");

            fetch_web();//if file didn\t exist fetch from web
        }
    }







    private class DownloadWebpageText extends AsyncTask {
        // arguments are given by execute() method call (defined in the parent): params[0] is the url.
        protected String doInBackground(Object... urls) {
            try {
                String st = downloadUrl((String) urls[0]);
                return st;
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Object result) {
            view1.setText((String)result);
            store_file();
        }
    }








    public void fetch_web() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            DownloadWebpageText task = new DownloadWebpageText();
            task.execute("https://meta.stackoverflow.com/feeds");
            view2.setText("Web Fetch");
            Log.v("WEB", "web fetch");
        } else {
            // display error

        }
    }











    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000); /* milliseconds */
            conn.setConnectTimeout(15000); /* milliseconds */
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("network", "The response is: " + response);
            is = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }






}//main
