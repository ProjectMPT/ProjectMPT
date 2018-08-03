

package com.projectmpt.projectmpt;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 123;
    Integer i = 0;
    Integer count = 0;

    //private DatabaseReference mDatabase;

    //ChildEventListener mChildEventListener;
    DatabaseReference mNeedsRef = FirebaseDatabase.getInstance().getReference("Needs");

    DatabaseReference mWelcome = FirebaseDatabase.getInstance().getReference("WelcomeMessage");

    private FusedLocationProviderClient mFusedLocationClient;
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // Intent intent = new Intent(this, SettingsActivity.class);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
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
    /*

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NeedLocations");
                            GeoFire geoFire = new GeoFire(ref);

                            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 10);

                            i=0;

                            geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {

                                @Override
                                public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                                    i = i + 1;
                                }

                                @Override
                                public void onDataExited(DataSnapshot dataSnapshot) {
                                }

                                @Override
                                public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                                    // ...
                                }

                                @Override
                                public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                                }

                                @Override
                                public void onGeoQueryReady() {
                             //       TextView tvNeeds = (TextView)findViewById(R.id.tvNeeds);
                              //      tvNeeds.setText(i.toString() + " needs in your area" );
                                }

                                @Override
                                public void onGeoQueryError(DatabaseError error) {

                              //      TextView tvNeeds = (TextView) findViewById(R.id.tvNeeds);
                              //      tvNeeds.setText("Error finding needs");
                                }

                            });


                            ref = FirebaseDatabase.getInstance().getReference("TransportLocations");
                            GeoFire geoFireTransport = new GeoFire(ref);

                            GeoQuery geoQueryTransport = geoFireTransport.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 10);

                            count=0;

                            geoQueryTransport.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {

                                @Override
                                public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                                    count = count + 1;
                                }

                                @Override
                                public void onDataExited(DataSnapshot dataSnapshot) {
                                }

                                @Override
                                public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                                    // ...
                                }

                                @Override
                                public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                                }

                                @Override
                                public void onGeoQueryReady() {
                                    TextView tvTransports = (TextView)findViewById(R.id.tvTransports);
                                    tvTransports.setText(count.toString() + " transport requests" );
                                }

                                @Override
                                public void onGeoQueryError(DatabaseError error) {

                                    TextView tvTransports = (TextView) findViewById(R.id.tvTransports);
                                    tvTransports.setText("Error finding transports");
                                }

                            });

                            if (location != null) {
                                // Logic to handle location object
                            }
                        }
                    });

            */

        }



        mWelcome.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String strWelcome = dataSnapshot.getValue().toString();

                TextView tvWelcome = (TextView)findViewById(R.id.tvWelcome);

                if (tvWelcome != null) tvWelcome.setText(strWelcome);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }




    //@Override
  //  public boolean onCreateOptionsMenu(Menu menu) {
   //     MenuInflater inflater = getMenuInflater();
   //     inflater.inflate(R.menu.appbar_menu, menu);
   //     return true;
 //   }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {


        } else {
            // not signed in
        }

    }


   /* @Override
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

    }*/


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





    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Uri photoUrl = user.getPhotoUrl();

                // if (photoUrl != null) {
                //ImageButton ibProfile = (ImageButton) findViewById(R.id.ibProfile);

                //   new DownloadImageTask((ImageButton) findViewById(R.id.action_profile))
                //       .execute(photoUrl.toString());
                //}

                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    //TextView tvStatus = (TextView)findViewById(R.id.tvStatus);
                    // tvStatus.setText("cancelled");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    //showSnackbar(R.string.unknown_error);
                    return;
                }
            }

            //showSnackbar(R.string.unknown_sign_in_response);
        }
    }


    public void startProfile(View view) {

        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {

            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);

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


    }

    public void startSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void startList(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }


  //  public void startList(View view) {
    //    Intent intent = new Intent(this, ListActivity.class);
     //   startActivity(intent);
   // }

   // public void startTransport(View view) {
  //      Intent intent = new Intent(this, ListTransportActivity.class);
   //     startActivity(intent);
  //  }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageButton bmImage;

        public DownloadImageTask(ImageButton bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    @Override
    public void onStop() {
        super.onStop();

    }

}