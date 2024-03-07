package lk.javainstitute.petpulse_v2.vet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import lk.javainstitute.petpulse_v2.R;

public class Vet_All_Appointments_Activity extends AppCompatActivity {


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    Button pendingApp,attendApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_all_appointments);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        pendingApp = findViewById(R.id.pendingAppointments);
        attendApp =findViewById(R.id.attendAppointments);

        pendingApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Vet_All_Appointments_Activity.this, Vet_Pending_Appointments_Activity.class);
                startActivity(intent);

            }
        });


        attendApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Vet_All_Appointments_Activity.this, Vet_Attend_Appointments_Activity.class);
                startActivity(intent);

            }
        });

        ImageButton backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new activity
                Intent intent = new Intent(Vet_All_Appointments_Activity.this,Doctor_Home_Activity.class); // Replace NextActivity.class with your target activity
                startActivity(intent);
            }
        });

    }
}