package com.GSI.SanchezVillafranca.gps;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.util.ArrayList;

public class GPSActivity extends AppCompatActivity {

    private MapView mapView;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private ArrayList<GeoPoint> geoPoints = new ArrayList<>();
    private LinearLayout gps_linearLayout2;
    private RelativeLayout gps_relativeLayout1;
    private TextView gps_tv_description;
    private Boolean calculateLocalization = false;
    private String text = "";
    private Boolean isRunning = true;
    private LocationRequest locationRequest;
    private Geocoder geocoder;

    public GPSActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        gps_relativeLayout1 = findViewById(R.id.gps_relativeLayout1);
        gps_linearLayout2 = findViewById(R.id.gps_linearLayout2);
        gps_tv_description = findViewById(R.id.gps_tv_description);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        LocationThread locationThread = new LocationThread(locationManager, geoPoints);


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        geocoder = new Geocoder(this);

        final Thread locationThread = new Thread(new Runnable() {


            private Context mContext;

            public Runnable setContext(Context context) {
                mContext = context;
                return this;
            }

            @Override
            public void run() {
                while (isRunning) {
                    try {
                        getCurrentLocation();
                        Thread.sleep(15000); // espera 15 segundos
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.setContext(getApplicationContext()));

        Button rute = findViewById(R.id.gps_get_location);
        rute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!calculateLocalization) {
                    try {
                        locationThread.start();
                        calculateLocalization = true;
                        geoPoints = new ArrayList<>();
                        rute.setText("Detener Ruta");
                        gps_relativeLayout1.setVisibility(View.VISIBLE);
                        gps_linearLayout2.setVisibility(View.GONE);
                    } catch (Exception e) {
                        recreate();
                    }
                } else if (calculateLocalization) {
                    // Detener y eliminar el hilo
                    isRunning = false;
                    locationThread.interrupt();
                    calculateLocalization = false;
                    generateMap();
                    rute.setText("Eliminar Trayecto");
                }
            }
        });
    }


    private void generateMap() {
        Context ctx = this.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        mapView = findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getController().setZoom(17.0);

        requestPermissionsIfNecessary(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET});
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mapView.setMultiTouchControls(true);


        CompassOverlay compassOverlay = new CompassOverlay(this, mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

//        geoPoints.add(new GeoPoint(38.990311, -3.921643));
//        geoPoints.add(new GeoPoint(38.990701, -3.919632));


        generateMarker(geoPoints.get(0), R.drawable.ic_people_blue, "Origen");
        generateMarker(geoPoints.get(geoPoints.size() - 1), R.drawable.ic_location_red, "Destino");

        Polyline polyline = new Polyline();
        polyline.setColor(Color.parseColor("#008080"));
        polyline.setPoints(geoPoints);
        mapView.getOverlayManager().add(polyline);
        mapView.getController().setCenter(geoPoints.get(geoPoints.size() / 2));
        //mapView.setMultiTouchControls(true);

        gps_relativeLayout1.setVisibility(View.GONE);
        gps_linearLayout2.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void generateMarker(GeoPoint geoPoint, int id, String title) {
        Drawable icon = getResources().getDrawable(id);
        int width = icon.getIntrinsicWidth();
        int height = icon.getIntrinsicHeight();
        icon.setBounds(0, 0, width, height);
        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(geoPoint);
        startMarker.setIcon(icon);
        startMarker.setTitle(title);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(startMarker);
    }

    private void getCurrentLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(GPSActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    try {
                        LocationServices.getFusedLocationProviderClient(GPSActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                LocationServices.getFusedLocationProviderClient(GPSActivity.this).removeLocationUpdates(this);
                                if (locationResult != null && locationResult.getLocations().size() > 0) {
                                    int index = locationResult.getLocations().size() - 1;
                                    geoPoints.add(new GeoPoint(locationResult.getLocations().get(index).getLatitude(), locationResult.getLocations().get(index).getLongitude()));
                                }
                            }
                        }, Looper.getMainLooper());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    private void turnOnGPS() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(GPSActivity.this, "GPS encendido", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(GPSActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }
}