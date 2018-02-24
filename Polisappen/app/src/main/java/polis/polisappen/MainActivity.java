package polis.polisappen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;

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
        mapButton.setOnClickListener(this) ;
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
    }
}
