package com.example.our_space;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Message {

    public String uid;
    public String author;
    public String body;
    public int likeCount = 0;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Message(String uid, String author, String body) {
        this.uid = uid;
        this.author = author;
        this.body = body;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("body", body);
        result.put("likeCount", likeCount);

        return result;
    }

    public static Message fromMap(Map<String, Object> map) {
        return new Message((String)map.get("uid"), (String)map.get("author"), (String)map.get("body"));
    }
}