package com.example.our_space;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Message {

    public String uid;
    public String author;
    public String room;
    public String body;
    public int likeCount = 0;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Message(String uid, String author, String room, String body) {
        this.uid = uid;
        this.author = author;
        this.room = room;
        this.body = body;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("room", room);
        result.put("body", body);
        result.put("likeCount", likeCount);

        return result;
    }
}