package polis.polisappen;
import android.content.Context;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

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
        //adda auth?
        client.get(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }


    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return API_URL + relativeUrl;
    }

    public static AsyncHttpResponseHandler getDefaultHandler(final HttpResponseNotifyable listener){
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                listener.notifyAboutResponse(response.toString());
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Do something with the response
                listener.notifyAboutResponse(response.toString());
            }
            /*
            @Override public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                listener.notifyAboutResponse(errorResponse.toString());
            }
            @Override public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse){
                listener.notifyAboutResponse(errorResponse.toString());
            }
            */
            @Override public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){
                listener.notifyAboutResponse(responseString);
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
}
