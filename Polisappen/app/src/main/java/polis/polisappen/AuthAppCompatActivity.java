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
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import polis.polisappen.LocalDatabase.Location;

public abstract class AuthAppCompatActivity extends AppCompatActivity implements HttpResponseNotifyable {

    private BroadcastReceiver receiver;
    private IntentFilter regFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String caller = null;
        if(savedInstanceState != null) {
            if (savedInstanceState.getString("CALLER") != null) {
                caller = savedInstanceState.getString("CALLER");
                savedInstanceState = null;
            }
        }
        super.onCreate(savedInstanceState);
        receiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                //check if the broadcast is our desired one
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                    invalidateAuth();
                }

            }};
        regFilter = new IntentFilter();
        // get device sleep evernt
        regFilter .addAction(Intent.ACTION_SCREEN_OFF);

        if(caller != null){
            if(caller.equals("MAINACTIVITY")){ //Mainactivity needs no auth
                System.out.println("Upptäckte att det var mainactivity");
                invalidateAuthWithoutInternet();
                return;
            }
        }
        System.out.println("uppräckte inte att det var mainactivity");
        if(!validAuth()){
            System.out.println("hade ingen valid auth");
            Toast.makeText(this,"You need to be authenticated", Toast.LENGTH_SHORT).show();
            forceLogin();
        }
        System.out.println("Vi hade valid auth!");
    }
    @Override
    public void notifyAboutFailedRequest(){
       //default is to do nothing, override to handle yourself...
    }

    @Override
    protected void onResume(){
        System.out.println("onResume called!!");
        super.onResume();
        /*
        if(!validAuth()){
            forceLogin();
        }
        */
        registerReceiver(receiver, regFilter );

    }

    protected String getUsername(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(AccountManager.USER_AUTH_REAL_NAME,null);
    }

    protected String getUserID(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(AccountManager.USER_AUTH_NAME,null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    protected void invalidateAuth(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        //editor.putString(AccountManager.USER_AUTH_TOKEN, null);
        editor.putString(AccountManager.USER_AUTH_STATUS,AccountManager.USER_NOT_AUTHENTICATED);
        editor.putString(AccountManager.USER_AUTH_TIMESTAMP, null);
        invalidateTokenAtServer();
        editor.apply();
    }
    protected void invalidateAuthWithoutInternet(){
        System.out.println("INVALIDATE WITHOUT INTERNET CALLED");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AccountManager.USER_AUTH_TOKEN, null);
        editor.putString(AccountManager.USER_AUTH_STATUS,AccountManager.USER_NOT_AUTHENTICATED);
        editor.putString(AccountManager.USER_AUTH_TIMESTAMP, null);
        editor.apply();
    }

    private void invalidateTokenAtServer(){
        RESTApiServer.logout(this,this);
    }

    public void notifyAboutResponse(HashMap<String,String> response){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        System.out.println("TOKEN IS NOW NULL");
        editor.putString(AccountManager.USER_AUTH_TOKEN, null);
        editor.apply();
        System.out.println("server responded from logout");
    }

    protected boolean validAuth(){
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
        System.out.println("forceLogin called, have invalidatedAuth");
        Intent intent = new Intent(this,AccountManager.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        System.out.println("Starting login activity....");
        startActivity(intent);
        try{
            stopService(new Intent(AuthAppCompatActivity.this,QoSManager.class));
        }catch (Exception e){
            Log.w("No started ", "service");
        }

    }
}
