package com.example.our_space;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageService {

    private DatabaseReference mDatabase;

    public MessageService() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void postMessageToRoom(String uid, String author, String roomId, String body) {
        String userMessageRef = "/users/" + uid + "/messages/";
        String roomMessageRef = "/rooms/" + roomId + "/messages/";

        String messageKey = mDatabase.child("users").child(uid).child("messages").push().getKey();
        Message message = new Message(uid, author, body);
        Map<String, Object> messageValues = message.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userMessageRef + messageKey, messageValues);
        childUpdates.put(roomMessageRef + messageKey, messageValues);

        mDatabase.updateChildren(childUpdates)
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
