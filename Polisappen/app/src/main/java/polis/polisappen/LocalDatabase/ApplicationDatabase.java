package polis.polisappen.LocalDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by karolwojtulewicz on 2018-02-06.
 */

@Database(entities = {Location.class}, version = 1)
public abstract class ApplicationDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}