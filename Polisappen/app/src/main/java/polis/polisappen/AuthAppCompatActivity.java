package polis.polisappen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class AuthAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAuth();
    }

    private void checkAuth(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userAuthStatus = preferences.getString(AccountManager.USER_AUTH_STATUS,null);
        if(userAuthStatus != null){
            if(userAuthStatus.equals(AccountManager.USER_AUTHENTICATED)){
                checkExpiry();
                return;
            }
        }
        forceLogin();
    }
    /* This method assumes the auth-token was valid, and checks only the validity of time*/
    private void checkExpiry(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String authExpiryDate = preferences.getString(AccountManager.USER_AUTH_TIMESTAMP, null);
        if(authExpiryDate == null){
            forceLogin();
            return;
        }
        long authExpiryDateNumber = Long.valueOf(authExpiryDate);
        long currentTimeNumber = System.currentTimeMillis();
        if((authExpiryDateNumber-currentTimeNumber)<=0) { //Auth has expired
            forceLogin();
        }
        //else it was a valid auth token


    }

    private void forceLogin(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AccountManager.USER_AUTH_STATUS,AccountManager.USER_NOT_AUTHENTICATED);
        editor.putString(AccountManager.USER_AUTH_TIMESTAMP, null);
        editor.apply();
        Intent intent = new Intent(this,AccountManager.class);
        startActivity(intent);
    }
}
