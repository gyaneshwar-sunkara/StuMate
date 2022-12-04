package com.stumate.main.eventListeners;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stumate.main.utils.dataTypes.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;


public class UserSearchListener extends Observable {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private HashMap<String, Object> allUserData =  new HashMap<>();
    private static List<User> all = new ArrayList<>();
    private static List<User> clubs = new ArrayList<>();
    private static List<User> cse = new ArrayList<>();
    private static List<User> ece = new ArrayList<>();
    private static List<User> eee = new ArrayList<>();
    private static List<User> mech = new ArrayList<>();
    private static List<User> civil = new ArrayList<>();

    private String collegeName;
    private String collegeShortName;

    private static final String TAG = "UserSearchListener";

    public UserSearchListener(String collegeName, String collegeShortName) {
        this.collegeName = collegeName;
        this.collegeShortName = collegeShortName;
    }

    public void getAllUsers() {
        db.collection("institutes").document(collegeName).collection("public")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "getUsersNClubs onEvent: Listen Failed", e);
                            return;
                        }
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            all.clear();
                            clubs.clear();
                            cse.clear();
                            ece.clear();
                            eee.clear();
                            mech.clear();
                            civil.clear();
                            for (QueryDocumentSnapshot doc :
                                    queryDocumentSnapshots) {
                                Log.d(TAG, "getAllUsers onEvent: queryDocumentSnapshot: " + doc.getId() + " " + doc.getData() + "more info: " + doc);
                                HashMap<String, Object> snap = (HashMap<String, Object>) doc.getData();

                                String uid = doc.getId();
                                String displayName = (String) snap.get("displayName");
                                String imageUrl = (String) snap.get("imageUrl");
                                String className = (String) snap.get("class");

                                all.add(new User(uid, displayName, imageUrl, className));

                                if (uid.startsWith("#")) {
                                    clubs.add(new User(uid, uid, imageUrl, "@ " + collegeShortName));
                                }
                                if (className != null) {
                                    if (className.startsWith("CSE")) {
                                        cse.add(new User(uid, displayName, imageUrl, className));
                                    }
                                    if (className.startsWith("ECE")) {
                                        ece.add(new User(uid, displayName, imageUrl, className));
                                    }
                                    if (className.startsWith("EEE")) {
                                        eee.add(new User(uid, displayName, imageUrl, className));
                                    }
                                    if (className.startsWith("MECH")) {
                                        mech.add(new User(uid, displayName, imageUrl, className));
                                    }
                                    if (className.startsWith("CIVIL")) {
                                        civil.add(new User(uid, displayName, imageUrl, className));
                                    }
                                }
                            }
                            allUserData.put("all", all);
                            allUserData.put("clubs", clubs);
                            allUserData.put("cse", cse);
                            allUserData.put("ece", ece);
                            allUserData.put("eee", eee);
                            allUserData.put("mech", mech);
                            allUserData.put("civil", civil);

                            setChanged();
                            notifyObservers(allUserData);
                        } else {
                            Log.d(TAG, "getUsersNClubs onEvent: queryDocumentSnapshots: null");
                        }
                    }
                });
    }
}
