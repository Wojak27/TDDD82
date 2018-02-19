package polis.polisappen;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RESTApiServer {
    private static final String API_URL = "http://itkand-1-1.tddd82-2018.ida.liu.se/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
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
