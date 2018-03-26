package polis.polisappen;

/**
 * Created by karolwojtulewicz on 2018-03-19.
 */

 enum SystemState {
    BATTERY_LOW, BATTERY_OKAY, NETWORK_DOWN, NETWORK_AVAILABLE
}
public class SystemStatus {
    private static SystemState batteryState = SystemState.BATTERY_OKAY;
    private static SystemState networkState = SystemState.NETWORK_AVAILABLE;
    public static void setBatteryStatus(SystemState currentBatteryStatus){
        batteryState = currentBatteryStatus;
    }

    public static SystemState getBatteryStatus(){
        return batteryState;
    }

    public static void setNetworkStatus(SystemState currentNetworkState){
        networkState = currentNetworkState;
    }

    public static SystemState getNetworkStatus(){
        return networkState;
    }
}


