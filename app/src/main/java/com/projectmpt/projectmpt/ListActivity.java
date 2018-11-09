package com.projectmpt.projectmpt;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;


public class ListActivity extends AppCompatActivity implements ClickListener {

    DatabaseReference mNeedsRef = FirebaseDatabase.getInstance().getReference("Transports");

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "MPTMapActivity";
    private FusedLocationProviderClient mFusedLocationClient;
    List<Transports> list;
    RecyclerView recycle;

    private DrawerLayout mDrawerLayout;
    private String strURI;
    public Integer NoNeedsVisible = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recycle = findViewById(R.id.recycle);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddNewActivity.class);
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

                            Query query = mNeedsRef;
                            query.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                list = new ArrayList<Transports>();
                                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                                   // if(dataSnapshot1.child("longitude").getValue(Double.class) > location.getLongitude()-gridSize && dataSnapshot1.child("longitude").getValue(Double.class) < location.getLongitude()+gridSize ) {

                                        NoNeedsVisible = 8;

                                        Transports value = dataSnapshot1.getValue(Transports.class);
                                        Transports transports = new Transports();

                                        String needkey = dataSnapshot1.getKey();
                                        String type = value.getType();
                                        String heading = value.getHeading();
                                        String description = value.getDescription();
                                        String provideowner = value.getProvideowner();
                                        Double providelatitude = value.getProvidelatitude();
                                        Double providelongitude = value.getProvidelongitude();
                                        String locationdetails = value.getLocationdetails();
                                        String providelocationdetails = value.getProvidelocationdetails();
                                        //String distanceto = value.getDistanceto();
                                        String email = value.getOwner();
                                        String transportowner = value.getTransportowner();
                                        Double latitude = value.getLatitude();
                                        Double longitude = value.getLongitude();
                                        Long timefrom = value.getTimefrom();
                                        Long timeto = value.getTimeto();
                                        Long providetimeto = value.getProvidetimeto();

                                        if(type.equals("Provide")){
                                            transports.setDistanceto(calculateDistanceInMiles(location.getLatitude(), location.getLongitude(), latitude, longitude) + " miles");
                                        }else{
                                            transports.setDistanceto(calculateDistanceInMiles(location.getLatitude(), location.getLongitude(), latitude, longitude) + " + "
                                                    + calculateDistanceInMiles(providelatitude, providelongitude, latitude, longitude) + " miles");
                                        }


                                        transports.setNeedkey(needkey);
                                        transports.setType(type);
                                        transports.setHeading(heading);
                                        transports.setOwner(email);
                                        transports.setProvideowner(provideowner);
                                        transports.setProvidelatitude(providelatitude);
                                        transports.setProvidelongitude(providelongitude);
                                        transports.setTransportowner(transportowner);
                                        transports.setDescription(description);
                                        transports.setLocationdetails(locationdetails);
                                        transports.setProvidelocationdetails(providelocationdetails);
                                        transports.setLatitude(latitude);
                                        transports.setLongitude(longitude);
                                        transports.setTimefrom(timefrom);
                                        transports.setTimeto(timeto);
                                        transports.setProvidetimeto(providetimeto);

                                        list.add(transports);



                                }


                                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list,ListActivity.this);
                                RecyclerView.LayoutManager recyce = new LinearLayoutManager(ListActivity.this);

                                recycle.setLayoutManager(recyce);
                                recycle.setItemAnimator( new DefaultItemAnimator());
                                recycle.setAdapter(recyclerAdapter);

                                recyclerAdapter.setClickListener(ListActivity.this);

                                TextView txtNoNeed = findViewById(R.id.txtNoNeeds);
                                if (NoNeedsVisible==8) {
                                    txtNoNeed.setVisibility(View.GONE);
                                }else{
                                    txtNoNeed.setText("No current needs in this area, add a new need by clicking the + button below.");
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

        switch(view.getId()  ) {

            case R.id.imgMap:

                if(list.get(position).getType().equals("Provide")){
                    strURI = "geo:0,0?q=" + list.get(position).getLatitude() + ","
                            + list.get(position).getLongitude() + "(" + list.get(position).getHeading()
                            + ")&z=18";
                }else{

                    strURI = "https://www.google.com/maps/dir/?api=1&destination=" + list.get(position).getLatitude() + "%2C" +
                            list.get(position).getLongitude()+"&waypoints="+
                            list.get(position).getProvidelatitude() + "%2C" +
                            list.get(position).getProvidelongitude() +
                            "&travelmode=walking";
                }



               //
                Uri gmmIntentUri = Uri.parse(strURI);
                Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        startActivity(unrestrictedIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        Toast.makeText(this, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }

              break;

            default:

                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();


                if(list.get(position).getType().equals("Transport")) {


                    if (list.get(position).getProvideowner().equals(fUser.getEmail())) {
                        intent = new Intent(ListActivity.this, ProvideActivity.class);
                    } else {
                        intent = new Intent(ListActivity.this, TransportActivity.class);
                    }

                }else if(list.get(position).getType().equals("In progress ")) {

                   // Log.d("urb", "Progress: " + fUser.getEmail());

                        if(list.get(position).getTransportowner().equals(fUser.getEmail())){
                            intent = new Intent(ListActivity.this, TransportActivity.class);
                        }else{
                            intent = new Intent(ListActivity.this, ListActivity.class);
                        }

                }else {

                    if(list.get(position).getOwner().equals(fUser.getEmail())){
                        intent = new Intent(ListActivity.this, AddNewActivity.class);
                    }else{
                        intent = new Intent(ListActivity.this, ProvideActivity.class);
                    }

                }




                intent.putExtra("Key", list.get(position).getNeedkey());
                intent.putExtra("Heading", list.get(position).getHeading());
                intent.putExtra("Description", list.get(position).getDescription());
                intent.putExtra("LocationDetails", list.get(position).getLocationdetails());
                intent.putExtra("Owner", list.get(position).getOwner());
                intent.putExtra("ProvideOwner", list.get(position).getProvideowner());
                intent.putExtra("ProvideLatitude", list.get(position).getProvidelatitude());
                intent.putExtra("ProvideLongitude", list.get(position).getProvidelongitude());
                intent.putExtra("ProvideLocationDetails", list.get(position).getProvidelocationdetails());
                intent.putExtra("ProvideTimeTo", list.get(position).getProvidetimeto());
                intent.putExtra("TransportOwner", list.get(position).getTransportowner());
                intent.putExtra("Latitude", list.get(position).getLatitude());
                intent.putExtra("Longitude", list.get(position).getLongitude());
                intent.putExtra("TimeFrom", list.get(position).getTimefrom());
                intent.putExtra("TimeTo", list.get(position).getTimeto());
                intent.putExtra("DistanceTo", list.get(position).getDistanceto());



                startActivity(intent);

        }
    }

    public int calculateDistanceInMiles(double userLat, double userLng,
                                        double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(3960 * c));
    }

}

