package com.projectmpt.projectmpt;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListTransportActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ClickListener {

    ChildEventListener mChildEventListener;
    DatabaseReference mTransportRef = FirebaseDatabase.getInstance().getReference("Transports");

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "MPTMapActivity";
    private FusedLocationProviderClient mFusedLocationClient;
    List<Transports> list;
    RecyclerView recycle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transport);

        recycle = (RecyclerView) findViewById(R.id.recycle_transport);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_transport);
        setSupportActionBar(myToolbar);

        final BottomNavigationView mBtmView = (BottomNavigationView) findViewById(R.id.bottom_navigation_transport);
        mBtmView.setOnNavigationItemSelectedListener(this);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {

                            final Double gridSize = 0.20;

                            Query query = mTransportRef.orderByChild("latitude").startAt(location.getLatitude()-gridSize).endAt(location.getLatitude()+gridSize);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    list = new ArrayList<Transports>();
                                    for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                                      //  if(dataSnapshot1.child("longitude").getValue(Double.class) > location.getLongitude()-gridSize && dataSnapshot1.child("longitude").getValue(Double.class) < location.getLongitude()+gridSize ) {

                                            Transports value = dataSnapshot1.getValue(Transports.class);

                                            Transports transports = new Transports();

                                            String needkey = value.getNeedkey();;
                                            String providelocationdetails = value.getProvidelocationdetails();
                                            String provideemail = value.getProvideowner();
                                            Double providelatitude = value.getProvidelatitude();
                                            Double providelongitude = value.getProvidelongitude();
                                            Long providetimefrom = value.getProvidetimefrom();
                                            Long providetimeto = value.getProvidetimeto();
                                            String heading = value.getHeading();
                                            String description = value.getDescription();
                                            String locationdetails = value.getLocationdetails();
                                            String email = value.getOwner();
                                            Double latitude = value.getLatitude();
                                            Double longitude = value.getLongitude();
                                            Long timefrom = value.getTimefrom();
                                            Long timeto = value.getTimeto();

                                            Location dest = new Location("dummyprovider");

                                            dest.setLatitude(providelatitude);
                                            dest.setLongitude(providelongitude);
                                            float [] dist = new float[1];
                                            Float totDistance = location.distanceTo(dest);

                                            Location.distanceBetween(providelatitude,providelongitude,latitude,longitude,dist);

                                            totDistance = totDistance + dist[0];

                                            //transports.setDistanceto(totDistance);
                                            Log.d("urb", "Distance: " +location.distanceTo(dest) + " transport distance=" + dist[0] );

                                            transports.setNeedkey(needkey);
                                            transports.setProvidelocationdetails(providelocationdetails);
                                            transports.setProvideowner(provideemail);
                                            transports.setProvidelatitude(providelatitude);
                                            transports.setProvidelongitude(providelongitude);
                                            transports.setProvidetimefrom(providetimefrom);
                                            transports.setProvidetimeto(providetimeto);
                                            transports.setHeading(heading);
                                            transports.setOwner(email);
                                            transports.setDescription(description);
                                            transports.setLocationdetails(locationdetails);
                                            transports.setLatitude(latitude);
                                            transports.setLongitude(longitude);
                                            transports.setTimefrom(timefrom);
                                            transports.setTimeto(timeto);



                                            list.add(transports);

                                        }

                                  //  }


                                    RecyclerAdapterTransports recyclerAdapter = new RecyclerAdapterTransports(list,ListTransportActivity.this);
                                    RecyclerView.LayoutManager recyce = new LinearLayoutManager(ListTransportActivity.this);

                                    recycle.setLayoutManager(recyce);
                                    recycle.setItemAnimator( new DefaultItemAnimator());
                                    recycle.setAdapter(recyclerAdapter);

                                    recyclerAdapter.setClickListener(ListTransportActivity.this);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);
        return true;
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_profile:

                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null) {

                    Intent intentp = new Intent(this, ProfileActivity.class);
                    startActivity(intentp);

                } else {

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    //.setLogo(@DrawableRes)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void itemClicked(View view, int position) {

        Log.d("urb", "Click:" + position);
        Intent intent = new Intent(ListTransportActivity.this, ListTransportDetailActivity.class);
        intent.putExtra("Key", list.get(position).getNeedkey());
        intent.putExtra("Heading", list.get(position).getHeading());
        intent.putExtra("Description", list.get(position).getDescription());
        intent.putExtra("LocationDetails", list.get(position).getLocationdetails());
        intent.putExtra("Owner", list.get(position).getOwner());
        intent.putExtra("Latitude", list.get(position).getLatitude());
        intent.putExtra("Longitude", list.get(position).getLongitude());
        intent.putExtra("TimeFrom", list.get(position).getTimefrom());
        intent.putExtra("TimeTo", list.get(position).getTimeto());
        intent.putExtra("DistanceTo", list.get(position).getDistanceto());
       // Log.d("urb", "What:" + list.get(position).getNeedkey());

        startActivity(intent);


    }



}
