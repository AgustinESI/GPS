package com.GSI.SanchezVillafranca.gps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setFullscreen(true);
        setContentView(R.layout.splash_screen);

        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

        TextView splashscreen_title = findViewById(R.id.splashscreen_title);
        TextView splashscreen_createdby = findViewById(R.id.splashscreen_createdby);
        ImageView splashscreen_image = findViewById(R.id.splashscreen_image);

        splashscreen_createdby.setAnimation(animation2);
        splashscreen_title.setAnimation(animation2);
        splashscreen_image.setAnimation(animation1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4000);
    }


    public void setFullscreen(boolean enabled) {
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        uiOptions = enabled ?
                // include some flags
                (uiOptions
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) :
                // exclude them
                (uiOptions
                        & ~View.SYSTEM_UI_FLAG_FULLSCREEN
                        & ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        & ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }
}
