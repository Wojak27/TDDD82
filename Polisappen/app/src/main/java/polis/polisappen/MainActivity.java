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
        Button newsfeed = (Button)findViewById(R.id.newsfeedButton);
        newsfeed.setOnClickListener(this);
        logout.setOnClickListener(this);
        setContentView(R.layout.activity_main);
        Button mapButton = (Button)findViewById(R.id.button2);
        final Intent mapIntent = new Intent(this, MapsActivity.class);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mapIntent);
            }
        });
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.newsfeedButton){
            Intent intent = new Intent(this,NewsfeedActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.logOutButton){
            Intent intent = new Intent(this,AccountManager.class);
            startActivity(intent);
        }
    }
}
