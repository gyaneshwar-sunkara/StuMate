package com.stumate.main.tabLayout.posts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stumate.main.R;
import com.stumate.main.tabLayout.TabLayoutActivity;
import com.stumate.main.utils.dataTypes.Post;

import java.util.List;

import io.fabric.sdk.android.services.common.SafeToast;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // A menu item view type.
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    // The banner ad view type.
    private static final int BANNER_AD_VIEW_TYPE = 1;

    // An Activity's Context.
    private final Context context;

    // The list of banner ads and menu items.
    private final List<Object> recyclerViewItems;


    public RecyclerViewAdapter(Context context, List<Object> recyclerViewItems) {
        this.context = context;
        this.recyclerViewItems = recyclerViewItems;
    }

    /**
     * The {@link MenuItemViewHolder} class.
     * Provides a reference to each view in the menu item view.
     */
    public class MenuItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePicture;
        private TextView displayName;
        private TextView tag;
        private TextView description;
        private ImageView image;
        private TextView inks;
        private ImageView ink;
        private ImageView save;
        private TextView time;

        MenuItemViewHolder(View view) {
            super(view);
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

    /**
     * The {@link AdViewHolder} class.
     */
    public class AdViewHolder extends RecyclerView.ViewHolder {

        AdViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        return recyclerViewItems.size();
    }

    /**
     * Determines the view type for the given position.
     */
    @Override
    public int getItemViewType(int position) {
        return (position % PostsFragment.ITEMS_PER_AD == 0) ? BANNER_AD_VIEW_TYPE
                : MENU_ITEM_VIEW_TYPE;
    }

    /**
     * Creates a new view for a menu item view or a banner ad view
     * based on the viewType. This method is invoked by the layout manager.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.layout_post, viewGroup, false);
                return new MenuItemViewHolder(menuItemLayoutView);
            case BANNER_AD_VIEW_TYPE:
                // fall through
            default:
                View bannerLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.layout_ad,
                        viewGroup, false);
                return new AdViewHolder(bannerLayoutView);
        }
    }

    /**
     * Replaces the content in the views that make up the menu item view and the
     * banner ad view. This method is invoked by the layout manager.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:
                final MenuItemViewHolder holder = (MenuItemViewHolder) h;
                final Post post = (Post) recyclerViewItems.get(position);

                Glide.with(context)
                        .load(TabLayoutActivity.getUserImageUrl(post.getUid()))
                        .placeholder(R.drawable.user)
                        .into(holder.profilePicture);
                holder.displayName.setText(TabLayoutActivity.getUserDisplayName(post.getUid()));
                holder.tag.setText(post.getTag());
                holder.description.setText(post.getCaption());
                Glide.with(context)
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
                        Intent intent = new Intent(context, CommentsActivity.class);
                        intent.putExtra("uid", post.getPid());
                        intent.putExtra("tag", "post");
                        context.startActivity(intent);
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
                                    .document(context.getSharedPreferences("userDetails", Context.MODE_PRIVATE).getString("collegeName", "xxx"))
                                    .collection("posts")
                                    .document(post.getPid())
                                    .update("saved", FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()));

                            SafeToast.makeText(context, "Post saved", SafeToast.LENGTH_SHORT).show();
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
                break;
            case BANNER_AD_VIEW_TYPE:
                // fall through
            default:
                AdViewHolder bannerHolder = (AdViewHolder) h;
                if (position != 0) {
                    AdView adView = (AdView) recyclerViewItems.get(position);
                    ViewGroup adCardView = (ViewGroup) bannerHolder.itemView;
                    // The AdViewHolder recycled by the RecyclerView may be a different
                    // instance than the one used previously for this position. Clear the
                    // AdViewHolder of any subviews in case it has a different
                    // AdView associated with it, and make sure the AdView for this position doesn't
                    // already have a parent of a different recycled AdViewHolder.
                    if (adCardView.getChildCount() > 0) {
                        adCardView.removeAllViews();
                    }
                    if (adView.getParent() != null) {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }

                    // Add the banner ad to the ad view.
                    adCardView.addView(adView);
                }
        }
    }
}