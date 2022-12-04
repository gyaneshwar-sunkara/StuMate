package com.stumate.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.stumate.main.tabLayout.TabLayoutActivity;
import com.stumate.main.tabLayout.personal.all.DeletePost;
import com.stumate.main.tabLayout.personal.all.InksAdapter;
import com.stumate.main.utils.dataTypes.Ink;
import com.stumate.main.utils.dataTypes.Post;

import java.io.Serializable;
import java.util.List;

public class PostsGridLayoutAdapter extends RecyclerView.Adapter<PostsGridLayoutAdapter.PostViewHolder> {
    private Context mContext;
    private List<Post> posts;
    private String tag;

    private static final String TAG = "PostsGridLayoutAdapter";

    public PostsGridLayoutAdapter() {
    }

    public PostsGridLayoutAdapter(Context context, List<Post> posts, String tag) {
        this.mContext = context;
        this.posts = posts;
        this.tag = tag;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_my_post, parent, false);
        return new PostsGridLayoutAdapter.PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        final Post post = posts.get(position);
        Log.d(TAG, "onBindViewHolder: hello oyo   "+post.getPostUrl().toString());
        Glide.with(mContext)
                .load(post.getPostUrl())
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String postObject = gson.toJson(post);
                Intent intent = new Intent(mContext, DeletePost.class);
                intent.putExtra("post", postObject);
                intent.putExtra("tag", tag);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
