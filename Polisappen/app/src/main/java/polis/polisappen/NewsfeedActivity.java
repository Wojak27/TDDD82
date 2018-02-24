package polis.polisappen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;


public class NewsfeedActivity extends AuthAppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString(AccountManager.USER_AUTH_TOKEN, null);
        JSONObject jsonParams = new JSONObject();
        try{
            jsonParams.put("token", token);
            RESTApiServer.get(this,"/secret",jsonParams, RESTApiServer.getDefaultHandler(this));
        }
        catch (Exception e){
            //do nothing
        }
    }

    @Override
    public void notifyAboutResponse(HashMap<String,String> response){
        TextView view = (TextView) findViewById(R.id.textView);
        view.setText("Fick ett svar från server...");
        for(String a:response.keySet()){
            view.append(response.get(a));
        }

    }


}
