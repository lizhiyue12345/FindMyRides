package com.example.findmyrides;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class DriverProf extends AppCompatActivity {
    Toolbar myToolbar2;
    GoogleSignInAccount user;
    ImageView profile_pic;
    TextView driver_name;
    TextView make, model, plate, cap;
    DatabaseReference driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_prof);

        myToolbar2 = (Toolbar) findViewById(R.id.my_toolbar2);
        setSupportActionBar(myToolbar2);
        getSupportActionBar().setTitle("");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab2 = getSupportActionBar();
        // Enable the Up button
        assert ab2 != null;
        ab2.setDisplayHomeAsUpEnabled(true);
        user = GoogleSignIn.getLastSignedInAccount(DriverProf.this);
        profile_pic = findViewById(R.id.driver_pic);
        make = findViewById(R.id.make);
        model = findViewById(R.id.model);
        plate = findViewById(R.id.plate);
        cap = findViewById(R.id.capacity);
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(profile_pic);
        driver_name = findViewById(R.id.driver_name);
        driver_name.setText(user.getDisplayName());

        driver = FirebaseDatabase.getInstance().getReference().child("Drivers")
                .child(user.getDisplayName());
        driver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Make") != null) {
                        make.setText(map.get("Make").toString());
                    }
                    if (map.get("Model") != null) {
                        model.setText(map.get("Model").toString());
                    }
                    if (map.get("Capacity") != null) {
                        cap.setText(map.get("Capacity").toString());
                    }
                    if (map.get("Plate") != null) {
                        plate.setText(map.get("Plate").toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
