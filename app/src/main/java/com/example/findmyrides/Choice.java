package com.example.findmyrides;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Choice extends AppCompatActivity {
    FirebaseAuth mAuth;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount user;
    DatabaseReference ref;
    DatabaseReference driver;
    ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        user = GoogleSignIn.getLastSignedInAccount(Choice.this);
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        ref = FirebaseDatabase.getInstance().getReference();
    }
    public void onClick_1(View view) {
        driver = ref.child("Drivers")
                .child(user.getDisplayName());
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    startActivity(new Intent(Choice.this, MainActivity.class));
                } else {
                    startActivity(new Intent(Choice.this, Driver.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        driver.addListenerForSingleValueEvent(eventListener);
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }
    public void onClick_2(View view) {
        startActivity(new Intent(Choice.this, MainActivity.class));
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        finish();
    }
    public void onClick_3(View view){
        signOut();
        startActivity(new Intent(Choice.this, Registration.class));
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        finish();
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = GoogleSignIn.getLastSignedInAccount(Choice.this);
        if(user == null){
            finish();
        }
    }

}
