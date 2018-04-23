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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import polis.polisappen.LocalDatabase.ApplicationDatabase;
import polis.polisappen.LocalDatabase.Location;

public class MapsActivity extends ExceptionAuthAppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, HttpResponseNotifyable, View.OnClickListener {

    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mRequestingLocationUpdates = false;
    private ApplicationDatabase db;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private BroadcastReceiver mMapUpdateBroadcastReciever;
    private BroadcastReceiver mBatteryLowBroadcastReciever;
    private TextView batteryStatusText;
    private final int NONSENSITIVE_DATA = 1;
    private final int SENSITIVE_DATA = 2;
    private final int SUPER_TOP_SECRET = 3;
    private LatLng currentPosition = null;
    public TextView textViewServerResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        RESTApiServer.getCoord(this,this, currentPosition);
        Button updateButton = (Button) findViewById(R.id.updateButtonMaps);
        textViewServerResponse = (TextView) findViewById(R.id.text_view_maps);
        batteryStatusText = (TextView) findViewById(R.id.battery_status_textbox);
        updateButton.setOnClickListener(this);
        db = Room.databaseBuilder(getApplicationContext(),
                ApplicationDatabase.class, "database-name").build();

        deleteMarkerFromDatabase();
        //adding new marker to the database
//        addMarkerToDatabase(new LatLng(2.1,3.2),"new Marker");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        makeLocationRequest();
        setupLocationCallback();
        mMapUpdateBroadcastReciever = new MapUpdateBroadcastReciever();
        mBatteryLowBroadcastReciever = new BatteryLowReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMapUpdateBroadcastReciever,new IntentFilter(QoSManager.UPDATE_MAP));
        LocalBroadcastManager.getInstance(this).registerReceiver(mBatteryLowBroadcastReciever,new IntentFilter(QoSManager.BATTERY_LOW));
    }

    private void makeLocationRequest(){
        if(SystemState.BATTERY_OKAY == SystemStatus.getBatteryStatus()){
            createLocationRequest();
        }else {
            createLocationRequestBestForBattery();
        }
    }

    private class MapUpdateBroadcastReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(getApplicationContext(), "Time to update the map", Toast.LENGTH_SHORT).show();
            updateMap();
        }
    }

    private class BatteryLowReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Time to update the battery", Toast.LENGTH_SHORT).show();
            if(SystemState.BATTERY_OKAY == SystemStatus.getBatteryStatus()){
                createLocationRequest();
            }else if(SystemState.BATTERY_LOW == SystemStatus.getBatteryStatus()){
                createLocationRequestBestForBattery();
            }
        }
    }
    private void setupLocationCallback(){
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (android.location.Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Log.w("Location", "update");
                    if(mMap != null) {
                        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        updateMap();
                    }

//                    laying markers for debugging
//                    mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));
                }

            }

        };
    }

    @Override
    public void notifyAboutResponse(HashMap<String, String> response) {
        if (response.containsKey("latitude")){
            textViewServerResponse.setText("");
            for(String key: response.keySet()){
                System.out.println(key + " : " + response.get(key));
            }
        }
        else{
            textViewServerResponse.setText("Meddelandet blev manipulerat");
        }
    }

    @Override
    public void notifyAboutResponseJSONArray(HashMap<String, HashMap<String, String>> response) {
        System.out.println("databasen svarade 2");
        for(String key : response.keySet()){
            System.out.println("Type: " + response.get(key).get("type"));
            addMarkerToLocalDB(new LatLng(Double.parseDouble(response.get(key).get("latitude")),Double.parseDouble(response.get(key).get("longitude"))),"title",response.get(key).get("report_text"), response.get(key).get("type"));
        }
    }

    @Override
    public void notifyAboutFailedRequest() {

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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Log.w("mMap", mMap.toString());
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        getLastKnownLocation();
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(getLastKnownLocation()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMap != null) {
            updateMap();
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        makeLocationRequest();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMapUpdateBroadcastReciever,new IntentFilter(QoSManager.UPDATE_MAP));
        LocalBroadcastManager.getInstance(this).registerReceiver(mBatteryLowBroadcastReciever,new IntentFilter(QoSManager.BATTERY_LOW));

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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBatteryLowBroadcastReciever);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMapUpdateBroadcastReciever);
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
//        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        batteryStatusText.setText("Battery OKAY");
    }

    public void createLocationRequestBestForBattery() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        batteryStatusText.setText("Battery LOW");
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
        if(SystemState.BATTERY_OKAY == SystemStatus.getBatteryStatus()){
//            Toast.makeText(this, "BatteryStatus works", Toast.LENGTH_SHORT).show();
        }
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
                        String type = Integer.toString(location.type);
                        addMarkerToMap(location, type);
                    }
                }
            }
        }.execute();
    }
    private void addMarkerToMap(Location location, String type){
        LatLng latLng = new LatLng(location.latitude,location.longitude);
        if(type.equals(Integer.toString(NONSENSITIVE_DATA)))
            mMap.addMarker(new MarkerOptions().position(latLng).title(location.title));
        else if(type.equals(Integer.toString(SENSITIVE_DATA)))
            mMap.addMarker(new MarkerOptions().position(latLng).title(location.title)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        else if(type.equals(Integer.toString(SUPER_TOP_SECRET)))
            mMap.addMarker(new MarkerOptions().position(latLng).title(location.title)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

    }

    private HashMap<String, String> createHashMapWithCoordinates(double latitude, double longitude, String type, String reportText){
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
        RESTApiServer.setCoord(this,this, createHashMapWithCoordinates(latLng.latitude,latLng.longitude,type,reportText));
    }
    @SuppressLint("StaticFieldLeak")
    private void addMarkerToLocalDB(LatLng latLng, String title, String reportText, String type){
        final Location location = new Location();
        location.latitude = latLng.latitude;
        location.longitude = latLng.longitude;
        location.title = title;
        if(Integer.parseInt(type) == SENSITIVE_DATA){
            location.type = SENSITIVE_DATA;
        }
        Log.v("text to databse: ", reportText);
        location.reportText = reportText;
        addMarkerToMap(location, type);
        //put the location we've created previously into the local database
        //everything needs to be done on a separate thread due to android constraints
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                if(db.userDao().selectSpecificMarker(location.latitude, location.longitude) == null) //safety check for uniqueness
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
        if (!title.equals("manipulated")) {
            addMarkerToLocalDB(latLng, title, reportText,type);
            addMarkerToOnlineDB(latLng, title, reportText,type);
        }
        else{
            System.out.println("MANIPULATED");
            RESTApiServer.setManipluatedCoord(this, this, createHashMapWithCoordinates(latLng.latitude, latLng.longitude, "1", reportText));
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void deleteMarkerFromDatabase(){ // location for debugging, needs fixing
//        final double lat = marker.getPosition().latitude;
//        final double lon = marker.getPosition().longitude;
        if(SystemState.NETWORK_DOWN == SystemStatus.getNetworkStatus()) {
//            Toast.makeText(this,"Maps Network down, not deleting all info", Toast.LENGTH_LONG).show();
            return;
        }
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
//                db.userDao().removeSensitiveData();
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
        super.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        updateMap();
    }

    private void updateMap(){
        deleteMarkerFromDatabase();
        RESTApiServer.getCoord(this,this, currentPosition);
        setMarkersFromDatabaseOnMap(mMap);
    }

}

