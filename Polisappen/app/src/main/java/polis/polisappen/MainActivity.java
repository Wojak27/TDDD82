package polis.polisappen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AuthAppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_screen);
        Button logout = (Button)findViewById(R.id.logOutButton);
        Button newsfeed = (Button)findViewById(R.id.newsfeedButton);
        newsfeed.setOnClickListener(this);
        logout.setOnClickListener(this);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                //check if the broadcast is our desired one
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                    MainActivity.super.invalidateAuth();
                }

            }};
        IntentFilter regFilter = new IntentFilter();
        // get device sleep evernt
        regFilter .addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, regFilter );
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
