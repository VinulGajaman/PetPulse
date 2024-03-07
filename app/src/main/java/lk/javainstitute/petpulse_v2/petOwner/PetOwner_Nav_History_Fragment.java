package lk.javainstitute.petpulse_v2.petOwner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import lk.javainstitute.petpulse_v2.R;
import lk.javainstitute.petpulse_v2.adapter.PetOwnerAppointmentAdapter;
import lk.javainstitute.petpulse_v2.adapter.VetPendingAppointmentAdapter;
import lk.javainstitute.petpulse_v2.model.Appointment;
import lk.javainstitute.petpulse_v2.vet.Vet_Pending_Appointments_Activity;


public class PetOwner_Nav_History_Fragment extends Fragment {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    FirebaseStorage storage;

    String userMobile;

    private ArrayList<Appointment> appointments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pet_owner__nav__history__, container, false);

        ImageView imageView = view.findViewById(R.id.histroyImg);

        // Set the image to the ImageView
        imageView.setImageResource(R.drawable.petownerapphistory);

        storage =  FirebaseStorage.getInstance();
        fAuth =  FirebaseAuth.getInstance();
        fStore =  FirebaseFirestore.getInstance();

        FirebaseUser currentUser = fAuth.getCurrentUser();
        userMobile = currentUser.getPhoneNumber();
        String formattedNumber = userMobile.replace("+94", "0");
//        Log.d(TAG, "Phone Number: " + formattedNumber);

        appointments = new ArrayList<>();

        RecyclerView itemView = view.findViewById(R.id.appHistoryList);


        PetOwnerAppointmentAdapter petOwnerAppointmentAdapter = new PetOwnerAppointmentAdapter(appointments, getContext());
        LinearLayoutManager linearLayout = new LinearLayoutManager(requireContext());
        itemView.setLayoutManager(linearLayout);
        itemView.setAdapter(petOwnerAppointmentAdapter);

        fStore.collection("appointments")
                .whereEqualTo("PetOwner Mobile", formattedNumber)
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("appointment Number", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        appointments.clear();
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            String vetMobile = documentSnapshot.getString("Vet Mobile");
                            String petOwnerMobile = documentSnapshot.getString("PetOwner Mobile");
                            String currentDate = documentSnapshot.getString("date");
                            String currentTime = documentSnapshot.getString("time");
                            String status = documentSnapshot.getString("status");
                            int appointmentNumber = documentSnapshot.getLong("appointment Number").intValue();

                            Appointment appointment = new Appointment();
                            appointment.setVetMobile(vetMobile);
                            appointment.setPetOwnerMobile(petOwnerMobile);
                            appointment.setAppointmentNumber(appointmentNumber);
                            appointment.setCurrentTime(currentTime);
                            appointment.setCurrentDate(currentDate);
                            appointment.setStatus(status);

                            appointments.add(appointment);

                            // Use the values as needed
                            Log.d("AppointmentDetails", "Vet Mobile: " + vetMobile);
                            Log.d("AppointmentDetails", "Appointment Number: " + appointmentNumber);
//                        Appointment appointment = documentSnapshot.toObject(Appointment.class);
//                        appointments.add(appointment);


                        }

                        petOwnerAppointmentAdapter.notifyDataSetChanged();

                    }
                });

        return view;
    }
}