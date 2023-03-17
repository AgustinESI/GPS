package com.GSI.SanchezVillafranca.gps;

import android.Manifest;
import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.GSI.SanchezVillafranca.gps.model.IndicacionesRVAdapter;
import com.GSI.SanchezVillafranca.gps.model.IndicacionesRVModal;
import com.GSI.SanchezVillafranca.gps.model.localization.Position;
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
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView tv_country_content, tv_city_content, tv_postal_code_content, tv_state_content, tv_address_content, main_tv_title;
    private ProgressBar spinner;
    private TextInputEditText main_tit_city;
    private ImageView iv_map, main_iv_search;
    private LocationRequest locationRequest;
    private Position position_start;
    //private Position position_end;
    private Geocoder geocoder;
    private RequestQueue requestQueue;
    private RecyclerView main_rv_indicaciones;
    private ArrayList<IndicacionesRVModal> indicacionesRVModals;
    private IndicacionesRVAdapter indicacionesRVAdapter;
    private static final String DEFAULT_PATH_GEOCODE = "https://geocode.search.hereapi.com/v1/geocode";
    private static final String DEFAULT_PATH_ROUTING = "https://image.maps.ls.hereapi.com/mia/1.6/routing";
    private static final String DEFAULT_PATH_INDICATIONS = "https://router.hereapi.com/v8/routes";
    private ArrayAdapter<String> adapter;
    private Spinner dropdown;
    private String[] cities = new String[]{"Item 1"};
    private Map<String, Position> locationData = new HashMap<String, Position>();
    private FrameLayout indications;

    private Object selectedItem;

    //https://www.google.es/maps/dir/'38.9862600,-3.9290700'/40.0749200,-2.1361500/data=!3m1!4b1!4m7!4m6!1m3!2m2!1d-3.92907!2d38.98626!1m0!3e0

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.overflow, menu);
        MenuItem menuItem = menu.findItem(R.id.overflow_ruta);
        menuItem.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    private void selectCity() {
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spinner.setVisibility(View.VISIBLE);
                    iv_map.setVisibility(View.GONE);
                    main_tv_title.setVisibility(View.GONE);
                    indications.setVisibility(View.GONE);
                    sendRequestRouting(locationData.get(cities[position]));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "Error buscando la ciudad", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_country_content = findViewById(R.id.tv_country_content);
        tv_city_content = findViewById(R.id.tv_city_content);
        tv_postal_code_content = findViewById(R.id.tv_postal_code_content);
        tv_state_content = findViewById(R.id.tv_state_content);
        tv_address_content = findViewById(R.id.tv_address_content);
        main_tit_city = findViewById(R.id.main_tit_city);
        main_iv_search = findViewById(R.id.main_iv_search);
        iv_map = (ImageView) findViewById(R.id.iv_map);
        main_tv_title = findViewById(R.id.main_tv_title);
        spinner = (ProgressBar) findViewById(R.id.main_progress_bar);
        main_rv_indicaciones = findViewById(R.id.main_rv_indicaciones);
        indications = findViewById(R.id.indications);

        indicacionesRVModals = new ArrayList<>();
        indicacionesRVAdapter = new IndicacionesRVAdapter(this, indicacionesRVModals);
        main_rv_indicaciones.setAdapter(indicacionesRVAdapter);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        geocoder = new Geocoder(this);
        requestQueue = Volley.newRequestQueue(this);
        position_start = new Position();


        dropdown = findViewById(R.id.main_s_dropdown);

        getCurrentLocation();
        selectCity();

        main_iv_search.setOnClickListener(view -> {
            String city = main_tit_city.getText().toString();
            if (city.length() > 0 && !city.equals("")) {
                if (cities.length > 1) {
                    spinner.setVisibility(View.VISIBLE);
                    iv_map.setVisibility(View.GONE);
                    main_tv_title.setVisibility(View.GONE);
                    indications.setVisibility(View.GONE);
                }
                sendRequestGeocode(city);
                hideKeyboard(MainActivity.this);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            selectedItem = intent.getSerializableExtra("selectedItem");
            if (selectedItem != null) {
                List<Address> addresses;
                String[] latlng = ((String) selectedItem).split("\\|");
                try {
                    addresses = geocoder.getFromLocation(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]), 1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (!addresses.isEmpty()) {
                    String address = addresses.get(0).getAddressLine(0);
                    String postalCode = addresses.get(0).getPostalCode();
                    String city = addresses.get(0).getLocality();


                    sendRequestGeocode(city + " " + postalCode + " " + address);
                }
            }
        }
    }

    private void getIndications(Position position_start, Position position_end) {
        String url = DEFAULT_PATH_INDICATIONS + "?transportMode=car&lang=es&origin=" + position_start.getLat() + "," + position_start.getLng() + "&destination=" + position_end.getLat() + "," + position_end.getLng() + "&return=polyline,actions,instructions,summary&apikey=" + getResources().getString(R.string.API_KEY);


        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                indicacionesRVModals.clear();

                try {
                    JSONObject route = response.getJSONArray("routes").getJSONObject(0);
                    JSONObject section = route.getJSONArray("sections").getJSONObject(0);
                    JSONArray actions = section.getJSONArray("actions");

                    for (int i = 0; i < actions.length(); i++) {

                        JSONObject obj = actions.getJSONObject(i);
                        IndicacionesRVModal modal;
                        if (obj.has("direction")) {
                            modal = new IndicacionesRVModal(obj.getString("action"), obj.getString("instruction"), obj.getString("direction"));
                        } else {
                            modal = new IndicacionesRVModal(obj.getString("action"), obj.getString("instruction"), null);
                        }

                        if (modal.getAction() != null) {
                            switch (modal.getAction()) {
                                case "continue":
                                case "depart":
                                case "keep":
                                case "ramp":
                                    modal.setIcon(R.drawable.ic_up_arrow);
                                    break;
                                case "turn":
                                    switch (modal.getDirection()) {
                                        case "right":
                                            modal.setIcon(R.drawable.ic_left_arrow);
                                            break;
                                        case "left":
                                            modal.setIcon(R.drawable.ic_right_arrow);
                                            break;
                                    }
                                    break;
                                case "roundaboutExit":
                                case "roundaboutPass":
                                    modal.setIcon(R.drawable.ic_roundabout);
                                    break;
                                case "arrive":
                                    modal.setIcon(R.drawable.ic_route);
                                    break;
                                case "exit":
                                    modal.setIcon(R.drawable.ic_exit_arrow);
                                    break;
                            }
                        }
                        indicacionesRVModals.add(modal);
                    }
                    indicacionesRVAdapter.notifyDataSetChanged();


                    String arrival_time = section.getJSONObject("arrival").getString("time");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    Date date = format.parse(arrival_time);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    String hour_min = timeFormat.format(date);
                    main_tv_title.setText("La hora estimada de llegada es " + hour_min);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                spinner.setVisibility(View.GONE);
                iv_map.setVisibility(View.VISIBLE);
                main_tv_title.setVisibility(View.VISIBLE);
                indications.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Culo abierto" + error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        queue.add(request);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.overflow_ruta:
                intent = new Intent(MainActivity.this, MainActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_tiempo:
                intent = new Intent(MainActivity.this, WeatherActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_gelery:
                intent = new Intent(MainActivity.this, GaleryActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_trafico:
                intent = new Intent(MainActivity.this, GasStationActivity.class);
                this.startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void getCurrentLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    try {
                        LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);


                                LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);

                                if (locationResult != null && locationResult.getLocations().size() > 0) {

                                    int index = locationResult.getLocations().size() - 1;
                                    position_start.setLat(round(locationResult.getLocations().get(index).getLatitude(), 4));
                                    position_start.setLng(round(locationResult.getLocations().get(index).getLongitude(), 4));
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
                                        String knownName = addresses.get(0).getFeatureName();
                                        tv_country_content.setText(country);
                                        tv_city_content.setText(city);
                                        tv_state_content.setText(state);
                                        tv_postal_code_content.setText(postalCode);

                                        final int mid = address.length() / 2; //get the middle of the String
                                        String[] parts = {address.substring(0, mid), address.substring(mid)};

                                        tv_address_content.setText(parts[0] + "\n" + parts[1]);
                                    }
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
                    Toast.makeText(MainActivity.this, "GPS encendido", Toast.LENGTH_SHORT).show();

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
        String url = DEFAULT_PATH_GEOCODE + "?apikey=" + getResources().getString(R.string.API_KEY) + "&q=" + city;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                locationData = new HashMap<>();
                try {
                    JSONArray items = response.getJSONArray("items");
                    cities = new String[items.length() + 1];
                    cities[0] = "Seleccione una ciudad";
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        //main_tv_title.setText(item.getString("title"));
                        cities[i + 1] = item.getString("title");

                        JSONObject position = item.getJSONObject("position");
                        locationData.put(item.getString("title"), new Position(position.getDouble("lat"), position.getDouble("lng")));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, cities);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(adapter);
                spinner.setVisibility(View.GONE);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spinner.setVisibility(View.GONE);
                iv_map.setVisibility(View.GONE);
                main_tv_title.setVisibility(View.GONE);
                indications.setVisibility(View.GONE);
                main_tit_city.setText("");
                Toast.makeText(MainActivity.this, "Introduce una localización válida", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(request);

    }


    private void sendRequestRouting(Position position) {
        String content = "";


        String url = DEFAULT_PATH_ROUTING + "?apiKey=" + getResources().getString(R.string.API_KEY) + "&waypoint0=" + position_start.getLat() + "," + position_start.getLng() + "&waypoint1=" + position.getLat() + "," + position.getLng() + "&lc=1652B4&lw=6&t=0&ppi=320&w=800&h=500";

        requestQueue = Volley.newRequestQueue(this);

        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                iv_map.setImageBitmap(bitmap);
                getIndications(position_start, position);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                spinner.setVisibility(View.GONE);
                iv_map.setVisibility(View.GONE);
                main_tv_title.setVisibility(View.GONE);
                indications.setVisibility(View.GONE);
                main_tit_city.setText("");
                Toast.makeText(MainActivity.this, "Error al calcular la ruta", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(request);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}