package com.example.our_space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private Button sendButton;
    private TextView newMessage;
    private EditText messageField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        messageField = (EditText) findViewById(R.id.messageField);
        newMessage = (TextView) findViewById(R.id.newMessage);
        sendButton = (Button) findViewById(R.id.sendButton);

    }

    @Override
    public void onStart() {
        super.onStart();

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String messageKey = mDatabase.child("messages").push().getKey();
                String messageString = messageField.getText().toString();
                mDatabase.child("messages").child(messageKey).setValue(messageString)
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
                messageField.setText("");
            }
        });


        ChildEventListener postListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String message = (String) dataSnapshot.getValue();
                TextView newMessageView = createNewTextView();
                newMessageView.setText(message);

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
                Log.w("MainActivity", "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(MainActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };

        mDatabase.child("messages").getRef().addChildEventListener(postListener);
    }

    public TextView createNewTextView() {
        TextView newMessageView = new TextView(this);
        newMessageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout linearLayout = findViewById(R.id.mainLinearLayout);
        linearLayout.addView(newMessageView);
        return newMessageView;
    }



}
