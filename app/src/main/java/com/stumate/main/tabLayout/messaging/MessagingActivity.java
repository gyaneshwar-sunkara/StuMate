package com.stumate.main.tabLayout.messaging;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stumate.main.ClubProfileActivity;
import com.stumate.main.MateProfileActivity;
import com.stumate.main.R;
import com.stumate.main.tabLayout.TabLayoutActivity;
import com.stumate.main.utils.dataTypes.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingActivity extends AppCompatActivity {

    private static final String TAG = "MessagingActivity";

    private TextView messengerDisplayName;
    private CircleImageView messengerImage;
    private ImageView resources;
    private ImageView profilePage;
    private EditText textMessage;
    private ImageView selectImage;
    private ImageView sendMessage;
    private RecyclerView recyclerView;


    private FirebaseFirestore db;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener valueEventListener;
    private String uid;

    private String collegeName;
    private String messagingId;
    private List<Message> messages;

    private boolean flag = false;

    private MessagingAdapter messagingAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        messengerDisplayName = findViewById(R.id.profile_name);
        messengerImage = findViewById(R.id.profile_picture);
        resources = findViewById(R.id.resources);
        profilePage = findViewById(R.id.profilePage);
        textMessage = findViewById(R.id.textMessage);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        ImageView back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        selectImage = findViewById(R.id.selectImage);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MessagingActivity.this, "Feature coming in next build.", Toast.LENGTH_SHORT).show();
            }
        });
        // TODO: select local image
        sendMessage = findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        // TODO: send message
        collegeName = getSharedPreferences("userDetails", MODE_PRIVATE).getString("collegeName", "ccc");

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        assert uid != null;
        if (uid.startsWith("#")) {
            messengerDisplayName.setText(uid);
            sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textMessage.getText() != null && !textMessage.getText().toString().equals("")) {
                        mDatabaseRef = FirebaseDatabase.getInstance().getReference(collegeName + "/clubs/" + uid.substring(2));
                        mDatabaseRef.push().setValue(new Message(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), textMessage.getText().toString(), Timestamp.now().toDate().toString()));
                        textMessage.getText().clear();
                    }
                }
            });
            findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConnected()) {
                        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                        getClubMessages();
                    } else {
                        Toast.makeText(MessagingActivity.this, "No Network", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                }
            });
            if (TabLayoutActivity.getClubImageUrl(uid) != null) {
                Glide.with(this)
                        .load(TabLayoutActivity.getClubImageUrl(uid))
                        .placeholder(R.drawable.user)
                        .into(messengerImage);
            }
            resources.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MessagingActivity.this, "Feature coming in next build...", Toast.LENGTH_SHORT).show();
                }
            });
            profilePage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MessagingActivity.this, ClubProfileActivity.class);
                    intent.putExtra("name", uid);
                    intent.putExtra("tag", "messaging");
                    startActivity(intent);
                }
            });
            getClubMessages();
        } else {
            messengerDisplayName.setText(TabLayoutActivity.getUserDisplayName(uid));
            sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textMessage.getText() != null && !textMessage.getText().toString().equals("")) {
                        mDatabaseRef = FirebaseDatabase.getInstance().getReference(collegeName + "/private/" + messagingId);
                        mDatabaseRef.push().setValue(new Message(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), textMessage.getText().toString(), Timestamp.now().toDate().toString()));
                        textMessage.getText().clear();
                    }
                }
            });
            findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isConnected()) {
                        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                        getMessages();
                    } else {
                        Toast.makeText(MessagingActivity.this, "No Network", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                }
            });
            if (TabLayoutActivity.getUserImageUrl(uid) != null) {
                Glide.with(this)
                        .load(TabLayoutActivity.getUserImageUrl(uid))
                        .placeholder(R.drawable.user)
                        .into(messengerImage);
            }
            resources.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MessagingActivity.this, "Feature coming in next build...", Toast.LENGTH_SHORT).show();
                }
            });
            profilePage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MessagingActivity.this, MateProfileActivity.class);
                    intent.putExtra("tag", "messaging");
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                }
            });
            getMessages();
        }
    }

    private void getClubMessages() {

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(collegeName + "/clubs/" + uid.substring(2));
        mDatabaseRef.keepSynced(true);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Message message = postSnapshot.getValue(Message.class);
                    messages.add(message);
                }
                flag = true;
                if (recyclerView != null && messages != null && messages.size() > 0) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    findViewById(R.id.error).setVisibility(View.GONE);
                    findViewById(R.id.send_message_container).setVisibility(View.VISIBLE);
                    sendMessage.setVisibility(View.VISIBLE);
                    final MessagingAdapterClubs messagingAdapter = new MessagingAdapterClubs(getApplicationContext(), messages);
                    recyclerView.setAdapter(messagingAdapter);
                    // TODO: if manually changed then remove listener
                    recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldleft, int oldtop, int oldright, int oldBottom) {
                            recyclerView.scrollToPosition(messagingAdapter.getItemCount() - 1);
                        }
                    });
                    recyclerView.scrollToPosition(messagingAdapter.getItemCount() - 1);
                } else {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    findViewById(R.id.error).setVisibility(View.VISIBLE);
                    TextView t = findViewById(R.id.textView);
                    t.setText("No messages yet!");
                    TextView t2 = findViewById(R.id.textView4);
                    ImageView i = findViewById(R.id.imageView2);
                    i.setVisibility(View.GONE);
                    t2.setText("Be the first to message");
                    findViewById(R.id.retry).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: getClubMessages: " + databaseError);
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.error).setVisibility(View.VISIBLE);
                TextView t = findViewById(R.id.textView);
                t.setText("Something's wrong!");
                TextView t2 = findViewById(R.id.textView4);
                t2.setText(databaseError.getMessage());
                ImageView i = findViewById(R.id.imageView2);
                i.setVisibility(View.GONE);
                findViewById(R.id.retry).setVisibility(View.GONE);
            }
        };
        mDatabaseRef.addValueEventListener(valueEventListener);
    }

    private void getMessages() {
        db = FirebaseFirestore.getInstance();
        db.collection("institutes").document(collegeName).collection("messaging")
                .whereEqualTo(FirebaseAuth.getInstance().getCurrentUser().getUid(), true)
                .whereEqualTo(uid, true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                messagingId = document.getId();
                            }

                            mDatabaseRef = FirebaseDatabase.getInstance().getReference(collegeName + "/private/" + messagingId);
                            mDatabaseRef.keepSynced(true);
                            valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    messages = new ArrayList<>();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        Message message = postSnapshot.getValue(Message.class);
                                        messages.add(message);
                                    }
                                    flag = true;
                                    if (recyclerView != null && messages != null && messages.size() > 0) {
                                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                                        findViewById(R.id.error).setVisibility(View.GONE);
                                        findViewById(R.id.send_message_container).setVisibility(View.VISIBLE);
                                        sendMessage.setVisibility(View.VISIBLE);
                                        final MessagingAdapter messagingAdapter = new MessagingAdapter(getApplicationContext(), messages);
                                        recyclerView.setAdapter(messagingAdapter);
                                        // TODO: if manually changed then remove listener
                                        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                            @Override
                                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldleft, int oldtop, int oldright, int oldBottom) {
                                                recyclerView.scrollToPosition(messagingAdapter.getItemCount() - 1);
                                            }
                                        });
                                        recyclerView.scrollToPosition(messagingAdapter.getItemCount() - 1);
                                    } else {
                                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                                        findViewById(R.id.error).setVisibility(View.VISIBLE);
                                        TextView t = findViewById(R.id.textView);
                                        t.setText("No messages yet!");
                                        TextView t2 = findViewById(R.id.textView4);
                                        t2.setText("Be the first to message");
                                        ImageView i = findViewById(R.id.imageView2);
                                        i.setVisibility(View.GONE);
                                        findViewById(R.id.retry).setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                                    findViewById(R.id.error).setVisibility(View.VISIBLE);
                                    TextView t = findViewById(R.id.textView);
                                    t.setText("Something's wrong!");
                                    TextView t2 = findViewById(R.id.textView4);
                                    t2.setText(databaseError.getMessage());
                                    ImageView i = findViewById(R.id.imageView2);
                                    i.setVisibility(View.GONE);
                                    findViewById(R.id.retry).setVisibility(View.GONE);
                                }
                            };
                            mDatabaseRef
                                    .addValueEventListener(valueEventListener);
                        } else {
                            Log.d(TAG, "onComplete: can't read messagesId correspondence");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private boolean isConnected() {
        // check for internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null)
            networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (flag) {
            if (recyclerView != null && messages != null && messages.size() > 0) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.error).setVisibility(View.GONE);
                findViewById(R.id.send_message_container).setVisibility(View.VISIBLE);
                sendMessage.setVisibility(View.VISIBLE);
                final MessagingAdapterClubs messagingAdapter = new MessagingAdapterClubs(getApplicationContext(), messages);
                recyclerView.setAdapter(messagingAdapter);
                // TODO: if manually changed then remove listener
                recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldleft, int oldtop, int oldright, int oldBottom) {
                        recyclerView.scrollToPosition(messagingAdapter.getItemCount() - 1);
                    }
                });
                recyclerView.scrollToPosition(messagingAdapter.getItemCount() - 1);
            } else {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.error).setVisibility(View.VISIBLE);
                TextView t = findViewById(R.id.textView);
                t.setText("No messages yet!");
                TextView t2 = findViewById(R.id.textView4);
                ImageView i = findViewById(R.id.imageView2);
                i.setImageResource(R.drawable.ic_message_black_24dp);
                t2.setText("Be the first to message");
                findViewById(R.id.retry).setVisibility(View.GONE);
            }
        }
    }
}