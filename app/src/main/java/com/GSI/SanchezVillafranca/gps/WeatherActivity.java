package com.GSI.SanchezVillafranca.gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.GSI.SanchezVillafranca.gps.model.localization.Position;
import com.GSI.SanchezVillafranca.gps.model.TemperaturaRVAdapter;
import com.GSI.SanchezVillafranca.gps.model.TemperaturaRVModal;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    private RelativeLayout weather_rl_home;
    private ProgressBar weather_pb_loading;
    private ImageView weather_iv_back, weather_iv_search, weather_iv_icon;
    private TextView weather_tv_ciudad, weather_tv_condition, weather_tv_temperatura;
    private LinearLayout weather_ll_edt;
    private TextInputLayout weather_tpl_ciudad;
    private TextInputEditText weather_tit_city;
    private RecyclerView weather_rv_tiempo;
    private ArrayList<TemperaturaRVModal> temperaturaRVModalArrayList;
    private TemperaturaRVAdapter temperaturaRVAdapter;
    private LocationManager locationManager;
    private int PERMISION_CODE = 1;

    private String city_name = "";
    private String address = "";
    private String city = "";
    private String state = "";
    private String country = "";
    private String postalCode = "";
    private String knownName = "";
    private Geocoder geocoder;
    private Position position_start = new Position();
    private LocationRequest locationRequest;

    private static final String API_KEY = "61dceff01df344fda3c151155230302";
    private static final String DEFAULT_PATH = "https://api.weatherapi.com/v1/forecast.json";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        weather_pb_loading = findViewById(R.id.weather_pb_loading);
        weather_rl_home = findViewById(R.id.weather_rl_home);
        weather_iv_back = findViewById(R.id.weather_iv_back);
        weather_tv_ciudad = findViewById(R.id.weather_tv_ciudad);
        weather_ll_edt = findViewById(R.id.weather_ll_edt);
        weather_tpl_ciudad = findViewById(R.id.weather_tpl_ciudad);
        weather_tit_city = findViewById(R.id.weather_tit_city);
        weather_iv_search = findViewById(R.id.weather_iv_search);
        weather_tv_temperatura = findViewById(R.id.weather_tv_temperatura);
        weather_iv_icon = findViewById(R.id.weather_iv_icon);
        weather_tv_condition = findViewById(R.id.weather_tv_condition);
        weather_rv_tiempo = findViewById(R.id.weather_rv_tiempo);
        temperaturaRVModalArrayList = new ArrayList<>();
        temperaturaRVAdapter = new TemperaturaRVAdapter(this, temperaturaRVModalArrayList);
        weather_rv_tiempo.setAdapter(temperaturaRVAdapter);
        geocoder = new Geocoder(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);


        weather_iv_search.setOnClickListener(view -> {
            String nombre = weather_tit_city.getText().toString();
            if (nombre.isEmpty()) {
                Toast.makeText(WeatherActivity.this, "Porfavor introduce el nombre de la ciudad", Toast.LENGTH_LONG).show();
            } else {

                weather_pb_loading.setVisibility(View.VISIBLE);
                weather_rl_home.setVisibility(View.GONE);

                weather_tv_ciudad.setText(nombre);
                getWeatherName(nombre);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //De inicio ponemos los valores que recogemos de la localizacion actual
        getCurrentLocation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);
        MenuItem menuItem = menu.findItem(R.id.overflow_tiempo);
        menuItem.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.overflow_ruta:
                intent = new Intent(WeatherActivity.this, MainActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_tiempo:
                intent = new Intent(WeatherActivity.this, WeatherActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_gelery:
                intent = new Intent(WeatherActivity.this, GaleryActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_trafico:
                intent = new Intent(WeatherActivity.this, GasStationActivity.class);
                this.startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    private String getCiudad(Position position) {
        String nombre = "No encontrado";
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(position.getLat(), position.getLng(), 10);

            for (Address address : addressList) {
                String city = address.getLocality();
                if (city != null && !city.equals("")) {
                    nombre = city;
                } else {
                    Toast.makeText(this, "Nombre de la ciudad no encontrado", Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nombre;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Tienes permiso", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Pfv, comprueba los permisos", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void getWeatherName(String name) {
        String url = DEFAULT_PATH + "?key=" + API_KEY + "&q=" + name + "&days=1&aqi=yes&alerts=yes";
        weather_tv_ciudad.setText(name);
        doRequest(url);
    }

    private void getWeatherPosition(Position position) {
        String url = DEFAULT_PATH + "?key=" + API_KEY + "&q=" + position.getLat() + "," + position.getLng() + "&days=1&aqi=yes&alerts=yes";
        doRequest(url);
    }


    private void doRequest(String url) {


        RequestQueue queue = Volley.newRequestQueue(WeatherActivity.this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                weather_pb_loading.setVisibility(View.GONE);
                weather_rl_home.setVisibility(View.VISIBLE);
                temperaturaRVModalArrayList.clear();
                String temperatura = "";
                String condition = "";
                String conditionIcon = "";
                int day = 0;

                try {
                    temperatura = response.getJSONObject("current").getString("temp_c");
                    weather_tv_temperatura.setText(temperatura + "ºC");
                    day = response.getJSONObject("current").getInt("is_day");
                    conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    Picasso.get().load("https:".concat(conditionIcon)).into(weather_iv_icon);
                    weather_tv_condition.setText(condition);
                    setDayNight(day);

                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONObject current = forecast.getJSONArray("forecastday").getJSONObject(0);

                    JSONArray hourArray = current.getJSONArray("hour");
                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String h = hourObj.getString("time");
                        String temp = hourObj.getString("temp_c");
                        String icon = hourObj.getJSONObject("condition").getString("icon");
                        String viento = hourObj.getString("wind_kph");
                        temperaturaRVModalArrayList.add(new TemperaturaRVModal(h, temp, icon, viento));
                    }
                    temperaturaRVAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(WeatherActivity.this, "Pfv, Introduce un nombre de ciudad valido" + error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        queue.add(request);
    }

    private void setDayNight(int day) {

        ImageView imageView = (ImageView) findViewById(R.id.weather_iv_back);
        String color = "";
        if (day == 1) {
            color = "#000000";
            imageView.setImageResource(R.drawable.sunny);
        } else {
            color = "#FFFFFF";
            imageView.setImageResource(R.drawable.night);
        }

        TextView weather_tv_ciudad = findViewById(R.id.weather_tv_ciudad);
        weather_tv_ciudad.setTextColor(Color.parseColor(color));

        TextInputEditText weather_tit_city = findViewById(R.id.weather_tit_city);
        weather_tit_city.setTextColor(Color.parseColor(color));

        TextInputLayout weather_tpl_ciudad = findViewById(R.id.weather_tpl_ciudad);
        weather_tpl_ciudad.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor(color)));

        ImageView weather_iv_search = findViewById(R.id.weather_iv_search);
        weather_iv_search.setColorFilter(new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP));

        TextView weather_tv_temperatura = findViewById(R.id.weather_tv_temperatura);
        weather_tv_temperatura.setTextColor(Color.parseColor(color));

        TextView weather_tv_condition = findViewById(R.id.weather_tv_condition);
        weather_tv_condition.setTextColor(Color.parseColor(color));

        TextView weather_tv_pronostico = findViewById(R.id.weather_tv_pronostico);
        weather_tv_pronostico.setTextColor(Color.parseColor(color));
    }


    private void getCurrentLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(WeatherActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);

                            LocationServices.getFusedLocationProviderClient(WeatherActivity.this).removeLocationUpdates(this);

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
                                    address = addresses.get(0).getAddressLine(0);
                                    city = addresses.get(0).getLocality();
                                    state = addresses.get(0).getAdminArea();
                                    country = addresses.get(0).getCountryName();
                                    postalCode = addresses.get(0).getPostalCode();
                                    knownName = addresses.get(0).getFeatureName();
                                    city_name = city;
                                }

                                getWeatherPosition(new Position(position_start.getLat(), position_start.getLng()));
                                weather_tv_ciudad.setText(city);
                                weather_tit_city.setText(city);
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
                    Toast.makeText(WeatherActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(WeatherActivity.this, 2);
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


}