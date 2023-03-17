package com.GSI.SanchezVillafranca.gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.GSI.SanchezVillafranca.gps.model.GasStationRVAdapter;
import com.GSI.SanchezVillafranca.gps.model.GasStationRVModal;
import com.GSI.SanchezVillafranca.gps.model.OnItemClickListener;
import com.GSI.SanchezVillafranca.gps.model.localization.Position;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GasStationActivity extends AppCompatActivity {


    private TextView gas_station_tv_country_content;
    private ImageView gas_iv_map;
    private TextView gas_station_sb_value, gas_station_tv_state_content, gas_station_tv_city_content, gas_station_tv_postal_code_content, gas_station_tv_address_content;
    private SeekBar gas_station_sb;
    private ProgressBar gas_progress_bar;
    private RecyclerView gas_station_rv_list;
    private ArrayList<GasStationRVModal> gasStationList;
    private ArrayList<GasStationRVModal> gasStationListAux;
    private GasStationRVAdapter gasStationRVAdapter;
    private RequestQueue requestQueue;
    private Position current_position;
    private LocationRequest locationRequest;
    private Geocoder geocoder;
    private static final String TAG = "GasStationActivity";


    public static final double Rr = 6372.8; // In kilometers


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasstation);


        gas_station_sb_value = findViewById(R.id.gas_station_sb_value);
        gas_station_sb = findViewById(R.id.gas_station_sb);
        gas_progress_bar = findViewById(R.id.gas_progress_bar);
        requestQueue = Volley.newRequestQueue(this);
        gas_station_rv_list = findViewById(R.id.gas_station_rv_list);
        gas_iv_map = findViewById(R.id.gas_iv_map);
        gas_station_tv_country_content = findViewById(R.id.gas_station_tv_country_content);
        gas_station_tv_state_content = findViewById(R.id.gas_station_tv_state_content);
        gas_station_tv_city_content = findViewById(R.id.gas_station_tv_city_content);
        gas_station_tv_postal_code_content = findViewById(R.id.gas_station_tv_postal_code_content);
        gas_station_tv_address_content = findViewById(R.id.gas_station_tv_address_content);


        gasStationList = new ArrayList<>();
        gasStationListAux = new ArrayList<>();
        gasStationRVAdapter = new GasStationRVAdapter(this, gasStationList, new OnItemClickListener() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public void onItemClick(Object item) {
                GasStationRVModal gasStation = gasStationList.get(Integer.valueOf(item.toString()));
                Intent intent = new Intent(GasStationActivity.this, MainActivity.class);
                intent.putExtra("selectedItem", gasStation.getLatitude() + "|" + gasStation.getLongitude());
                startActivity(intent);
            }
        });


        gas_station_rv_list.setAdapter(gasStationRVAdapter);
        current_position = new Position();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        geocoder = new Geocoder(this);


        gas_station_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                gas_station_sb_value.setText(String.valueOf(progress + " Km"));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                gas_station_rv_list.setVisibility(View.GONE);
                gas_progress_bar.setVisibility(View.VISIBLE);
                gas_iv_map.setVisibility(View.GONE);
                gasStationList.clear();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                getGasStation(seekBar.getProgress());

                gas_progress_bar.setVisibility(View.GONE);
                gas_station_rv_list.setVisibility(View.VISIBLE);
                gas_iv_map.setVisibility(View.VISIBLE);

            }
        });

        getCurrentLocation();
    }


    private void getGasStation(int distance) {
        gasStationList.clear();
        int i = 1;
        for (GasStationRVModal gasStation : gasStationListAux) {
            if (filterGasStation(gasStation, distance)) {
                gasStation.setInd(i++);
                gasStationList.add(gasStation);
            }
        }
        gasStationRVAdapter.notifyDataSetChanged();
        generateMap(current_position);
    }

    private void generateMap(Position position) {
        String content = "";

        String latlng = "";
        int i = 0;
        for (GasStationRVModal gasstation : gasStationList) {
            if (i++ <= 70) {
                latlng += gasstation.getLatitude() + "," + gasstation.getLongitude() + ",";
            }
        }

        latlng = latlng.substring(0, latlng.length() - 1);

        String url = "https://image.maps.ls.hereapi.com/mia/1.6/mapview?apiKey=" + getResources().getString(R.string.API_KEY) + "&ctr=" + position.getLat() + "," + position.getLng() + "&z=18&poi=" + latlng + "&w=600&h=600&ml=es&ppi=200";
        requestQueue = Volley.newRequestQueue(this);

        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                gas_iv_map.setImageBitmap(bitmap);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(GasStationActivity.this, "Error al calcular el mapa", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(request);
    }


    private void readCSV(int distance) {
        InputStream inputStream = null;
        int i = 0;
        int j = 1;
        try {

            List<String[]> data = new ArrayList<>();
            AssetManager manager = getAssets();
            inputStream = manager.open("preciosEESS_es.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while (reader != null && (line = reader.readLine()) != null) {
                i++;
                if (i > 4) {
                    String[] row = line.split(";");
                    // data.add(row);

                    GasStationRVModal gasStation = new GasStationRVModal();
                    gasStation.setProvince(row[0]);
                    gasStation.setMunicipality(row[1]);
                    gasStation.setTown(row[2]);
                    gasStation.setPostal_code(row[3]);
                    gasStation.setAddress(row[4]);
                    gasStation.setMargin(row[5]);
                    gasStation.setLatitude(Double.valueOf(row[7].replace(",", ".")));
                    gasStation.setLongitude(Double.valueOf(row[6].replace(",", ".")));
                    gasStation.setData(row[8]);
                    gasStation.setTime(row[29]);
                    gasStation.setName(row[26]);
                    gasStation.setGasoline_95(row[9]);
                    gasStation.setGasoline_98(row[12]);
                    gasStation.setDiesel_e(row[14]);
                    gasStation.setDiesel_10e(row[15]);
                    if (filterGasStation(gasStation, distance)) {
                        gasStation.setInd(j++);
                        gasStationList.add(gasStation);
                    } else {
                        Log.i(TAG, "-" + gasStation.toString());
                    }
                    gasStationListAux.add(gasStation);
                }
            }
            gasStationRVAdapter.notifyDataSetChanged();

            gas_progress_bar.setVisibility(View.GONE);
            gas_station_rv_list.setVisibility(View.VISIBLE);
            gas_iv_map.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(GasStationActivity.this, "No se ha podido recoger informacion de las gasolineras" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private boolean filterGasStation(GasStationRVModal gasStation, int distanceMax) {


        double maxDistance = Double.valueOf(distanceMax); // Maximum distance in kilometers
        double distance = haversine(current_position.getLat(), current_position.getLng(), gasStation.getLatitude(), gasStation.getLongitude());
        if (distance <= maxDistance) {
            return true;
        }

        return false;
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return Rr * c;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);
        MenuItem menuItem = menu.findItem(R.id.overflow_trafico);
        menuItem.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.overflow_ruta:
                intent = new Intent(GasStationActivity.this, GasStationActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_tiempo:
                intent = new Intent(GasStationActivity.this, WeatherActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_gelery:
                intent = new Intent(GasStationActivity.this, GaleryActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_trafico:
                intent = new Intent(GasStationActivity.this, GasStationActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_map:
                intent = new Intent(GasStationActivity.this, GPSActivity.class);
                this.startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void getCurrentLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(GasStationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(GasStationActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);

                            LocationServices.getFusedLocationProviderClient(GasStationActivity.this).removeLocationUpdates(this);

                            if (locationResult != null && locationResult.getLocations().size() > 0) {

                                int index = locationResult.getLocations().size() - 1;
                                current_position.setLat(round(locationResult.getLocations().get(index).getLatitude(), 4));
                                current_position.setLng(round(locationResult.getLocations().get(index).getLongitude(), 4));
                                List<Address> addresses;

                                try {
                                    addresses = geocoder.getFromLocation(current_position.getLat(), current_position.getLng(), 1);
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
                                    gas_station_tv_country_content.setText(country);
                                    gas_station_tv_city_content.setText(city);
                                    gas_station_tv_state_content.setText(state);
                                    gas_station_tv_postal_code_content.setText(postalCode);

                                    final int mid = address.length() / 2; //get the middle of the String
                                    String[] parts = {address.substring(0, mid), address.substring(mid)};

                                    gas_station_tv_address_content.setText(parts[0] + "\n" + parts[1]);


                                }

                            }

                            readCSV(gas_station_sb.getProgress());
                            generateMap(current_position);
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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
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
                    Toast.makeText(GasStationActivity.this, "GPS encendido", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(GasStationActivity.this, 2);
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