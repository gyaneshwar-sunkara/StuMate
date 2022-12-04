package com.stumate.main.tabLayout.messaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.stumate.main.R;
import com.stumate.main.tabLayout.TabLayoutActivity;
import com.stumate.main.utils.dataTypes.Message;

import java.util.List;

public class MessagingAdapterClubs extends RecyclerView.Adapter<MessagingAdapterClubs.RequestViewHolder> {
    private Context mContext;
    private List<Message> mMessages;

    private static final String TAG = "MessagingAdapterClubs";

    public MessagingAdapterClubs(Context context, List<Message> messages) {
        mContext = context;
        mMessages = messages;
    }

    @NonNull
    @Override
    public MessagingAdapterClubs.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.layout_club_my_message, parent, false);
            return new MessagingAdapterClubs.RequestViewHolder(v);
        } else {
            View v = LayoutInflater.from(mContext).inflate(R.layout.layout_club_message, parent, false);
            return new RequestViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final MessagingAdapterClubs.RequestViewHolder holder, int position) {
        final Message message = mMessages.get(position);
        holder.message.setText(message.getMessage());

        String timeDate[] = message.getTimestamp().split(" ");
        holder.time.setText(timeDate[3].split(":")[0] + ":" + timeDate[3].split(":")[1]);


        if (position > 0 && message.getUid().equals(mMessages.get(position - 1).getUid())) {
            holder.displayName.setVisibility(View.GONE);
            holder.mentor.setVisibility(View.GONE);
            holder.profilePhoto.setVisibility(View.INVISIBLE);
        } else {
            if (TabLayoutActivity.getUserDisplayName(message.getUid()) != null) {
                holder.displayName.setText(TabLayoutActivity.getUserDisplayName(message.getUid()));
            }
            if (TabLayoutActivity.getUserImageUrl(message.getUid()) != null) {
                Glide.with(mContext)
                        .load(TabLayoutActivity.getUserImageUrl(message.getUid()))
                        .placeholder(R.drawable.loading)
                        .into(holder.profilePhoto);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        private TextView message;
        private TextView time;
        private ImageView profilePhoto;
        private TextView displayName;
        private TextView mentor;

        private RequestViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageText);
            time = itemView.findViewById(R.id.messageTime);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            displayName = itemView.findViewById(R.id.displayName);
            mentor = itemView.findViewById(R.id.mentor);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessages.get(position).getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return 0;
        } else {
            return 1;
        }
    }
}

