package lk.javainstitute.petpulse_v2.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.javainstitute.petpulse_v2.R;
import lk.javainstitute.petpulse_v2.model.Appointment;
import lk.javainstitute.petpulse_v2.petOwner.PetOwner_Appointment_Full_Details_Activity;


public class PetOwnerAppointmentAdapter extends RecyclerView.Adapter<PetOwnerAppointmentAdapter.ViewHolder>{

    ArrayList<Appointment> appointments;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    FirebaseStorage storage;

    String userMobile;
    private Context context;

    public PetOwnerAppointmentAdapter(ArrayList<Appointment> appointments, Context context) {
        this.appointments = appointments;
        this.context = context;
        this.storage =  FirebaseStorage.getInstance();
        this.fAuth =  FirebaseAuth.getInstance();
        this.fStore =  FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public PetOwnerAppointmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View inflate = inflater.inflate(R.layout.pet_owner_appoinment_histrory_row, parent, false);
        return new PetOwnerAppointmentAdapter.ViewHolder(inflate);

    }

    @Override
    public void onBindViewHolder(@NonNull PetOwnerAppointmentAdapter.ViewHolder holder, int position) {


        Appointment appointment = appointments.get(position);
        holder.appointmentNum.setText("0"+appointment.getAppointmentNumber());
        holder.vetMobile.setText(appointment.getVetMobile());
        holder.appointmentDate.setText(appointment.getCurrentDate());
        holder.appointmentTime.setText(appointment.getCurrentTime());

        DocumentReference vet = fStore.collection("vet").document(appointment.getVetMobile());

        vet.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("First Name");
                String lastName = documentSnapshot.getString("Last Name");


                holder.vetName.setText(firstName + " " + lastName);


            } else {
                // Document does not exist
            }
        }).addOnFailureListener(e -> {
            // Handle failure to fetch data from Firestore
        });

        int color = ContextCompat.getColor(context, R.color.green);
        int color2 = ContextCompat.getColor(context, R.color.red);


        if (appointment.getStatus().equals("done")) {
            // Set button color to green
            holder.btnViewApp.setBackgroundColor(color); // Replace with your desired color
        } else {
            // Set button color to red
            holder.btnViewApp.setBackgroundColor(color2); // Replace with your desired color
        }


        holder.btnViewApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (appointment != null) {

                    // Perform action when the button is clicked
                    Context context = v.getContext();
                    Intent intent = new Intent(context, PetOwner_Appointment_Full_Details_Activity.class);
                    intent.putExtra("vet_Mobile", appointment.getVetMobile());
                    intent.putExtra("petOwner_Mobile", appointment.getPetOwnerMobile());
                    intent.putExtra("appointmentNum", appointment.getAppointmentNumber());
                    intent.putExtra("date", appointment.getCurrentDate());
                    // Add any extras to the intent if needed
                    context.startActivity(intent);
                    

                } else {
                    // Handle the case where appointment is null
                }

            }
        });




    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView appointmentNum,vetMobile,vetName,appointmentDate,appointmentTime;


        Appointment appointment;
        Button btnViewApp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentNum = itemView.findViewById(R.id.appNumPO1);
            vetMobile = itemView.findViewById(R.id.vetApprMobile2);
            vetName = itemView.findViewById(R.id.vetApprName2);
            appointmentDate = itemView.findViewById(R.id.appDate);
            appointmentTime= itemView.findViewById(R.id.appTime);
            btnViewApp = itemView.findViewById(R.id.btnView2);



        }
    }
}
