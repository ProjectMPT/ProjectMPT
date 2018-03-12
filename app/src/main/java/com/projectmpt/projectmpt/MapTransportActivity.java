package com.projectmpt.projectmpt;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MapTransportActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, BottomNavigationView.OnNavigationItemSelectedListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    private FusedLocationProviderClient mFusedLocationClient;

    private static final String TAG = "MPTMapTransportActivity";



    HashMap<String, Transports> markerData = new HashMap<String, Transports>();
    private DatabaseReference mDatabase;

    ChildEventListener mChildEventListener;
    DatabaseReference mNeedsRef = FirebaseDatabase.getInstance().getReference("Transports");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_transport);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.transportmap);
        mapFrag.getMapAsync(this);

        BottomNavigationView mBtmView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mBtmView.setOnNavigationItemSelectedListener(this);



    }


    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    @Override
    public void onStop() {
        if (mChildEventListener != null)
            mNeedsRef.removeEventListener(mChildEventListener);
        super.onStop();
    }

    private void addMarkersToMap(final GoogleMap map) {

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {


                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NeedLocations");

                            final Double gridSize = 0.20;
                            final Double lowLong = location.getLongitude() - gridSize;
                            final Double highLong = location.getLongitude() + gridSize;

                            Query query = mNeedsRef.orderByChild("latitude").startAt(location.getLatitude() - gridSize).endAt(location.getLatitude() + gridSize);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                                        // Needs marker = item.getValue(Needs.class);
                                        Transports markerdata = item.getValue(Transports.class);

                                        if (markerdata.getLongitude() > lowLong && markerdata.getLongitude() < highLong) {

                                            Long millis = markerdata.getTimeto() - System.currentTimeMillis();
                                            Long hours = TimeUnit.MILLISECONDS.toHours(millis);
                                            Long mins = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));


                                            Location dest = new Location("dummyprovider");
                                            dest.setLatitude(markerdata.getLatitude());
                                            dest.setLongitude(markerdata.getLongitude());

                                            float distanceTo = location.distanceTo(dest);

                                            markerdata.setDistanceto(distanceTo);

                                            LatLng mlocation = new LatLng(markerdata.getLatitude(), markerdata.getLongitude());
                                            Marker newMarker = map.addMarker(new MarkerOptions().position(mlocation).title(markerdata.getHeading()).snippet(String.format("%.1f",(distanceTo*0.00062137)) + " miles away, expires: " + hours + ":" + mins));

                                            markerData.put(newMarker.getId(),markerdata);

                                            LatLng mprovidelocation = new LatLng(markerdata.getProvidelatitude(), markerdata.getProvidelongitude());
                                            Marker newProvideMarker = map.addMarker(new MarkerOptions().position(mprovidelocation).title(markerdata.getHeading()).snippet(String.format("%.1f",(distanceTo*0.00062137)) +
                                                    " miles away, expires: " + hours + ":" + mins).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_help)));

                                            markerData.put(newMarker.getId(),markerdata);


                                            map.addPolyline(new PolylineOptions().add(mprovidelocation, mlocation).width(7).color(Color.MAGENTA));



                                            //  Log.w(TAG, "AddMarker: " + mlocation.toString() + description);

                                        }

                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Getting Post failed, log a message
                                    //   Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                }
                            });


                            if (location != null) {
                                // Logic to handle location object
                            }
                        }
                    });

        }






    }


    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }

        addMarkersToMap(googleMap);


        googleMap.setOnInfoWindowClickListener(MapTransportActivity.this);



    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);



            LatLng currentlatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlatLng,13));

        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}



    @Override
    public void onLocationChanged(Location location)
    {
        //  mLastLocation = location;
        //   if (mCurrLocationMarker != null) {
        //       mCurrLocationMarker.remove();
        //  }

        //Place current location marker
        // LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // MarkerOptions markerOptions = new MarkerOptions();
        // markerOptions.position(latLng);
        //markerOptions.title("Current Position");
        // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        //mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_home:

                Intent intentMain = new Intent(this, MainActivity.class);
                startActivity(intentMain);
                break;

            case R.id.action_add_new:

                Intent intentList = new Intent(this, MeetActivity.class);
                startActivity(intentList);
                break;

            case R.id.action_help:

                // Intent intentNew = new Intent(this, ListActivity.class);
                //startActivity(intentNew);



        }
        return true;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapTransportActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {

        Transports markervalues = markerData.get(marker.getId());



        if (marker.equals(marker)) {
            Intent intent = new Intent(MapTransportActivity.this, ListTransportDetailActivity.class);
            intent.putExtra("Key", markervalues.getNeedkey());
            intent.putExtra("Heading", markervalues.getHeading());
            intent.putExtra("Description", markervalues.getDescription());
            intent.putExtra("LocationDetails", markervalues.getLocationdetails());
            intent.putExtra("Owner", markervalues.getOwner());
            intent.putExtra("Latitude", markervalues.getLatitude());
            intent.putExtra("Longitude", markervalues.getLongitude());
            intent.putExtra("TimeFrom", markervalues.getTimefrom());
            intent.putExtra("TimeTo", markervalues.getTimeto());
            intent.putExtra("DistanceTo", markervalues.getDistanceto());


            // Log.d("urb", "What:" + list.get(position).getNeedkey());

            startActivity(intent);
        }

    }

}