package polis.polisappen;

/**
 * Created by karolwojtulewicz on 2018-03-19.
 */

 enum SystemState {
    BATTERY_LOW, BATTERY_OKAY, DATA_DELETED, DATA_AVAILABLE
}
public class BatteryStatus {
    private static SystemState batteryState = SystemState.BATTERY_OKAY;
    public static void setBatteryStatus(SystemState currentBatteryStatus){
        batteryState = currentBatteryStatus;
    }

    public static SystemState getBatteryStatus(){
        return batteryState;
    }
}
