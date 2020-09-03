package com.lambton.fcmanddatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.lambton.fcmanddatabase.model.ApplicationStatus;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LocationListener locationListener;
    LocationManager locationManager;
    public static final String TAG = "MainActivity";
    public static final int REQUEST_CODE = 1;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    double latitudeIntent;
    double longitudeIntent;
    ApplicationStatus applicationStatus;
    View userCredentials;
    View latLngValues;
    EditText nameET;
    Button submitBtn;
    TextView userNameTV;
    TextView latTV;
    TextView lngTV;
    TextView speedTV;
    double speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }

        findViewById(R.id.buttonCreateWidget).setOnClickListener(this);

        requestLocationPermission();

        notificationTestProcedure();

        initials();
    }

    private void initials() {
        userCredentials = findViewById(R.id.userCredentials);
        latLngValues = findViewById(R.id.coordinatesValues);

        //---------//
        nameET = findViewById(R.id.nameEditText);
        submitBtn = findViewById(R.id.buttonSubmitUserName);
        userNameTV = findViewById(R.id.userTextView);
        latTV = findViewById(R.id.latTextView);
        lngTV = findViewById(R.id.longTextView);
        speedTV = findViewById(R.id.speedTextView);
        userCredentials.setVisibility(View.VISIBLE);
        latLngValues.setVisibility(View.INVISIBLE);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameTV.setText("User: "+nameET.getText());
                userCredentials.setVisibility(View.INVISIBLE);
                latLngValues.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nameET.getWindowToken(), 0);
            }
        });

    }

    private void notificationTestProcedure() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
//To do//
                            alertToCloseApp();
                            return;
                        }

// Get the Instance ID token//
                        String token = task.getResult().getToken();
//                        String msg = getString(R.string.fcm_token, token);
                        Log.d(TAG, "Token: " + token);

                    }
                });
    }

    private void requestLocationPermission() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            Location lastLoc;
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, "onLocationChanged: " + location);

                latTV.setText("Latitude: " + String.format("%.6f", location.getLatitude()));
                lngTV.setText("Longitude: " + String.format("%.6f",location.getLongitude()));


                if (this.lastLoc != null)
                    speed = Math.sqrt(
                            Math.pow(location.getLongitude() - lastLoc.getLongitude(), 2)
                                    + Math.pow(location.getLatitude() - lastLoc.getLatitude(), 2)
                    ) / (location.getTime() - this.lastLoc.getTime());
                //if there is speed from location
                if (location.hasSpeed())
                    speed = location.getSpeed();
                this.lastLoc = location;

                speedTV.setText("Speed: "+ String.format("%.2f", speed)+"m/s");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            intent.putExtra("latitude",latitudeIntent);
            intent.putExtra("longitude", longitudeIntent);
            startService(new Intent(MainActivity.this, FloatingViewService.class));
            finish();
        } else if (Settings.canDrawOverlays(this)) {
            startService(new Intent(MainActivity.this, FloatingViewService.class));
            finish();
        } else {
            askPermission();
            Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
        }
      /*  FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("notifications");

        applicationStatus = new ApplicationStatus("Foreground: ","timestamp");

        myRef.push().setValue(applicationStatus);*/
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
      /*
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            intent.putExtra("latitude",latitudeIntent);
            intent.putExtra("longitude", longitudeIntent);
            startService(new Intent(MainActivity.this, FloatingViewService.class));
            finish();
        } else if (Settings.canDrawOverlays(this)) {
            startService(new Intent(MainActivity.this, FloatingViewService.class));
            finish();
        } else {
            askPermission();
            Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
        }

       */
        enterPictureInPicture();
    }

    @Override
    protected void onUserLeaveHint() {
        Log.d(TAG, "onUserLeaveHint: ");

//        finish();
    }

    private void alertToCloseApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test dialog");
        builder.setIcon(R.mipmap.fgf_logo);
        builder.setMessage("Content");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Do something
                dialog.dismiss();
            }});

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onPause() {
        super.onPause();

//        Toast.makeText(this, "onPause", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onPause: ");

    }

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        if(isInPictureInPictureMode){
//            getActionBar().hide();
        }else{
//            getActionBar().show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        enterPictureInPicture();
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


}