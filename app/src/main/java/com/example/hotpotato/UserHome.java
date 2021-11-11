package com.example.hotpotato;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.auth.User;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.geojson.Point;

import java.util.List;

public class UserHome extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("Users");
    ImageButton goToMap;
    TextView landmarksDisplay;
    String dispLandmarks = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        goToMap = findViewById(R.id.btnMap);
        landmarksDisplay = findViewById(R.id.txtLandmarks);
        Intent intent = getIntent();

        String landMarksID = intent.getStringExtra("landmarkID").toString();
        String userID = intent.getStringExtra("user").toString();


        DocumentReference docRef = ref.document(userID).collection("FavouriteLandmarks").document(landMarksID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<GeoPoint> landmarksFound = (List<GeoPoint>)document.get("ListLandmarks") ;
                        int i  = 0;
                        do {
                            LatLng landmark = new LatLng(landmarksFound.get(i).getLatitude(),landmarksFound.get(i).getLongitude());
                            MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                                    .accessToken("@string/mapbox_access_token")
                                    .query(Point.fromLngLat(landmarksFound.get(i).getLatitude(),landmarksFound.get(i).getLongitude()))
                                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                                    .build();

                            dispLandmarks += reverseGeocode;

                            i++;
                        }while(i < landmarksFound.size());
                        landmarksDisplay.setText(dispLandmarks);

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });




        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserHome.this,MapboxMapActivity.class);
            }
        });
    }
}