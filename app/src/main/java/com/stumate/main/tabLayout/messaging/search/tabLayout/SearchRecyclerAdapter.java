package com.stumate.main.tabLayout.messaging.search.tabLayout;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stumate.main.ClubProfileActivity;
import com.stumate.main.MateProfileActivity;
import com.stumate.main.R;
import com.stumate.main.tabLayout.messaging.MessagingActivity;
import com.stumate.main.utils.dataTypes.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> {

    private  Context mContext;
    private List<User> mUser;
    private boolean flag = false;
    private String flag2 = "";

    public SearchRecyclerAdapter(Context mContext, List<User> user) {
        this.mContext = mContext;
        this.mUser = user;
    }
    public SearchRecyclerAdapter(Context mContext, List<User> user, boolean flag) {
        this.mContext = mContext;
        this.mUser = user;
        this.flag = flag;
    }
    public SearchRecyclerAdapter(Context mContext, List<User> user, String flag2) {
        this.mContext = mContext;
        this.mUser = user;
        this.flag2 = flag2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_messengers_search, parent, false);
        return new SearchRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // TODO: Add onClick listener to parent layout
        // TODO: Also has uid to send through listener
        final User user = mUser.get(position);
        holder.textView.setText(user.getDisplayName());
        holder.textView2.setText(user.getClassName());
        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty() && !user.getImageUrl().equals("")){
            Glide.with(mContext)
                    .load(user.getImageUrl())
                    .placeholder(R.drawable.user)
                    .into(holder.circleImageView);
        }
        if (!flag2.equals("no")) {
            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Move to profile activity of specified user;
                    if (!flag) {

                        if (user.getUid().startsWith("#")){
                            Intent intent = new Intent(mContext, ClubProfileActivity.class);
                            intent.putExtra("name", user.getDisplayName());
                            mContext.startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(mContext, MateProfileActivity.class);
                            intent.putExtra("uid", user.getUid());
                            mContext.startActivity(intent);
                        }
                    }
                    else {
                        Intent intent = new Intent(mContext, MessagingActivity.class);
                        intent.putExtra("uid", user.getUid());
                        mContext.startActivity(intent);
                        // TODO: Open messaging activity with corresponding extras...
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout constraintLayout;
        private CircleImageView circleImageView;
        private TextView textView;
        private TextView textView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            circleImageView = itemView.findViewById(R.id.circleImageView);
            constraintLayout = itemView.findViewById(R.id.constrainLayout);

        }
    }
}


