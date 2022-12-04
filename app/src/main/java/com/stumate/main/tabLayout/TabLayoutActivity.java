package com.stumate.main.tabLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stumate.main.MainActivity;
import com.stumate.main.R;
import com.stumate.main.SettingsActivity;
import com.stumate.main.eventListeners.UserDataListener;
import com.stumate.main.tabLayout.messaging.MessagingFragment;
import com.stumate.main.tabLayout.messaging.search.SearchActivity;
import com.stumate.main.tabLayout.personal.PersonalFragment;
import com.stumate.main.tabLayout.posts.PostsFragment;
import com.stumate.main.tabLayout.posts.uploads.FileUpload;
import com.stumate.main.utils.dataTypes.Message;
import com.stumate.main.utils.dataTypes.Messenger;
import com.stumate.main.utils.dataTypes.Post;
import com.stumate.main.utils.dataTypes.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;


public class TabLayoutActivity extends AppCompatActivity implements Observer {
    // variables
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FloatingActionButton mFloatingActionButton;

    private String collegeName;
    private String collegeShortName;
    private String className;

    boolean flag1 = false;
    boolean flag2 = false;

    private String mUid;

    private boolean flag = false;

    private FirebaseFirestore db;
    private DatabaseReference databaseReference;
    private List<String> mMates;
    private List<String> mClubs;

    ValueEventListener valueEventListener;

    private DatabaseReference mDatabaseRef;

    private static List<User> allMates = new ArrayList<>();
    private static List<User> clubs;
    private static List<User> cse = new ArrayList<>();
    private static List<User> ece = new ArrayList<>();
    private static List<User> eee = new ArrayList<>();
    private static List<User> mech = new ArrayList<>();
    private static List<User> civil = new ArrayList<>();
    private static HashMap<String, String> userDisplayName = new HashMap<>();
    private static HashMap<String, String> userImageUrl = new HashMap<>();
    private static HashMap<String, String> clubImageUrl = new HashMap<>();
    private static List<Messenger> messengers;

    private static final String TAG = "TabLayoutActivity";

