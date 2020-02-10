package com.lasys.app.nearme.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lasys.app.nearme.R;
import com.lasys.app.nearme.constants.InternetPermission;
import com.lasys.app.nearme.places.GetNearbyPlacesData;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapView extends AppCompatActivity implements View.OnClickListener,
                                                          OnMapReadyCallback,
                                                          GoogleApiClient.ConnectionCallbacks,
                                                          GoogleApiClient.OnConnectionFailedListener,
                                                          LocationListener {
    private TextView textView;
    private ImageView arrow_back ;

    private GoogleMap mgoogleMap;
    private GoogleApiClient mgoogleApiClient;
    private LocationRequest mlocationRequest;
   // private Location lastlocation;
    private Marker currentLocationUserMarker;
   // private static final int Request_user_location_code = 99;
    private int PROXIMITY_RADIUS = 10000;
    private LocationManager locationManager;

   private View view;
   private String message = "No internet connection!";
   private int duration = Snackbar.LENGTH_LONG;

    private int Count = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        if (googleServiceAvailable())
        {
            setContentView(R.layout.activity_map_view);
            initMap();
        } else
        {
            Toast.makeText(this, "Google Services Not Available", Toast.LENGTH_SHORT).show();
        }

        textView  = findViewById(R.id.mapview_text);
        arrow_back  = findViewById(R.id.arrow_back_mapView);

        Intent intent = getIntent();
        String itemName = intent.getStringExtra("fieldName");
        //String nearByPlace = intent.getStringExtra("nearbyPlace");

        textView.setText(itemName);

        arrow_back.setOnClickListener(this);

        view = findViewById(R.id.p_layout);
    }

    private boolean googleServiceAvailable()
    {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvalable = api.isGooglePlayServicesAvailable(MapView.this);

        if (isAvalable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvalable)) {
            Dialog dialog = api.getErrorDialog(MapView.this, isAvalable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to playservices", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    //Obtain the SupportMapFragment and get notified when the map is ready to be used.
    private void initMap()
    {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mgoogleMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling

            buildGoogleApi();

            mgoogleMap.setMyLocationEnabled(true);

            //Toast.makeText(this, "onMapReady", Toast.LENGTH_SHORT).show();

        }

        // Changing map type
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);


        // Enable / Disable zooming controls
        mgoogleMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable / Disable my location button
        mgoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Enable / Disable Compass icon
        mgoogleMap.getUiSettings().setCompassEnabled(true);

        // Enable / Disable Rotate gesture
        mgoogleMap.getUiSettings().setRotateGesturesEnabled(true);

        // Enable / Disable zooming functionality
        mgoogleMap.getUiSettings().setZoomGesturesEnabled(true);

    }

    //Initializing googleapi client
    protected synchronized void buildGoogleApi()
    {
        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                .build();

        mgoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        mlocationRequest = new LocationRequest();
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setInterval(3000);
        mlocationRequest.setFastestInterval(3000);

        //Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient, mlocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "ConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "ConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged( Location location)
    {
        //Count ++ ;

        //Toast.makeText(this, "Location Changed == "+Count, Toast.LENGTH_SHORT).show();
        //lastlocation = location;

        if (currentLocationUserMarker != null)
        {
            currentLocationUserMarker.remove();
        }

        if (location == null)
        {
            Toast.makeText(this, "Can't get Current Location", Toast.LENGTH_SHORT).show();
        } else
            {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

            //Searching Places Based On User Requirement
            searchPlaces(getIntent().getStringExtra("nearbyPlace"),location.getLatitude(), location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(ll);
            markerOptions.title(getAddressFromLocation(location.getLatitude(),location.getLongitude()));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerOptions.anchor(0.5f, 0.5f);
            //markerOptions.snippet("Your Current Location"+location.getLatitude()+""+location.getLongitude());
            markerOptions.snippet("Your Current Location");

            currentLocationUserMarker = mgoogleMap.addMarker(markerOptions);
            currentLocationUserMarker.showInfoWindow();

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll,10);
            mgoogleMap.animateCamera(cameraUpdate);

            //mgoogleMap.moveCamera(cameraUpdate);
            //Toast.makeText(this, "Latitude == "+ll.latitude +"\n Longitude == "+ll.longitude, Toast.LENGTH_LONG).show();

            if (mgoogleApiClient != null)
            {
                LocationServices.FusedLocationApi.removeLocationUpdates(mgoogleApiClient, this);
            }

        }
    }
    public  String  getAddressFromLocation(final double latitude, final double longitude)
    {

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        String address ="",city,state,country,postalCode,knownName ;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0)
        {
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
        }
        else
        {
            Toast.makeText(this, "Unable to get the Address", Toast.LENGTH_SHORT).show();
        }

        return address ;
    }


    //**
    // Checking client side mobile GPS Location Enable or Not
    //
    // **//
    /*public void statusCheck()
    {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            buildAlertMessageNoGps();

        }
    }*/


    //**
    // Aleart dialog box shows GPSLocation Enabled or not ask user to enable enable it
    // **//
    /*private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }*/


    //searching user requirement hospitals, banks, atms...

    public void searchPlaces(String nearbyPlace,double latitude, double longitude)
    {
        mgoogleMap.clear();
        String url = getUrl(latitude,longitude, nearbyPlace);

        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mgoogleMap;
        DataTransfer[1] = url;

        if (InternetPermission.isOnline(MapView.this))
        {
            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            getNearbyPlacesData.execute(DataTransfer);
        }
        else
        {
            showSnackbar(view,message,duration);
        }

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyB9RFpON4pOpDUuPwENK1hh7i83r5vqWek");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.arrow_back_mapView :
            {
               finish();
               break;
            }
        }
    }

    public void showSnackbar(View view, String message, int duration)
    {
        Snackbar.make(view, message, duration).show();
    }



    /*@Override
    protected void onStart()
    {
        mgoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        mgoogleApiClient.disconnect();
        super.onStop();
    }*/
}
