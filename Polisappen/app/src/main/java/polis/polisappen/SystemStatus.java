package polis.polisappen;

/**
 * Created by karolwojtulewicz on 2018-03-19.
 */

 enum SystemState {
    BATTERY_LOW, BATTERY_OKAY, DATA_DELETED, DATA_AVAILABLE
}
public class SystemStatus {
    private static SystemState batteryState = SystemState.BATTERY_OKAY;
    private static SystemState sensitiveDataState = SystemState.DATA_AVAILABLE;
    public static void setBatteryStatus(SystemState currentBatteryStatus){
        batteryState = currentBatteryStatus;
    }

    public static SystemState getBatteryStatus(){
        return batteryState;
    }

    public static void setSensitiveDataStatus(SystemState currentSensitiveDataState){
        sensitiveDataState = currentSensitiveDataState;
    }

    public static SystemState getSensitiveDataState(){
        return sensitiveDataState;
    }
}
