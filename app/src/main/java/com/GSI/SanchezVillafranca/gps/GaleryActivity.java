package com.GSI.SanchezVillafranca.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.GSI.SanchezVillafranca.gps.model.localization.Position;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.List;

public class GaleryActivity extends AppCompatActivity {

    private ExifInterface exif;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private RequestQueue requestQueue;
    private ProgressBar galery_pb_loading;
    private RelativeLayout relative_layout_data, relative_layout_3;
    private ImageView galery_iv_map;
    private Geocoder geocoder;
    private EditText galery_et_pais, galery_et_provincia, galery_et_ciudad, galery_et_codigo_postal, galery_et_direccion;

    private static final String DEFAULT_PATH_GEOCODE = "https://geocode.search.hereapi.com/v1/geocode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galery);
        requestQueue = Volley.newRequestQueue(this);
        geocoder = new Geocoder(this);
        galery_pb_loading = findViewById(R.id.galery_pb_loading);
        relative_layout_data = findViewById(R.id.relative_layout_data);
        relative_layout_3 = findViewById(R.id.relative_layout_3);

        galery_et_pais = findViewById(R.id.galery_et_pais);
        galery_et_provincia = findViewById(R.id.galery_et_provincia);
        galery_et_ciudad = findViewById(R.id.galery_et_ciudad);
        galery_et_codigo_postal = findViewById(R.id.galery_et_codigo_postal);
        galery_et_direccion = findViewById(R.id.galery_et_direccion);

        galery_iv_map = findViewById(R.id.galery_iv_map);


        Button selectImageButton = findViewById(R.id.galery_b_open_galery);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the device's gallery app
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
                galery_pb_loading.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);
        MenuItem menuItem = menu.findItem(R.id.overflow_gelery);
        menuItem.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.overflow_ruta:
                intent = new Intent(GaleryActivity.this, MainActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_tiempo:
                intent = new Intent(GaleryActivity.this, WeatherActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_gelery:
                intent = new Intent(GaleryActivity.this, GaleryActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_trafico:
                intent = new Intent(GaleryActivity.this, TrafficActivity.class);
                this.startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the image URI
            Uri selectedImageUri = data.getData();

            // Use the image URI to load the image into an ImageView or other UI element
            ImageView imageView = findViewById(R.id.galery_iv_imagen);
            imageView.setImageURI(selectedImageUri);

            // You can also get the path of the image file using the image URI, if needed
            String imagePath = getPathFromUri(selectedImageUri);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request permission from the user
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
            } else {

                try {
                    String[] projection = {MediaStore.Images.Media.LATITUDE, MediaStore.Images.Media.LONGITUDE};
                    Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        @SuppressLint("Range") double latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE));
                        @SuppressLint("Range") double longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
                        if (latitude != 0.0 || longitude != 0.0) {
                            Log.d("TAG", "Latitude: " + latitude + ", Longitude: " + longitude);
                            sendRequestRouting(new Position(latitude, longitude));
                        } else {
                            Log.d("TAG", "Image file doesn't have location data.");
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(GaleryActivity.this, "Culo abierto2", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }

    private void sendRequestRouting(Position position) {
        String url = getResources().getString(R.string.DEFAULT_PATH_MAP_VIEW) + "?apiKey=" + getResources().getString(R.string.API_KEY) + "&c=" + position.getLat() + "," + position.getLng() + "&z=16&w=600&h=500";
        requestQueue = Volley.newRequestQueue(this);
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public void onResponse(Bitmap bitmap) {
                galery_iv_map.setImageBitmap(bitmap);
                getLocation(position);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                galery_iv_map.setVisibility(View.GONE);
                galery_pb_loading.setVisibility(View.GONE);
                relative_layout_data.setVisibility(View.GONE);
                relative_layout_3.setVisibility(View.GONE);
                Toast.makeText(GaleryActivity.this, "Error al cargar el mapa", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(request);
    }

    private void getLocation(Position position) {
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(position.getLat(), position.getLng(), 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!addresses.isEmpty()) {
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();

            galery_et_pais.setText("Pais:" + country);
            galery_et_provincia.setText("Provincia:" + state);
            galery_et_ciudad.setText("Ciudad" + city);
            galery_et_codigo_postal.setText("Código Postal:" + postalCode);
            galery_et_direccion.setText("Dirección:" + address);

            galery_iv_map.setVisibility(View.VISIBLE);
            galery_pb_loading.setVisibility(View.GONE);
            relative_layout_data.setVisibility(View.VISIBLE);
            relative_layout_3.setVisibility(View.VISIBLE);

        }
    }

}