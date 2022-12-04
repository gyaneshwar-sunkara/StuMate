package com.stumate.main.tabLayout.posts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stumate.main.R;
import com.stumate.main.tabLayout.TabLayoutActivity;
import com.stumate.main.utils.dataTypes.Post;

import java.util.List;

import io.fabric.sdk.android.services.common.SafeToast;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ImageViewHolder> {


    private Context mContext;
    private List<Post> posts;

    PostsAdapter(Context context, List<Post> uploadDBS) {
        mContext = context;
        posts = uploadDBS;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_post, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
        final Post post = posts.get(position);
        Glide.with(mContext)
                .load(TabLayoutActivity.getUserImageUrl(post.getUid()))
                .placeholder(R.drawable.user)
                .into(holder.profilePicture);
        holder.displayName.setText(TabLayoutActivity.getUserDisplayName(post.getUid()));
        holder.tag.setText(post.getTag());
        holder.description.setText(post.getCaption());
        Glide.with(mContext)
                .load(post.getPostUrl())
                .placeholder(R.drawable.loading)
                .into(holder.image);
        if (post.getInks() == 1) {
            holder.inks.setText(post.getInks() + " Ink");
        }
        else {
            holder.inks.setText(post.getInks() + " Inks");
        }
        holder.ink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("uid", post.getPid());
                intent.putExtra("tag", "post");
                mContext.startActivity(intent);
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
            holder.save.setImageResource(R.drawable.wallet_closed);
        }
        else {
            holder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.save.setOnClickListener(null);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("institutes")
                            .document(mContext.getSharedPreferences("userDetails", Context.MODE_PRIVATE).getString("collegeName", "xxx"))
                            .collection("posts")
                            .document(post.getPid())
                            .update("saved", FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()));

                    SafeToast.makeText(mContext, "Post saved", SafeToast.LENGTH_SHORT).show();
                    holder.save.setImageResource(R.drawable.wallet_closed);
                }
            });
        }
        if (post.getTimestamp().toDate().getDate() == Timestamp.now().toDate().getDate()) {
            holder.time.setText("Today \n "+post.getTimestamp().toDate().getHours()+":"+post.getTimestamp().toDate().getMinutes());
        }
        else {
            holder.time.setText(post.getTimestamp().toDate().toString().split(" ")[1] + " " + post.getTimestamp().toDate().toString().split(" ")[2] + "\n " +post.getTimestamp().toDate().getHours()+":"+post.getTimestamp().toDate().getMinutes());
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePicture;
        private TextView displayName;
        private TextView tag;
        private TextView description;
        private ImageView image;
        private TextView inks;
        private ImageView ink;
        private ImageView save;
        private TextView time;

        private ImageViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.post_view_profile_picture);
            displayName = itemView.findViewById(R.id.post_view_profile_name);
            tag = itemView.findViewById(R.id.post_view_group_tag);
            image = itemView.findViewById(R.id.post_view_image_view);
            description = itemView.findViewById(R.id.post_view_description);
            inks = itemView.findViewById(R.id.inks);
            ink = itemView.findViewById(R.id.ink);
            save = itemView.findViewById(R.id.save);
            time = itemView.findViewById(R.id.timestamp);
        }
    }
}
