package polis.polisappen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import polis.polisappen.LocalDatabase.ApplicationDatabase;
import polis.polisappen.LocalDatabase.Location;

public class MapsActivity extends AuthAppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,
        HttpResponseNotifyable, View.OnClickListener {

    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mRequestingLocationUpdates = false;
    private ApplicationDatabase db;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private BroadcastReceiver mBroadcastReciever;
    private boolean isBatteryLow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        RESTApiServer.getCoord(this,this);
        Button updateButton = (Button) findViewById(R.id.updateButtonMaps);
        updateButton.setOnClickListener(this);
//Debugging
        mBroadcastReciever = new BatteryBroadcastReceiver();
///////////////
        db = Room.databaseBuilder(getApplicationContext(),
                ApplicationDatabase.class, "database-name").build();
        deleteMarkerFromDatabase();
        //adding new marker to the database
//        addMarkerToDatabase(new LatLng(2.1,3.2),"new Marker");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
               .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupLocationCallback();
        createLocationRequest();
    }

    private void setupLocationCallback(){
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (android.location.Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Log.w("Location", "update");
//                    laying markers for debugging
//                    mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));
                }

            }
        };
    }

    @Override
    public void notifyAboutResponse(HashMap<String, String> response) {
        System.out.println("Server sa:" + "\n");
        for(String key: response.keySet()){
            System.out.println(key + " : " + response.get(key));
        }
    }

    @Override
    public void notifyAboutResponseJSONArray(HashMap<String, HashMap<String, String>> response) {
        System.out.println("databasen svarade 2");
        for(String key : response.keySet()){
            addMarkerToLocalDB(new LatLng(Double.parseDouble(response.get(key).get("latitude")),Double.parseDouble(response.get(key).get("longitude"))),"title",response.get(key).get("report_text"), response.get(key).get("type"));
        }
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
        if (requestCode == 1) {
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
            requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
        Log.w("mMap", mMap.toString());
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastKnownLocation();
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(getLastKnownLocation()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    private void startLocationUpdates() {
        try{
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }catch (SecurityException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
//        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void createLocationRequestBestForBattery() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void moveCameraToCurrentPostition(LatLng location){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f));
    }

    private void getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {
                        Log.w("success", "successful");
                        setMyCoordinates(new LatLng(location.getLatitude(), location.getLongitude()));
                        moveCameraToCurrentPostition(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                });
        mRequestingLocationUpdates = true;
        startLocationUpdates(); //fro debugging
    }

    LatLng myLocation;

    private void setMyCoordinates(LatLng latLng){
        myLocation = latLng;
    }

    private LatLng getMyCoordinates(){
        return myLocation;
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
        Log.w("mMap", mMap.toString());
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
        }else {
            setMyLocation();
        }

        //adds marker on the selected position (after long click)
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                reportFormPopup(latLng);
            }
        });

        //removing marker on click
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                marker.remove();
//                deleteMarkerFromDatabase(marker);
                openReportWindow(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude));  // need to be done in async
                return false;
            }
        });
        setMarkersFromDatabaseOnMap(mMap);
    }

    @SuppressLint("StaticFieldLeak")
    private void openReportWindow(final LatLng marker){
        final Intent intent = new Intent(this, ReportWindowActivity.class);

        new AsyncTask<Void, Void, Integer>() {
            Location location;
            @Override
            protected Integer doInBackground(Void... params) {
//                location = db.userDao().loadById(1); //using query I created in UserDau.java
                location = db.userDao().selectSpecificMarker(marker.latitude, marker.longitude);
                return 1;
            }

            @Override
            protected void onPostExecute(Integer numOfLocations) {
                intent.putExtra("reportText", location.reportText);
                System.out.println("location text " + location.reportText.toString());
                startReportIntent(intent);
            }
        }.execute();
    }

    private void startReportIntent(Intent intent){
        startActivity(intent);
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
        mMap.clear();
        //gets location from the database
        new AsyncTask<Void, Void, Integer>() {
            //            Location location;
            List<Location> locations = new ArrayList<>(); // hashset to store all locations from the database
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
                        LatLng latLng = new LatLng(location.latitude,location.longitude);
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location.title));
                    }
                }
            }
        }.execute();

    }

    private HashMap<String, String> createMapWithCoordinates(double latitude, double longitude, String type, String reportText){
        HashMap<String,String> hashMapCoordinates = new HashMap<>();
        hashMapCoordinates.put("latitude", Double.toString(latitude));
        hashMapCoordinates.put("longitude", Double.toString(longitude));
        hashMapCoordinates.put("type", type);
        hashMapCoordinates.put("report_text", reportText);
        return hashMapCoordinates;
    }

    @SuppressLint("StaticFieldLeak")
    private void addMarkerToOnlineDB(LatLng latLng, String title, String reportText, String type){
        Log.w("latitude", Double.toString(latLng.latitude));
        Log.w("longitude", Double.toString(latLng.longitude));
        RESTApiServer.setCoord(this,this,createMapWithCoordinates(latLng.latitude,latLng.longitude,type,reportText));
    }
    private void addMarkerToLocalDB(LatLng latLng, String title, String reportText, String type){
        mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        final Location location = new Location();
        location.latitude = latLng.latitude;
        location.longitude = latLng.longitude;
        location.title = title;
        Log.v("text to databse: ", reportText);
        location.reportText = reportText;
        if (type == "2")
            location.type = 2;
        //put the location we've created previously into the local database
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
                    Log.w("Location","successful");
                }
            }
        }.execute();
    }
    public void addMarkerToDatabase(LatLng latLng, String title, String reportText, String type){ // uid for debugging
        addMarkerToLocalDB(latLng,title,reportText, type);
        addMarkerToOnlineDB(latLng,title,reportText, type);

    }

    @SuppressLint("StaticFieldLeak")
    private void deleteMarkerFromDatabase(){ // location for debugging, needs fixing
//        final double lat = marker.getPosition().latitude;
//        final double lon = marker.getPosition().longitude;
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                db.userDao().deleteAll();
//                Location location = db.userDao().selectSpecificMarker(lat, lon);
//                if(location != null) db.userDao().delete(location);
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

    @Override
    protected void onStart() {
        registerReceiver(mBroadcastReciever, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onStart();
    }
    @Override
    protected void onStop() {
        unregisterReceiver(mBroadcastReciever);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        deleteMarkerFromDatabase();
        RESTApiServer.getCoord(this,this);
        setMarkersFromDatabaseOnMap(mMap);
    }

    // checks for changes in battery
    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        private final static String BATTERY_LEVEL = "level";
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BATTERY_LEVEL, 0);
            Toast.makeText(context,Integer.toString(level),Toast.LENGTH_SHORT).show();

            if(level < 21 && !isBatteryLow){
                createLocationRequestBestForBattery();
                isBatteryLow = true;
            }else if(level > 20 && isBatteryLow){
                createLocationRequest();
                isBatteryLow = false;
            }
        }

    }

}

