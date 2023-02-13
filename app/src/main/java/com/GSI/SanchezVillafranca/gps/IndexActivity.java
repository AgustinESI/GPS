package com.GSI.SanchezVillafranca.gps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class IndexActivity extends AppCompatActivity {

    CardView index_cv_rute, index_cv_weather, index_cv_traffic, index_cv_galery, esi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);


        index_cv_rute = findViewById(R.id.index_cv_rute);
        index_cv_weather = findViewById(R.id.index_cv_weather);
        esi = findViewById(R.id.esi);
        index_cv_traffic = findViewById(R.id.index_cv_traffic);
        index_cv_galery = findViewById(R.id.index_cv_galery);

        esi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://esi.uclm.es/"));
                startActivity(browserIntent);
            }
        });

        index_cv_rute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, MainActivity.class);
                IndexActivity.this.startActivity(intent);
            }
        });

        index_cv_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, WeatherActivity.class);
                IndexActivity.this.startActivity(intent);
            }
        });

        index_cv_traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, TrafficActivity.class);
                IndexActivity.this.startActivity(intent);
            }
        });

        index_cv_galery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, GaleryActivity.class);
                IndexActivity.this.startActivity(intent);
            }
        });

    }
}