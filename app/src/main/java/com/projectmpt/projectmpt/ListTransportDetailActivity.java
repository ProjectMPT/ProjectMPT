package com.projectmpt.projectmpt;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ListTransportDetailActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private TextView Textv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transport_detail);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_transport);
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

        final BottomNavigationView mBtmView = (BottomNavigationView) findViewById(R.id.bottom_navigationDetail_transport);
        mBtmView.setOnNavigationItemSelectedListener(this);

        Button cmdProvide = (Button) findViewById(R.id.cmdTransport);


        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("Heading");
                Textv = (TextView)findViewById(R.id.headingDetail_transport);
                Textv.setText(newString);


                cmdProvide.setText("Transport " + newString);


                newString= extras.getString("Description");
                Textv = (TextView)findViewById(R.id.descriptionDetail_transport);
                Textv.setText(newString);
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

    public void transportItem(View view) {
//        Intent intent = new Intent(this, TransportActivity.class);
//
//        Bundle needsBundle = getIntent().getExtras();
//
//        intent.putExtras(needsBundle);
//
//        startActivity(intent);
    }


}
