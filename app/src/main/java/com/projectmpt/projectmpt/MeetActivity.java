package com.projectmpt.projectmpt;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TabHost;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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


public class MeetActivity extends AppCompatActivity {

    //FragmentPagerAdapter adapterViewPager;

    public Button cmdOutput;
    String strDateTime;
    Long epTimeFrom;
    Long epTimeTo;
    LatLng llNeedLocation;
    String needTxt;
    String locTxt;

    //ScrollView mScrollView;
    //GoogleMap mGoogleMap;
    //SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

   private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                MeetActivity.this));



        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);



    }


    public void cancelNeed(View view) {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

    }


    public void cmdNext1(View view) {

        TabLayout tabhost = (TabLayout)  this.findViewById(R.id.sliding_tabs);
        tabhost.getTabAt(1).select();

    }

    public void cmdNext2(View view) {

        TabLayout tabhost = (TabLayout)  this.findViewById(R.id.sliding_tabs);
        tabhost.getTabAt(2).select();

    }


    public void saveNeed(View view) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            String strNeed = this.needTxt;
            Log.i("unique tag 0996", this.needTxt);

            String strLocationDetails = this.locTxt;
            Log.i("unique tag 0996", this.locTxt);

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


    public void showTimePickerDialog(View v) {

        final Calendar c = Calendar.getInstance();

        Integer mYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer mMonth = Calendar.getInstance().get(Calendar.MONTH);
        Integer mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);


        cmdOutput = (Button) findViewById(v.getId());

        DatePickerDialog datePickerDialog = new DatePickerDialog(MeetActivity.this, new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Integer mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
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
                }, mHour, mMinute, DateFormat.is24HourFormat(MeetActivity.this));
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


    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String tabTitles[] = new String[] { "What", "Where", "When" };
        private Context context;

        public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return FirstFragment.newInstance(0);

                case 1: // Fragment # 0 - This will show FirstFragment different title

                    return SecondFragment.newInstance(1);
                case 2: // Fragment # 1 - This will show SecondFragment
                    return ThirdFragment.newInstance(2);
                default:
                    return null;

            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }



}
