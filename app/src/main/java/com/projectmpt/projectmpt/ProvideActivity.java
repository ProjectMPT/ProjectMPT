package com.projectmpt.projectmpt;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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



public class ProvideActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    public Button cmdOutput;
    String strDateTime;
    Long epTimeFrom;
    Long epTimeTo;
    LatLng llNeedLocation;

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    private DatabaseReference mDatabase;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provide);

        final BottomNavigationView mBtmView = (BottomNavigationView) findViewById(R.id.bottom_navigationProvide);
        mBtmView.setOnNavigationItemSelectedListener(this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.providemap);
        mapFrag.getMapAsync(this);

        EditText editText = (EditText) this.findViewById(R.id.txtProvideLocation);

        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

    }




    public void showTimePickerDialogProvide(View v) {

        final Calendar c = Calendar.getInstance();

        Integer mYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer mMonth = Calendar.getInstance().get(Calendar.MONTH);
        Integer mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);


        cmdOutput = (Button) findViewById(v.getId());

        DatePickerDialog datePickerDialog = new DatePickerDialog(ProvideActivity.this, new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Integer mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                Integer mMinute = Calendar.getInstance().get(Calendar.MINUTE);

                if (cmdOutput.getId() == R.id.cmdTo) mHour = mHour + 2;

                strDateTime = (monthOfYear + 1) + "/" + dayOfMonth  + "/" + year;

                TimePickerDialog timePickerDialog = new TimePickerDialog(ProvideActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        strDateTime = strDateTime + " " + hourOfDay + ":" + minute;

                        cmdOutput.setText(strDateTime);

                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        try {

                            Date date = df.parse(strDateTime);

                            if (cmdOutput.getId() == R.id.cmdToProvide) {
                                epTimeTo = date.getTime(); }
                            else {
                                epTimeFrom = date.getTime();
                            }

                        } catch (java.text.ParseException e) {
                            // TODO

                        }

                    }
                }, mHour, mMinute, DateFormat.is24HourFormat(ProvideActivity.this));
                timePickerDialog.show();
            }
        },  mYear, mMonth, mDay);
        datePickerDialog.show();



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
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
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
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
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
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

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



  //  @Override
 //   public void onLocationChanged(Location location)
 //   {
//          mLastLocation = location;
//           if (mCurrLocationMarker != null) {
//               mCurrLocationMarker.remove();
//          }
//
//       // Place current location marker
//         LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//         MarkerOptions markerOptions = new MarkerOptions();
//         markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//         markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
//
//        //move map camera
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));

   // }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ProvideActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
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


                break;

        }
        return true;
    }

    public void cancelProvide(View view) {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void saveProvide(View view){

        Boolean bolFail = false;
        String strMessage = "";


        if (epTimeFrom==null){
            bolFail=true;
            strMessage = strMessage + "Please enter a from time\n";
        }

        if (epTimeTo==null){
            bolFail=true;
            strMessage = strMessage + "Please enter a to time\n";
        }

        TextView locText = (TextView) findViewById(R.id.txtProvideLocation);

        String strLocation = locText.getText().toString();

        if (strLocation.length()<4) {
            bolFail=true;
            strMessage = strMessage + "Please enter location details\n";

        }

       if (bolFail) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(strMessage);
            //builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                         //   showTab(intTab);
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        }else{

            saveProvidetoDataBase();

        }


    }

    public void saveProvidetoDataBase() {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            //Log.i("unique tag 0996", this.headingTxt);
            TextView locText = (TextView) findViewById(R.id.txtProvideLocation);

            String strLocationDetails = locText.getText().toString();

            //Intent intent = new Intent(this, listDetailActivity.class);

            Bundle needsBundle = getIntent().getExtras();

           // Log.i("Bundle", needsBundle.getString("Key"));


            DatabaseReference mTransportsRef = FirebaseDatabase.getInstance().getReference("Transports");

            Transports marker = new Transports(needsBundle.getString("Key"), strLocationDetails, user.getEmail().toString(), llNeedLocation.latitude, llNeedLocation.longitude, epTimeFrom, epTimeTo,
                    needsBundle.getString("Heading"), needsBundle.getString("Description"), needsBundle.getString("LocationDetails"),
                    needsBundle.getString("Owner"), needsBundle.getDouble("Latitude"), needsBundle.getDouble("Longitude"),
                    needsBundle.getLong("TimeFrom"), needsBundle.getLong("TimeTo"),0);

            String strKey = needsBundle.getString("Key");

            DatabaseReference mNeedsRef = FirebaseDatabase.getInstance().getReference("Needs");
            mNeedsRef.child(strKey).removeValue();

            DatabaseReference mNeedsLocationRef = FirebaseDatabase.getInstance().getReference("NeedLocations");
            mNeedsLocationRef.child(strKey).removeValue();

            mTransportsRef.push().setValue(marker, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        System.err.println("There was an error saving: " + databaseError);
                    } else {

                        String key = databaseReference.getKey();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TransportLocations");
                        GeoFire geoFire = new GeoFire(ref);

                        geoFire.setLocation(key, new GeoLocation(llNeedLocation.latitude, llNeedLocation.longitude));
                    }

                }
            });





            final Intent intent = new Intent(this, MainActivity.class);

            AlertDialog alertDialog = new AlertDialog.Builder(ProvideActivity.this).create();
            alertDialog.setTitle("Saved");
            alertDialog.setMessage("Saved successfully");
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



}
