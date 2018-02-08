package polis.polisappen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class AuthAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAuth();
    }

    private void checkAuth(){
        //SET
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AccountManager.USER_INFO_AUTH,"true");
        editor.apply();
        //READ
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String authed = preferences.getString(AccountManager.USER_INFO_AUTH, "");
        if(authed.equals("true")){
            //OK
        }
        else{
            Intent intent = new Intent(this,AccountManager.class);
            startActivity(intent);
        }
    }
}
