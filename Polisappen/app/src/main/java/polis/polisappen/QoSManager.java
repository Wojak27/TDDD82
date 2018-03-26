package polis.polisappen;

import android.annotation.SuppressLint;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import polis.polisappen.LocalDatabase.ApplicationDatabase;

/**
 * Created by karolwojtulewicz on 2018-03-13.
 */

public class QoSManager extends Service {
    private BroadcastReceiver mBatteryBroadcastReciever;
    private BroadcastReceiver mNetworkBroadcastReciever;
    private final int batteryRestrictionLimit = 20;
    private SystemState batteryStatus = SystemState.BATTERY_OKAY;
    private ApplicationDatabase db;
    private String LOCAL_TAG = "QoSManager";
    public static final String UPDATE_MAP= "polis.polisappen.UPDATE_MAP";


    private boolean isOnline(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();
        if(isOnline()){
            SystemStatus.setNetworkStatus(SystemState.NETWORK_AVAILABLE);
            Toast.makeText(this, "Network available changing state", Toast.LENGTH_LONG).show();
        }else {
            SystemStatus.setNetworkStatus(SystemState.NETWORK_DOWN);
            Toast.makeText(this, "Network down changing state", Toast.LENGTH_LONG).show();
        }
        db = Room.databaseBuilder(getApplicationContext(),
                ApplicationDatabase.class, "database-name").build();
        // Get the HandlerThread's Looper and use it for our Handler
//        Toast.makeText(getApplicationContext(),"Service Started", Toast.LENGTH_LONG);

//        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        mBatteryBroadcastReciever = new BatteryBroadcastReceiver();
        mNetworkBroadcastReciever = new NetworkBroadcastReceiver();
        registerReceiver(mBatteryBroadcastReciever, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(mNetworkBroadcastReciever, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
    }

    // checks for changes in battery
    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        private final static String BATTERY_LEVEL = "level";
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BATTERY_LEVEL, 0);
//            Toast.makeText(context,Integer.toString(level),Toast.LENGTH_SHORT).show();
            localBatteryManager(context, level);
        }

    }

    private class NetworkBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(isOnline()){
                Toast.makeText(getApplicationContext(), "deletes data", Toast.LENGTH_LONG).show();
                Log.w(LOCAL_TAG, "delete data");
                SystemStatus.setNetworkStatus(SystemState.NETWORK_DOWN);
                deleteSensitiveData();
            }else if(!isOnline()){
                Toast.makeText(getApplicationContext(), "Network back online", Toast.LENGTH_LONG).show();
                sendBroadcastToMapsActivity();
                SystemStatus.setNetworkStatus(SystemState.NETWORK_AVAILABLE);
            }
        }
    }

    private void sendBroadcastToMapsActivity(){
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(UPDATE_MAP));
    }
    @SuppressLint("StaticFieldLeak")
    private void deleteSensitiveData(){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                db.userDao().removeSensitiveData();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getApplicationContext(),"sendBroadcast", Toast.LENGTH_LONG).show();
                sendBroadcastToMapsActivity();
            }
        }.execute();
    }
    private void localBatteryManager(Context context, int currentBatteryLevel){
        if(currentBatteryLevel <= batteryRestrictionLimit && batteryStatus == SystemStatus.getBatteryStatus()){
//            Toast.makeText(context,"battery under 21 procent",Toast.LENGTH_SHORT).show();
            SystemStatus.setBatteryStatus(SystemState.BATTERY_LOW);
        }else if(currentBatteryLevel > batteryRestrictionLimit && batteryStatus == SystemStatus.getBatteryStatus()){
//            Toast.makeText(context,"battery over 20 procent",Toast.LENGTH_SHORT).show();
            SystemStatus.setBatteryStatus(SystemState.BATTERY_OKAY);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        unregisterReceiver(mBatteryBroadcastReciever);
    }
}
