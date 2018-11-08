package com.projectmpt.projectmpt;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TransportActivity extends AppCompatActivity {

DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Transports");

public Bundle needsBundle;
private TextView Textv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);


        Spinner spinner = (Spinner) findViewById(R.id.transport_spinner);

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
                Textv = findViewById(R.id.txtNeedTransportHeader);
                Textv.setText(newString);

                newString= extras.getString("Description");
                Textv = (TextView)findViewById(R.id.txtNeedTransportDetail);
                Textv.setText(newString);


                newString= extras.getString("LocationDetails");
                Textv = findViewById(R.id.txtNeedLocationDetail);
                Textv.setText(newString);

                newLong= extras.getLong("TimeTo");
                Textv = (TextView)findViewById(R.id.timeNeedExpire);
                Date date=new Date(newLong);
                final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT);
                Textv.setText("Delivery Expires: " +  dateFormat.format(date));

               newString= extras.getString("ProvideLocationDetails");
                Textv = (TextView)findViewById(R.id.txtProvideLocationDetail);
                Log.d("UBBE",newString + " huh?");
                Textv.setText(newString);

                newLong= extras.getLong("ProvideTimeTo");
                Textv = (TextView)findViewById(R.id.timeProvideExpire);
                date =  new Date(newLong);
                //final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT);
                Textv.setText("Pickup Expires: " +  dateFormat.format(date));


            }
        } else {
            newString= (String) savedInstanceState.getSerializable("Description");
        }





    }

    public void cancelTransport(View view) {

        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);

    }

    public void saveTransport(View view){

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            DatabaseReference mTransportsRef = FirebaseDatabase.getInstance().getReference("Transports");

            DatabaseReference provideRef = mTransportsRef.child(needsBundle.getString("Key"));
            Map<String, Object> provideUpdates = new HashMap<>();
            provideUpdates.put("type", "In progress ");
            provideUpdates.put("transportowner", user.getEmail());

            provideRef.updateChildren(provideUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        System.err.println("There was an error saving: " + databaseError);
                    } else {

                    }
                    ;


                }


            });





            final Intent intent = new Intent(this, ListActivity.class);

            AlertDialog alertDialog = new AlertDialog.Builder(TransportActivity.this).create();
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



    public void transportDirections(View view) {

        Bundle extras = getIntent().getExtras();

        String strURI = "https://www.google.com/maps/dir/?api=1&destination=" + extras.getDouble("Latitude") + "%2C" +
                extras.getDouble("Longitude") + "&waypoints=" +
                extras.getDouble("ProvideLatitude") + "%2C" +
                extras.getDouble("ProvideLongitude") +
                "&travelmode=walking";
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

    }





}

