package com.example.hotpotato;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Ratings extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("Ratings");
    String landmark;
    List<Data> landmarkRatings;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);
        Intent intent = getIntent();
        landmark = intent.getStringExtra("landmark");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        landmarkRatings = new ArrayList<>();
        Toast.makeText(Ratings.this,landmark,Toast.LENGTH_SHORT).show();

        ImageButton back = findViewById(R.id.btnRatingsBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Ratings.this, UserHome.class);
                i.putExtra("user",user.getUid());
                startActivity(i);
            }
        });

        DocumentReference docRef = ref.document(landmark);
        docRef.collection("CollectionOfRatings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size() > 0){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String desc = document.getString("Description");
                                    String user= document.getString("Username");
                                    int rate = document.getLong("Rating").intValue();
                                    switch (rate){
                                        case 1:
                                            landmarkRatings.add(new Data(user + " : " + Long.toString(rate), desc, R.drawable.potato1));
                                            break;

                                        case 2:
                                            landmarkRatings.add(new Data(user + " : " +Long.toString(rate), desc, R.drawable.potato2));
                                            break;

                                        case 3:
                                            landmarkRatings.add(new Data(user + " : " +Long.toString(rate), desc, R.drawable.potato3));
                                            break;

                                        case 4:
                                            landmarkRatings.add(new Data(user + " : " +Long.toString(rate), desc, R.drawable.potato4));
                                            break;

                                        case 5:
                                            landmarkRatings.add(new Data(user + " : " +Long.toString(rate), desc, R.drawable.potato5));
                                            break;
                                    }
                                    //landmarkRatings.add(new Data(Long.toString(rate), desc, R.drawable.hotpotato_icon_foreground));
                                    RecyclerView recyclerView = findViewById(R.id.recRatingsView);
                                    RatingsRecAdapter adapter = new RatingsRecAdapter(landmarkRatings, getApplication());
                                    recyclerView.setLayoutManager(new LinearLayoutManager(Ratings.this));
                                    recyclerView.setAdapter(adapter);

                                }
                            }
                            else{
                                Toast.makeText(Ratings.this,"No Ratings for " + landmark, Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }
}