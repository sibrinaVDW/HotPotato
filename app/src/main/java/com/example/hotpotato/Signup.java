package com.example.hotpotato;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("Users");
    ImageButton login;
    private ImageButton add;
    private EditText email;
    private EditText password;

    List<GeoPoint> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        points = new ArrayList<>();

        email = findViewById(R.id.edtSignupEmail);
        password = findViewById(R.id.edtSignupPassword);
        add = findViewById(R.id.btnSignUp);
        login = findViewById(R.id.btnLoginBack);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Signup.this, Login.class);
                startActivity(i);

                login.setColorFilter(Color.argb(255, 255, 255, 255));
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameText = email.getText().toString();
                String passwordText = password.getText().toString();
                createAccount(nameText,passwordText);
            }
        });
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //updateUI(user);

                            Map<String, Object> userObj = new HashMap<>();
                            userObj.put("name", "");
                            userObj.put("email", email);
                            userObj.put("prefLandmarks", "Modern");
                            userObj.put("unitsPref", "km");

                            ref.document(user.getUid())
                                    .set(userObj)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });

                            Map<String, Object> data = new HashMap<>();
                            data.put("ListLandmarks", points);
                            ref.document(user.getUid()).collection("FavouriteLandmarks").document("Landmarks").set(data);

                            Toast.makeText(Signup.this, email + " created successfully!",
                                    Toast.LENGTH_SHORT).show();

                            //Open intent for profile activity
                            Intent i = new Intent(Signup.this,UserHome.class);
                            i.putExtra("user", user.getUid());
                            startActivity(i);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Signup.this, "Authentication failed. Invalid email format",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

}