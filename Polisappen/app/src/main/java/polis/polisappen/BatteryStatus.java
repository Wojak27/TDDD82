package polis.polisappen;

/**
 * Created by karolwojtulewicz on 2018-03-19.
 */

 enum BatteryState {
    BATTERY_LOW, BATTERY_OKAY
}
public class BatteryStatus {
    private static BatteryState batteryState = BatteryState.BATTERY_OKAY;
    public static void setBatteryStatus(BatteryState currentBatteryStatus){
        batteryState = currentBatteryStatus;
    }

    public static BatteryState getBatteryStatus(){
        return batteryState;
    }
}
