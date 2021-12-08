package com.example.hotpotato;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AddRating extends AppCompatActivity {
    String landmark;
    String ratingDescription;
    String username;
    int rating;
    boolean foundLandmark;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("Ratings");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rating);
        Intent intent = getIntent();
        landmark = intent.getStringExtra("landmark");
        username = "";
        mAuth = FirebaseAuth.getInstance();
        TextView ratingDesc;
        TextView title;
        title = findViewById(R.id.placetxt);
        title.setText(landmark);
        ImageButton pot1;
        ImageButton pot2;
        ImageButton pot3;
        ImageButton pot4;
        ImageButton pot5;
        ImageButton ratingApply;
        ImageButton addRatingsBack;
        ratingDesc = findViewById(R.id.txtplaceDescription);
        ratingApply = findViewById(R.id.btnRatingsApply);
        pot1 = findViewById(R.id.btnPotato1);
        pot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating =  1;
                Toast.makeText(AddRating.this, "Rating changed to  " + rating, Toast.LENGTH_SHORT).show();
            }
        });
        pot2 = findViewById(R.id.btnPotato2);
        pot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating =  2;
                Toast.makeText(AddRating.this, "Rating changed to  " + rating, Toast.LENGTH_SHORT).show();
            }
        });
        pot3 = findViewById(R.id.btnPotato3);
        pot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating =  3;
                Toast.makeText(AddRating.this, "Rating changed to  " + rating, Toast.LENGTH_SHORT).show();
            }
        });
        pot4 = findViewById(R.id.btnPotato4);
        pot4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating =  4;
                Toast.makeText(AddRating.this, "Rating changed to  " + rating, Toast.LENGTH_SHORT).show();
            }
        });
        pot5 = findViewById(R.id.btnPotato5);
        pot5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating =  5;
                Toast.makeText(AddRating.this, "Rating changed to  " + rating, Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = db.collection("Users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        username = document.getString("name");
                    }
                }
            }
        });

        addRatingsBack = findViewById(R.id.btnBackhome);
        addRatingsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddRating.this,UserHome.class);
                i.putExtra("user",user.getUid());
                startActivity(i);
            }
        });



        ratingApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking if doc exists. if not create, but if it is then add new doc to it for a rating.
                ratingDescription = ratingDesc.getText().toString();
                ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId() == landmark){
                                    foundLandmark = true;
                                    Map<String, Object> userObj = new HashMap<>();
                                    userObj.put("Username",username);
                                    userObj.put("Description", ratingDescription);
                                    userObj.put("Rating", rating);

                                    DocumentReference newRatingRef = db.collection("Ratings").document(landmark).collection("CollectionOfRatings").document();

                                    newRatingRef.set(userObj);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

                if(!foundLandmark){
                    //no ratings for this landmark yet, create document for it and appropriate collections.
                    ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Map<String, Object> userObj = new HashMap<>();
                                userObj.put("Username",username);
                                userObj.put("Description", ratingDescription);
                                userObj.put("Rating", rating);
                                ref.document(landmark).collection("CollectionOfRatings").add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(AddRating.this, "Rating added! :)  " + rating, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }
                    });
                }
            }
        });


    }
}