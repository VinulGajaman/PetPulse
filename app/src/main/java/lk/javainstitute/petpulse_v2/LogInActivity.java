package lk.javainstitute.petpulse_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import lk.javainstitute.petpulse_v2.petOwner.PetOwner_LoginActivity;
import lk.javainstitute.petpulse_v2.vet.Vet_LogInActivity;

public class LogInActivity extends AppCompatActivity {

    private ImageView imageView;



   ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

//        // Inside your Activity or Fragment
//        imageView = findViewById(R.id.imageView2);
//
//        // Set the image to the ImageView
//        imageView.setImageResource(R.drawable.continueas);


        Button button = findViewById(R.id.btnPetOwner);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the SecondActivity
                Intent intent = new Intent(LogInActivity.this, PetOwner_LoginActivity.class);

                // Start the SecondActivity
                startActivity(intent);
            }
        });

        Button button2 = findViewById(R.id.btnVet);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the SecondActivity
                Intent intent = new Intent(LogInActivity.this, Vet_LogInActivity.class);

                // Start the SecondActivity
                startActivity(intent);
            }
        });
    }
}