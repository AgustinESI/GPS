package com.GSI.SanchezVillafranca.gps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class IndexActivity extends AppCompatActivity {

    CardView index_cv_rute, index_cv_weather, index_cv_traffic, index_cv_galery, path_cv;
    TextView footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);


        index_cv_rute = findViewById(R.id.index_cv_rute);
        index_cv_weather = findViewById(R.id.index_cv_weather);
        path_cv = findViewById(R.id.path_cv);
        index_cv_traffic = findViewById(R.id.index_cv_traffic);
        index_cv_galery = findViewById(R.id.index_cv_galery);
        footer = findViewById(R.id.textView9);

        footer.setText("©Derechos Reservados 2023\n" +
                "Agustín Sáncez & Ángel Villafranca.\n" +
                "Trabajo teórico propuesto GPS, el proyecto que consiste en el desarrollo de un pequeño sistema de información avanzado relacionado con los sistemas de información empresariales");


        path_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, GPSActivity.class);
                IndexActivity.this.startActivity(intent);
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
                Intent intent = new Intent(IndexActivity.this, GasStationActivity.class);
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