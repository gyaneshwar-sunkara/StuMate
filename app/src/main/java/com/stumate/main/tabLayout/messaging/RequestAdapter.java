package com.stumate.main.tabLayout.messaging;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stumate.main.R;
import com.stumate.main.utils.dataTypes.Message;
import com.stumate.main.utils.dataTypes.Requests;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    private Context mContext;
    private List<Requests> mRequests;

    FirebaseUser mUser;
    FirebaseFirestore db;

    private static final String TAG = "RequestAdapter";

    public RequestAdapter(Context context, List<Requests> requests) {
        mContext = context;
        mRequests = requests;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_request, parent, false);
        return new RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RequestViewHolder holder, int position) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        final Requests request = mRequests.get(position);
        /*
        Picasso.get()
                .load(uploadDBCurrent.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
         */
        Glide.with(mContext)
                .load(request.getImageUrl())
                .placeholder(R.drawable.loading)
                .into(holder.profilePhoto);
        holder.displayName.setText(request.getDisplayName());
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.accept.setBackgroundColor(mContext.getColor(R.color.green));
                    holder.block.setEnabled(false);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            // TODO Create a private chat bucket for these tow mUser.getUid & request.getUid()
                            HashMap<String, Object> msg = new HashMap<>();
                            msg.put(mUser.getUid(), true);
                            msg.put(request.getUid(), true);

                            db.collection("institutes")
                                    .document(Objects.requireNonNull(mContext.getSharedPreferences("userDetails", Context.MODE_PRIVATE).getString("collegeName", null)))
                                    .collection("messaging")
                                    .add(msg)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "onSuccess: Messaging id obtained");
                                            HashMap<String, Object> update = new HashMap<>();
                                            update.put("pending", FieldValue.arrayRemove(request.getUid()));
                                            update.put("mates", FieldValue.arrayUnion(request.getUid()));
                                            db.collection("users")
                                                    .document(mUser.getUid())
                                                    .update(update);
                                            db.collection("users")
                                                    .document(request.getUid())
                                                    .update("mates", FieldValue.arrayUnion(mUser.getUid()));
                                        }
                                    });
                        }
                    }, 500);
                }
            }
        });

        holder.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.block.setBackgroundColor(mContext.getColor(R.color.red));
                    holder.accept.setEnabled(false);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            HashMap<String, Object> update = new HashMap<>();
                            update.put("pending", FieldValue.arrayRemove(request.getUid()));
                            update.put("blocked", FieldValue.arrayUnion(request.getUid()));
                            db.collection("users")
                                    .document(mUser.getUid())
                                    .update(update);
                        }
                    }, 500);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        private TextView displayName;
        private CircleImageView profilePhoto;
        private Button accept;
        private Button block;

        private RequestViewHolder(View itemView) {
            super(itemView);
            displayName = itemView.findViewById(R.id.displayName);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            accept = itemView.findViewById(R.id.accept);
            block = itemView.findViewById(R.id.block);
        }
    }
}
