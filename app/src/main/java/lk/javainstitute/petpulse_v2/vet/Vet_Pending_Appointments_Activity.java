package lk.javainstitute.petpulse_v2.vet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

import lk.javainstitute.petpulse_v2.R;
import lk.javainstitute.petpulse_v2.adapter.VetPendingAppointmentAdapter;
import lk.javainstitute.petpulse_v2.model.Appointment;

public class Vet_Pending_Appointments_Activity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    FirebaseStorage storage;

    String userMobile;

    private ArrayList<Appointment> appointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_pending_appointments);

        storage =  FirebaseStorage.getInstance();
       fAuth =  FirebaseAuth.getInstance();
        fStore =  FirebaseFirestore.getInstance();

        ImageButton backbtn = findViewById(R.id.backbtn4);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new activity
                Intent intent = new Intent(Vet_Pending_Appointments_Activity.this,Vet_All_Appointments_Activity.class); // Replace NextActivity.class with your target activity
                startActivity(intent);
            }
        });


        FirebaseUser currentUser = fAuth.getCurrentUser();
        userMobile = currentUser.getPhoneNumber();
        String formattedNumber = userMobile.replace("+94", "0");
//        Log.d(TAG, "Phone Number: " + formattedNumber);


        appointments = new ArrayList<>();

        RecyclerView itemView = findViewById(R.id.appointmentView);

        VetPendingAppointmentAdapter vetPendingAppointmentAdapter = new VetPendingAppointmentAdapter(appointments,Vet_Pending_Appointments_Activity.this);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext());
        itemView.setLayoutManager(linearLayout);
        itemView.setAdapter(vetPendingAppointmentAdapter);


        fStore.collection("appointments")
                .whereEqualTo("Vet Mobile", formattedNumber)
                .whereEqualTo("status", "Pending")
                .orderBy("date", Query.Direction.ASCENDING)
                .orderBy("appointment Number", Query.Direction.ASCENDING)

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore", "Error getting appointments", error);
                            return;
                        }

                        if (value != null) {
                            appointments.clear();
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                String vetMobile = documentSnapshot.getString("Vet Mobile");
                                String petOwnerMobile = documentSnapshot.getString("PetOwner Mobile");
                                String currentDate = documentSnapshot.getString("date");
                                String imgUrl = documentSnapshot.getString("petImageUrl");
                                int appointmentNumber = documentSnapshot.getLong("appointment Number").intValue();

                                Appointment appointment = new Appointment();
                                appointment.setVetMobile(vetMobile);
                                appointment.setPetOwnerMobile(petOwnerMobile);
                                appointment.setAppointmentNumber(appointmentNumber);
                                appointment.setImageURL(imgUrl);
                                appointment.setCurrentDate(currentDate);

                                appointments.add(appointment);

                                // Use the values as needed
                                Log.d("AppointmentDetails", "Vet Mobile: " + vetMobile);
                                Log.d("AppointmentDetails", "Appointment Number: " + appointmentNumber);
                            }

                            vetPendingAppointmentAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore", "No appointments found.");
                        }
                    }
                });

    }



    }
