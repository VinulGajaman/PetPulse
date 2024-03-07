package lk.javainstitute.petpulse_v2.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.javainstitute.petpulse_v2.R;
import lk.javainstitute.petpulse_v2.model.Appointment;
import lk.javainstitute.petpulse_v2.vet.Vet_Appointments_Full_Details_Activity;

public class VetPendingAppointmentAdapter extends RecyclerView.Adapter<VetPendingAppointmentAdapter.ViewHolder> {

   ArrayList<Appointment> appointments;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    FirebaseStorage storage;

    String userMobile;
    private Context context;

    public VetPendingAppointmentAdapter(ArrayList<Appointment> appointments, Context context) {
        this.appointments = appointments;
        this.context = context;
        this.storage =  FirebaseStorage.getInstance();
        this.fAuth =  FirebaseAuth.getInstance();
        this.fStore =  FirebaseFirestore.getInstance();


    }

    @NonNull
    @Override
    public VetPendingAppointmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View inflate = inflater.inflate(R.layout.vet_appoinments_row, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull VetPendingAppointmentAdapter.ViewHolder holder, int position) {

        Appointment appointment = appointments.get(position);
        holder.appointmentNum.setText("0"+appointment.getAppointmentNumber());
        holder.petOwnerMobile.setText(appointment.getPetOwnerMobile());
        Picasso.get()
                .load(appointment.getImageURL())
                .resize(450, 450)
                .centerCrop()
                .into(holder.image);

        holder.btnViewApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (appointment != null) {

                    // Perform action when the button is clicked
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Vet_Appointments_Full_Details_Activity.class);
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

        DocumentReference userRef = fStore.collection("petOwners").document(appointment.getPetOwnerMobile());

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("First Name");
                String lastName = documentSnapshot.getString("Last Name");


                holder.petOwnerName.setText(firstName + " " + lastName);


            } else {
                // Document does not exist
            }
        }).addOnFailureListener(e -> {
            // Handle failure to fetch data from Firestore
        });




    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView appointmentNum,petOwnerMobile,petOwnerName;
        ImageView image;

        Appointment appointment;
        Button btnViewApp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentNum = itemView.findViewById(R.id.appNumPO);
            petOwnerMobile = itemView.findViewById(R.id.vetApprMobile);
            petOwnerName = itemView.findViewById(R.id.vetApprName);
            image = itemView.findViewById(R.id.petImg);
            btnViewApp = itemView.findViewById(R.id.btnView);



        }
    }
}
