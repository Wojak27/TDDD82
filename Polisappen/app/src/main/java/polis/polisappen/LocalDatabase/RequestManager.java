package polis.polisappen.LocalDatabase;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;


public class RequestManager extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... strings) {
        URL url;
        HttpURLConnection client = null;
        ArrayList<String> arrayList= new ArrayList<String>();

        try {
            url = new URL("http://itkand-1-1.tddd82-2018.ida.liu.se/users/1w");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");
            client.setRequestProperty("Key", "Value");
            client.setDoOutput(true);
            OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
            //writeStream(outputPost);
            outputPost.flush();
            outputPost.close();

            if (client.getResponseCode() == 200){
                InputStream inputStream = client.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                JsonReader jsonReader = new JsonReader(inputStreamReader);
                jsonReader.beginObject();
                while(jsonReader.hasNext()){
                    String key = jsonReader.nextString();
                    if(key.equals("name")){
                        arrayList.add(jsonReader.nextString());
                    }
                }
                jsonReader.close();

            }

        } catch (Exception error) {
            //Handles an incorrectly entered URL
        } finally {
            if (client != null) // Make sure the connection is not null.

                client.disconnect();
        }
        return "asdasd";
    }
    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(String result) {
        // this is executed on the main thread after the process is over
        // update your UI here

    }
}
