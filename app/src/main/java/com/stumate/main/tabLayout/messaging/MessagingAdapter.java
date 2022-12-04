package com.stumate.main.tabLayout.messaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.stumate.main.R;
import com.stumate.main.utils.dataTypes.Message;

import java.util.List;

public class MessagingAdapter extends RecyclerView.Adapter<MessagingAdapter.RequestViewHolder> {
    private Context mContext;
    private List<Message> mMessages;

    public MessagingAdapter(Context context, List<Message> messages) {
        mContext = context;
        mMessages = messages;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.layout_my_message, parent, false);
            return new RequestViewHolder(v);
        } else {
            View v = LayoutInflater.from(mContext).inflate(R.layout.layout_message, parent, false);
            return new RequestViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final MessagingAdapter.RequestViewHolder holder, int position) {
        final Message message = mMessages.get(position);
        holder.message.setText(message.getMessage());
        String timeDate[] = message.getTimestamp().split(" ");
        holder.time.setText(timeDate[3].split(":")[0] + ":" + timeDate[3].split(":")[1]);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        private TextView message;
        private TextView time;

        private RequestViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageText);
            time = itemView.findViewById(R.id.messageTime);
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
