package polis.polisappen;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AuthAppCompatActivity implements View.OnClickListener{

    Intent qoSIntent;
    public static final String BATTERY_LOW_LEVEL_CHANGED= "polis.polisappen.BATTERY_LOW_LEVEL_CHANGED";
    private Button logout;
    private int currentBattery = 50;
    private int currentBatteryLimit = 10;
    private BroadcastReceiver currentBatteryChangeBR;
    private BroadcastReceiver currentBatteryLimitBR;
    TextView currentBatteryTV;
    TextView currentBatteryLimitTV;
    TextView eftersomTV;
    Button batteryOKAYButton;
    Button batteryLOWButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState == null){
            savedInstanceState = new Bundle();
        }
        savedInstanceState.putString("CALLER","MAINACTIVITY");
        super.onCreate(savedInstanceState);
        QoSManager.auth = this;
        setContentView(R.layout.menu_screen);
        logout = (Button)findViewById(R.id.logOutButton);
        if(!validAuth()){
            logout.setText("Log in");
        }
        logout.setOnClickListener(this);
        Button mapButton = (Button)findViewById(R.id.mapsButton);
        mapButton.setOnClickListener(this);
        Button contactsButton = (Button)findViewById(R.id.contactsButton);
        contactsButton.setOnClickListener(this);
        Button callButton = (Button)findViewById(R.id.callButton);
        callButton.setOnClickListener(this);
        Button changebatterytoGoodButton = (Button) findViewById(R.id.changeBatterytoGoodButton);
        Button changeBatteryToLowButton = (Button) findViewById(R.id.changeBatterytoLowButton);
        changebatterytoGoodButton.setOnClickListener(this);
        changeBatteryToLowButton.setOnClickListener(this);
        initTextViews();
        initButtons();
    }
    private void initTextViews(){
        currentBatteryLimitTV = (TextView) findViewById(R.id.currentbatteryLimitTV);
        currentBatteryTV = (TextView) findViewById(R.id.currentBatteryTV);
        eftersomTV = (TextView) findViewById(R.id.eftersomTV);
    }

    private void initButtons(){
        batteryLOWButton = (Button) findViewById(R.id.changeBatterytoLowButton);
        batteryOKAYButton = (Button) findViewById(R.id.changeBatterytoGoodButton);
        batteryLOWButton.setOnClickListener(this);
        batteryOKAYButton.setOnClickListener(this);
    }

    private void setTextOnEftersomTV(){
        if(SystemStatus.getBatteryStatus() == SystemState.BATTERY_OKAY){
            eftersomTV.setText("Batteri är bra eftersom " + Integer.toString(currentBattery)+"% > " +Integer.toString(currentBatteryLimit)+"%");
        }else if (SystemStatus.getBatteryStatus() == SystemState.BATTERY_LOW){
            eftersomTV.setText("Batteri är låg eftersom " + Integer.toString(currentBattery)+"% < " +Integer.toString(currentBatteryLimit)+"%");
        }
    }

    private void registerReceivers(){
        if (currentBatteryChangeBR == null){
            currentBatteryChangeBR = new CurrentBatteryChangeBroadcastReceiver();
        }
        if( currentBatteryLimitBR == null ){
            currentBatteryLimitBR = new CurrentBatteryLimitChangeBroadcastReceiver();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(currentBatteryChangeBR, new IntentFilter(QoSManager.BATTERY_CHANGE));
        LocalBroadcastManager.getInstance(this).registerReceiver(currentBatteryLimitBR, new IntentFilter(QoSManager.BATTERY_LOW));
//        Toast.makeText(this,"receiver", Toast.LENGTH_SHORT).show();
    }

    private void unregisterReceivers(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(currentBatteryLimitBR);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(currentBatteryChangeBR);
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceivers();
        if(validAuth()){
            logout.setText("Log out");
        }
        if(!isServiceRunning(QoSManager.class)){
            if (qoSIntent == null){
                qoSIntent = new Intent(this, QoSManager.class);
                startService(qoSIntent);
            }else{
                startService(qoSIntent);
            }
            if(validAuth()){

            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceivers();
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.logOutButton){
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
        else if(view.getId() == R.id.contactsButton){
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.callButton){
            Intent intent = new Intent(this, VideoAndVoiceChat.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.changeBatterytoGoodButton){
            int lowBatteryLimit = currentBattery - 3;
            Intent intent = new Intent(BATTERY_LOW_LEVEL_CHANGED);
            intent.putExtra("LowBatteryIndicator", lowBatteryLimit);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//            Toast.makeText(this, "yeye", Toast.LENGTH_LONG).show();
        }
        else if (view.getId() == R.id.changeBatterytoLowButton){
            int lowBatteryLimit = currentBattery + 3;
            Intent intent = new Intent(BATTERY_LOW_LEVEL_CHANGED);
            intent.putExtra("LowBatteryIndicator", lowBatteryLimit);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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

    private class CurrentBatteryChangeBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(getApplicationContext(), "received", Toast.LENGTH_LONG).show();
            currentBattery = intent.getIntExtra("level", 50);
            currentBatteryTV.setText("Nuvarande batteri: " + Integer.toString(currentBattery) + "%");
            setTextOnEftersomTV();
        }
    }
    private class CurrentBatteryLimitChangeBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(getApplicationContext(), "received limit", Toast.LENGTH_LONG).show();
            currentBatteryLimit = intent.getIntExtra("level", 10);
            currentBatteryLimitTV.setText("Låg Batteri gräns: " + Integer.toString(currentBatteryLimit) + "%");
            setTextOnEftersomTV();
        }
    }
}
