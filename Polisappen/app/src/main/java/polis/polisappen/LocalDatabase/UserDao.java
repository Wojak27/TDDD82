package polis.polisappen.LocalDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by karolwojtulewicz on 2018-02-06.
 */

@Dao
public interface UserDao {
    @Query("SELECT * FROM Location")
    List<Location> getAll();

    @Query("SELECT * FROM Location WHERE uid IN (:userIds)")
    List<Location> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM Location WHERE uid IN (:userId)")
    Location loadById(int userId);

    @Insert
    void insertAll(Location... locations);

    @Insert
    void insert(Location location);

    @Delete
    void delete(Location location);
}
