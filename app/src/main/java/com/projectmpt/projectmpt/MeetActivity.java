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
    String headingTxt;
    String locTxt;
    Integer intTab = 2;

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

    public void showTab(Integer intTab ) {

        TabLayout tabhost = (TabLayout)  this.findViewById(R.id.sliding_tabs);
        tabhost.getTabAt(intTab).select();

    }

    public void saveNeed(View view){




        Boolean bolFail = false;
        String strMessage = "";
        intTab = 2;

        if (epTimeFrom==null){
            bolFail=true;
            strMessage = strMessage + "Please enter a from time\n";
        }

        if (epTimeTo==null){
            bolFail=true;
            strMessage = strMessage + "Please enter a to time\n";
        }

        String strLocation = "";
        if (this.locTxt!=null) strLocation = this.locTxt;
        if (strLocation.length()<4) {
            bolFail=true;
            strMessage = strMessage + "Please enter location details\n";
            intTab = 1;
        }

        String strHeading = "";
        if (this.headingTxt != null) strHeading = this.headingTxt;
        if (strHeading.length()<4) {
            bolFail=true;
            strMessage = strMessage + "Please enter a short description";
            intTab = 0;
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
                            showTab(intTab);
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


            String strNeedHeader = this.headingTxt;

            String strNeed = this.needTxt;


            //Log.i("unique tag 0996", this.headingTxt);

            String strLocationDetails = this.locTxt;
            //Log.i("unique tag 2 0996", this.locTxt);

            DatabaseReference mNeedsRef = FirebaseDatabase.getInstance().getReference("Transports");
            //Transports marker = new Transports(strNeedHeader, strNeed, strLocationDetails, user.getEmail(), llNeedLocation.latitude, llNeedLocation.longitude, epTimeFrom, epTimeTo);

            Transports marker = new Transports("","Provide", "", "",0 , 0, 0, 0,
                    strNeedHeader, strNeed, strLocationDetails,
                    user.getEmail().toString(), llNeedLocation.latitude, llNeedLocation.longitude,
                    epTimeFrom, epTimeTo,0);


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

                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Integer mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                Integer mMinute = Calendar.getInstance().get(Calendar.MINUTE);

                if (cmdOutput.getId() == R.id.cmdTo) mHour = mHour + 2;

                strDateTime = (monthOfYear + 1) + "/" + dayOfMonth  + "/" + year;

                TimePickerDialog timePickerDialog = new TimePickerDialog(MeetActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                       strDateTime = strDateTime + " " + hourOfDay + ":" + minute;

                        cmdOutput.setText(strDateTime);

                        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        c.set(Calendar.MINUTE,minute);

                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        try {

                            Date date = df.parse(strDateTime);
                            date.getTime();

                            Log.d("Urban", c.getTimeInMillis() + ", " + date.getTime());

                            if (cmdOutput.getId() == R.id.cmdTo) {

                                epTimeTo = c.getTimeInMillis(); }
                            else {
                                epTimeFrom = c.getTimeInMillis();
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
