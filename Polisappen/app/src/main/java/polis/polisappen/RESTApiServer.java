package polis.polisappen;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_URL = "/logout";
    private static final String SECRET_URL = "/secret";
    private static final String COORD_URL = "/coord";
    private static final String SETCOORD_URL = "/setCoord";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static void get(Context context, String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        StringEntity entity = new StringEntity(params.toString(), "UTF-8");
        addAuthHeadersToClient(params);
        client.get(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    private static void addAuthHeadersToClient(JSONObject params){
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/json");
        try{
            if(params.get("token") != null){
                client.addHeader("Authorization", "Bearer " + params.get("token"));
            }
        }catch (Exception e){
            //do nothing
        }
    }

    public static void login(Context context, HttpResponseNotifyable listener,String nfcCardNumber, String pin){
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("id", nfcCardNumber);
            jsonParams.put("password", pin);
            post(context,LOGIN_URL,jsonParams, RESTApiServer.getDefaultHandler(listener));
        }
        catch (Exception e){
            Toast.makeText(context, "Exception..", Toast.LENGTH_LONG).show();
            return;
        }
    }
    public static void logout(Context context, HttpResponseNotifyable listener){
        JSONObject params = getAuthParams(context);
        post(context,LOGOUT_URL,params, RESTApiServer.getDefaultHandler(listener));
    }

    public static void getCoord(Context context, HttpResponseNotifyable listener){
        JSONObject params = getAuthParams(context);
        get(context,COORD_URL,params, RESTApiServer.getDefaultHandler(listener));
    }

    public static void setCoord(Context context, HttpResponseNotifyable listener, HashMap<String, String> coordData){
        double latitude = Double.parseDouble(coordData.get("latitude"));
        double longitude = Double.parseDouble(coordData.get("longitude"));
        int type = Integer.parseInt(coordData.get("type"));
        String reportText = coordData.get("report_text");
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("latitude", latitude);
            jsonParams.put("longitude", longitude);
            jsonParams.put("type", type);
            jsonParams.put("report_text", reportText);
            post(context,SETCOORD_URL,addAuthParams(context,jsonParams), RESTApiServer.getDefaultHandler(listener));
        }
        catch (Exception e){
            Toast.makeText(context, "Exception..", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public static void getSecret(Context context, HttpResponseNotifyable listener){
        JSONObject params = getAuthParams(context);
        get(context,SECRET_URL,params, RESTApiServer.getDefaultHandler(listener));
    }

    private static JSONObject addAuthParams(Context context, JSONObject params){
        JSONObject authParams = getAuthParams(context);
        Iterator<String> authKeys = authParams.keys();
        while (authKeys.hasNext()){
            try{
                String key = authKeys.next();
                String value = authParams.getString(key);
                params.put(key,value);
            }catch (Exception e){
                return null;
            }
        }
        return params;
    }

    private static JSONObject getAuthParams(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = preferences.getString(AccountManager.USER_AUTH_TOKEN, null);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("token", token);
            System.out.println("token: " + token);
            return jsonParams;
        }
        catch (Exception e){
            return null;
        }
    }


    private static void post(Context context, String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        StringEntity entity = new StringEntity(params.toString(), "UTF-8");
        addAuthHeadersToClient(params);
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return API_URL + relativeUrl;
    }

    private static AsyncHttpResponseHandler getDefaultHandler(final HttpResponseNotifyable listener){
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Called if JSONObject was successfully returned

                listener.notifyAboutResponse(RESTApiServer.parseJSON(response));
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Do something with the response
                System.out.println("databas");
                try{
                    HashMap<String, HashMap<String, String>> yttreHashMap = new HashMap<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonobject = response.getJSONObject(i);
                        HashMap hashMap = parseJSON(jsonobject);
                        yttreHashMap.put(Integer.toString(i), hashMap);
                    }
                    listener.notifyAboutResponseJSONArray(yttreHashMap);
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

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

    private static HashMap<String,String> parseJSON(JSONObject object) {
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

