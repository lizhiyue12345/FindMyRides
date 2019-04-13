package com.example.findmyrides;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.ConstraintHorizontalLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.cast.framework.media.widget.MiniControllerFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{
    GoogleApiClient mGoogleApiClient;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount user;
    DatabaseReference ref;
    FirebaseAuth mAuth;
    private GoogleMap mMap;
    GeoDataClient mGeoDataClient;
    PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationClient;
    public boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_CURRENT_LOCATION = "cur_location";
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "key";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private CameraPosition mCameraPosition;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mLastKnownLocation;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    private ArrayList<Marker> markers = new ArrayList<>();
    private String PREFS_NAME = "com.example.findmyrides.prefs";
    private int cam = 0;
    //private boolean vis = true;
    private boolean bool;
    Intent intent = null;
    DatabaseReference driver, passenger, acceptedReq, customReq, customInfo;
    ValueEventListener eventListener, eventListener_2;
    String messageID = "";
    String key = null;
    RelativeLayout showInfo;
    android.support.constraint.ConstraintLayout con;
    TextView dtime, dday, dfrom, ades, inf, name, dri;
    String customer;
    SharedPreferences prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRequestingLocationUpdates = false;
        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Saved Instance State
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            //saveLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        }

        // Extend layout to status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // Design Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.drawer, null);
                toolbar.setNavigationIcon(d);
            }
        });

        // Map (From Map activity)
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //
        mSettingsClient = LocationServices.getSettingsClient(this);

        // Google
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(LocationServices.API)
                .build();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        user = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        showInfo = findViewById(R.id.request);
        con = findViewById(R.id.confirm);
        inf = findViewById(R.id.information);
        dtime = findViewById(R.id.dtime);
        dday = findViewById(R.id.dday);
        dfrom = findViewById(R.id.dfrom);
        ades = findViewById(R.id.des);
        name = findViewById(R.id.customerName);
        dri = findViewById(R.id.driver);


        // Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Drawer menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.user_name);
        TextView nav_email = (TextView) hView.findViewById(R.id.user_email);
        ImageView picture = (ImageView) hView.findViewById(R.id.profile_pic);
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(picture);
        nav_user.setText(user.getDisplayName());
        nav_email.setText(user.getEmail());
        navigationView.setNavigationItemSelectedListener(this);

        // Database
        ref = FirebaseDatabase.getInstance().getReference();


        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        detectMessage();
        accReq();

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
        startLocationUpdates();
    }

    private void accReq() {
        acceptedReq = ref.child("AcceptedRide");
        acceptedReq.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if(dataSnapshot.exists() && dataSnapshot.child("Customer").exists() && dataSnapshot.child("Notify").exists() && dataSnapshot.child("Customer").getValue().equals(user.getDisplayName()) && dataSnapshot.child("Notify").getValue().equals("No")){
                    inf.setText("Your request is accepted by");
                    dri.setText(dataSnapshot.child("Driver").getValue().toString());
                    con.setVisibility(View.VISIBLE);
                    acceptedReq.child(messageID).child("Notify").setValue("Yes");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void detectMessage() {
        customReq = ref.child("Message");
        customReq.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                messageID = dataSnapshot.getKey();
                if(dataSnapshot.child("acc").getValue().equals("Not accepted")) {
                    callDriver();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void callDriver() {
        driver = FirebaseDatabase.getInstance().getReference().child("Drivers")
                .child(user.getDisplayName());
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    showInfo.setVisibility(View.VISIBLE);
                    showCustomerInfo();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        driver.addListenerForSingleValueEvent(eventListener);
    }

    private void showCustomerInfo() {
        intent = getIntent();
        key = intent.getStringExtra("key");
        //Log.i(TAG, "nbsl: "+key);
        if(key != null) {
            customInfo = ref.child("Message").child(key);
            //getIntent().removeExtra("key");
            key = null;
        }else {
            customInfo = ref.child("Message").child(messageID);
        }
        customInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0)) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("userTime") != null) {
                        dtime.setText(map.get("userTime").toString());
                    }else{
                        Log.d(TAG, "Some Error");
                    }

                    if (map.get("userDay") != null) {
                        dday.setText(map.get("userDay").toString());
                    }else{
                        Log.d(TAG, "Some Error");
                    }

                    if (map.get("userFrom") != null) {
                        dfrom.setText(map.get("userFrom").toString());
                    }else{
                        Log.d(TAG, "Some Error");
                    }

                    if (map.get("userName") != null) {
                        name.setText(map.get("userName").toString());
                        customer = map.get("userName").toString();
                    }
                    else{
                        Log.d(TAG, "Some Error");
                    }
                    if (map.get("userDes") != null) {
                        ades.setText(map.get("userDes").toString());
                    }
                    else{
                        Log.d(TAG, "Some Error");
                    }
                }
                bool = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void accept(View view){
        intent = getIntent();
        key = intent.getStringExtra("key");
        DatabaseReference current_db_acc;
        DatabaseReference current_db_message;
        if(key != null) {
            current_db_acc = FirebaseDatabase.getInstance().getReference()
                    .child("AcceptedRide").child(key);
            current_db_message = FirebaseDatabase.getInstance().getReference()
                    .child("Message").child(key);
            getIntent().removeExtra("key");
            key = null;
        }else {
            current_db_acc = FirebaseDatabase.getInstance().getReference()
                    .child("AcceptedRide").child(messageID);
            current_db_message = FirebaseDatabase.getInstance().getReference()
                    .child("Message").child(messageID);
        }
        Map newPost_1 = new HashMap();
        newPost_1.put("Driver", user.getDisplayName());
        newPost_1.put("Customer", customer);
        newPost_1.put("Notify", "No");
        current_db_acc.setValue(newPost_1);
        current_db_message.child("acc").setValue("Accepted by "+user.getDisplayName());
        showInfo.setVisibility(View.INVISIBLE);
        moveCamera();
    }
    public void moveCamera(){
        passenger = ref.child("Passengers")
                .child(customer);
        eventListener_2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng((double)dataSnapshot.child("Latitude").getValue(),
                                    (double)dataSnapshot.child("Longitude").getValue()), DEFAULT_ZOOM));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        passenger.addListenerForSingleValueEvent(eventListener_2);
    }
    public void decline(View view){
        showInfo.setVisibility(View.INVISIBLE);
    }
    public void ok(View view){
        con.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startActivity(new Intent(MainActivity.this, Profile.class));
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        } else if (id == R.id.nav_driver) {
            driver = ref.child("Drivers")
                    .child(user.getDisplayName());
            eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        startActivity(new Intent(MainActivity.this, DriverProf.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, Driver.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            driver.addListenerForSingleValueEvent(eventListener);
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

        } else if (id == R.id.nav_out) {
            signOut();
            startActivity(new Intent(MainActivity.this, Registration.class));
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            finish();

        }else if (id == R.id.nav_messageboard){
            startActivity(new Intent(MainActivity.this, MessageBoard.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        // Do other setup activities here too, as described elsewhere in this tutorial.

    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (!shouldProvideRationale) {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                }else{
                    mLocationPermissionGranted = true;
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mLastKnownLocation = null;
                }
            }
            break;
        }
    }
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        if(mCurrentLocation != null){
            saveLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    ArrayList<Double> latit = new ArrayList<>();
                    ArrayList<Double> longi = new ArrayList<>();
                    ArrayList<String> name = new ArrayList<>();
                    for(DataSnapshot user: dataSnapshot.getChildren()){
                        latit.add(user.child("Latitude").getValue(Double.class));
                        longi.add(user.child("Longitude").getValue(Double.class));
                        name.add(user.child("name").getValue(String.class));
                    }
                    for(int i=0; i<name.size(); i++) {
                        if (markers.size() != name.size()) {
                            if(latit.get(i) != null && longi.get(i) != null){
                                Marker m = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latit.get(i), longi.get(i)))
                                        .title(name.get(i)));
                                markers.add(m);
                            }
                        } else {
                            if(markers.get(i) != null && latit.get(i) != null && longi.get(i) != null) {
                                markers.get(i).setPosition(new LatLng(latit.get(i), longi.get(i)));
                            }
                        }
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                getDeviceLocation();

                //saveLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            } else {
                mMap.setMyLocationEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            saveLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                            mMap.setMyLocationEnabled(true);
                            if(cam == 0) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                cam ++;
                            }


                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            outState.putParcelable(KEY_CURRENT_LOCATION, mCurrentLocation);
            outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                    mRequestingLocationUpdates);
            super.onSaveInstanceState(outState);
        }
    }

    public void saveLocation(double latitude, double longitude) {
        String name = user.getDisplayName();
        String email = user.getEmail();
        DatabaseReference current_db = FirebaseDatabase.getInstance().getReference()
                .child("Passengers").child(name);
        Map newPost = new HashMap();
        newPost.put("name", name);
        newPost.put("email", email);
        newPost.put("Latitude", latitude);
        newPost.put("Longitude", longitude);
        current_db.setValue(newPost);
    }
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //mLastKnownLocation = locationResult.getLastLocation();
                mCurrentLocation = locationResult.getLastLocation();
                updateLocationUI();
            }
        };
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        mRequestingLocationUpdates = false;
                        updateLocationUI();
                        break;
                }
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
        startLocationUpdates();
        updateLocationUI();
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                mRequestingLocationUpdates = false;
                        }
                        updateLocationUI();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove location updates to save battery.
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        // Update the value of mRequestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }

        // ...
        if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCurrentLocation = savedInstanceState.getParcelable(KEY_CURRENT_LOCATION);
        }

        // Update UI to match restored state
        updateLocationUI();
    }

}