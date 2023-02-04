package com.GSI.SanchezVillafranca.gps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class IndexActivity extends AppCompatActivity {

    CardView ruta, tiempo, esi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);


        ruta = findViewById(R.id.ruta);
        tiempo = findViewById(R.id.tiempo);
        esi = findViewById(R.id.esi);

        esi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://esi.uclm.es/"));
                startActivity(browserIntent);
            }
        });

        ruta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, MainActivity.class);
                IndexActivity.this.startActivity(intent);
            }
        });

        tiempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, WeatherActivity.class);
                IndexActivity.this.startActivity(intent);
            }
        });

    }
}