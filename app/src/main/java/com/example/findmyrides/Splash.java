package com.example.findmyrides;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1500;
    GoogleSignInAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                user = GoogleSignIn.getLastSignedInAccount(Splash.this);
                if(user != null) {
                    startActivity(new Intent(Splash.this, MainActivity.class));
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    finish();
                }
                else {
                    startActivity(new Intent(Splash.this, Registration.class));
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
