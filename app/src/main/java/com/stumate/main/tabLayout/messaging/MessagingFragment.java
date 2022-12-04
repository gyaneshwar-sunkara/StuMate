package com.stumate.main.tabLayout.messaging;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stumate.main.R;
import com.stumate.main.tabLayout.TabLayoutActivity;
import com.stumate.main.utils.dataTypes.Messenger;
import com.stumate.main.utils.dataTypes.Requests;
import com.stumate.main.utils.dataTypes.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MessagingFragment extends Fragment implements Observer {

    private LinearLayout linearLayoutRequestContainer;
    private RecyclerView mRecyclerViewMessaging;
    private RecyclerView mRecyclerViewRequests;
    private TextView noOfRequests;

    private List<String> mates;
    private List<String> clubs;
    private View mView;
    private List<String> pending;
    private List<Requests> pendingRequests = new ArrayList<>();

    private DatabaseReference mDatabaseRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "MessagingFragment";

    public MessagingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_messaging, container, false);

        linearLayoutRequestContainer = mView.findViewById(R.id.messageRequest);
        noOfRequests = mView.findViewById(R.id.noOfRequests);
        ImageView showRequests = mView.findViewById(R.id.showRequests);
        showRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerViewRequests.setVisibility(View.VISIBLE);
            }
        });

        mRecyclerViewRequests = mView.findViewById(R.id.messageRequestRecyclerView);
        mRecyclerViewRequests.setHasFixedSize(true);
        mRecyclerViewRequests.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (pendingRequests.size() < 0 || pendingRequests == null || pendingRequests.isEmpty()) {
            mRecyclerViewRequests.setVisibility(View.GONE);
            linearLayoutRequestContainer.setVisibility(View.GONE);
        } else {
            linearLayoutRequestContainer.setVisibility(View.VISIBLE);
            noOfRequests.setText(String.valueOf(pendingRequests.size()));
            mRecyclerViewRequests.setAdapter(new RequestAdapter(getContext(), pendingRequests));
        }

        mRecyclerViewMessaging = mView.findViewById(R.id.fragmentMessagingRecyclerView);
        mRecyclerViewMessaging.setHasFixedSize(true);
        mRecyclerViewMessaging.setItemViewCacheSize(20);
        mRecyclerViewMessaging.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (TabLayoutActivity.getMessengers() != null) {
            if (TabLayoutActivity.getMessengers().size() <1) {
                mView.findViewById(R.id.note).setVisibility(View.VISIBLE);
                mView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
            else {
                mView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                mView.findViewById(R.id.note).setVisibility(View.GONE);
                updateMessengers(TabLayoutActivity.getMessengers());
            }
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TabLayoutActivity.getMessengers() != null) {
            if (TabLayoutActivity.getMessengers().size() <1) {
                mView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                mView.findViewById(R.id.note).setVisibility(View.VISIBLE);
            }
            else {
                mView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                mView.findViewById(R.id.note).setVisibility(View.GONE);
                updateMessengers(TabLayoutActivity.getMessengers());
            }
        }
        updateRequests();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (TabLayoutActivity.getMessengers() != null) {
            if (TabLayoutActivity.getMessengers().size() <1) {
                mView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                mView.findViewById(R.id.note).setVisibility(View.VISIBLE);
            }
            else {
                mView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                mView.findViewById(R.id.note).setVisibility(View.GONE);
                updateMessengers(TabLayoutActivity.getMessengers());
            }
        }
        updateRequests();
    }

    public void updateRequests() {
        if (getActivity() != null && linearLayoutRequestContainer != null) {
            if (pending != null) {
                pendingRequests.clear(); // TODO: Uncomment this without fail
                for (String s :
                        pending) {
                    User u = TabLayoutActivity.getUser(s);
                    Requests r = new Requests();
                    if (u != null) {
                        r.setDisplayName(u.getDisplayName());
                        r.setImageUrl(u.getImageUrl());
                        r.setUid(u.getUid());
                        r.setStatus("pending");
                        pendingRequests.add(r);
                    }
                }
            }
            if (pendingRequests.size() < 0 || pendingRequests == null || pendingRequests.isEmpty()) {
                mRecyclerViewRequests.setVisibility(View.GONE);
                linearLayoutRequestContainer.setVisibility(View.GONE);
            } else {
                linearLayoutRequestContainer.setVisibility(View.VISIBLE);
                if (pendingRequests.size() < 10) {
                    noOfRequests.setText("0" + pendingRequests.size());
                } else {
                    noOfRequests.setText(String.valueOf(pendingRequests.size()));
                }
                mRecyclerViewRequests.setAdapter(new RequestAdapter(getContext(), pendingRequests));
            }
        }
    }

    public void updateMessengers(List<Messenger> messengers) {
        if (getActivity() != null && TabLayoutActivity.getMessengers() != null) {
            if (messengers.size() <1) {
                mView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                mView.findViewById(R.id.note).setVisibility(View.VISIBLE);
            }
            else {
                mView.findViewById(R.id.note).setVisibility(View.GONE);
                mView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                MessengerAdapter messengerAdapter = new MessengerAdapter(getContext(), messengers);
                mRecyclerViewMessaging.setAdapter(messengerAdapter);
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        HashMap<String, Object> documentSnapshot = null;
        String mUid = "";
        if (o != null) {
            documentSnapshot = (HashMap<String, Object>) o;
            mUid = user.getUid();
        }
        if (documentSnapshot != null) {
            Log.d(TAG, "getUserDetails: onEvent: documentSnapshot: " + documentSnapshot);

            final String collegeName = (String) documentSnapshot.get("collegeName");
            mates = (List<String>) documentSnapshot.get("mates");
            clubs = (List<String>) documentSnapshot.get("clubs");

            pending = (List<String>) documentSnapshot.get("pending");
            updateRequests();
            if (getActivity() != null && TabLayoutActivity.getMessengers() != null) {
                if (TabLayoutActivity.getMessengers().size() <1) {
                    mView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    mView.findViewById(R.id.note).setVisibility(View.VISIBLE);
                }
                else {
                    mView.findViewById(R.id.note).setVisibility(View.GONE);
                    mView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    updateMessengers(TabLayoutActivity.getMessengers());
                }
            }
        } else {
            Log.d(TAG, "No such document");
            Toast.makeText(getActivity(), "Could'nt get messengers", Toast.LENGTH_SHORT).show();
        }
    }
}