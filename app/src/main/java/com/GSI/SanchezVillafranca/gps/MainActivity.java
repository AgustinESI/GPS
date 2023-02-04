package com.GSI.SanchezVillafranca.gps;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.GSI.SanchezVillafranca.gps.model.Position;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView resultado, tv_country, tv_country_content, tv_city, tv_city_content, tv_postal_code, tv_postal_code_content, tv_state, tv_state_content, tv_address, tv_address_content, tv_destino_title;
    private EditText et_destino;
    private ImageView iv_map;
    private LocationRequest locationRequest;
    // Hold a reference to the current animator, so that it can be canceled mid-way.
    private Animator currentAnimator;
    // The system "short" animation time duration, in milliseconds. This duration is ideal
    // for subtle animations or animations that occur very frequently.
    private int shortAnimationDuration;
    private Position position_start;
    private Position position_end;
    private Geocoder geocoder;
    private Button btn_location;
    private RequestQueue requestQueue;
    private static final String API_KEY = "lCDUb5Ol6Xos1uRifLp-DFTlgbuXjyMDn73PSETQXhc";
    private static final String DEFAULT_PATH_GEOCODE = "https://geocode.search.hereapi.com/v1/geocode";
    private static final String DEFAULT_PATH_ROUTING = "https://image.maps.ls.hereapi.com/mia/1.6/routing";
    private String city_name = "";
    private ProgressBar pb_loading;
    private LinearLayout linear_layout;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_country = findViewById(R.id.tv_country);
        tv_country_content = findViewById(R.id.tv_country_content);
        tv_city = findViewById(R.id.tv_city);
        tv_city_content = findViewById(R.id.tv_city_content);
        tv_postal_code = findViewById(R.id.tv_postal_code);
        tv_postal_code_content = findViewById(R.id.tv_postal_code_content);
        tv_state = findViewById(R.id.tv_state);
        tv_state_content = findViewById(R.id.tv_state_content);
        tv_address = findViewById(R.id.tv_address);
        tv_address_content = findViewById(R.id.tv_address_content);
        resultado = findViewById(R.id.resultado);
        tv_destino_title = findViewById(R.id.tv_destino_title);
        et_destino = findViewById(R.id.et_destino);
        iv_map = (ImageView) findViewById(R.id.iv_map);
        pb_loading = findViewById(R.id.pb_loading);
        linear_layout = findViewById(R.id.content);

        btn_location = (Button) findViewById(R.id.button);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        geocoder = new Geocoder(this);
        requestQueue = Volley.newRequestQueue(this);
        position_start = new Position();
        position_end = new Position();


        getCurrentLocation();

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_destino.length() > 0 && !et_destino.getText().equals("") && et_destino.getText().length() > 0) {
                    sendRequestGeocode(et_destino.getText().toString());
                }
            }
        });
        pb_loading.setVisibility(View.GONE);
        linear_layout.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.mapa:
                //Intent intent1 = new Intent(this, MainActivity.class);
                //this.startActivity(intent1);
                //return true;
                Toast.makeText(this, "Mapa", Toast.LENGTH_LONG).show();
                break;
            case R.id.ruta:
                Toast.makeText(this, "Ruta", Toast.LENGTH_LONG).show();
                break;
            case R.id.tiempo:
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                this.startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCurrentLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);

                            LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);

                            if (locationResult != null && locationResult.getLocations().size() > 0) {

                                int index = locationResult.getLocations().size() - 1;
                                position_start.setLat(round(locationResult.getLocations().get(index).getLatitude(), 4));
                                position_start.setLng(round(locationResult.getLocations().get(index).getLongitude(), 4));
                                //tv_lat.setText("Latitude: " + latitude + "\n" + "Longitude: " + longitude);

                                List<Address> addresses;

                                try {
                                    addresses = geocoder.getFromLocation(position_start.getLat(), position_start.getLng(), 1);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                if (!addresses.isEmpty()) {
                                    String address = addresses.get(0).getAddressLine(0);
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                                    tv_country.setText("Pais:" + country);
                                    tv_address.setText("Dirección:" + address);
                                    tv_city.setText("Ciudad:" + city);
                                    tv_state.setText("Provincia:" + state);
                                    tv_postal_code.setText("Código Postal:" + postalCode);
//                                    tv_country_content.setText(country);
//                                    tv_address_content.setText(address);
//                                    tv_city_content.setText(city);
//                                    tv_state_content.setText(state);
//                                    tv_postal_code_content.setText(postalCode);
                                    city_name = city;
                                }

                            }
                        }
                    }, Looper.getMainLooper());

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
                    Toast.makeText(MainActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
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


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    private void sendRequestGeocode(String city) {
        String url = DEFAULT_PATH_GEOCODE + "?apikey=" + API_KEY + "&q=" + city;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray items = response.getJSONArray("items");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        tv_destino_title.setText(item.getString("title"));

                        JSONObject position = item.getJSONObject("position");
                        position_end = new Position(position.getDouble("lat"), position.getDouble("lng"));
                    }


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                sendRequestRouting(position_end);
            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        resultado.setText(error.toString());
                    }
                });

        requestQueue.add(request);

    }


    private void sendRequestRouting(Position position) {
        String content = "";


        String url = DEFAULT_PATH_ROUTING + "?apiKey=" + API_KEY + "&waypoint0=" + position_start.getLat() + "," + position_start.getLng() + "&waypoint1=" + position_end.getLat()
                + "," + position_end.getLng() + "&lc=1652B4&lw=6&t=0&ppi=320&w=800&h=500";

        requestQueue = Volley.newRequestQueue(this);

        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        iv_map.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        iv_map.setImageResource(R.drawable.alert);
                        Toast.makeText(MainActivity.this, "Error" + error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(request);
    }

}