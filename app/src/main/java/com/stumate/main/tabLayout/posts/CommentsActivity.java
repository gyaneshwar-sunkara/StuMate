package com.stumate.main.tabLayout.posts;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stumate.main.R;
import com.stumate.main.tabLayout.personal.all.InksAdapter;
import com.stumate.main.utils.dataTypes.Ink;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity {

    private ImageView imageView;
    private String collegeName;
    private RecyclerView mRecyclerView;
    private EditText textMessage;
    private TextView note;
    private String inkDoc;
    private List<Ink> inks = new ArrayList<>();
    private boolean flag = false;

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        note = findViewById(R.id.note);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        textMessage = findViewById(R.id.textMessage);
        findViewById(R.id.sendMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textMessage.getText() != null && !textMessage.getText().toString().equals("")) {
                    textMessage.getText().clear();
                }
            }
        });

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        collegeName = getSharedPreferences("userDetails", MODE_PRIVATE).getString("collegeName", "ccc");
        inkDoc = getIntent().getStringExtra("uid");
        final String tag = getIntent().getStringExtra("tag");

        if (inkDoc != null && collegeName != null) {
            getInks();
            findViewById(R.id.sendMessage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textMessage.getText() != null && !textMessage.getText().toString().equals("")) {

                        mDatabaseRef = FirebaseDatabase.getInstance().getReference(collegeName + "/inks/" + inkDoc);
                        mDatabaseRef.push().setValue(new Ink(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), textMessage.getText().toString(), Timestamp.now().toDate().toString()))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (tag !=null && tag.equals("post")) {
                                            db.collection("institutes")
                                                    .document(collegeName)
                                                    .collection("posts")
                                                    .document(inkDoc)
                                                    .update("inks", FieldValue.increment(1));
                                        }
                                    }
                                });

                        textMessage.getText().clear();
                    }
                }
            });
        }
    }

    private void getInks() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(collegeName + "/inks/" + inkDoc);
        mDatabaseRef.keepSynced(true);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inks.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Ink ink = postSnapshot.getValue(Ink.class);
                    inks.add(ink);
                }
                flag = true;

                if (inks.size() > 0) {
                    note.setVisibility(View.GONE);
                    final InksAdapter inksAdapter = new InksAdapter(getApplicationContext(), inks);
                    mRecyclerView.setAdapter(inksAdapter);
                    mRecyclerView.scrollToPosition(inksAdapter.getItemCount() - 1);
                    mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldleft, int oldtop, int oldright, int oldBottom) {
                            mRecyclerView.scrollToPosition(inksAdapter.getItemCount() - 1);
                        }
                    });
                } else {
                    note.setText("No Inks!");
                    note.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseRef.addValueEventListener(valueEventListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (flag) {
            if (inks.size() > 0) {
                mRecyclerView.setAdapter(new InksAdapter(getApplicationContext(), inks));
            } else {
                note.setText("No Inks!");
                note.setVisibility(View.VISIBLE);
            }
        }
        else {
            note.setText("Loading...");
            note.setVisibility(View.VISIBLE);
        }
    }
}
