package com.stumate.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stumate.main.tabLayout.TabLayoutActivity;
import com.stumate.main.tabLayout.messaging.MessagingActivity;
import com.stumate.main.tabLayout.personal.all.InksFragment;
import com.stumate.main.tabLayout.posts.CommentsActivity;
import com.stumate.main.utils.dataTypes.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.services.common.SafeToast;

public class MateProfileActivity extends AppCompatActivity {
    // variables
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView textViewDisplayName;
    private TextView textViewCollegeName;
    private TextView textViewBio;
    private CircleImageView profilePicture;
    private ImageView header;
    private Button ink;
    private Button msg;
    private FloatingActionButton fab;


    private ProgressBar mProgressBar;
    private static final String TAG = "MateProfileActivity";

    FirebaseUser mUser;
    FirebaseFirestore db;
    String mUid;
    String uid;
    String displayName;
    String imageUrl;
    String headerUrl;
    String bio;
    String collegeShortName;
    String className;
    List<String> inks;
    List<String> posts;
    List<String> mates;
    List<String> pending;
    List<String> blocked;
    List<String> clubs;

    // TODO: Buggy extremely buggy...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mate_profile);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUid = Objects.requireNonNull(mUser).getUid();
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTabLayout = (TabLayout) findViewById(R.id.fragment_personal_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.fragment_personal_view_pager);
        header = findViewById(R.id.imageView);
        profilePicture = (CircleImageView) findViewById(R.id.circleImageView);
        textViewDisplayName = (TextView) findViewById(R.id.textView);
        textViewCollegeName = (TextView) findViewById(R.id.textView2);
        textViewBio = (TextView) findViewById(R.id.textView3);
        mProgressBar = findViewById(R.id.progressBar);
        ink = findViewById(R.id.ink);
        msg = findViewById(R.id.buttonMsg);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        mProgressBar.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, "onComplete: document: " + document);
                            if (document.exists()) {
                                displayName = (String) document.get("displayName");
                                imageUrl = (String) document.get("imageUrl");
                                headerUrl = (String) document.get("headerUrl");
                                bio = (String) document.get("bio");
                                collegeShortName = (String) document.get("collegeShortName");
                                className = (String) document.get("class");
                                inks = (ArrayList<String>) document.get("inks");
                                posts = (ArrayList<String>) document.get("posts");
                                mates = (ArrayList<String>) document.get("mates");
                                pending = (ArrayList<String>) document.get("pending");
                                blocked = (ArrayList<String>) document.get("blocked");
                                clubs = (ArrayList<String>) document.get("clubs");

                                // TODO: Also get posts, mates and clubs
                                if(uid.equals(mUid)) {
                                    ink.setEnabled(false);
                                    ink.setOnClickListener(null);
                                    msg.setEnabled(false);
                                    msg.setText("Message");
                                    msg.setOnClickListener(null);
                                    Snackbar.make(findViewById(R.id.linearLayout), "This is how others see your profile", Snackbar.LENGTH_LONG).show();
                                }
                                else {
                                    ink.setEnabled(false);
                                    ink.setOnClickListener(null);
                                    msg.setText("Send request");
                                    msg.setEnabled(true);

                                    msg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            msg.setEnabled(false);
                                            db.collection("users")
                                                    .document(uid)
                                                    .update("pending", FieldValue.arrayUnion(mUid));
                                            //Toast.makeText(MateProfileActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                                            SafeToast.makeText(MateProfileActivity.this, "Request sent", SafeToast.LENGTH_SHORT).show();
                                            msg.setText("Request sent");
                                        }
                                    });

                                    //TODO: add pending users list for better user Use.
                                    //TODO: get blocked users list and check if mUid is in it or not...

                                    if (pending != null) {
                                        for (String id :
                                                pending) {
                                            if(id.equals(mUid)){
                                                ink.setEnabled(false);
                                                //TODO: goto ink activity
                                                msg.setText("Request sent");
                                                msg.setEnabled(false);
                                                msg.setOnClickListener(null);
                                                //TODO: goto messaging activity
                                            }
                                        }
                                    }
                                    if (blocked != null) {
                                        for (String id :
                                                blocked) {
                                            if(id.equals(mUid)){
                                                ink.setEnabled(false);
                                                msg.setText("Request sent");
                                                msg.setEnabled(false);
                                                msg.setOnClickListener(null);
                                            }
                                        }
                                    }
                                    if (mates != null) {
                                        for (String id :
                                                mates) {
                                            if(id.equals(mUid)){
                                                ink.setEnabled(true);
                                                ink.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
                                                        intent.putExtra("uid", uid);
                                                        startActivity(intent);
                                                    }
                                                });
                                                msg.setText("Message");
                                                msg.setEnabled(true);
                                                msg.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        //TODO: goto messaging activity
                                                        String tag = getIntent().getStringExtra("tag");
                                                        if (tag != null) {
                                                            if (tag.equals("messaging")){
                                                                finish();
                                                            }
                                                        }
                                                        else {
                                                            Intent intent = new Intent(MateProfileActivity.this, MessagingActivity.class);
                                                            intent.putExtra("uid", uid);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }

                                }
                                mProgressBar.setVisibility(View.GONE);
                                textViewDisplayName.setText(displayName);
                                textViewCollegeName.setText("@ " + collegeShortName + " " + className);
                                textViewBio.setText(bio);
                                if (imageUrl != null) {
                                    Glide.with(MateProfileActivity.this)
                                            .load(imageUrl)
                                            .placeholder(R.drawable.user)
                                            .into(profilePicture);
                                }
                                if (headerUrl != null && !headerUrl.equals("")) {
                                    Glide.with(MateProfileActivity.this)
                                            .load(headerUrl)
                                            .into(header);
                                }
                                List<User> mClubs = new ArrayList<>();
                                List<User> mUsers = new ArrayList<>();

                                if (mates != null) {
                                    for (String mate :
                                            mates) {
                                        User u = TabLayoutActivity.getUser(mate);
                                        if (u != null) {
                                            mUsers.add(u);
                                        }
                                    }
                                }

                                if (clubs != null) {
                                    for (String club :
                                            clubs) {
                                        User c = TabLayoutActivity.getClub(club);
                                        if (c != null) {
                                            mClubs.add(c);
                                        }
                                    }
                                }

                                MateProfileActivityAdapter mTabLayoutAdapter = new MateProfileActivityAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), uid, mUsers, mClubs); // TODO: getChildFragmentManager not SupportFragmentManager from parent activity
                                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
                                mViewPager.setAdapter(mTabLayoutAdapter);

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
                            } else {
                                mProgressBar.setVisibility(View.GONE);
                                msg.setText("Error");
                                Snackbar.make(findViewById(R.id.linearLayout), "Something's wrong try again later...", Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            msg.setText("Error");
                            Snackbar.make(findViewById(R.id.linearLayout), "Something's wrong try again later...", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
