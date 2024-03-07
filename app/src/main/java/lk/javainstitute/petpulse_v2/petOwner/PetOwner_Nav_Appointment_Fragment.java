package lk.javainstitute.petpulse_v2.petOwner;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import lk.javainstitute.petpulse_v2.R;


public class PetOwner_Nav_Appointment_Fragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pet_owner__nav__appointment_, container, false);




        Button goToOTP = view.findViewById(R.id.btnAppointment);
        goToOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new activity
                Intent intent = new Intent(getActivity(), Location_Map_Activity.class);
                startActivity(intent);

                //Toast.makeText(requireContext(), "Wada", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }



}