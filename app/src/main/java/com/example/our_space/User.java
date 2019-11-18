package com.example.our_space;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public String uuid;
    public String email;
    public String username;
    public ArrayList<Message> messages;
    public int likeCount = 0;

    public User() {
        // uh
    }

    public User(String uuid, String email, String username, ArrayList<Message> messages, int likeCount) {
        this.uuid = uuid;
        this.email = email;
        this.username = username;
        this.messages = messages;
        this.likeCount = likeCount;
    }

//    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("uid", uuid);
//        result.put("author", author);
//        result.put("room", room);
//        result.put("body", body);
//        result.put("likeCount", likeCount);
//
//        return result;
//    }
}