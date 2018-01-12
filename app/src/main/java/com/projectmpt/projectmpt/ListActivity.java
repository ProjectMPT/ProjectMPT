package com.projectmpt.projectmpt;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ListActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ChildEventListener mChildEventListener;
    DatabaseReference mNeedsRef = FirebaseDatabase.getInstance().getReference("Needs");

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "MPTMapActivity";
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final BottomNavigationView mBtmView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mBtmView.setOnNavigationItemSelectedListener(this);



        final ListView lstNeeds = (ListView) findViewById(R.id.lstNeeds);

        //final ArrayList<String> myStringArray = new ArrayList<String>();

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
                               Log.d(TAG, "datasnaphot " + dataSnapshot.toString());

                                final List<Needs> listNeeds = new ArrayList<Needs>();

                                for(DataSnapshot item: dataSnapshot.getChildren())
                                {
                                    Needs N = item.getValue(Needs.class);

                                    if(item.child("longitude").getValue(Double.class) > location.getLongitude()-gridSize && item.child("longitude").getValue(Double.class) < location.getLongitude()+gridSize ) {
                                        Log.d(TAG, "key " + item.getKey());
                                        N.setKey(item.getKey());
                                        listNeeds.add(N);
                                    }

                                }


                                ArrayAdapter myAdapter = new ArrayAdapter<Needs>(ListActivity.this, android.R.layout.simple_list_item_1, listNeeds){
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                    //TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                                    text1.setText(listNeeds.get(position).getDescription());
                                   // text2.setText(listNeeds.get(position).getLocationdetails());
                                    return view;
                                }};



               // Log.d(TAG, "List: " + listNeeds.size());

                lstNeeds.setAdapter(myAdapter);


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



}

