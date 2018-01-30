package com.projectmpt.projectmpt;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
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
    List<Needs> list;
    RecyclerView recycle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recycle = (RecyclerView) findViewById(R.id.recycle);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final BottomNavigationView mBtmView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
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

                            Query query = mNeedsRef.orderByChild("latitude").startAt(location.getLatitude()-gridSize).endAt(location.getLatitude()+gridSize);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                list = new ArrayList<Needs>();
                                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                                    Needs value = dataSnapshot1.getValue(Needs.class);
                                    Needs need = new Needs();
                                    String name = value.getHeading();
                                    String address = value.getDescription();
                                    String email = value.getOwner();
                                    need.setHeading(name);
                                    need.setOwner(email);
                                    need.setDescription(address);
                                    list.add(need);

                                }


                                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list,ListActivity.this);
                                RecyclerView.LayoutManager recyce = new LinearLayoutManager(ListActivity.this);
                                /// RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
                                // recycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
                                recycle.setLayoutManager(recyce);
                                recycle.setItemAnimator( new DefaultItemAnimator());
                                recycle.setAdapter(recyclerAdapter);

                                //  Log.d(TAG, "datasnaphot " + dataSnapshot.toString());

                                //final List<Needs> listNeeds = new ArrayList<Needs>();
                               // final List<String> listNeeds2 = new ArrayList<String>();

//                                for(DataSnapshot item: dataSnapshot.getChildren())
//                                {
//                                    Needs N = item.getValue(Needs.class);
//
//                                    if(item.child("longitude").getValue(Double.class) > location.getLongitude()-gridSize && item.child("longitude").getValue(Double.class) < location.getLongitude()+gridSize ) {
//                                        Log.d(TAG, "key " + item.getKey());
//                                        N.setKey(item.getKey());
//                                        listNeeds.add(N);
//
//
//                                      //  listNeeds2.add(item.child("heading").getValue(String.class));
//
//                                    }
//
//                                }

                               // expandableListAdapter = new CustomexpandableListAdapter(ListActivity.this, listNeeds2, expandableListDetail);


//                                ArrayAdapter myAdapter = new ArrayAdapter<Needs>(ListActivity.this, android.R.layout.simple_list_item_1, listNeeds){
//                                @Override
//                                public View getView(int position, View convertView, ViewGroup parent) {
//                                    View view = super.getView(position, convertView, parent);
//                                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
//                                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);
//
//                                    text1.setText(listNeeds.get(position).getHeading());
//                                   //text2.setText(listNeeds.get(position).getDescription());
//                                    return view;
//                                }};



               // Log.d(TAG, "List: " + listNeeds.size());

              //  lstNeeds.setAdapter(expandableListAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
             //   Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });

//           view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//
//                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list,ListActivity.this);
//                RecyclerView.LayoutManager recyce = new GridLayoutManager(ListActivity.this,1);
//                /// RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
//                // recycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
//                recycle.setLayoutManager(recyce);
//                recycle.setItemAnimator( new DefaultItemAnimator());
//                recycle.setAdapter(recyclerAdapter);
//
//
//
//
//                                }
//                            });



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

