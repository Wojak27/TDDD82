package polis.polisappen;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class MainActivity extends AuthAppCompatActivity implements View.OnClickListener{

    Intent qoSIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_screen);
        Button logout = (Button)findViewById(R.id.logOutButton);
        logout.setOnClickListener(this);
        Button newsfeed = (Button)findViewById(R.id.newsfeedButton);
        newsfeed.setOnClickListener(this);
        Button mapButton = (Button)findViewById(R.id.mapsButton);
        mapButton.setOnClickListener(this);
        Button contactsButton = (Button)findViewById(R.id.contactsButton);
        contactsButton.setOnClickListener(this);
        Button startQoS = (Button)findViewById(R.id.startQoS);
        startQoS.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isServiceRunning(QoSManager.class)){
            if(validAuth()){
                if (qoSIntent == null){
                    qoSIntent = new Intent(this, QoSManager.class);
                    startService(qoSIntent);
                }else{
                    startService(qoSIntent);
                }
            }
        }

    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.newsfeedButton){
            Intent intent = new Intent(this,NewsfeedActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.logOutButton){
            super.invalidateAuth();
            Intent intent = new Intent(this,AccountManager.class);
            startActivity(intent);
            if(qoSIntent != null){
                stopService(qoSIntent);
            }
        }
        else if(view.getId() == R.id.mapsButton){
            Intent intent = new Intent(this,MapsActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.searchButton){
            Intent intent = new Intent(this,MapsActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.contactsButton){
            Intent intent = new Intent(this, VideoAndVoiceChat.class);
            startActivity(intent);
        }
    }
    //tagen fran stack overflow, svart att skriva egen.............
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(qoSIntent != null){
            stopService(qoSIntent);
        }
    }

    @Override
    public void notifyAboutResponseJSONArray(HashMap<String, HashMap<String, String>> response) {

    }
}