    private MessagingFragment messagingFragment;
    private PostsFragment postsFragment;
    PersonalFragment personalFragment;
    private UserDataListener userDataListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            mUid = mUser.getUid();
        }
        SharedPreferences prefs = getSharedPreferences("userDetails", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        collegeName = prefs.getString("collegeName", null);
        collegeShortName = prefs.getString("collegeShortName", "NRCM");
        className = prefs.getString("class", null);

        // Initialize variables
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mFloatingActionButton.setImageResource(R.drawable.ic_add_black_24dp);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TabLayoutActivity.this, FileUpload.class);
                startActivity(intent);
            }
        });

        // Add Icons to tabs
        Objects.requireNonNull(mTabLayout.getTabAt(0)).setIcon(R.drawable.ic_chat_bubble_outline_black_24dp);
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setCustomView(R.layout.layout_wordmark);  // Stumate symbol
        Objects.requireNonNull(mTabLayout.getTabAt(2)).setIcon(R.drawable.ic_person_outline_black_24dp);

        // Add page change listener
        // TODO: Changes made....
        messagingFragment = new MessagingFragment();
        postsFragment = new PostsFragment();
        personalFragment = new PersonalFragment();
        userDataListener = new UserDataListener();
        userDataListener.addObserver(this);
        final TabLayoutAdapter mTabLayoutAdapter = new TabLayoutAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), userDataListener, messagingFragment, postsFragment, personalFragment);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setAdapter(mTabLayoutAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mFloatingActionButton.setImageResource(R.drawable.ic_search_24dp);
                        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TabLayoutActivity.this, SearchActivity.class);
                                startActivity(intent);
                            }
                        });
                        break;
                    case 1:
                        mFloatingActionButton.setImageResource(R.drawable.ic_add_black_24dp);
                        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TabLayoutActivity.this, FileUpload.class);
                                startActivity(intent);
                            }
                        });
                        break;
                    case 2:
                        mFloatingActionButton.setImageResource(R.drawable.ic_settings_24dp);
                        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TabLayoutActivity.this, SettingsActivity.class);
                                startActivity(intent);
                            }
                        });
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // Add tab select listener
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    // TODO: This executes after user details are obtained resulting in lag for requests;
    private void getAllUsers() {
//        flag = true;
        db.collection("institutes").document(collegeName).collection("public")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(TabLayoutActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "getAllUsers onEvent: Listen Failed", e);
                            return;
                        }
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                            allMates.clear();
                            clubs = new ArrayList<>();
                            cse.clear();
                            ece.clear();
                            eee.clear();
                            mech.clear();
                            civil.clear();

                            for (QueryDocumentSnapshot doc :
                                    queryDocumentSnapshots) {
                                Log.d(TAG, "getAllUsers onEvent: queryDocumentSnapshot: " + doc.getId() + " " + doc.getData());
                                HashMap<String, Object> snap = (HashMap<String, Object>) doc.getData();

                                String uid = doc.getId();
                                String displayName = (String) snap.get("displayName");
                                String imageUrl = (String) snap.get("imageUrl");
                                String className = (String) snap.get("class");

                                // TODO:: Watch this when sending data into another activity with user data

                                if (uid.startsWith("#")) {
                                    clubs.add(new User(uid, uid, imageUrl, "@ " + collegeShortName));
                                    clubImageUrl.put(uid, imageUrl);
                                }
                                if (className != null) {
                                    if (className.startsWith("CSE")) {
                                        cse.add(new User(uid, displayName, imageUrl, className));
                                        allMates.add(new User(uid, displayName, imageUrl, className));
                                        userDisplayName.put(uid, displayName);
                                        userImageUrl.put(uid, imageUrl);
                                    }
                                    if (className.startsWith("ECE")) {
                                        ece.add(new User(uid, displayName, imageUrl, className));
                                        allMates.add(new User(uid, displayName, imageUrl, className));
                                        userDisplayName.put(uid, displayName);
                                        userImageUrl.put(uid, imageUrl);
                                    }
                                    if (className.startsWith("EEE")) {
                                        eee.add(new User(uid, displayName, imageUrl, className));
                                        allMates.add(new User(uid, displayName, imageUrl, className));
                                        userDisplayName.put(uid, displayName);
                                        userImageUrl.put(uid, imageUrl);
                                    }
                                    if (className.startsWith("MECH")) {
                                        mech.add(new User(uid, displayName, imageUrl, className));
                                        allMates.add(new User(uid, displayName, imageUrl, className));
                                        userDisplayName.put(uid, displayName);
                                        userImageUrl.put(uid, imageUrl);
                                    }
                                    if (className.startsWith("CIVIL")) {
                                        civil.add(new User(uid, displayName, imageUrl, className));
                                        allMates.add(new User(uid, displayName, imageUrl, className));
                                        userDisplayName.put(uid, displayName);
                                        userImageUrl.put(uid, imageUrl);
                                    }
                                }
                            }

                            if (messagingFragment != null) {
                                messagingFragment.updateRequests();

                                messengers = new ArrayList<>();

                                if (mMates != null && mClubs != null) {
                                    if (mClubs.size() < 1) {
                                        flag1 = true;
                                    }
                                    if (mMates.size() < 1) {
                                        flag2 = true;
                                    }
                                    if (flag1 && flag2) {
                                        sortMessengers(messengers);
                                        messagingFragment.updateMessengers(messengers);
                                    }

                                    for (final String s :
                                            mClubs) {

                                        mDatabaseRef = FirebaseDatabase.getInstance().getReference(getSharedPreferences("userDetails", Context.MODE_PRIVATE).getString("collegeName", null) + "/clubs/" + s.substring(2));
                                        mDatabaseRef
                                                .limitToLast(1)
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String lastMessage;
                                                        String lastMessageTime;
                                                        boolean flag = false;

                                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                            Message message = postSnapshot.getValue(Message.class);
                                                            if (message.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                                lastMessage = "You: " + message.getMessage();
                                                            } else {
                                                                lastMessage = TabLayoutActivity.getUserDisplayName(message.getUid()).split(" ")[0] + ": " + message.getMessage();
                                                            }


                                                            lastMessageTime = message.getTimestamp();
                                                            for (Messenger m :
                                                                    messengers) {
                                                                if (m.getUid().equals(s)) {
                                                                    flag = true;
                                                                    m.setLastMessage(lastMessage);
                                                                    m.setTimestamp(lastMessageTime);
                                                                }
                                                            }
                                                            if (!flag) {
                                                                Messenger messenger = new Messenger(s, s, TabLayoutActivity.getClubImageUrl(s), "0", lastMessage, lastMessageTime);
                                                                messengers.add(messenger);
                                                            }
                                                        }
                                                        flag1 = true;
                                                        sortMessengers(messengers);
                                                        if (flag1 && flag2) {
                                                            messagingFragment.updateMessengers(messengers);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        Log.d(TAG, "onCancelled: getClassMessages: " + databaseError);
                                                    }
                                                });

                                    }
                                    for (final String s :
                                            mMates) {
                                        db = FirebaseFirestore.getInstance();
                                        db.collection("institutes").document(getSharedPreferences("userDetails", Context.MODE_PRIVATE).getString("collegeName", null)).collection("messaging")
                                                .whereEqualTo(FirebaseAuth.getInstance().getCurrentUser().getUid(), true)
                                                .whereEqualTo(s, true)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                        if (task.isSuccessful()) {
                                                            String messagingId = "";
                                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                                                messagingId = document.getId();
                                                            }

                                                            mDatabaseRef = FirebaseDatabase.getInstance().getReference(getSharedPreferences("userDetails", Context.MODE_PRIVATE).getString("collegeName", null) + "/private/" + messagingId);
                                                            mDatabaseRef
                                                                    .limitToLast(1)
                                                                    .addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            String lastMessage;
                                                                            String lastMessageTime;
                                                                            boolean flag = false;

                                                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                                                Message message = postSnapshot.getValue(Message.class);


                                                                                if (message.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                                                    lastMessage = "You: " + message.getMessage();
                                                                                } else {
                                                                                    lastMessage = TabLayoutActivity.getUserDisplayName(message.getUid()).split(" ")[0] + ": " + message.getMessage();
                                                                                }

                                                                                lastMessageTime = message.getTimestamp();

                                                                                for (Messenger m :
                                                                                        messengers) {
                                                                                    if (m.getUid().equals(s)) {
                                                                                        flag = true;
                                                                                        m.setLastMessage(lastMessage);
                                                                                        m.setTimestamp(lastMessageTime);
                                                                                    }
                                                                                }
                                                                                if (!flag) {
                                                                                    Messenger messenger = new Messenger(s, TabLayoutActivity.getUserDisplayName(s), TabLayoutActivity.getUserImageUrl(s), "0", lastMessage, lastMessageTime);
                                                                                    messengers.add(messenger);
                                                                                }
                                                                            }
                                                                            flag2 = true;
                                                                            sortMessengers(messengers);
                                                                            if (flag1 && flag2) {
                                                                                messagingFragment.updateMessengers(messengers);
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {
                                                                        }
                                                                    });
                                                        } else {
                                                            Log.d(TAG, "onComplete: can't read messagesId correspondence");
                                                        }
                                                    }
                                                });


                                    }
                                }
                            }

                            Log.d(TAG, "onEvent: obtained values are: Clubs: " + clubs.size() + "\nALL Users: " + allMates.size() + "\nCSE: " + cse.size());
                        } else {
                            Log.d(TAG, "getAllUsers onEvent: queryDocumentSnapshots: null");
                        }
                    }
                });
    }

    private void sortMessengers(List<Messenger> messengers) {
        Collections.sort(messengers, new Comparator<Messenger>() {
            @Override
            public int compare(Messenger messenger, Messenger t1) {
                return messenger.getTimestamp().compareTo(t1.getTimestamp());
            }
        });
        Collections.reverse(messengers);
    }

    public static User getUser(String uid) {
        Log.d(TAG, "getUser: " + allMates);
        for (User u :
                allMates) {
            if (u.getUid().equals(uid))
                return u;
        }
        return null;
    }

    public static String getUserDisplayName(String uid) {
        return userDisplayName.get(uid);
    }

    public static String getUserImageUrl(String uid) {
        return userImageUrl.get(uid);
    }

    public static String getClubImageUrl(String uid) {
        return clubImageUrl.get(uid);
    }

    public static User getClub(String name) {
        Log.d(TAG, "getClub: " + clubs);
        for (User c :
                clubs) {
            if (c.getUid().equals(name))
                return c;
        }
        return null;
    }

    public static List<User> getClubs() {
        return clubs;
    }

    public static List<User> getCse() {
        return cse;
    }

    public static List<Messenger> getMessengers() {
        return messengers;
    }

    public static List<User> getEce() {
        return ece;
    }

    public static List<User> getEee() {
        return eee;
    }

    public static List<User> getMech() {
        return mech;
    }

    public static List<User> getCivil() {
        return civil;
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Exit", true);
            startActivity(intent);
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void update(Observable observable, Object o) {
        HashMap<String, Object> documentSnapshot = (HashMap<String, Object>) o;
        if (documentSnapshot != null) {
            Log.d(TAG, "getUserDetails: onEvent: documentSnapshot: " + documentSnapshot);
            mMates = (List<String>) documentSnapshot.get("mates");
            mClubs = (List<String>) documentSnapshot.get("clubs");
            collegeName = (String) documentSnapshot.get("collegeName");
            collegeShortName = (String) documentSnapshot.get("collegeShortName");
            className = (String) documentSnapshot.get("class");

            // TODO: Database Calls and listeners...
            if (!flag) {
                getAllUsers();
            }
        }
    }
}