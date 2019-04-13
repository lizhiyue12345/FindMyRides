package com.example.findmyrides;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Profile extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    GoogleSignInAccount user;
    DatabaseReference current_db,refer;

    EditText editProfileName, editProfileEma, editPhoNum;

    TextView profileName, profileEmail, profilePhoNum;

    Button edit,editConfirm,editCancel;

    ConstraintLayout ViewPage,EditPage;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        user = GoogleSignIn.getLastSignedInAccount(Profile.this);
        name = user.getDisplayName();
        current_db = FirebaseDatabase.getInstance().getReference().child("Profile");
        refer = FirebaseDatabase.getInstance().getReference().child("Profile").child(name);


        profileName = (TextView) findViewById(R.id.profileName);
        profileEmail = (TextView) findViewById(R.id.profileEmail);
        profilePhoNum = (TextView) findViewById(R.id.profilePhoNum);

        editProfileName = (EditText) findViewById(R.id.editProfileName);
        editProfileEma = (EditText) findViewById(R.id.editProfileEma);
        editPhoNum = (EditText) findViewById(R.id.editPhoNum);

        edit = (Button) findViewById(R.id.proEdit);
        editConfirm = (Button) findViewById(R.id.confirmEdit);
        editCancel = (Button) findViewById(R.id.cancelEdit);

        ViewPage = (ConstraintLayout) findViewById(R.id.ViewPage);
        EditPage = (ConstraintLayout) findViewById(R.id.EditPage);


        ImageView profileIcon = (ImageView) findViewById(R.id.profileIcon);
        ImageView editProfileIcon = (ImageView) findViewById(R.id.editProfileIcon);

        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(profileIcon);
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(editProfileIcon);

        checkInfo();


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewPage.setVisibility(View.GONE);
                EditPage.setVisibility(View.VISIBLE);
            }
        });

        editConfirm.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String enterName = editProfileName.getText().toString();
                String enterEmail = editProfileEma.getText().toString();
                String enterPhoNum = editPhoNum.getText().toString();
                if (enterPhoNum.length() < 10 && enterPhoNum.length() != 0) {

                    Toast.makeText(Profile.this, "Please enter the 10 digits phone number.", Toast.LENGTH_LONG).show();

                }else{

                    Message message = new Message(enterName, enterEmail, enterPhoNum, user.getDisplayName());
                    refer.setValue(message);
                    Toast.makeText(Profile.this, "Edit successfully", Toast.LENGTH_LONG).show();
                    updateInfo();
                    ViewPage.setVisibility(View.VISIBLE);
                    EditPage.setVisibility(View.GONE);
                }
            }
        });
        editCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewPage.setVisibility(View.VISIBLE);
                EditPage.setVisibility(View.GONE);

            }
        });
    }

    private void checkInfo() {

        refer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    profileName.setText(map.get("profileName").toString());
                    profileEmail.setText(map.get("profileEmail").toString());
                    profilePhoNum.setText(map.get("profilePhoNum").toString());


                }else{
                    String eN = user.getDisplayName();
                    String eE = user.getEmail();
                    String ePN = "";
                    Message message = new Message(eN, eE, ePN, user.getDisplayName());
                    refer.setValue(message);
                    profileName.setText(eN);
                    profileEmail.setText(eE);
                    profilePhoNum.setText(ePN);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateInfo(){

        current_db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "NMSL: "+dataSnapshot.child("finalName").getValue());
                if(dataSnapshot.child("finalName").getValue().equals(user.getDisplayName())) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    profileName.setText(map.get("profileName").toString());
                    profileEmail.setText(map.get("profileEmail").toString());
                    profilePhoNum.setText(map.get("profilePhoNum").toString());
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.child("finalName").getValue().equals(user.getDisplayName())) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    profileName.setText(map.get("profileName").toString());
                    profileEmail.setText(map.get("profileEmail").toString());
                    profilePhoNum.setText(map.get("profilePhoNum").toString());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void proBack(View view) {
        finish();
    }


}
