package com.example.findmyrides;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Driver extends AppCompatActivity {
    public static final String NAME = "com.example.findmyrides.NAME";
    public int Verify = 101;
    Toolbar myToolbar;
    GoogleSignInAccount user;
    ImageView profile_pic;
    TextView driver_name;
    EditText make, model, plate, cap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        user = GoogleSignIn.getLastSignedInAccount(Driver.this);
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
    }

    public void onClick_4(View view) {
        Intent intent = new Intent(this, Scanner.class);
        user = GoogleSignIn.getLastSignedInAccount(Driver.this);
        String name = user.getDisplayName();
        intent.putExtra(NAME, name);
        startActivityForResult(intent, Verify);
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == Verify) {
            if (resultCode == RESULT_OK) {
                Button check = (Button) findViewById(R.id.button3);
                check.setBackgroundResource(R.drawable.check);
                check.setEnabled(false);

                String carMake = make.getText().toString();
                String carModel = model.getText().toString();
                String carPlate = plate.getText().toString();
                String carCap = cap.getText().toString();
                String name = user.getDisplayName();
                DatabaseReference current_db = FirebaseDatabase.getInstance().getReference()
                        .child("Drivers").child(name);
                Map newPost = new HashMap();
                newPost.put("Make", carMake);
                newPost.put("Model", carModel);
                newPost.put("Plate", carPlate);
                newPost.put("Capacity", carCap);
                current_db.setValue(newPost);
                Button button = findViewById(R.id.buttonDone);
                button.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onClick_1(View view){
        finish();
    }

}
