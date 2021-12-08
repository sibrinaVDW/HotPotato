package com.example.hotpotato;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHome extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("Users");
    ImageButton goToMap;
    ImageButton imageButton13;
    ImageButton goToPlayerList;
    TextView userList;
    List<Data> landmarkData;
    List<Data> userData;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        goToMap = findViewById(R.id.imageButton9);
        goToPlayerList = findViewById(R.id.otherUsersButton);

        //userList = findViewById(R.id.userListTextbox);
        final String[] userListString = {""};

        Intent intent = getIntent();
        userID = intent.getStringExtra("user");
        landmarkData = new ArrayList<>();
        userData = new ArrayList<>();

        DocumentReference docRef = ref.document(userID).collection("FavouriteLandmarks").document("Landmarks");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<Object> geoPoint = (List<Object>) document.get("ListLandmarks");
                        for (Object geoObject : geoPoint){
                            GeoPoint gp = (GeoPoint) geoObject;
                            MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                                    .accessToken(getString(R.string.mapbox_access_token))
                                    .query(Point.fromLngLat(gp.getLongitude(),gp.getLatitude()))
                                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                                    .build();

                            reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
                                @Override
                                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                                    List<CarmenFeature> results = response.body().features();

                                    if (results.size() > 0) {
                                        CarmenFeature feature;
                                        Point firstResultPoint = results.get(0).center();
                                        feature=results.get(0);
                                      
                                       // Toast.makeText(UserHome.this, "" + feature.placeName(), Toast.LENGTH_LONG).show();
                                        List<Data> users = new ArrayList<>();
                                        landmarkData.add(new Data(feature.placeName(), "Click for more options",R.drawable.hotpotato_icon_foreground));
                                        RecyclerView recyclerView = findViewById(R.id.recLandmarkView);
                                        RecAdapter adapter = new RecAdapter(landmarkData, getApplication());
                                        recyclerView.setLayoutManager(new LinearLayoutManager(UserHome.this));
                                        recyclerView.setAdapter(adapter);

                                    } else {
                                        // No result for the request were found.
                                        Toast.makeText(UserHome.this, "Not found", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            });
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

        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserHome.this,MapboxMapActivity.class);
                i.putExtra("user",userID);
                i.putExtra("favorite","");
                startActivity(i);
            }
        });

        goToPlayerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    LayoutInflater li = LayoutInflater.from(getApplicationContext());
                                    View userListpopup = LayoutInflater.from(UserHome.this).inflate(R.layout.activity_userlist_popup,null);
                                    AlertDialog.Builder alertBuild = new AlertDialog.Builder(UserHome.this).setView(userListpopup).setTitle("VisibleUsers");
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        if (!document.getId().equals(userID)){
                                            //userListString[0] += document.getId() + " => " + document.getString("name") + "\n";
                                            //Log.d(TAG, document.getId() + " => " + document.getData());

                                            //List<Data> users = new ArrayList<>();
                                            userData.add(new Data(document.getString("name"), "Click to follow",R.drawable.user_btn));
                                            RecyclerView userListRecyclerView = userListpopup.findViewById(R.id.userList_rcv);
                                            RecAdapter userListAdapter = new RecAdapter(userData, getApplication());
                                            userListRecyclerView.setLayoutManager(new LinearLayoutManager(UserHome.this));
                                            userListRecyclerView.setAdapter(userListAdapter);
                                        }
                                    }

                                    //recycler
                                    AlertDialog alertDiag = alertBuild.show();
                                    /*userList = userListpopup.findViewById(R.id.userListTextbox);
                                    userList.setText(userListString[0]);*/
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }

                            }
                        });
            }
        });


        imageButton13 = (ImageButton) findViewById(R.id.imageButton13);
        imageButton13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();
            }
        });


    }


    private void showDialog()
    {
        final Dialog dialog = new Dialog(UserHome.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsettingslayout);

        LinearLayout unitLayout = dialog.findViewById(R.id.layoutUnits);
        LinearLayout landmarkPrefLayout = dialog.findViewById(R.id.layoutLandmarkpref);

        unitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(UserHome.this, "Units is clicked",Toast.LENGTH_LONG).show();
                showUnits();
                dialog.dismiss();

            }
        });

        landmarkPrefLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(UserHome.this, "Landmark Pref is clicked",Toast.LENGTH_LONG).show();
                showLandpref();
                dialog.dismiss();

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showUnits()
    {
        final Dialog dialog = new Dialog(UserHome.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomunitslayout);

        LinearLayout kmLayout = dialog.findViewById(R.id.layoutKm);
        LinearLayout miLayout = dialog.findViewById(R.id.layoutMi);

        kmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.document(userID).update("unitsPref","km").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserHome.this, "Unit preference changed to km", Toast.LENGTH_SHORT).show();
                    }
                });

                //Toast.makeText(UserHome.this, "KM chosen",Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });

        miLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.document(userID).update("unitsPref","mi").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserHome.this, "Unit preference changed to mi", Toast.LENGTH_SHORT).show();
                    }
                });
                //Toast.makeText(UserHome.this, "Mi chosen",Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showLandpref()
    {
        final Dialog dialog = new Dialog(UserHome.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottompreflayout);

        LinearLayout historyLayout = dialog.findViewById(R.id.layoutHistorical);
        LinearLayout popularLayout = dialog.findViewById(R.id.layoutPopular);
        LinearLayout modernLayout = dialog.findViewById(R.id.layoutModern);

        historyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.document(userID).update("prefLandmarks","Historical").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserHome.this, "Landmark preference changed to Historical", Toast.LENGTH_LONG).show();
                    }
                });
                //Toast.makeText(UserHome.this, "Historical chosen",Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });

        popularLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.document(userID).update("prefLandmarks","Popular").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserHome.this, "Landmark preference changed to Popular", Toast.LENGTH_LONG).show();
                    }
                });
                //Toast.makeText(UserHome.this, "Popular chosen",Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });

        modernLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.document(userID).update("prefLandmarks","Modern").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserHome.this, "Landmark preference changed to Modern", Toast.LENGTH_LONG).show();
                    }
                });
                //Toast.makeText(UserHome.this, "Modern chosen",Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

}