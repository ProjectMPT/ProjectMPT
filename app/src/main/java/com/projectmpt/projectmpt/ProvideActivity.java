package com.projectmpt.projectmpt;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ProvideActivity extends AppCompatActivity  {

    public Double dblLatitude = 0.0;
    public Double dblLongitude = 0.0;

    public static final String ARG_PAGE = "ARG_PAGE";

    private DatabaseReference mDatabase;
    private FusedLocationProviderClient mFusedLocationClient;
    public Bundle needsBundle;
    private TextView Textv;
    private EditText Editv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provide);


        Spinner spinner = (Spinner) findViewById(R.id.commit_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expire_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        needsBundle = getIntent().getExtras();

        String newString;
        Long newLong;


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("Heading");
                Textv = findViewById(R.id.txtCommitHeader);
                Textv.setText(newString);

                newString= extras.getString("Description");
                Textv = findViewById(R.id.txtProvideDetail);
                Textv.setText(newString);

                newLong= extras.getLong("TimeTo");
                Textv = findViewById(R.id.timeCommitExpire);
                Date date=new Date(newLong);
                final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT);
                Textv.setText("Expires: " +  dateFormat.format(date));

                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

                if (extras.getString("ProvideOwner").equals(fUser.getEmail())) {

                    newString = extras.getString("ProvideLocationDetails");

                    Editv = findViewById(R.id.txtProvideLocation);
                    Editv.setText(newString);

                    Button btn = findViewById(R.id.cmdSaveCommit);
                    btn.setText("Update");

                }

            }
        } else {

        }



        EditText editText =  this.findViewById(R.id.txtProvideLocation);

        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(this, GPSTrackerActivity.class);
            startActivityForResult(intent,1);

            Log.i("GPS", "REQUEST");

        } else {


        }

        Button btn =  findViewById(R.id.cmdCancelCommit);
        btn.requestFocus();

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


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

              if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

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

    

    public void cancelProvide(View view) {

        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);

    }

    public void saveProvide(View view){

        Boolean bolFail = false;
        String strMessage = "";



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

            updateProvidetoDataBase();

        }


    }

    public void updateProvidetoDataBase() {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            TextView locText = (TextView) findViewById(R.id.txtProvideLocation);

            String strLocationDetails = locText.getText().toString();

            Spinner spExpire = findViewById(R.id.commit_spinner);

            Long dblExpire = Long.valueOf(spExpire.getSelectedItem().toString());

            Long lngExpire = System.currentTimeMillis() + (dblExpire*360000);

            Bundle needsBundle = getIntent().getExtras();



            DatabaseReference mTransportsRef = FirebaseDatabase.getInstance().getReference("Transports");

            DatabaseReference provideRef = mTransportsRef.child(needsBundle.getString("Key"));
            Map<String, Object> provideUpdates = new HashMap<>();
            provideUpdates.put("type", "Transport");
            provideUpdates.put("providelocationdetails", strLocationDetails);
            provideUpdates.put("providelatitude", dblLatitude);
            provideUpdates.put("providelongitude", dblLongitude);
            provideUpdates.put("provideowner", user.getEmail());
            provideUpdates.put("providetimefrom", System.currentTimeMillis());
            provideUpdates.put("providetimeto", lngExpire);


            provideRef.updateChildren(provideUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        System.err.println("There was an error saving: " + databaseError);
                    } else {



                    }

                }
            });





            final Intent intent = new Intent(this, ListActivity.class);

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
