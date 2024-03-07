package lk.javainstitute.petpulse_v2.petOwner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import lk.javainstitute.petpulse_v2.LogInActivity;
import lk.javainstitute.petpulse_v2.R;
import lk.javainstitute.petpulse_v2.vet.Doctor_Home_Activity;

public class PetOwner_Home_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "TAG";
    private DrawerLayout drawerLayout;

    private TextView name;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String userMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_owner_home);

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("PET PULSE");

        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);

        //Display Data From Firestore
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = fAuth.getCurrentUser();
        userMobile = currentUser.getPhoneNumber();
        String formattedNumber = userMobile.replace("+94", "0");
//        Log.d(TAG, "Phone Number: " + formattedNumber);


      DocumentReference userRef = fStore.collection("petOwners").document(formattedNumber);

        userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                String userFirstName = documentSnapshot.getString("First Name");
                String userLastName = documentSnapshot.getString("Last Name");
                String mobile = documentSnapshot.getString("mobile");

                View headerView = navigationView.getHeaderView(0);
                // Find the TextView within the header layout
                TextView userNameTextView = headerView.findViewById(R.id.user_name);
                TextView userMobileTextView = headerView.findViewById(R.id.user_mobile);

                userNameTextView.setText( userFirstName +" "+userLastName);
                userMobileTextView.setText( mobile);

            }
        });

        //Display Data From Firestore



        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PetOwner_Nav_Appointment_Fragment()).commit();
            navigationView.setCheckedItem(R.id.nav_appointment);
        }
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_appointment) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PetOwner_Nav_Appointment_Fragment())
                    .commit();
        }else if (itemId == R.id.nav_history) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PetOwner_Nav_History_Fragment())
                    .commit();
        } else if (itemId == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PetOwner_Nav_Profile_Fragment())
                    .commit();
        } else if (itemId == R.id.nav_logout) {

            if (fAuth != null) {
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                fAuth.signOut();
                Intent intent = new Intent(PetOwner_Home_Activity.this,LogInActivity.class);
                startActivity(intent);
                finish();


            }


        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}