package com.stumate.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class ClubProfileActivity extends AppCompatActivity {
    // variables
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView textViewDisplayName;
    private TextView textViewBio;
    private ProgressBar mProgressBar;
    private CircleImageView profilePicture;
    private ImageView header;
    private Button ink;
    private Button joinMsg;
    private FloatingActionButton fab;

    FirebaseUser mUser;
    FirebaseFirestore db;
    String mUid;
    String name;
    String imageUrl;
    String headerUrl;
    String bio;
    List<String> inks;
    List<String> posts;
    List<String> mates;

    private String tag;

    private static final String TAG = "ClubProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_profile);

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
        profilePicture = (CircleImageView) findViewById(R.id.circleImageView);
        header = findViewById(R.id.imageView);
        textViewDisplayName = (TextView) findViewById(R.id.textView);
        textViewBio = (TextView) findViewById(R.id.textView3);
        mProgressBar = findViewById(R.id.progressBar);
        ink = findViewById(R.id.ink);
        joinMsg = findViewById(R.id.buttonJoinMsg);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        tag = intent.getStringExtra("tag");

        textViewDisplayName.setText(name);

        mProgressBar.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        if (name != null) {
            db.collection("institutes").document(getSharedPreferences("userDetails", MODE_PRIVATE).getString("collegeName", "xxx")).collection("clubs").document(name).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (Objects.requireNonNull(document).exists()) {
                                    imageUrl = (String) document.get("imageUrl");
                                    headerUrl = (String) document.get("headerUrl");
                                    bio = (String) document.get("bio");
                                    mates = (ArrayList<String>) document.get("mates");

                                    Log.d(TAG, "onComplete: inks: --> " + inks + " posts: --> " + posts + " mates: --> " + mates);
                                    // TODO: Also get posts, mates and clubs

                                    mProgressBar.setVisibility(View.GONE);

                                    ink.setEnabled(false);
                                    joinMsg.setText("Join Club");
                                    joinMsg.setEnabled(true);
                                    joinMsg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // TODO: Add uid into clubs and club into user clubs
                                            joinMsg.setEnabled(false);
                                            db.collection("institutes")
                                                    .document(getSharedPreferences("userDetails", MODE_PRIVATE).getString("collegeName", "xxx"))
                                                    .collection("clubs")
                                                    .document(name)
                                                    .update("mates", FieldValue.arrayUnion(mUid));
                                            db.collection("users")
                                                    .document(mUid)
                                                    .update("clubs", FieldValue.arrayUnion(name));
                                            Toast.makeText(ClubProfileActivity.this, "Joined " + name, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    if (mates != null) {
                                        for (String id :
                                                mates) {
                                            if(id.equals(mUid)){
                                                ink.setEnabled(true);
                                                ink.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(ClubProfileActivity.this, CommentsActivity.class);
                                                        intent.putExtra("uid", name.substring(2));
                                                        startActivity(intent);
                                                    }
                                                });
                                                joinMsg.setText("Message");
                                                joinMsg.setEnabled(true);
                                                joinMsg.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        if (tag != null) {
                                                            if (tag.equals("messaging")){
                                                                finish();
                                                            }
                                                        }
                                                        else {
                                                            Intent intent = new Intent(ClubProfileActivity.this, MessagingActivity.class);
                                                            intent.putExtra("uid", name);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    textViewDisplayName.setText(document.getId());
                                    Log.d(TAG, "onComplete: docId: --> " + document.getId() + " name: --> " + name);
                                    textViewBio.setText(bio);
                                    if (imageUrl != null && !imageUrl.equals("") && imageUrl.startsWith("http")) {
                                        Glide.with(ClubProfileActivity.this)
                                                .load(imageUrl)
                                                .into(profilePicture);
                                    }
                                    if (headerUrl != null && !headerUrl.equals("") && headerUrl.startsWith("http")) {
                                        Glide.with(ClubProfileActivity.this)
                                                .load(headerUrl)
                                                .into(header);
                                    }
                                    List<User> mUsers = new ArrayList<>();
                                    for (String mate :
                                            mates) {
                                        User u = TabLayoutActivity.getUser(mate);
                                        if (u != null) {
                                            mUsers.add(u);
                                        }
                                    }
                                    ClubProfileActivityAdapter mTabLayoutAdapter = new ClubProfileActivityAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), name, mUsers); // TODO: getChildFragmentManager not SupportFragmentManager from parent activity
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
                                }
                                else {
                                    mProgressBar.setVisibility(View.GONE);
                                    joinMsg.setText("Error");
                                    Snackbar.make(findViewById(R.id.linearLayout), "Something's wrong try again later...", Snackbar.LENGTH_LONG).show();
                                }
                            }
                            else {
                                mProgressBar.setVisibility(View.GONE);
                                joinMsg.setText("Error");
                                Snackbar.make(findViewById(R.id.linearLayout), "Something's wrong try again later...", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
