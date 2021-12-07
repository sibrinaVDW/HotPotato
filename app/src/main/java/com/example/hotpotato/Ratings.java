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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Map;

public class Ratings extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("Users");
    String landmark;
    List<Data> landmarkRatings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);
        Intent intent = getIntent();
        landmark = intent.getStringExtra("landmark");

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
                        List<Rating> rates = document.toObject(RatingDco.class).rates;
                        //List<Map<String, Object>> ratings = (List<Map<String, Object>>) document.get("RatingLandmarks");
                        //Map<String, Object> data = new HashMap<>();
                        //List<String> titles = new ArrayList<>();
                        for (Rating rating : rates) {
                            String desc = rating.getDesc();
                            int rate = rating.getRatingNum();

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

                            landmarkRatings.add(new Data(Integer.toString(rate), desc, R.drawable.hotpotato_icon_foreground));
                            RecyclerView recyclerView = findViewById(R.id.recRatingsView);
                            RecAdapter adapter = new RecAdapter(landmarkRatings, getApplication());
                            recyclerView.setLayoutManager(new LinearLayoutManager(Ratings.this));
                            recyclerView.setAdapter(adapter);
                        }

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}