package polis.polisappen;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RESTApiServer {
    private static final String API_URL_SERVER_1 = "http://itkand-1-1.tddd82-2018.ida.liu.se/";
    private static final String API_URL_SERVER_2 = "http://itkand-1-2.tddd82-2018.ida.liu.se/";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_URL = "/logout";
    private static final String SECRET_URL = "/secret";
    private static final String COORD_URL = "/coord";
    private static final String SETCOORD_URL = "/setCoord";
    private static String lastUsedSubURL;
    private static JSONObject lastUsedJSONObject;
    private static Context lastUsedContext;
    private static AsyncHttpResponseHandler lastUsedAsyncHttpResponseHandler;
    private static boolean lastUsedIsGetRequest;
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static final String TAG = "RESTApiServer";

    private static void get(Context context, String url, JSONObject params, AsyncHttpResponseHandler responseHandler, boolean sendToBackupServer) {
        StringEntity entity = new StringEntity(params.toString(), "UTF-8");
        addAuthHeadersToClient(params);
        setLastUsedParameters(url, params, context,responseHandler, true);
        client.get(context, getAbsoluteUrl(url, sendToBackupServer), entity, "application/json", responseHandler);
    }

    private static void resendRequestToBackupServer(){
        if (lastUsedIsGetRequest){
            Log.w(TAG, "resending GET Request");
            get(lastUsedContext, lastUsedSubURL, lastUsedJSONObject, lastUsedAsyncHttpResponseHandler, true);
        }else {
            Log.w(TAG, "resending POST Request");
            post(lastUsedContext, lastUsedSubURL, lastUsedJSONObject, lastUsedAsyncHttpResponseHandler, true);
        }
    }

    private static void setLastUsedParameters(String url, JSONObject jsonObject, Context context, AsyncHttpResponseHandler asyncHttpResponseHandler, boolean isGetRequest){
        lastUsedSubURL = url;
        lastUsedJSONObject = jsonObject;
        lastUsedContext = context;
        lastUsedAsyncHttpResponseHandler = asyncHttpResponseHandler;
        lastUsedIsGetRequest = isGetRequest;
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
            post(context,LOGIN_URL,jsonParams, RESTApiServer.getDefaultHandler(listener), false);
        }
        catch (Exception e){
            Toast.makeText(context, "Exception..", Toast.LENGTH_LONG).show();
            return;
        }
    }
    public static void logout(Context context, HttpResponseNotifyable listener){
        JSONObject params = getAuthParams(context);
        post(context,LOGOUT_URL,params, RESTApiServer.getDefaultHandler(listener), false);
    }

    public static void getCoord(Context context, HttpResponseNotifyable listener){
        JSONObject params = getAuthParams(context);
        get(context,COORD_URL,params, RESTApiServer.getDefaultHandler(listener), false);
    }



    public static void setCoord(Context context, HttpResponseNotifyable listener, HashMap<String, String> coordData){
        String reportText = coordData.get("report_text");
        String sign_key = getUsername(context) + getUserAuthToken(context);
        try {
            JSONObject jsonParams = new JSONObject();
            System.out.println("Sending to server...");
            jsonParams.put("latitude", coordData.get("latitude"));
            jsonParams.put("longitude", coordData.get("longitude"));
            jsonParams.put("type", coordData.get("type"));
            jsonParams.put("report_text", reportText);
            System.out.println("checksum: " + hashSHA256(getJSONToStringSetCoord(jsonParams),sign_key));
            jsonParams.put("checksum",hashSHA256(getJSONToStringSetCoord(jsonParams),sign_key));
            System.out.println("latitude: " + coordData.get("latitude"));
            System.out.println("longitude: " + coordData.get("longitude"));
            System.out.println("type: " + coordData.get("type"));
            System.out.println("report_text: " + reportText);
            post(context,SETCOORD_URL,addAuthParams(context,jsonParams), RESTApiServer.getDefaultHandler(listener), false);
        }
        catch (Exception e){
            Toast.makeText(context, "Exception..", Toast.LENGTH_LONG).show();
            return;
        }
    }

    protected static String getUsername(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(AccountManager.USER_AUTH_NAME,"");
    }
    protected static String getUserAuthToken(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(AccountManager.USER_AUTH_TOKEN,null);
    }

    private static String hashSHA256(String toEncrypt, String key){
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return encodeHexString(sha256_HMAC.doFinal(toEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e){
            return null;
        }
    }
    //Egentligen vill vi använda Hex.encodeHexString men det fungerar inte pga
    //apache kan inte döpa sina paket (nån namn krock)....
    //OBS: DENNA KOD SNODD FRÅN (684 upvotes):
    // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    private static String encodeHexString(byte[] bytes){
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    private static String getJSONToString(JSONObject object){
        String finalString = "";
        Iterator<String> keys = object.keys();
        while(keys.hasNext()){
            try{
                String key = keys.next();
                String value = object.getString(key);
                finalString = finalString + value;
            }
            catch (Exception e){
                return null;
            }
        }
        return finalString;
    }
    //This method sucks, we need another way....
    private static String getJSONToStringSetCoord(JSONObject object){
        String result = "";
        try {
            result += object.getString("latitude");
            result += object.getString("longitude");
            result += object.getString("type");
            result += object.getString("report_text");
            return result;
        }
        catch (Exception e){
            return null;
        }

    }

    public static void getSecret(Context context, HttpResponseNotifyable listener){
        JSONObject params = getAuthParams(context);
        get(context,SECRET_URL,params, RESTApiServer.getDefaultHandler(listener), false);
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


    private static void post(Context context, String url, JSONObject params, AsyncHttpResponseHandler responseHandler, boolean sendToBackupServer) {
        StringEntity entity = new StringEntity(params.toString(), "UTF-8");
        addAuthHeadersToClient(params);
        setLastUsedParameters(url, params,context,responseHandler, false);
        client.post(context, getAbsoluteUrl(url, sendToBackupServer), entity, "application/json", responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl, boolean isBackupserver) {
        if(isBackupserver){
            Log.w(TAG, "Using Backup Server");
            return API_URL_SERVER_2 + relativeUrl;
        }else {
            Log.w(TAG, "Using Primary Server");
            return API_URL_SERVER_1 + relativeUrl;
        }

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
                //throws exception if internet not available
                resendRequestToBackupServer();
                try {
                    listener.notifyAboutResponse(RESTApiServer.parseJSON(errorResponse));
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
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

