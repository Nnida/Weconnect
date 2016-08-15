package com.example.nida.medhini;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by nida on 23-07-2016.
 */
public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "service tag";
    static LocationRequest locationRequest;
    static GoogleApiClient googleApiClient;

    boolean isConnected = false;


    DatabaseReference databaseReference;


    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }



    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(MyService.this, "Created Service", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCreate: ");

        databaseReference = FirebaseDatabase.getInstance().getReference();


        //LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//


        connectToGoogleApi();


    }

    public void connectToGoogleApi() {


        Log.d(TAG, "connectToGoogleApi: ");
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();


            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
                Log.d("TAG", "not connected");
            }
        } else {
            Log.e("TAG", "unable to connect to google play services.");
        }

    }


    @Override
    public void onConnected(Bundle bundle) {

        Log.d(TAG, "onConnected: ");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        isConnected = true;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }

        Log.d(TAG, "going to location callack");
        
        if (isConnected) {

            Log.d(TAG, "going to location callack 2");

            LocationServices.FusedLocationApi.requestLocationUpdates(
            googleApiClient, locationRequest, new com.google.android.gms.location.LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                            Log.d(TAG, "onLocationChanged() called with: " + "location = [" + location + "]");

                            Log.d("LOCATION", location.getLatitude() + " " + location.getLongitude() + "asdfg");
                            Toast.makeText(MyService.this, location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                            addToDb(location.getLatitude(), location.getLongitude());

                        }
                    });

        }


    }

    private void addToDb(double latitude, double longitude) {


        Log.d("tag", "addToDb: testing the db");


        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("lat", latitude);
        hashmap.put("lon", longitude);

        HashMap<String, Object> hm = new HashMap<>();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        hm.put("/" + uid + "/locations/" + getCurrentTimeStamp(), hashmap);

        databaseReference.updateChildren(hm);

        databaseReference.child("test").setValue("testing val");


    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static String getCurrentTimeStamp() {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date());

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

}

