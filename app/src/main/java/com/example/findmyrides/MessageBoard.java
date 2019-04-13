package com.example.findmyrides;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageBoard extends AppCompatActivity {

    private static final String TAG = MessageBoard.class.getSimpleName();
    EditText Mday;
    EditText Mtime;
    EditText Mfrom;
    EditText Mdes;
    RelativeLayout info;
    GoogleSignInAccount user;
    Toolbar myToolbar2;
    Button button;
    String id, message_key;
    ValueEventListener eventListener;
    DatabaseReference current_db, driver, ref;
    boolean d = false;
    ListView MessageList;
    List<Message> userList;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board);

        user = GoogleSignIn.getLastSignedInAccount(MessageBoard.this);
        myToolbar2 = (Toolbar) findViewById(R.id.my_toolbar2);
        setSupportActionBar(myToolbar2);
        getSupportActionBar().setTitle("");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab2 = getSupportActionBar();
        // Enable the Up button
        assert ab2 != null;
        ab2.setDisplayHomeAsUpEnabled(true);

        Mday = findViewById(R.id.Mday);
        Mtime = findViewById(R.id.Mtime);
        Mfrom = findViewById(R.id.Mfrom);
        Mdes = findViewById(R.id.Mdes);
        info = findViewById(R.id.info_lo);
        MessageList = findViewById(R.id.MessageList);
        myToolbar2.setTitle("Message Board");
        isDriver();
        MessageList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter,View v, int position, long id){
                Message item = (Message)adapter.getItemAtPosition(position);
                String acc = item.getAcc();
                intent = new Intent(MessageBoard.this,MainActivity.class);
                if(acc.equals("Not accepted") && d) {
                    intent.putExtra("key", item.getKey());
                    Log.i(TAG, "nmsl: "+item.getKey());
                    startActivity(intent);
                }
            }
        });
        userList = new ArrayList<>();

        current_db = FirebaseDatabase.getInstance().getReference().child("Message");
        ref = FirebaseDatabase.getInstance().getReference().child("AcceptedRide");
        ImageView picture = (ImageView) findViewById(R.id.tou);
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(picture);

    }

    public void isDriver(){
        driver = FirebaseDatabase.getInstance().getReference().child("Drivers")
                .child(user.getDisplayName());
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    d = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        driver.addListenerForSingleValueEvent(eventListener);
    }

    @Override
    protected void onStart(){
        super.onStart();
        current_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren() ){
                    Map<String, Object> map = (Map<String, Object>)userSnapshot.getValue();
                    Message message = new Message(map.get("userName").toString(),
                            map.get("userDay").toString(),
                            map.get("userTime").toString(),
                            map.get("userFrom").toString(),
                            map.get("userDes").toString(),
                            map.get("acc").toString(),
                            map.get("key").toString());
                    userList.add(message);
                }
                MessageList adapter = new MessageList(MessageBoard.this, 0,userList);
                MessageList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void Back(View view){
        info.setVisibility(view.INVISIBLE);
        MessageList.setVisibility(view.VISIBLE);

    }

    public void Post(View view) {
        String name = user.getDisplayName();
        String mday = Mday.getText().toString();
        String mtime = Mtime.getText().toString();
        String mfrom = Mfrom.getText().toString();
        String mdes = Mdes.getText().toString();
        String acc = "Not accepted";

        if (mday.length() < 10) {
            Toast.makeText(this, "The day should like 01/01/2018.", Toast.LENGTH_LONG).show();
        } else if (mtime.length() < 5) {
            Toast.makeText(this, "The time should like 01:01", Toast.LENGTH_LONG).show();
        } else if (mfrom.length() == 0) {
            Toast.makeText(this, "The Departure can't be empty", Toast.LENGTH_LONG).show();
        }else if (mdes.length() == 0) {
            Toast.makeText(this, "The Destination can't be empty", Toast.LENGTH_LONG).show();

        }else{
            id = current_db.push().getKey();
            Message message = new Message(name, mday, mtime, mfrom, mdes, acc, id);
            current_db.child(id).setValue(message);
            Toast.makeText(this, "Add successfully", Toast.LENGTH_LONG).show();
            info.setVisibility(view.INVISIBLE);
            MessageList.setVisibility(view.VISIBLE);
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        }


    }

    public void onClick(View view) {
        Mday.setText("");
        Mtime.setText("");
        Mfrom.setText("");
        Mdes.setText("");
        info.setVisibility(view.VISIBLE);
        MessageList.setVisibility(view.INVISIBLE);
    }

}
