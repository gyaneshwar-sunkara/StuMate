package com.stumate.main.tabLayout.messaging;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stumate.main.R;
import com.stumate.main.utils.dataTypes.Messenger;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessengerAdapter extends RecyclerView.Adapter<MessengerAdapter.RequestViewHolder> {
    private Context mContext;
    private List<Messenger> mMessengers;
    private String messagingId;
    FirebaseUser mUser;
    private FirebaseFirestore db;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener valueEventListener;

    private static final String TAG = "MessengerAdapter";

    public MessengerAdapter(Context context, List<Messenger> messengers) {
        mContext = context;
        mMessengers = messengers;
    }

    @NonNull
    @Override
    public MessengerAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_messenger, parent, false);
        return new MessengerAdapter.RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessengerAdapter.RequestViewHolder holder, int position) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        final Messenger messenger = mMessengers.get(position);

        Glide.with(mContext)
                .load(messenger.getImageUrl())
                .placeholder(R.drawable.user)
                .into(holder.profilePhoto);

        holder.displayName.setText(messenger.getDisplayName());

        holder.messengerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessagingActivity.class);
                intent.putExtra("uid", messenger.getUid());
                mContext.startActivity(intent);
            }
        });
        holder.lastMessage.setText(messenger.getLastMessage());

        String lastMessageTime;
        String timeDate[] = messenger.getTimestamp().split(" ");
        lastMessageTime = timeDate[3].split(":")[0] + ":" + timeDate[3].split(":")[1];
        holder.lastMessageTIme.setText(lastMessageTime);

    }

    @Override
    public int getItemCount() {
        return mMessengers.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        private TextView displayName;
        private CircleImageView profilePhoto;
        private TextView lastMessage;
        private TextView lastMessageTIme;
        private ConstraintLayout messengerContainer;

        private RequestViewHolder(View itemView) {
            super(itemView);
            displayName = itemView.findViewById(R.id.textView);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            messengerContainer = itemView.findViewById(R.id.messengerContainer);
            lastMessage = itemView.findViewById(R.id.textView3);
            lastMessageTIme = itemView.findViewById(R.id.textView2);
        }
    }
}
