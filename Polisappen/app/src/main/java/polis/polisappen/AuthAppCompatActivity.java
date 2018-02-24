package polis.polisappen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

public abstract class AuthAppCompatActivity extends AppCompatActivity implements HttpResponseNotifyable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                //check if the broadcast is our desired one
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                    invalidateAuth();
                }

            }};
        IntentFilter regFilter = new IntentFilter();
        // get device sleep evernt
        regFilter .addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, regFilter );
        if(!validAuth()){
            forceLogin();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(!validAuth()){
            forceLogin();
        }
    }

    protected void invalidateAuth(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AccountManager.USER_AUTH_STATUS,AccountManager.USER_NOT_AUTHENTICATED);
        editor.putString(AccountManager.USER_AUTH_TIMESTAMP, null);
        invalidateTokenAtServer();
        editor.apply();
    }

    private void invalidateTokenAtServer(){
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String token = preferences.getString(AccountManager.USER_AUTH_TOKEN, null);
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("token", token);
            RESTApiServer.post(this, "/logout", jsonParams, RESTApiServer.getDefaultHandler(this));
        }
        catch (Exception e){
            System.out.println("fuuuuck exception");
        }
    }

    public void notifyAboutResponse(HashMap<String,String> response){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AccountManager.USER_AUTH_TOKEN, null);
        editor.apply();
        //we dont care...

    }

    private boolean validAuth(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userAuthStatus = preferences.getString(AccountManager.USER_AUTH_STATUS,null);
        if(userAuthStatus != null){
            if(userAuthStatus.equals(AccountManager.USER_AUTHENTICATED)){
                if(authExpired()){
                    Toast.makeText(this,"Your token has expired, please log in again",Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
            return false;
        }
        return false;
    }
    /* This method assumes the auth-token was valid, and checks only the validity of time*/
    private boolean authExpired(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String authExpiryDate = preferences.getString(AccountManager.USER_AUTH_TIMESTAMP, null);
        if(authExpiryDate == null){
            return true;
        }
        long authExpiryDateNumber = Long.valueOf(authExpiryDate);
        long currentTimeNumber = System.currentTimeMillis();
        if((authExpiryDateNumber-currentTimeNumber)<=0) { //Auth has expired
            return true;
        }
        return false;
        //else it was a valid auth token
    }

    private void forceLogin(){
        invalidateAuth();
        Intent intent = new Intent(this,AccountManager.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
