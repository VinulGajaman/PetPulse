package lk.javainstitute.petpulse_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inside your Activity or Fragment
        imageView = findViewById(R.id.welcomeImg);

       // Set the image to the ImageView
        imageView.setImageResource(R.drawable.mainimgapp);

        button = findViewById(R.id.btnStart);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the SecondActivity
                Intent intent = new Intent(MainActivity.this, LogInActivity.class);

                // Start the SecondActivity
                startActivity(intent);
            }
        });
    }
}