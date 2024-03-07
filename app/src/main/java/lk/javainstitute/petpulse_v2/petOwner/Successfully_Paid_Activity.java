package lk.javainstitute.petpulse_v2.petOwner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import lk.javainstitute.petpulse_v2.R;

public class Successfully_Paid_Activity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;



    String appointmentNum,appointmentDate,appointmentTime;


    TextView appointmentNumber,dateAppointment,timeAppointment;

    Intent intent;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successfully_paid);

        intent = getIntent();
        if (intent != null) {
            appointmentNum = intent.getStringExtra("appointmentNum");
            appointmentDate = intent.getStringExtra("date");
            appointmentTime = intent.getStringExtra("time");

            appointmentNumber = findViewById(R.id.numberAppointment);
            dateAppointment = findViewById(R.id.dateAppointment);
            timeAppointment = findViewById(R.id.timeAppointment);

            appointmentNumber.setText("0"+appointmentNum);
            dateAppointment.setText(appointmentDate);
            timeAppointment.setText(appointmentTime);

        }

        button = findViewById(R.id.backToHome);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the SecondActivity
                Intent intent = new Intent(Successfully_Paid_Activity.this, PetOwner_Home_Activity.class);

                // Start the SecondActivity
                startActivity(intent);
                finish();
            }
        });
    }


}