package com.stumate.main.tabLayout.posts;


import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stumate.main.exceptions.NetworkException;
import com.stumate.main.R;
import com.stumate.main.tabLayout.posts.uploads.FileUpload;
import com.stumate.main.utils.dataTypes.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.view.View.INVISIBLE;

public class PostsFragment extends Fragment implements Observer {

    // Variables
    SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout error;
    private FrameLayout frameLayout;
    private RecyclerView mRecyclerView;
    private PostsAdapter mAdapter;
    private ProgressBar mProgressBar;

    private FloatingActionButton mFloatingActionButton;

    private List<Post> posts = new ArrayList<>();
    private String collegeName;
    private String className;
    private String collegeShortName;
    private String dept;
    private String year;

    private List<Object> recyclerViewItems = new ArrayList<>();

    private List<String> clubs = new ArrayList<>();

    private List<String> tmp = new ArrayList<>();
    int listLength = -1;

    private static final String TAG = "PostsFragment";

    // A banner ad is placed in every 3rd position in the RecyclerView.
    public static final int ITEMS_PER_AD = 3;

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/4177191030";  //ToDo: production

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mView = inflater.inflate(R.layout.fragment_posts, container, false);
        // Initialize Variables
        frameLayout = (FrameLayout) mView.findViewById(R.id.fragment_posts_frame_layout);




