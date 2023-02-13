package com.GSI.SanchezVillafranca.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TrafficActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
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
                intent = new Intent(TrafficActivity.this, MainActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_tiempo:
                intent = new Intent(TrafficActivity.this, WeatherActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_gelery:
                intent = new Intent(TrafficActivity.this, GaleryActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.overflow_trafico:
                intent = new Intent(TrafficActivity.this, TrafficActivity.class);
                this.startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}