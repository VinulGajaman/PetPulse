package lk.javainstitute.petpulse_v2.petOwner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lk.javainstitute.petpulse_v2.R;

public class Appointment_Details_Activity extends AppCompatActivity {

    public static final String TAG = "TAG";
    ImageButton uploadImg;
    FirebaseAuth fAuth;

    FirebaseFirestore fStore;
    String[] animal = {"Dog","Cat","Bird","Fish","Hamster"};

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> arrayAdapter;

    ProgressBar progressBar;
    String userMobile;


    String VetmobileNumber;
    String item;
    private Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        uploadImg = findViewById(R.id.btn_petImg);

        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        if (intent != null) {
            VetmobileNumber = intent.getStringExtra("Vet_Mobile");
        }

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher.launch(Intent.createChooser(intent,"Select Image"));

            }
        });

        //Dropdown View
        autoCompleteTextView = findViewById(R.id.auto_complte_txt);
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.list_animals,animal);

        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(Appointment_Details_Activity.this,"Your Pet is a: "+item,Toast.LENGTH_SHORT).show();
            }
        });
        //Dropdown View


        //Add New Record
        Button btnGoToMap = findViewById(R.id.goToMap);

        btnGoToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextInputEditText des = findViewById(R.id.vetNote2);
                String petDes = des.getText().toString().trim();
                String pet = autoCompleteTextView.getText().toString().trim();
//                Drawable drawable = uploadImg.getDrawable();
                //validation
                if (TextUtils.isEmpty(pet)){
                    Toast.makeText(getApplicationContext(), "Please Select Your Pet", Toast.LENGTH_SHORT).show();

                }else if (imagePath == null) {
                    // Check the constant state of the drawable
                    Toast.makeText(Appointment_Details_Activity.this, "Upload an Image", Toast.LENGTH_SHORT).show();

            }else if(TextUtils.isEmpty(petDes)){
                    Toast.makeText(getApplicationContext(), "Please enter Description", Toast.LENGTH_SHORT).show();


                } else{

                    progressBar.setVisibility(View.VISIBLE);


                String imageId = UUID.randomUUID().toString();

                FirebaseUser currentUser = fAuth.getCurrentUser();
//
                //Image Record
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("petImages/" + imageId + ".jpg");
//
//                // Get the bitmap from the ImageView (Replace petImageView with your ImageView)
                    if (uploadImg.getDrawable() != null && uploadImg.getDrawable() instanceof BitmapDrawable) {
                        Bitmap bitmap = ((BitmapDrawable) uploadImg.getDrawable()).getBitmap();

                        if (bitmap != null) {


                // Convert the bitmap to a byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                // Upload the image to Firebase Storage
                storageRef.putBytes(imageData)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Image uploaded successfully; get its download URL
                            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
//
//                                //Add New Record
//                                if (currentUser != null) {
//                                    userMobile = currentUser.getPhoneNumber();
//                                    String formattedNumber = userMobile.replace("+94", "0");
////        Log.d(TAG, "Phone Number: " + formattedNumber);
//
//                                    // Create a reference to the current user's document in the petOwners collection
//                                    DocumentReference userRef = fStore.collection("petOwners").document(formattedNumber);
//
//                                    // Create a Map object with the pet's details including the image UUID and download URL
//                                    Map<String, Object> petData = new HashMap<>();
//                                    petData.put("pet", pet);
//                                    petData.put("pet descritpion", petDes);
//                                    petData.put("petImageUUID", imageId);
//                                    petData.put("petImageUrl", downloadUrl);
//
//                                    // Update the document in Firestore with pet information
//                                    userRef.set(petData, SetOptions.merge())
//                                            .addOnSuccessListener(aVoid -> {
//                                                // Handle successful update
//                                                Log.d("TAG", "Image UUID added to Firestore");

                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(Appointment_Details_Activity.this,"Uploaded",Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(Appointment_Details_Activity.this, Checkout_Activity.class);
                                                intent.putExtra("Vet_Mobile", VetmobileNumber);
                                                intent.putExtra("petType", pet);
                                                intent.putExtra("petDes", petDes);
                                                intent.putExtra("ImgURL", downloadUrl);
                                                startActivity(intent);

//                                            })
//                                            .addOnFailureListener(e -> {
//                                                // Handle failure in updating the document
//                                                Log.w("TAG", "Error updating pet information", e);
//                                            });
//                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure in uploading the image to Storage
                            Log.w("TAG", "Error uploading image to Firebase Storage", e);
                        });
                        } else {
                            // Bitmap is null
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Appointment_Details_Activity.this, "Invalid Image", Toast.LENGTH_SHORT).show();
                        }
                }

                }
            }
        });

    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode()== Activity.RESULT_OK){

                        imagePath = result.getData().getData();
                        Picasso.get()
                                .load(imagePath)
                                .resize(450,450)
                                .centerCrop()
                                .into(uploadImg);




                    }
                }
            }
    );





}