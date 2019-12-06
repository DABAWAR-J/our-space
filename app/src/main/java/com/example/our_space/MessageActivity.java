package com.example.our_space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MessageActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private Button sendButton;
    private EditText messageField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        FirebaseApp.initializeApp(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        messageField = findViewById(R.id.messageField);
        sendButton = findViewById(R.id.sendButton);

        Spinner mySpinner = (Spinner) findViewById(R.id.spinner1);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MessageActivity.this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.locations));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
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


        ChildEventListener postListener = new ChildEventListener() {
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

        mDatabase.child("users").getRef().addChildEventListener(postListener);
    }

    public TextView createNewTextView() {
        TextView newMessageView = new TextView(this);
        newMessageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout linearLayout = findViewById(R.id.mainLinearLayout);
        linearLayout.addView(newMessageView);
        return newMessageView;
    }

    public void sendMessage(String message) {
        String messageKey = mDatabase.child("users").child(getUid()).push().getKey();
        String author = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Message newMessage = new Message(getUid(), author, message);
        mDatabase.child("users").child(messageKey).setValue(newMessage.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("basicWrite","Database call was successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("basicWrite", e.toString());
                        Log.e("basicWrite","Database call failed");
                    }
                });
    }



}
