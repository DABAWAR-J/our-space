package com.example.our_space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends BaseActivity implements
        View.OnClickListener{

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Button sendButton;
    private Button logOutButton;
    private EditText messageField;
    private Spinner mySpinner;
    private String currentRoom;
    private Integer currentPosition=0;
    private ChildEventListener postListener;

    //Location Tracking
    private LocationListener locationListener;
    private LocationManager locationManager;

    private final long MIN_TIME = 500; // 1 second
    private final long MIN_DIST = 1; // 5 meters

    private double lat; //latitude
    private double lng; //longitude

    private String latText = ""; //latitude string
    private String lngText; //longitude string

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        messageField = findViewById(R.id.messageField);
        sendButton = findViewById(R.id.sendButton);
        logOutButton = findViewById(R.id.logOutButton);

        mySpinner = findViewById(R.id.spinner1);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MessageActivity.this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.locations));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        currentRoom = (String) mySpinner.getSelectedItem();


        //Location Tracking
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lat = location.getLatitude();
                lng = location.getLongitude();

                latText = String.valueOf(lat);
                lngText = String.valueOf(lng);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String messageString = messageField.getText().toString();
                sendMessage(messageString);
                messageField.setText("");
            }
        });

        logOutButton.setOnClickListener(this);

        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                messageField.setEnabled(true);
                messageField.setText("");
                String previousRoom = currentRoom;
                Integer previousInt = currentPosition;
                currentRoom = (String) parent.getItemAtPosition(position);
                currentPosition = position;
                Double roomLat = 0.0;
                Double roomLng = 0.0;
                if (currentRoom.equals("DECKER HALL 360")) {
                    roomLat = 40.11179156;
                    roomLng = -85.66601368;
                }
                if (currentRoom.equals("DECKER HALL 351")) {
                    roomLat = 40.1116857;
                    roomLng = -85.66618809;
                }
                if (currentRoom.equals("DECKER LOUNGE")) {
                    roomLat = 40.11163259;
                    roomLng = 85.66634455;
                }
                if(((latText != "") && (lat - roomLat < 0.00005)&& (lng - roomLng < .00005))|| currentRoom.equals("CHOOSE ROOM")) {
                    if (currentRoom.equals("CHOOSE ROOM")) {
                        messageField.setEnabled(false);
                    }
                }else {
                    parent.setSelection(previousInt);
                }


                if(!previousRoom.equals(currentRoom)) {
                    LinearLayout linearLayout = findViewById(R.id.messageLinearLayout);
                    linearLayout.removeAllViewsInLayout();
                    mDatabase.child("rooms").child(previousRoom).getRef().removeEventListener(postListener);
                    mDatabase.child("rooms").child(currentRoom).getRef().addChildEventListener(postListener);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        postListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Message message = Message.fromMap((HashMap<String, Object>)dataSnapshot.getValue());
                TextView newMessageView = createNewTextView();
                newMessageView.setText(message.body);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("MessageActivity", "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(MessageActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };

        mDatabase.child("rooms").child(currentRoom).getRef().addChildEventListener(postListener);
    }

    public TextView createNewTextView() {
        TextView newMessageView = new TextView(this);
        newMessageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout linearLayout = findViewById(R.id.messageLinearLayout);
        linearLayout.addView(newMessageView);
        return newMessageView;
    }

    public void sendMessage(String message) {
        String messageKey = mDatabase.child("users").child(getUid()).push().getKey();
        String author = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Message newMessage = new Message(getUid(), author, currentRoom, message);
        Map<String, Object> newMessageValues = newMessage.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + getUid() + "/" + messageKey, newMessageValues);
        childUpdates.put("/rooms/" + currentRoom + "/" + messageKey, newMessageValues);

        mDatabase.updateChildren(childUpdates);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.logOutButton) {
            mAuth.signOut();
            Intent intent = new Intent(this, GoogleSignInActivity.class);
            startActivity(intent);
        }
    }

}
