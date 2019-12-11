package com.example.our_space;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Message {

    public String uid;
    public String author;
    public String room;
    public String body;
    public Long timestamp;
    public int likeCount = 0;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Message(String uid, String author, String room, String body) {
        this.uid = uid;
        this.author = author;
        this.room = room;
        this.body = body;
        this.timestamp = System.currentTimeMillis();
    }

    public Message(String uid, Long timeStamp, String author, String room, String body) {
        this.uid = uid;
        this.author = author;
        this.room = room;
        this.body = body;
        this.timestamp = timeStamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("room", room);
        result.put("body", body);
        result.put("likeCount", likeCount);
        result.put("timeStamp", timestamp);

        return result;
    }

    public static Message fromMap(Map<String, Object> map) {
        return new Message((String)map.get("uid"), (Long) map.get("timeStamp"), (String)map.get("author"), (String)map.get("room"), (String)map.get("body"));
    }
}