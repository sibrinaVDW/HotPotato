package com.example.hotpotato;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Ratings extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("Ratings");
    String landmark;
    List<Data> landmarkRatings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);
        Intent intent = getIntent();
        landmark = intent.getStringExtra("landmark");
        landmarkRatings = new ArrayList<>();
        Toast.makeText(Ratings.this,landmark,Toast.LENGTH_SHORT).show();

        ImageButton back = findViewById(R.id.btnRatingsBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Ratings.this, UserHome.class);
                startActivity(i);
            }
        });

        DocumentReference docRef = ref.document(landmark);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        List<Rating> rates = new ArrayList<>();
                        List<Map<String, Object>> users = (List<Map<String, Object>>) document.get("RatingLandmark");
                        assert users != null;
                        for (Map<String, Object> rating:users){
                            //Collections ratesData = (Collections) rating.values().toArray();
                            //Toast.makeText(Ratings.this, ratesData[1],Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(Ratings.this,"users size: " + users.size(),Toast.LENGTH_SHORT).show();
                        //rates = document.toObject(RatingDco.class).rates;
                        //makeText(Ratings.this,"rating size: " + rates.size(),Toast.LENGTH_SHORT).show();
                        //List<Map<String, Object>> ratings = (List<Map<String, Object>>) document.get("RatingLandmarks");
                        //Map<String, Object> data = new HashMap<>();
                        //List<String> titles = new ArrayList<>();
                        for (Rating rating : rates) {
                            String desc = rating.getDesc();
                            int rate = rating.getRatingNum();
                            Toast.makeText(Ratings.this,desc + " " + rate,Toast.LENGTH_SHORT).show();

                            switch (rate){
                                case 0:
                                    break;

                                case 1:
                                    break;

                                case 2:
                                    break;

                                case 3:
                                    break;

                                case 4:
                                    break;

                                case 5:
                                    break;
                            }

                            Toast.makeText(Ratings.this,"In",Toast.LENGTH_SHORT).show();
                            landmarkRatings.add(new Data(Integer.toString(rate), desc, R.drawable.hotpotato_icon_foreground));
                            RecyclerView recyclerView = findViewById(R.id.recRatingsView);
                            RatingsRecAdapter adapter = new RatingsRecAdapter(landmarkRatings, getApplication());
                            recyclerView.setLayoutManager(new LinearLayoutManager(Ratings.this));
                            recyclerView.setAdapter(adapter);
                        }

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                        Toast.makeText(Ratings.this,"No such document",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(Ratings.this,"get failed with "+ task.getException(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}