package com.stumate.main.tabLayout.personal.all;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stumate.main.R;
import com.stumate.main.tabLayout.TabLayoutActivity;
import com.stumate.main.utils.dataTypes.Ink;

import java.util.List;

public class InksAdapter extends RecyclerView.Adapter<InksAdapter.InkViewHolder> {
    private Context mContext;
    private List<Ink> mInks;

    private static final String TAG = "InksAdapter";
    public InksAdapter() {
    }

    public InksAdapter(Context context, List<Ink> inks) {
        this.mContext = context;
        this.mInks = inks;
    }

    @NonNull
    @Override
    public InkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.layout_ink_left, parent, false);
            return new InkViewHolder(v);
        } else {
            View v = LayoutInflater.from(mContext).inflate(R.layout.layout_ink_right, parent, false);
            return new InkViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull InkViewHolder holder, int position) {
        final Ink ink = mInks.get(position);
        holder.message.setText(ink.getMessage());
        String t = ink.getTimestamp().split(" ")[1] + " " + ink.getTimestamp().split(" ")[2];
        holder.time.setText(t);
        if (position > 0 && ink.getUid().equals(mInks.get(position - 1).getUid())) {
            holder.displayName.setVisibility(View.GONE);
            holder.profilePhoto.setVisibility(View.INVISIBLE);
        } else {
            if (TabLayoutActivity.getUserDisplayName(ink.getUid()) != null) {
                holder.displayName.setText(TabLayoutActivity.getUserDisplayName(ink.getUid()));
            }
            if (TabLayoutActivity.getUserImageUrl(ink.getUid()) != null) {
                Glide.with(mContext)
                        .load(TabLayoutActivity.getUserImageUrl(ink.getUid()))
                        .placeholder(R.drawable.loading)
                        .into(holder.profilePhoto);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mInks.size();
    }

    class InkViewHolder extends RecyclerView.ViewHolder {
        private TextView message;
        private TextView time;
        private ImageView profilePhoto;
        private TextView displayName;

        private InkViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageText);
            time = itemView.findViewById(R.id.messageTime);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            displayName = itemView.findViewById(R.id.displayName);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mInks.size() > 1) {
            if (position+1 < mInks.size() && mInks.get(position).getUid().equals(mInks.get(position + 1).getUid())) {
                return 0;
            }
            else {
                    if (mInks.get(position).getUid().equals(mInks.get(position - 1).getUid())) {
                        return 0;
                    }
                else {
                    return 1;
                }
            }
        }
        return 0;
    }
}
