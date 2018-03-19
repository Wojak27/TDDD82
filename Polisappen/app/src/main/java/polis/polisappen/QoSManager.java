package polis.polisappen;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.widget.Toast;

/**
 * Created by karolwojtulewicz on 2018-03-13.
 */

public class QoSManager extends Service {
    private BroadcastReceiver mBroadcastReciever;
    private final int batteryRestrictionLimit = 20;
    private boolean isBatteryLow = false;



    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Toast.makeText(getApplicationContext(),"Service Started", Toast.LENGTH_LONG);

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        mBroadcastReciever = new BatteryBroadcastReceiver();
        registerReceiver(mBroadcastReciever, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
//        mBroadcastReciever = new BatteryBroadcastReceiver();
//        registerReceiver(mBroadcastReciever, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//        // For each start request, send a message to start a job and deliver the
//        // start ID so we know which request we're stopping when we finish the job
//        Message msg = mServiceHandler.obtainMessage();
//        msg.arg1 = startId;
//        mServiceHandler.sendMessage(msg);
//
//        // If we get killed, after returning from here, restart
//        return START_NOT_STICKY;
//    }

    // checks for changes in battery
    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        private final static String BATTERY_LEVEL = "level";
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BATTERY_LEVEL, 0);
//            Toast.makeText(context,Integer.toString(level),Toast.LENGTH_SHORT).show();

            if(level <= batteryRestrictionLimit && !isBatteryLow){
                Toast.makeText(context,"battery under 21 procent",Toast.LENGTH_SHORT).show();
                isBatteryLow = true;
            }else if(level > batteryRestrictionLimit && isBatteryLow){
                Toast.makeText(context,"battery over 20 procent",Toast.LENGTH_SHORT).show();
                isBatteryLow = false;
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        unregisterReceiver(mBroadcastReciever);
    }
}
