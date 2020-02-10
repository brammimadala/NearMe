package com.lasys.app.nearme.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.lasys.app.nearme.R;
import com.lasys.app.nearme.adapter.DashBoardAdapter;
import com.lasys.app.nearme.constants.InternetPermission;

import static com.lasys.app.nearme.intrface.AppConstants.itemNames;
import static com.lasys.app.nearme.intrface.AppConstants.nearByPlace;

public class DashBoard extends AppCompatActivity implements AdapterView.OnItemClickListener {

    View view;
    String message = "No internet connection!";
    int duration = Snackbar.LENGTH_LONG;
    private  ImageView arrow_back_dashboard ;
    private static final int Request_user_location_code = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        view =  findViewById(R.id.mConstraintLayout);
        GridView gridview =  findViewById(R.id.grid_view);

        DashBoardAdapter dashBoardAdapter = new DashBoardAdapter(DashBoard.this);
        gridview.setAdapter(dashBoardAdapter);
        gridview.setOnItemClickListener(this);

        //arrow_back_dashboard= findViewById(R.id.arrow_back_dashboard);
        //arrow_back_dashboard.setOnClickListener(this);

    }

    //Gridview onItemClick Listner
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        if (InternetPermission.isOnline(DashBoard.this))
        {
            Intent intent = new Intent(DashBoard.this,MapView.class);

            intent.putExtra("fieldName",itemNames[position]);
            intent.putExtra("nearbyPlace",nearByPlace[position]);

            startActivity(intent);
        }
        else
        {
           showSnackbar(view, message, duration);
        }

    }

    @Override
    public void onBackPressed()
    {
        appExitDialog();
    }

    public void showSnackbar(View view, String message, int duration)
    {
        Snackbar.make(view, message, duration).show();
    }

    private boolean checkUserLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_location_code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_location_code);

            }
            return false;
        } else {

            return true;
        }
    }

    public void statusCheck()
    {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            buildAlertMessageNoGps();
        }
    }

    //**
    // Aleart dialog box shows GPSLocation Enabled or not ask user to enable enable it
    // **//
    private void buildAlertMessageNoGps() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }



    public void appExitDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Please confirm");
        builder.setMessage("Are you want to exit the app?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });

        // Create the alert dialog using alert dialog builder
        AlertDialog dialog = builder.create();

        // Finally, display the dialog when user press back button
        dialog.show();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
            statusCheck();

        }
    }
}
