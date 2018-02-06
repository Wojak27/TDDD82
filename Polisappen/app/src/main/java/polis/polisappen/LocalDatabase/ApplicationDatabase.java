package polis.polisappen.LocalDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by karolwojtulewicz on 2018-02-06.
 */

@Database(entities = {User.class}, version = 1)
public abstract class ApplicationDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}