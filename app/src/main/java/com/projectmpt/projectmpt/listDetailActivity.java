package com.projectmpt.projectmpt;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.net.Uri.parse;

public class listDetailActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private TextView Textv;
    private String strURI;
    private String strURIDirections;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Project MPT");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final BottomNavigationView mBtmView = (BottomNavigationView) findViewById(R.id.bottom_navigationDetail);
        mBtmView.setOnNavigationItemSelectedListener(listDetailActivity.this);

        Button cmdProvide = (Button) findViewById(R.id.cmdProvide);


        String newString;
        Double newDouble;
        Long newLong;

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("Heading");
                Textv = (TextView)findViewById(R.id.headingDetail);
                Textv.setText(newString);

                cmdProvide.setText("Provide " + newString);

                newString= extras.getString("Description");
                Textv = (TextView)findViewById(R.id.descriptionDetail);
                Textv.setText(newString);

                newLong= extras.getLong("TimeFrom");
                Textv = (TextView)findViewById(R.id.timeEntered);
                Date date=new Date(newLong);
                Textv.setText("Entered: " +  df.format(date));

                newLong= extras.getLong("TimeTo");
                date=new Date(newLong);
                Textv = (TextView)findViewById(R.id.timeExpire);
                Textv.setText("Expires: " + df.format(date));

                newDouble= extras.getDouble("DistanceTo");
                Textv = (TextView)findViewById(R.id.distanceDetail);
                Textv.setText(String.format("%.1f",(newDouble*0.00062137)) + " miles away");

                strURI = "geo:0,0?q=" + extras.getDouble("Latitude") + ","
                 + extras.getDouble("Longitude") + "(" + extras.getString("Heading")
                 + ")&z=18";

                strURIDirections = "google.navigation:q=" + extras.getDouble("Latitude") + ","
                        + extras.getDouble("Longitude");


            }
        } else {
            newString= (String) savedInstanceState.getSerializable("Description");
        }

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

    public void provideMap(View view) {

        Uri gmmIntentUri = Uri.parse(strURI);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

    }

    public void provideDirections(View view) {

        Uri gmmIntentUri = Uri.parse(strURIDirections);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

    }


    public void provideItem(View view) {
        Intent intent = new Intent(this, ProvideActivity.class);

        Bundle needsBundle = getIntent().getExtras();

        intent.putExtras(needsBundle);

        startActivity(intent);
    }



}
