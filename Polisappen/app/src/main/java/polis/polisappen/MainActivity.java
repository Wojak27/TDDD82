package polis.polisappen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class MainActivity extends AuthAppCompatActivity implements View.OnClickListener{

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
        Button callButton = (Button)findViewById(R.id.callButton);
        callButton.setOnClickListener(this);
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
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.callButton){
            Intent intent = new Intent(this, VideoAndVoiceChat.class);
            startActivity(intent);
        }
    }

    @Override
    public void notifyAboutResponseJSONArray(HashMap<String, HashMap<String, String>> response) {

    }
}
