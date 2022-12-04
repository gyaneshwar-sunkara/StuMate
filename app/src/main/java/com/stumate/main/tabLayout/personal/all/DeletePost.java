package com.stumate.main.tabLayout.personal.all;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.stumate.main.R;
import com.stumate.main.tabLayout.TabLayoutActivity;
import com.stumate.main.tabLayout.posts.CommentsActivity;
import com.stumate.main.utils.dataTypes.Post;

import io.fabric.sdk.android.services.common.SafeToast;

public class DeletePost extends AppCompatActivity {
    private ImageView profilePicture;
    private TextView displayName;
    private TextView tag;
    private TextView description;
    private ImageView image;
    private TextView inks;
    private ImageView ink;
    private ImageView save;
    private TextView time;
    private LinearLayout deleteId;
    private TextView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_post);

        profilePicture = findViewById(R.id.post_view_profile_picture);
        displayName = findViewById(R.id.post_view_profile_name);
        tag = findViewById(R.id.post_view_group_tag);
        image = findViewById(R.id.post_view_image_view);
        description = findViewById(R.id.post_view_description);
        inks = findViewById(R.id.inks);
        ink = findViewById(R.id.ink);
        save = findViewById(R.id.save);
        time = findViewById(R.id.timestamp);
        deleteId = findViewById(R.id.deleteId);
        delete = findViewById(R.id.delete);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Gson gson = new Gson();
        final Post post = gson.fromJson(getIntent().getStringExtra("post"), Post.class);

        if (post != null) {
            if (getIntent().getStringExtra("tag").equals("MyPostsFragment")) {
                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(post.getUid())) {
                    deleteId.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.VISIBLE);
                    delete.setText("Delete Post");
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (post.getUid() != null) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(DeletePost.this);
                                builder.setTitle("Delete")
                                        .setMessage("you can not undo this action.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("institutes")
                                                        .document(getSharedPreferences("userDetails", Context.MODE_PRIVATE).getString("collegeName", "xxx"))
                                                        .collection("posts")
                                                        .document(post.getPid())
                                                        .delete();
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        })
                                        .show();
                            }
                        }
                    });
                } else {
                    deleteId.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                }
            } else if (getIntent().getStringExtra("tag").equals("SavedFragment")) {
                delete.setText("Remove Post");
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (post.getUid() != null) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(DeletePost.this);
                            builder.setTitle("Remove")
                                    .setMessage("you can not undo this action.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("institutes")
                                                    .document(getSharedPreferences("userDetails", Context.MODE_PRIVATE).getString("collegeName", "xxx"))
                                                    .collection("posts")
                                                    .document(post.getPid())
                                                    .update("saved", FieldValue.arrayRemove(post.getUid()));
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
            }


            Glide.with(this)
                    .load(TabLayoutActivity.getUserImageUrl(post.getUid()))
                    .placeholder(R.drawable.user)
                    .into(profilePicture);
            displayName.setText(TabLayoutActivity.getUserDisplayName(post.getUid()));
            tag.setText(post.getTag());
            description.setText(post.getCaption());
            Glide.with(this)
                    .load(post.getPostUrl())
                    .placeholder(R.drawable.loading)
                    .into(image);
            if (post.getInks() == 1) {
                inks.setText(post.getInks() + " Ink");
            } else {
                inks.setText(post.getInks() + " Inks");
            }
            ink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DeletePost.this, CommentsActivity.class);
                    intent.putExtra("uid", post.getPid());
                    intent.putExtra("tag", "post");
                    startActivity(intent);
                }
            });

            boolean flag = false;
            if (post.getSaved() != null) {
                for (String s :
                        post.getSaved()) {
                    if (s.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        flag = true;
                    }
                }
            }
            if (flag) {
                save.setImageResource(R.drawable.wallet_closed);
            } else {
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        save.setOnClickListener(null);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("institutes")
                                .document(getSharedPreferences("userDetails", Context.MODE_PRIVATE).getString("collegeName", "xxx"))
                                .collection("posts")
                                .document(post.getPid())
                                .update("saved", FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()));

                        SafeToast.makeText(DeletePost.this, "Post saved", SafeToast.LENGTH_SHORT).show();
                        save.setImageResource(R.drawable.wallet_closed);
                    }
                });
            }
            if (post.getTimestamp().toDate().getDate() == Timestamp.now().toDate().getDate()) {
                time.setText("Today \n" + post.getTimestamp().toDate().getHours() + ":" + post.getTimestamp().toDate().getMinutes());
            } else {
                time.setText(post.getTimestamp().toDate().toString().split(" ")[1] + " " + post.getTimestamp().toDate().toString().split(" ")[2] + "\n" + post.getTimestamp().toDate().getHours() + ":" + post.getTimestamp().toDate().getMinutes());
            }

        }
    }
}
