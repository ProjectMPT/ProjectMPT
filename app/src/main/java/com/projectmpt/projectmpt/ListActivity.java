package com.projectmpt.projectmpt;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
//import com.firebase.ui.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ListActivity extends AppCompatActivity implements ClickListener {

    ChildEventListener mChildEventListener;
    DatabaseReference mNeedsRef = FirebaseDatabase.getInstance().getReference("Needs");

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "MPTMapActivity";
    private FusedLocationProviderClient mFusedLocationClient;
    List<Transports> list;
    RecyclerView recycle;

    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recycle = (RecyclerView) findViewById(R.id.recycle);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MeetActivity.class);
                startActivity(intent);
                //return true;
            }
        });


        Toolbar myToolbar = (Toolbar) findViewById(R.id.listtoolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.list_drawer_layout);

        NavigationView navigationView = findViewById(R.id.list_nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);


                        switch (menuItem.getItemId()) {

                            case R.id.settings:
                                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                                startActivity(intent);
                                return true;
                        }




                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });







        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {

                            final Double gridSize = 0.20;

                            Query query = mNeedsRef.orderByChild("latitude").startAt(location.getLatitude()-gridSize).endAt(location.getLatitude()+gridSize);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                list = new ArrayList<Transports>();
                                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                                    if(dataSnapshot1.child("longitude").getValue(Double.class) > location.getLongitude()-gridSize && dataSnapshot1.child("longitude").getValue(Double.class) < location.getLongitude()+gridSize ) {

                                        Needs value = dataSnapshot1.getValue(Needs.class);
                                        Transports transports = new Transports();

                                        String needkey = dataSnapshot1.getKey();
                                        String heading = value.getHeading();
                                        String description = value.getDescription();
                                        String locationdetails = value.getLocationdetails();
                                        String email = value.getOwner();
                                        Double latitude = value.getLatitude();
                                        Double longitude = value.getLongitude();
                                        Long timefrom = value.getTimefrom();
                                        Long timeto = value.getTimeto();

                                        Location dest = new Location("dummyprovider");
                                        dest.setLatitude(latitude);
                                        dest.setLongitude(longitude);

                                        transports.setDistanceto(location.distanceTo(dest));
                                        //Log.d("urb", "Distance: " +location.distanceTo(dest) );

                                        transports.setNeedkey(needkey);
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

                                }


                                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list,ListActivity.this);
                                RecyclerView.LayoutManager recyce = new LinearLayoutManager(ListActivity.this);

                                recycle.setLayoutManager(recyce);
                                recycle.setItemAnimator( new DefaultItemAnimator());
                                recycle.setAdapter(recyclerAdapter);

                                recyclerAdapter.setClickListener(ListActivity.this);


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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(View view, int position) {

         //   Log.d("urb", "Click:" + position);
            Intent intent = new Intent(ListActivity.this, listDetailActivity.class);
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

