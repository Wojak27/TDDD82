package polis.polisappen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.arch.persistence.room.Room;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import polis.polisappen.LocalDatabase.ApplicationDatabase;
import polis.polisappen.LocalDatabase.Location;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST_CODE = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private ApplicationDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
        }

        db = Room.databaseBuilder(getApplicationContext(),
                ApplicationDatabase.class, "database-name").build();

        //adding new marker to the database
//        addMarkerToDatabase(new LatLng(2.1,3.2),"new Marker");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private boolean isPermissionGranted() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("log", "Permission is granted");
            return true;
        } else {
            Log.v("log", "Permission not granted");
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            setMyLocation();
        }
    }

    private void setMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //adds marker on the selected position (after long click)
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String title = "My marker";
                reportFormPopup(latLng);
            }
        });

        //removing marker on click
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                deleteMarkerFromDatabase(marker);
                return false;
            }
        });
        setMarkersFromDatabaseOnMap(mMap);


    }

    private void reportFormPopup(LatLng latLng){
        Bundle data = new Bundle();
        data.putDouble("latidude", latLng.latitude);
        data.putDouble("longitude", latLng.longitude);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ReportFormFragment reportFormFragment = new ReportFormFragment();
        reportFormFragment.setArguments(data);
        fragmentTransaction.add(R.id.fragment_container, reportFormFragment, "COORDINATES");
        fragmentTransaction.commit();
    }

    @SuppressLint("StaticFieldLeak")
    private void setMarkersFromDatabaseOnMap(final GoogleMap mMap){
        //gets location from the database
        new AsyncTask<Void, Void, Integer>() {
//            Location location;
            List<Location> locations = new ArrayList<Location>(); // hashset to store all locations from the database
            @Override
            protected Integer doInBackground(Void... params) {
//                location = db.userDao().loadById(1); //using query I created in UserDau.java
                locations = db.userDao().getAll();
                return db.userDao().getAll().size();
            }

            @Override
            protected void onPostExecute(Integer numOfLocations) {
                if(numOfLocations > 0){ // loops through all of the locations in the database (if locations.size > 0)
                    for(Location location : locations){
                        LatLng sydney = new LatLng(location.latitude,location.longitude);
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    }
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void addMarkerToDatabase(LatLng latLng, String title, String reportText){ // uid for debugging

        mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        final Location location = new Location();
        location.latitude = latLng.latitude;
        location.longitude = latLng.longitude;
        location.title = reportText;
        location.reportText = reportText;

        //now I put the location I've created previously into the local database
        //everything needs to be done on a separate thread due to android constraints
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                db.userDao().insert(location);  //using query I created in UserDau.java

                return db.userDao().getAll().size();
            }

            @Override
            protected void onPostExecute(Integer locationCount) {
                if (locationCount>0){
                    Log.w("Location","successl");
                }
            }
        }.execute();
    }

    /**
     * Todo: Database primary keys for marker adding and retrievial
     * @param
     */

    @SuppressLint("StaticFieldLeak")
    private void deleteMarkerFromDatabase(final Marker marker){ // location for debugging, needs fixing
        final double lat = marker.getPosition().latitude;
        final double lon = marker.getPosition().longitude;
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {

                Location location = db.userDao().selectSpecificMarker(lat, lon);
                if(location != null) db.userDao().delete(location);
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
            }
        }.execute();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull android.location.Location location) {

    }
}
