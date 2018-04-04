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
public interface LocationDao {
    @Query("SELECT * FROM Location")
    List<Location> getAll();

//    @Query("SELECT * FROM Location WHERE uid IN (:userId)")
//    Location loadById(int userId);

    //do this to get uid number
    @Query("SELECT * FROM Location WHERE latitude = :lat AND longitude = :lon")
    Location selectSpecificMarker(double lat, double lon);

    @Query("DELETE FROM Location WHERE type = 2")
    void removeSensitiveData();

    @Insert
    void insertAll(Location... locations);

    @Insert
    void insert(Location location);

    @Delete
    void delete(Location location);

    @Query("DELETE FROM Location")
    void deleteAll();

//    Doesn't work in Room, needs to create custom solution
//    @Query("DELETE FROM Location WHERE latitude = :lat AND longitude = :lon AND label = :title")
//    void deleteSpecificMarker(double lat, double lon, String title);
}
