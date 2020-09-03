package com.lambton.fcmanddatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PictureInPictureParams;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 1;
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    //Fused location provider client
    private FusedLocationProviderClient mClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private PointerSpeedometer speedometer;
    double speed;
    double speedDouble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mClient = LocationServices.getFusedLocationProviderClient(this);

        notificationTestProcedure();

        initials();
    }

    private void initials() {
        speedometer = findViewById(R.id.speedViewMapActivity);
        Button mapPIP = findViewById(R.id.mapPIPBtn);
        mapPIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPictureInPicture();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (!hasLocationPermission()) {
            requestLocationPermission();
        } else {
            startUpdateLocation();
        }

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void notificationTestProcedure() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
//To do//
//                            alertToCloseApp();
                            return;
                        }

// Get the Instance ID token//
                        String token = task.getResult().getToken();
//                        String msg = getString(R.string.fcm_token, token);
                        Log.d(TAG, "Token: " + token);

                    }
                });
    }

    //MARK: start update location
    private void startUpdateLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);

        locationCallback = new LocationCallback() {
            Location lastLoc;
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mMap.clear();
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();

                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));

                    if (this.lastLoc != null)
                        speed = Math.sqrt(
                                Math.pow(location.getLongitude() - lastLoc.getLongitude(), 2)
                                        + Math.pow(location.getLatitude() - lastLoc.getLatitude(), 2)
                        ) / (location.getTime() - this.lastLoc.getTime());
                    //if there is speed from location
                    if (location.hasSpeed())
                        speed = location.getSpeed();
                    this.lastLoc = location;

                    speedDouble = Double.parseDouble(String.format("%.2f", speed));
                    speedDouble = speedDouble * 3.6179;
//                    "Speed: "+ String.format("%.2f", speedDouble)+"Km/h";

                    assert speedometer != null;
                    speedometer.setWithTremble(false);
                    speedometer.speedTo(Math.round(speedDouble), 500);


                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                    MyFirebaseMessagingService.user = "Anmol";
                    MyFirebaseMessagingService.lat = String.format("%.6f",location.getLatitude());
                    MyFirebaseMessagingService.lng = String.format("%.6f",location.getLongitude());
                    MyFirebaseMessagingService.speed = String.format("%.2f", speedDouble)+"Km/h";
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mClient.requestLocationUpdates(locationRequest, locationCallback, null);

    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setMessage("The permission is necessary")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestLocationPermission();
                            }
                        }).create();
            }else {
                startUpdateLocation();
            }
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        enterPictureInPicture();
    }

    @Override
    protected void onPause() {
        super.onPause();
        enterPictureInPicture();
    }

    public void enterPictureInPicture(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            Rational aspectRatio = new Rational(width, height);
            PictureInPictureParams.Builder mPictureInPictureParamsHandler = new PictureInPictureParams.Builder();
            mPictureInPictureParamsHandler.setAspectRatio(aspectRatio).build();

            enterPictureInPictureMode(mPictureInPictureParamsHandler.build());
        }

    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        if(isInPictureInPictureMode){
//            getActionBar().hide();
        }else{
//            getActionBar().show();
        }
    }
}