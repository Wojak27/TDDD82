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

import polis.polisappen.LocalDatabase.ApplicationDatabase;

/**
 * Created by karolwojtulewicz on 2018-03-13.
 */

public class QoSManager extends Service {
    private BroadcastReceiver mBatteryBroadcastReciever;
    private BroadcastReceiver mNetworkBroadcastReciever;
    private final int batteryRestrictionLimit = 80;
    private SystemState batteryStatus = SystemState.BATTERY_OKAY;
    private ApplicationDatabase db;
    private String LOCAL_TAG = "QoSManager";
    public static final String UPDATE_MAP= "polis.polisappen.UPDATE_MAP";
    public static final String BATTERY_LOW= "polis.polisappen.BATTERY_LOW";

    @Override
    public void onCreate() {
        createThreadForService();
        createDataBaseInstance();
        setNetworkStatus();
        registerBroadcastRecievers();
    }

    /**
     * the method checks the network status
     * @return
     */
    private boolean isOffline(){
        ConnectivityManager connMgr = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * The method checks current network status and sets the Enum value accordingly
     */
    private void setNetworkStatus(){
        if(isOffline()){
            SystemStatus.setNetworkStatus(SystemState.NETWORK_AVAILABLE);
        }else {
            SystemStatus.setNetworkStatus(SystemState.NETWORK_DOWN);
        }
    }

    /**
     * The method creates a new thread for the service to not block the main thread
     */
    private void createThreadForService(){
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();
    }
    private void createDataBaseInstance(){
        db = Room.databaseBuilder(getApplicationContext(), ApplicationDatabase.class, "database-name").build();
    }
    private void registerBroadcastRecievers(){
        mBatteryBroadcastReciever = new BatteryBroadcastReceiver();
        mNetworkBroadcastReciever = new NetworkBroadcastReceiver();
        registerReceiver(mBatteryBroadcastReciever, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(mNetworkBroadcastReciever, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
    }

    /**
     * The method monitors the battery state and calls localBatteryManager
     */
    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        private final static String BATTERY_LEVEL = "level";
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BATTERY_LEVEL, 0);
            localBatteryManager(context, level);
        }

    }

    /**
     * The method monitors the network status and deletes all sensitive data in case of internet connection is lost
     */
    private class NetworkBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(isOffline()){
                Log.w(LOCAL_TAG, "delete data");
                SystemStatus.setNetworkStatus(SystemState.NETWORK_DOWN);
                deleteSensitiveData();
            }else if(!isOffline()){
                sendBroadcastToMapsActivity();
                SystemStatus.setNetworkStatus(SystemState.NETWORK_AVAILABLE);
            }
        }
    }

    /**
     * the method sends broadcast to mapsactivity to delete all sensitive pins
     */
    private void sendBroadcastToMapsActivity(){
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(UPDATE_MAP));
    }

    /**
     * the method sends broadcast to mapsactivity to change
     */
    private void sendBroadcastBatteryStatusChanged(){
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BATTERY_LOW));
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
//                Toast.makeText(getApplicationContext(),"sendBroadcast", Toast.LENGTH_LONG).show();
                sendBroadcastToMapsActivity();
            }
        }.execute();
    }
    private void localBatteryManager(Context context, int currentBatteryLevel){
        if(currentBatteryLevel <= batteryRestrictionLimit && batteryStatus == SystemStatus.getBatteryStatus()){
//            Toast.makeText(context,"battery under 21 procent",Toast.LENGTH_SHORT).show();
            SystemStatus.setBatteryStatus(SystemState.BATTERY_LOW);
            sendBroadcastBatteryStatusChanged();
        }else if(currentBatteryLevel > batteryRestrictionLimit && batteryStatus == SystemStatus.getBatteryStatus()){
//            Toast.makeText(context,"battery over 20 procent",Toast.LENGTH_SHORT).show();
            SystemStatus.setBatteryStatus(SystemState.BATTERY_OKAY);
            sendBroadcastBatteryStatusChanged();
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
