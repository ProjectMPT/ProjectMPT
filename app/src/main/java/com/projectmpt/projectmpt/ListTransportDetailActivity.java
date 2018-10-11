package com.projectmpt.projectmpt;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ListTransportDetailActivity extends AppCompatActivity {

    private TextView Textv;
    private DatabaseReference mDatabase;

    public Bundle needsBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transport_detail);

        needsBundle = getIntent().getExtras();

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        Button cmdProvide = (Button) findViewById(R.id.cmdSaveTransport);
        Button cmdCancel = (Button) findViewById(R.id.cmdCancelTransport);
        Button cmdDelivered = (Button) findViewById(R.id.cmdDelivered);

        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("Heading");
                Textv = (TextView)findViewById(R.id.headingDetail_transport);
                Textv.setText(newString);

                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();


                if (needsBundle.getString("ProvideOwner").equals(fUser.getEmail())) {

                cmdProvide.setVisibility(View.GONE);
                cmdDelivered.setVisibility(View.VISIBLE);
                cmdCancel.setText("Back");

                };


                cmdProvide.setText("Transport " + newString);


                newString= extras.getString("Description");
                Textv = (TextView)findViewById(R.id.descriptionDetail_transport);
                Textv.setText(newString);
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("Description");
        }

    }


    public void cancelTransport(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    public void itemDelivered(View view) {

        DatabaseReference mTransportsRef = FirebaseDatabase.getInstance().getReference("Transports");
        mTransportsRef.child(needsBundle.getString("Key")).removeValue();

        mTransportsRef = FirebaseDatabase.getInstance().getReference("TransportLocations");
        mTransportsRef.child(needsBundle.getString("Key")).removeValue();

        final Intent intent = new Intent(this, ListActivity.class);

        AlertDialog alertDialog = new AlertDialog.Builder(ListTransportDetailActivity.this).create();
        alertDialog.setTitle("Delivered");
        alertDialog.setMessage(needsBundle.getString("Heading") + " marked as delivered, thank you!");
        alertDialog.setIcon(R.drawable.done_check_mark);

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                startActivity(intent);

            }
        });

        alertDialog.show();
    }


    public void transportItem(View view) {


            mDatabase = FirebaseDatabase.getInstance().getReference();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (auth.getCurrentUser() != null) {

                //Log.i("unique tag 0996", this.headingTxt);



                // Log.i("Bundle", needsBundle.getString("Key"));


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





                final Intent intent = new Intent(this, MainActivity.class);

                AlertDialog alertDialog = new AlertDialog.Builder(ListTransportDetailActivity.this).create();
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



