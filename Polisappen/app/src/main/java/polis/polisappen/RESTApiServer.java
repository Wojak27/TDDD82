package polis.polisappen;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Iterator;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RESTApiServer {
    private static final String API_URL_SERVER_1 = "https://itkand-1-1.tddd82-2018.ida.liu.se/";
    private static final String API_URL_SERVER_2 = "https://itkand-1-2.tddd82-2018.ida.liu.se/";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_URL = "/logout";
    private static final String SECRET_URL = "/secret";
    private static final String COORD_URL = "/coord";
    private static final String COORD_NOPOS_URL = "/coordNoPos";
    private static final String SETCOORD_URL = "/setCoord";
    private static final String VERIFY_TOKEN = "/verifyToken";
    private static String lastUsedSubURL;
    private static JSONObject lastUsedJSONObject;
    private static Context lastUsedContext;
    private static AsyncHttpResponseHandler lastUsedAsyncHttpResponseHandler;
    private static boolean lastUsedIsGetRequest;
    private static final String GET_CONTACTS_URL = "/users";
    private static final String GET_MESSAGES_URL = "/chatMessages";
    private static final String SEND_CHAT_MSG_URL = "/sendMessage";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static long timeSinceLastRequest = 0;

    private static void get(Context context, String url, JSONObject params, AsyncHttpResponseHandler responseHandler, boolean sendToBackupServer) {
        StringEntity entity = new StringEntity(params.toString(), "UTF-8");
        params = addAuthParams(context,params);
        addAuthHeadersToClient(params);
        setLastUsedParameters(url, params, context,responseHandler, true);
        client.get(context, getAbsoluteUrl(url, sendToBackupServer), entity, "application/json", responseHandler);
    }

    private static void resendRequestToBackupServer(){
        if (lastUsedIsGetRequest){
            get(lastUsedContext, lastUsedSubURL, lastUsedJSONObject, lastUsedAsyncHttpResponseHandler, true);
        }else {
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

    public static void sendChatMsg(Context context, HttpResponseNotifyable listener,String msg, String receiver_id){
        JSONObject params = new JSONObject();
        String sign_key = getUsername(context) + getUserAuthToken(context);
        try {
            params.put("receiver_id",receiver_id);
            System.out.println("reciever_id " +receiver_id);
            params.put("message",msg);
            System.out.println("message "+msg);
            System.out.println("token " + getUserAuthToken(context));
            params.put("checksum", hashSHA256(getJSONToStringSendMsg(params),sign_key));
            System.out.println("Checksum " + hashSHA256(getJSONToStringSendMsg(params),sign_key));
            post(context,SEND_CHAT_MSG_URL,params,RESTApiServer.getDefaultHandler(listener),false);
        } catch (JSONException e) {
            //TODO gör en textview
            //e.printStackTrace();
        }
    }
    public static void sendManipulatedChatMsg(Context context, HttpResponseNotifyable listener,String msg, String receiver_id){
        JSONObject params = new JSONObject();
        String sign_key = getUsername(context) + getUserAuthToken(context);
        try {
            params.put("receiver_id",receiver_id);
            params.put("message",msg);
            params.put("checksum", hashSHA256(getJSONToStringSendMsg(params),sign_key));
            params.put("message", msg + "manipulated");
            post(context,SEND_CHAT_MSG_URL,params,RESTApiServer.getDefaultHandler(listener),false);
        } catch (JSONException e) {
            //TODO gör en textview
            //e.printStackTrace();
        }
    }

    public static void validateToken(Context context, HttpResponseNotifyable listener){
        JSONObject params = new JSONObject();
        System.out.println(getUserAuthToken(context));
        post(context,VERIFY_TOKEN,params,RESTApiServer.getDefaultHandler(listener),false);
    }

    public static void login(Context context, HttpResponseNotifyable listener,String nfcCardNumber, String pin){
        try {
            System.out.println("ATTEMPTING TO LOGIN");
            timeSinceLastRequest = System.currentTimeMillis();
            client.setConnectTimeout(1000);//1000 is the lowest possible value according to API
            client.setMaxRetriesAndTimeout(0,0);
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

    public static void getCoord(Context context, HttpResponseNotifyable listener, LatLng position){
        client.setConnectTimeout(1000);//1000 is the lowest possible value according to API
        client.setMaxRetriesAndTimeout(0,0);
        JSONObject params = getAuthParams(context);
        Log.w("position", position.toString());
        if(position != null){
            Log.w("RESTApi", "Has position");
            try{
                double latitude = position.latitude;
                double longitude = position.longitude;
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                get(context,COORD_URL,params, RESTApiServer.getDefaultHandler(listener), false);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }else{ // no position given
            get(context,COORD_NOPOS_URL,params, RESTApiServer.getDefaultHandler(listener), false);
        }

    }

    public static void getContacts(Context context, HttpResponseNotifyable listener){
        JSONObject params = getAuthParams(context);
        get(context,GET_CONTACTS_URL,params, RESTApiServer.getDefaultHandler(listener),false);
    }
    public static void getMessages(Context context, HttpResponseNotifyable listener, Contact chatbuddy) {
        JSONObject params = new JSONObject();
        client.setConnectTimeout(1000);//1000 is the lowest possible value according to API
        client.setMaxRetriesAndTimeout(0,0);
        try {
            System.out.println("catbuddy " + chatbuddy.getId());
            params.put("chat_partner_id",chatbuddy.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        get(context, GET_MESSAGES_URL, params, RESTApiServer.getDefaultHandler(listener),false);
    }

    public static void setManipluatedCoord(Context context, HttpResponseNotifyable listener, HashMap<String, String> coordData){
        String reportText = coordData.get("report_text");
        String sign_key = getUsername(context) + getUserAuthToken(context);
        try {
            JSONObject jsonParams = new JSONObject();
            System.out.println("Sending to server...");
            jsonParams.put("latitude", coordData.get("latitude"));
            jsonParams.put("longitude", coordData.get("longitude"));
            jsonParams.put("type", coordData.get("type"));
            jsonParams.put("report_text", reportText);
            jsonParams.put("checksum",hashSHA256(getJSONToStringSetCoord(jsonParams),sign_key));
            jsonParams.put("type", coordData.get("type")+"manipulated");
            System.out.println("latitude: " + coordData.get("latitude"));
            System.out.println("longitude: " + coordData.get("longitude"));
            System.out.println("type: " + coordData.get("type"));
            System.out.println("report_text: " + reportText);
            System.out.println("checksum: " + hashSHA256(getJSONToStringSetCoord(jsonParams),sign_key));
            System.out.println("token: " +  getUserAuthToken(context));
            post(context,SETCOORD_URL,addAuthParams(context,jsonParams), RESTApiServer.getDefaultHandler(listener),false);
        }
        catch (Exception e){
            //TODO gör om till en textview
            //Toast.makeText(context, "Exception..", Toast.LENGTH_LONG).show();
            return;
        }
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
            //TODO gör om till en textview
            //Toast.makeText(context, "Exception..", Toast.LENGTH_LONG).show();
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
    private static String getJSONToStringSendMsg(JSONObject object){
        String resultat = "";
        try{
            resultat += object.getString("receiver_id");
            resultat += object.getString("message");
            return resultat;
        }
        catch (Exception e){
            return null;
        }
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
            if (token == null){
                token = "";
            }
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
        addAuthParams(context,params);
        addAuthHeadersToClient(params);
        setLastUsedParameters(url, params,context,responseHandler, false);
        client.post(context, getAbsoluteUrl(url, sendToBackupServer), entity, "application/json", responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl, boolean isBackupserver) {
        if(isBackupserver){
            return API_URL_SERVER_2 + relativeUrl;
        }else {
            return API_URL_SERVER_1 + relativeUrl;
        }

    }

    private static AsyncHttpResponseHandler getDefaultHandler(final HttpResponseNotifyable listener){
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Called if JSONObject was successfully returned
                System.out.println("REQUEST SUCCESSFULL!");
                System.out.println("ELLAPSED TIME: " + (System.currentTimeMillis() - timeSinceLastRequest));
                listener.notifyAboutResponse(RESTApiServer.parseJSON(response));
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Do something with the response
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
                if(errorResponse == null){
                    System.out.println("onFailure called, attempting backupserver");
                    System.out.println(statusCode);
                    resendRequestToBackupServer();
                    return;
                }
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