        mFloatingActionButton = (FloatingActionButton) Objects.requireNonNull(getActivity()).findViewById(R.id.floatingActionButton);
        mFloatingActionButton.setImageResource(R.drawable.ic_add_black_24dp);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FileUpload.class);
                startActivity(intent);
            }
        });
        mView.findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    if (clubs != null) {
                        getPosts(clubs);
                    }
                } else {
                    updateUI(new NetworkException("No Network"));
                }
            }
        });
        swipeRefreshLayout = mView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnected()) {
                    if (clubs != null) {
                        getPosts(clubs);
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    updateUI(new NetworkException("No Network"));
                }
            }
        });
        mRecyclerView = mView.findViewById(R.id.fragment_posts_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgressBar = mView.findViewById(R.id.fragment_posts_progressBar);
        error = (ConstraintLayout) mView.findViewById(R.id.error);

        return mView;
    }


    private void getPosts(List<String> clubs) {

        final List<String> postListeners = new ArrayList<>();
        postListeners.add("# Featured");
        postListeners.add("# Stumate");
        postListeners.add("# " + collegeShortName);
        postListeners.add("# " + dept);
        postListeners.add("# " + year);

        postListeners.addAll(clubs);

        listLength = postListeners.size();
        tmp = new ArrayList<>();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        posts.clear();
        for (final String s :
                postListeners) {
            //TODO: download posts from all the above posts and store it
            //TODO: Try to cache this and maybe you can improve the calls

            if (collegeName != null) {
                db.collection("institutes")
                        .document(collegeName)
                        .collection("posts")
                        .whereEqualTo("tag", s)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Log.d(TAG, "onComplete: club: " + s + " meta: " + task.getResult().getMetadata() + " data: " + task.getResult().getDocuments());
                                List<Post> mPosts = new ArrayList<>();
                                for (DocumentSnapshot document :
                                        task.getResult()) {
                                    mPosts.add(new Post(document.getId(), Objects.requireNonNull(document.toObject(Post.class)),(List<String>) document.get("saved")));
                                }
                                posts.addAll(mPosts);
                                sortPosts(posts);
                                tmp.add(s);
                                if (tmp.size() == listLength) {
                                    if (getActivity() != null) {
//                                        recyclerViewItems = new ArrayList<>();
//                                        recyclerViewItems.addAll(posts);
//                                        addBannerAds();
//                                        loadBannerAds();
                                        updateUI();
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + s + "\n" + e);
                                tmp.add(s);
                                if (tmp.size() == listLength) {
                                    if (getActivity() != null) {
//                                        recyclerViewItems = new ArrayList<>();
//                                        recyclerViewItems.addAll(posts);
//                                        addBannerAds();
//                                        loadBannerAds();
                                        updateUI();
                                    }
                                }
                            }
                        });
            }
        }

    }

    private void sortPosts(List<Post> posts) {
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post post, Post t1) {
                return post.getTimestamp().compareTo(t1.getTimestamp());
            }
        });
        Collections.reverse(posts);
    }

    private boolean isConnected() {
        // check for internet connection
        if (getActivity() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null)
                networkInfo = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
            return isConnected;
        }
        return true;

        /*
        boolean isWiFi;
        if (isConnected)
            isWiFi = Objects.requireNonNull(networkInfo).getType() == ConnectivityManager.TYPE_WIFI;
            isMobile = Objects.requireNonNull(networkInfo).getType() == ConnectivityManager.TYPE_MOBILE;
         */
    }

    /**
     * Adds banner ads to the items list.
     */
    private void addBannerAds() {
        // Loop through the items array and place a new banner ad in every ith position in
        // the items List.
        for (int i = 0; i <= recyclerViewItems.size(); i += ITEMS_PER_AD) {
            final AdView adView = new AdView(getContext());
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(AD_UNIT_ID);
            recyclerViewItems.add(i, adView);
        }
    }

    /**
     * Sets up and loads the banner ads.
     */
    private void loadBannerAds() {
        // Load the first banner ad in the items list (subsequent ads will be loaded automatically
        // in sequence).
        loadBannerAd(0);
    }

    /**
     * Loads the banner ads in the items list.
     */
    private void loadBannerAd(final int index) {

        if (index >= recyclerViewItems.size()) {
            return;
        }

        Object item = recyclerViewItems.get(index);
        if (!(item instanceof AdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a banner ad"
                    + " ad.");
        }

        final AdView adView = (AdView) item;

        // Set an AdListener on the AdView to wait for the previous banner ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // The previous banner ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadBannerAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // The previous banner ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous banner ad failed to load. Attempting to"
                        + " load the next banner ad in the items list.");
                loadBannerAd(index + ITEMS_PER_AD);
            }
        });

        // Load the banner ad.
        adView.loadAd(new AdRequest.Builder().build());
    }

    private void updateUI() {
        error.setVisibility(View.GONE);
        mProgressBar.setVisibility(INVISIBLE);
        if (recyclerViewItems != null) {
            if (mRecyclerView != null) {
                mAdapter = new PostsAdapter(getContext(), posts);
//                RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(getContext(), recyclerViewItems);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
        if (swipeRefreshLayout.isRefreshing()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 1000);
        }

    }

    private void updateUI(Exception e) {
        if (getActivity() != null) {
            if (swipeRefreshLayout.isRefreshing()) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(INVISIBLE);
            if (posts.size() == 0) {
                error.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
//        if (tmp.size() == listLength) {
//            updateUI();
//        }
    }

//    @Override
//    public void onResume() {
//        for (Object item : recyclerViewItems) {
//            if (item instanceof AdView) {
//                AdView adView = (AdView) item;
//                adView.resume();
//            }
//        }
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        for (Object item : recyclerViewItems) {
//            if (item instanceof AdView) {
//                AdView adView = (AdView) item;
//                adView.pause();
//            }
//        }
//        super.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        for (Object item : recyclerViewItems) {
//            if (item instanceof AdView) {
//                AdView adView = (AdView) item;
//                adView.destroy();
//            }
//        }
//        super.onDestroy();
//    }

    @Override
    public void update(Observable observable, Object o) {
        HashMap<String, Object> documentSnapshot = (HashMap<String, Object>) o;
        if (documentSnapshot != null) {
            Log.d(TAG, "getUserDetails: onEvent: documentSnapshot: " + documentSnapshot);

            clubs = (List<String>) documentSnapshot.get("clubs");
            collegeName = (String) documentSnapshot.get("collegeName");
            collegeShortName = (String) documentSnapshot.get("collegeShortName");
            className = (String) documentSnapshot.get("class");
            dept = className.split(" ")[0];
            year = className.split(" ")[0] + " " + className.split(" ")[1];

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPosts(clubs);
                }
            }, 1000);
        }
    }


}