package polis.polisappen;
import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class RESTApiServer {
    private static final String API_URL = "http://itkand-1-1.tddd82-2018.ida.liu.se/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void get(Context context, String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        StringEntity entity = new StringEntity(params.toString(), "UTF-8");
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/json");
        try{
            if(params.get("token") != null){
                client.addHeader("Authorization", "Bearer" + params.get("token"));
            }
        }catch (Exception e){
            //do nothing
        }
        //adda auth?
        client.get(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }


    public static void post(Context context, String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        StringEntity entity = new StringEntity(params.toString(), "UTF-8");
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/json");
        try{
            if(params.get("token") != null){
                client.addHeader("Authorization", "Bearer " + params.get("token"));
            }
        }catch (Exception e){
            //do nothing
        }
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return API_URL + relativeUrl;
    }

    public static AsyncHttpResponseHandler getDefaultHandler(final HttpResponseNotifyable listener){
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Called if JSONObject was successfully returned

                listener.notifyAboutResponse(RESTApiServer.parseJSON(response));
            }
            /*@Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Do something with the response

                listener.notifyAboutResponse(response.toString());
            }
            */
            @Override public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                // Called if statuscode was 40x
                listener.notifyAboutResponse(RESTApiServer.parseJSON(errorResponse));
            }
            /*@Override public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse){

                listener.notifyAboutResponse(errorResponse.toString());
            }
            */
            @Override public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){
                // denna metoden kallas om INTE en JSON returneras
                System.out.println("anslutning till server");
                System.out.println("statuscode" + statusCode);
                System.out.println("server sa:" + responseString);

            }
            /*
            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
            */
                /*
            @Override
            public void onStart() {
                // called before request is started
            }
            */
        };
    }

    public static HashMap<String,String> parseJSON(JSONObject object) {
        Iterator<String> jsonItr = object.keys();
        HashMap<String, String> result = new HashMap<String,String>();
        while(jsonItr.hasNext()){
            String key = jsonItr.next();
            try{
                result.put(key, object.getString(key));
            }catch (JSONException e){
                return null;
            }
        }
        return result;
    }
}
