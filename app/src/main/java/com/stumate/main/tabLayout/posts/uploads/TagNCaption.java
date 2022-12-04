package com.stumate.main.tabLayout.posts.uploads;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.stumate.main.R;
import com.stumate.main.tabLayout.TabLayoutActivity;
import com.stumate.main.tabLayout.personal.PersonalFragment;
import com.stumate.main.utils.dataTypes.Post;
import com.stumate.main.utils.dataTypes.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TagNCaption extends AppCompatActivity implements RecyclerViewAdapter.ItemListener{

    private Uri mImageUri;

    private ImageView back;
    private TextView post;
    private TextView tag;
    private EditText caption;

    private RecyclerView otherTagsRecyclerView;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    private String college;
    private String collegeName;
    private String className;
    public List<String> clubs;

    public String postReference;

    private static final String TAG = "TagNCaption";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_n_caption);

        college = Objects.requireNonNull(getSharedPreferences("userDetails", Context.MODE_PRIVATE).getString("collegeShortName", null));
        collegeName = getSharedPreferences("userDetails", Context.MODE_PRIVATE).getString("collegeName", null);
        className = getSharedPreferences("userDetails", Context.MODE_PRIVATE).getString("class", null);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        Intent intent = getIntent();

        String uriString = intent.getStringExtra("uri");
        mImageUri = Uri.parse(uriString);

        caption = findViewById(R.id.captionValue);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_right, R.anim.right_left);
            }
        });

        post = findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageUri != null) {
                    // TODO: Upload post to club specified
                    uploadPost();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        tag = findViewById(R.id.selectedTag);
        tag.setText("# "+ className);

        otherTagsRecyclerView = findViewById(R.id.otherTagsRecyclerView);
        otherTagsRecyclerView.setHasFixedSize(true);
        otherTagsRecyclerView.setItemViewCacheSize(20);
        otherTagsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        updateUI();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadPost() {
        if (mImageUri != null) {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            mStorageRef = FirebaseStorage.getInstance().getReference(collegeName + "/" + tag.getText().toString() + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/posts");
            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            Toast.makeText(this, "Finishing up your post... ", Toast.LENGTH_LONG).show();

            mUploadTask = storageReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                            Post uploadPost = new Post(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getPhotoUrl().toString(), uri.toString(), caption.getText().toString(), 0, tag.getText().toString(), Timestamp.now(), new ArrayList());

                                            db.collection("institutes")
                                                    .document(collegeName)
                                                    .collection("posts")
                                                    .add(uploadPost)
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            postReference = Objects.requireNonNull(task.getResult()).getId();
                                                            db.collection("users").document(firebaseUser.getUid())
                                                                    .update("posts", FieldValue.arrayUnion(postReference));
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(TagNCaption.this, "Upload failed, try again.", Toast.LENGTH_LONG).show();
                                                        }
                                                    });


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: url can't be found... weird");
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TagNCaption.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onItemClick(String mtag) {
        tag.setText(mtag);
        updateUI();
    }

    public void updateUI() {
        clubs =  new ArrayList<>();
        if (PersonalFragment.getClubs() != null) {
            clubs.addAll(PersonalFragment.getClubs());
        }
        clubs.remove(tag.getText().toString());
        if (otherTagsRecyclerView != null) {
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, clubs, this);
            otherTagsRecyclerView.setAdapter(adapter);
        }
    }
}