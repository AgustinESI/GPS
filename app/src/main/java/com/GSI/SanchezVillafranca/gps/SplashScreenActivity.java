package com.GSI.SanchezVillafranca.gps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setFullscreen(true);
        setContentView(R.layout.splash_screen);

        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

        TextView splashscreen_title = findViewById(R.id.splashscreen_title);
        //ImageView splashscreen_image = findViewById(R.id.splashscreen_image);

        splashscreen_title.setAnimation(animation2);
        //splashscreen_image.setAnimation(animation1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, IndexActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }


    public void setFullscreen(boolean enabled) {
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        uiOptions = enabled ?
                // include some flags
                (uiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) :
                // exclude them
                (uiOptions & ~View.SYSTEM_UI_FLAG_FULLSCREEN & ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY & ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }
}
