package lk.javainstitute.petpulse_v2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;



public class SplashActivity extends AppCompatActivity {

    //AnimatedVectorDrawable animatedlogo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PetPulse_v2_FullScreen);
        setContentView(R.layout.activity_splash);

        ImageView imageView = findViewById(R.id.imageView3);

// Get the animated drawable
        AnimatedVectorDrawableCompat animatedDrawable = AnimatedVectorDrawableCompat
                .create(this, R.drawable.avd_anim);

        if (animatedDrawable != null) {
            imageView.setImageDrawable(animatedDrawable);
            animatedDrawable.start();
        }



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        },5000);


    }
}