package com.stumate.main.tabLayout.posts.uploads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stumate.main.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private List<String> tags;
    private ItemListener mListener;

    RecyclerViewAdapter(Context context, List<String> mTags, ItemListener itemListener) {
        mContext = context;
        tags = mTags;
        mListener = itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_other_tags, parent, false);
        return new RecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tag.setText(tags.get(position));
        holder.tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(tags.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Button tag;

        private ViewHolder(View itemView) {
            super(itemView);

            tag = itemView.findViewById(R.id.selectedTag);

        }
    }
    public interface ItemListener {
        void onItemClick(String tag);
    }
}
