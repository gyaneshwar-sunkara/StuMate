package com.stumate.main.eventListeners;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Objects;
import java.util.Observable;


public class UserDataListener extends Observable {

    private HashMap<String, Object> userData;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "UserDataListener";

    public void getUserDetails() {

        db.collection("users")
                .document(Objects.requireNonNull(user).getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.d(TAG, "get failed with ", e);
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            userData = (HashMap<String, Object>) documentSnapshot.getData();
                            Log.d(TAG, "getUserDetails: onEvent: documentSnapshot: aaaaaaaaa" + userData + "         extra info: " + documentSnapshot);
                            setChanged();
                            notifyObservers(userData);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                });
    }
}
/*
//    private String displayName;
//    private String imageUrl;
//    private String headerUrl;
//    private String bio;
//    private String collegeName;
//    private String collegeShortName;
//    private String className;
//    private String email;
//    private String phone;
//    private String uid;
//    private List<String> clubs = new ArrayList<>();
//    private List<String> mates = new ArrayList<>();
//    private List<String> pending = new ArrayList<>();
//    private List<String> blocked = new ArrayList<>();
//    private List<String> posts = new ArrayList<>();
//    private List<String> inks = new ArrayList<>();
//

//                            displayName = (String) documentSnapshot.get("displayName");
//                            imageUrl = (String) documentSnapshot.get("imageUrl");
//                            headerUrl = (String) documentSnapshot.get("headerUrl");
//                            bio = (String) documentSnapshot.get("bio");
//                            collegeName = (String) documentSnapshot.get("collegeName");
//                            collegeShortName = (String) documentSnapshot.get("collegeShortName");
//                            className = (String) documentSnapshot.get("class");
//                            email = (String) documentSnapshot.get("email");
//                            phone = (String) documentSnapshot.get("phone");
//                            uid = (String) documentSnapshot.get("uid");
//
//                            clubs = (List<String>) documentSnapshot.get("clubs");
//                            mates = (List<String>) documentSnapshot.get("mates");
//                            posts = (List<String>) documentSnapshot.get("Post");
//                            inks = (List<String>) documentSnapshot.get("inks");
//                            pending = (List<String>) documentSnapshot.get("pending");
//                            blocked = (List<String>) documentSnapshot.get("blocked");
//
//                            userData.put("displayName", displayName);
//                            userData.put("imageUrl", imageUrl);
//                            userData.put("headerUrl", headerUrl);
//                            userData.put("bio", bio);
//                            userData.put("collegeName", collegeName);
//                            userData.put("collegeShortName", collegeShortName);
//                            userData.put("className", className);
//                            userData.put("email", email);
//                            userData.put("phone", phone);
//                            userData.put("uid", uid);
//                            userData.put("clubs", displayName);
//                            userData.put("mates", mates);
//                            userData.put("posts", posts);
//                            userData.put("inks", inks);
//                            userData.put("pending", pending);
//                            userData.put("blocked", blocked);
 */