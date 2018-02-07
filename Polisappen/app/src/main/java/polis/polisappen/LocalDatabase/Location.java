package polis.polisappen.LocalDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
/**
 * Created by karolwojtulewicz on 2018-02-06.
 */

@Entity(tableName = "Location", primaryKeys =
        {"latitude","longitude"})
public class Location {
//    @PrimaryKey
//    public int uid;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "label")
    public String title;

    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
}
