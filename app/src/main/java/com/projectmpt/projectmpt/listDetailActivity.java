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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.net.Uri.parse;

public class listDetailActivity extends AppCompatActivity {

    private TextView Textv;
    private String strURI;
    private String strURIDirections;

    public Bundle needsBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);

        Button cmdProvide = (Button) findViewById(R.id.cmdProvide);

        needsBundle = getIntent().getExtras();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        if (needsBundle.getString("ProvideOwner").equals(fUser.getEmail())) {

            cmdProvide.setVisibility(View.GONE);

        };

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
