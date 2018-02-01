package com.projectmpt.projectmpt;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class listDetailActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private TextView Textv;

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

    public void provideItem(View view) {
        Intent intent = new Intent(this, ProvideActivity.class);

        //Log.d("urb", "Click:" + position);
        //Intent intent = new Intent(ListActivity.this, listDetailActivity.class);

        Bundle needsBundle = getIntent().getExtras();

        intent.putExtras(needsBundle);

        startActivity(intent);
    }



}
