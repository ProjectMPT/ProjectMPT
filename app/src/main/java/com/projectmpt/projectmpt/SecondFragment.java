package com.projectmpt.projectmpt;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SecondFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    public static final String ARG_PAGE = "ARG_PAGE";


    private int mPage;
 //   GoogleMap mGoogleMap;
  //  SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    //Marker mCurrLocationMarker;

    public static SecondFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        SecondFragment fragment = new SecondFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_second, container, false);


        EditText editText = (EditText) view.findViewById(R.id.txtLocation);

        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((MeetActivity)SecondFragment.this.getActivity()).locTxt = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

      //  SupportMapFragment mapFrag = (SupportMapFragment) this.getChildFragmentManager()
    //            .findFragmentById(R.id.map);

     //   if (mapFrag != null) mapFrag.getMapAsync(this);

    //    return view;
 //   }
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mGoogleMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }

   //     @Override
 //   public void onMapReady(GoogleMap googleMap)
 //   {
   //     mGoogleMap=googleMap;
   //     mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
     //   mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
 //       mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

    //    mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

         //   @Override
        //    public void onMarkerDragStart(Marker marker) {

      //      }

         //   @Override
        //    public void onMarkerDragEnd(Marker marker) {
        //        ((MeetActivity)SecondFragment.this.getActivity()).llNeedLocation = marker.getPosition();
         //   }
//
        //    @Override
         //   public void onMarkerDrag(Marker marker) {

      //     }
  //      });


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this.getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
             //   mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
      //      mGoogleMap.setMyLocationEnabled(true);
        }

        return view;
        
  }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            ((MeetActivity)SecondFragment.this.getActivity()).llNeedLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
       //     mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(((MeetActivity)SecondFragment.this.getActivity()).llNeedLocation,18));

        //    mGoogleMap.addMarker(new MarkerOptions()
       //             .position(((MeetActivity)SecondFragment.this.getActivity()).llNeedLocation)
        //            .title("Location, hold and drag to fine tune")
        //            .draggable(true));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}



    @Override
    public void onLocationChanged(Location location)
    {
//          mLastLocation = location;
//           if (mCurrLocationMarker != null) {
//               mCurrLocationMarker.remove();
//          }
//
//       // Place current location marker
//         LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//         MarkerOptions markerOptions = new MarkerOptions();
//         markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//         markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
//
//        //move map camera
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this.getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SecondFragment.this.getActivity(),
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(SecondFragment.this.getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }


}