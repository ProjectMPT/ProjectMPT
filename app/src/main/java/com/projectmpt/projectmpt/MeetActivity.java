package com.projectmpt.projectmpt;

import android.*;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MeetActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public Button cmdOutput;
    String strDateTime;
    Long epTimeFrom;
    Long epTimeTo;
    LatLng llNeedLocation;

    ScrollView mScrollView;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

   private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet);

        mapFrag = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
       mapFrag.getMapAsync(this);


        mScrollView = (ScrollView) findViewById(R.id.meet_scroll); //parent scrollview in xml, give your scrollview id value

        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        mScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });




    }


    public void cancelNeed(View view) {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

    }



    public void saveNeed(View view) {

        mDatabase = FirebaseDatabase.getInstance().getReference();



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            EditText txtNeed = (EditText) findViewById(R.id.txtNeed);
            String strNeed = txtNeed.getText().toString();

            EditText txtLocationDetails = (EditText) findViewById(R.id.txtLocation);
            String strLocationDetails = txtLocationDetails.getText().toString();


            DatabaseReference mNeedsRef = FirebaseDatabase.getInstance().getReference("Needs");
            Needs marker = new Needs(strNeed, strLocationDetails, user.getEmail(), llNeedLocation.latitude, llNeedLocation.longitude, epTimeFrom, epTimeTo);



            //mNeedsRef.push().setValue(marker);

            mNeedsRef.push().setValue(marker, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError,
                                               DatabaseReference databaseReference) {

                            if (databaseError != null) {
                                System.err.println("There was an error saving the location to GeoFire: " + databaseError);
                            } else {

                                String key = databaseReference.getKey();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NeedLocations");
                                GeoFire geoFire = new GeoFire(ref);

                                geoFire.setLocation(key, new GeoLocation(llNeedLocation.latitude, llNeedLocation.longitude));
                            }

                           }
                    });





            final Intent intent = new Intent(this, MainActivity.class);

            AlertDialog alertDialog = new AlertDialog.Builder(MeetActivity.this).create();
            alertDialog.setTitle("Saved");
            alertDialog.setMessage("New need saved successfully");
            alertDialog.setIcon(R.drawable.done_check_mark);

            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {


                    startActivity(intent);

                }
            });

            alertDialog.show();



        } else {

        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

        mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                llNeedLocation = marker.getPosition();
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }
        });


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }

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
            llNeedLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(llNeedLocation,18));

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(llNeedLocation)
                    .title("Location, hold and drag to fine tune")
                    .draggable(true));
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




    public void showTimePickerDialog(View v) {

        final Calendar c = Calendar.getInstance();

        Integer mYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer mMonth = Calendar.getInstance().get(Calendar.MONTH);
        Integer mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);


        cmdOutput = (Button) findViewById(v.getId());

        DatePickerDialog datePickerDialog = new DatePickerDialog(MeetActivity.this, new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Integer mHour = Calendar.getInstance().get(Calendar.HOUR);
                Integer mMinute = Calendar.getInstance().get(Calendar.MINUTE);

                if (cmdOutput.getId() == R.id.cmdTo) mHour = mHour + 2;

                strDateTime = (monthOfYear + 1) + "/" + dayOfMonth  + "/" + year;

                TimePickerDialog timePickerDialog = new TimePickerDialog(MeetActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                       strDateTime = strDateTime + " " + hourOfDay + ":" + minute;

                        cmdOutput.setText(strDateTime);

                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        try {

                            Date date = df.parse(strDateTime);

                            if (cmdOutput.getId() == R.id.cmdTo) {
                                epTimeTo = date.getTime(); }
                            else {
                                epTimeFrom = date.getTime();
                            }

                        } catch (java.text.ParseException e) {
                            // TODO

                        }

                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        },  mYear, mMonth, mDay);
        datePickerDialog.show();



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
                                ActivityCompat.requestPermissions(MeetActivity.this,
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


}
