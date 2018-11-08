package com.projectmpt.projectmpt;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class AddNewActivity extends AppCompatActivity {

    public String strLocation;
    public String strHeading;

    public Boolean bolFail = false;
    public String strMessage = "";
    public Double dblLatitude = 0.0;
    public Double dblLongitude = 0.0;
    private FusedLocationProviderClient mFusedLocationClient;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        Spinner spinner = (Spinner) findViewById(R.id.expire_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expire_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(this, GPSTrackerActivity.class);
                startActivityForResult(intent,1);

                Log.i("GPS", "REQUEST");

        } else {


        }


        EditText editText = (EditText) findViewById(R.id.txtNeedShort);
        editText.requestFocus();

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Bundle extras = data.getExtras();
            dblLongitude = extras.getDouble("Longitude");
            dblLatitude = extras.getDouble("Latitude");

            Log.i("GPS", dblLongitude.toString() + " " + dblLatitude.toString());

        }
    }

    public void cancelNeed(View view) {

        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);

    }


    public void saveNeed(View view){

        bolFail=false;
        strMessage="";
       // intTab = 2;


        EditText editText = (EditText) findViewById(R.id.txtLocation);

        strLocation = editText.getText().toString();
        //if (this.locTxt!=null) strLocation = this.locTxt;
        if (strLocation.length()<4) {
            bolFail=true;
            strMessage = strMessage + "Please enter location details\n";
           // intTab = 1;
        }

        editText = (EditText) findViewById(R.id.txtNeedShort);

        strHeading =  editText.getText().toString();;
        //if (this.headingTxt != null) strHeading = this.headingTxt;
        if (strHeading.length()<4) {
            bolFail=true;
            strMessage = strMessage + "Please enter a short description";
           // intTab = 0;
        }

        if (dblLatitude+dblLongitude==0.0) {
            bolFail=true;
            strMessage = strMessage + "Unable to read current location";
            // intTab = 0;
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
                           // showTab(intTab);
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        }else{

            saveNeedtoDataBase();

        }




    }

    public void saveNeedtoDataBase() {



        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            EditText editText = (EditText) findViewById(R.id.txtNeed);
            String strNeed = editText.getText().toString();

            Spinner spExpire = findViewById(R.id.expire_spinner);
            Long dblExpire = Long.valueOf(spExpire.getSelectedItem().toString());

            Long lngExpire = System.currentTimeMillis() + (dblExpire*3600000);

            DatabaseReference mNeedsRef = FirebaseDatabase.getInstance().getReference("Transports");
            //Needs marker = new Needs(strHeading, strNeed, strLocation, user.getEmail(), dblLatitude, dblLongitude, System.currentTimeMillis(), lngExpire);

            Transports marker = new Transports("","Provide", "", "",0 , 0, 0, 0, "",
                    strHeading, strNeed, strLocation,
                    user.getEmail().toString(), dblLatitude, dblLongitude,
                    System.currentTimeMillis(), lngExpire,"");



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

                        //geoFire.setLocation(key, new GeoLocation(dblLatitude, dblLongitude));


                        geoFire.setLocation(key, new GeoLocation(dblLatitude, dblLongitude), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {

                            }

                        });


                    }

                }
            });

            final Intent intent = new Intent(this, ListActivity.class);

            AlertDialog alertDialog = new AlertDialog.Builder(AddNewActivity.this).create();
            alertDialog.setTitle("Saved");
            alertDialog.setMessage("New need saved successfully");
            alertDialog.setIcon(R.drawable.done_check_mark);

            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {


                    startActivity(intent);

                }
            });

            alertDialog.show();

        }


    }

}
