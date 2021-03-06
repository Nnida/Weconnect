package com.example.nida.medhini;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private static final String TAG = "service tag";
    String uuid;
    String data;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: "+dataSnapshot);
                HashMap<String,Object> hm = (HashMap<String, Object>) dataSnapshot.getValue();

                Set<String> set = hm.keySet();

                for(String uid:set){
                    Log.d(TAG, "onDataChange: "+uid);
                    HashMap<String,Object> hm1 = (HashMap<String, Object>) dataSnapshot.child(uid).child("locations").getValue();
                    Log.d(TAG, "onDataChange: "+hm1);
                    Set<String> set1 = hm1.keySet();
                    double lon = 0;
                    double lat = 0;
                    for(String key:set1){
                        HashMap<String,Object> locHm = (HashMap<String, Object>) hm1.get(key);
                        Log.d(TAG, "onDataChange: loc "+locHm);

                        lat = (double) locHm.get("lat");
                        lon = (double) locHm.get("lon");

                        Log.d(TAG, "onDataChange: lat "+lat+" lon "+lon);

                    }

                    LatLng latLng = new LatLng(lat,lon);

                    mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError);
            }
        });

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        startService(new Intent(MapsActivity.this,MyService.class));
    }
}
