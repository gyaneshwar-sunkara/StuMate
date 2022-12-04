package com.stumate.main.tabLayout.personal;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.material.tabs.TabLayout;
import com.stumate.main.R;
import com.stumate.main.tabLayout.personal.settings.AccountSettings;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class PersonalFragment extends Fragment implements Observer {
    // variables
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView textViewDisplayName;
    private TextView textViewCollegeName;
    private TextView textViewBio;
    private TextView textViewAccountSettings;
    private View mView;
    private boolean loaded = false;
    private ImageView header;
    private ImageView addHeader;
    private Button support;
    private CircleImageView profilePicture;

    private static final String TAG = "PersonalFragment";

    private String uid;
    private String displayName;
    private String imageUrl;
    private String headerUrl;
    private String bio;
    private String email;
    private String phone;
    private String collegeShortName;
    private String collegeName;
    private String className;
    private List<String> inks;
    private List<String> posts;
    private static List<String> mates;
    private static List<String> clubs;
    private List<String> pending;
    private List<String> blocked;

    private RewardedVideoAd rewardedVideoAd;

    private Uri tmpURI;
    private Uri mImageUri;
    private InterstitialAd interstitialAd;

    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
            updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
//        mViewPager.setCurrentItem(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_personal, container, false);

        loadInterstitialAd();

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getContext());
        //rewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        final Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.setData(Uri.parse("mailto:"));

        mailIntent.putExtra(Intent.EXTRA_EMAIL, "stumatedeveloper@gmail.com");
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, "User review");

        // Initialize variables
        mTabLayout = (TabLayout) mView.findViewById(R.id.fragment_personal_tab_layout);
        mViewPager = (ViewPager) mView.findViewById(R.id.fragment_personal_view_pager);
        header = mView.findViewById(R.id.imageView);
        addHeader = mView.findViewById(R.id.addHeader);
        addHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewAccountSettings.performClick();
            }
        });
        support = mView.findViewById(R.id.button);
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                String[] values = {"Watch a video", "Click on an ad", "Write a review"};
                builder.setTitle(null)
                        .setItems(values, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    if (rewardedVideoAd.isLoaded()) {
                                        rewardedVideoAd.show();
                                        loadRewardedVideoAd();
                                    } else {
                                        Log.d(TAG, "onClick: RewardedVideoAd is not loaded");
                                        Toast.makeText(getContext(), "Try after some time", Toast.LENGTH_SHORT).show();
                                    }
                                } else if (which == 1) {
                                    if (interstitialAd.isLoaded()) {
                                        interstitialAd.show();
                                        loadInterstitialAd();
                                    }
                                    else {
                                        Log.d(TAG, "onClick: InterstitialAd is not loaded");
                                        Toast.makeText(getContext(), "Try after some time", Toast.LENGTH_SHORT).show();
                                    }

                                } else if (which == 2) {
                                    Toast.makeText(getContext(), "Coming in next build...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                builder.show();
            }
        });
        profilePicture = (CircleImageView) mView.findViewById(R.id.circleImageView);
        textViewDisplayName = (TextView) mView.findViewById(R.id.textView);
        textViewCollegeName = (TextView) mView.findViewById(R.id.textView2);
        textViewBio = (TextView) mView.findViewById(R.id.textView3);

        textViewAccountSettings = (TextView) mView.findViewById(R.id.textView4);
        textViewAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AccountSettings.class);
                startActivity(intent);
            }
        });
        loaded = true;
        // Add page change listener
        PersonalAdapter mTabLayoutAdapter = new PersonalAdapter(getChildFragmentManager(), mTabLayout.getTabCount()); // TODO: getChildFragmentManager not SupportFragmentManager from parent activity
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setAdapter(mTabLayoutAdapter);

        // Add tab select listener
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return mView;
    }

    private void loadRewardedVideoAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        rewardedVideoAd.loadAd("ca-app-pub-6129418659811438/7819822069",
                adRequest);
    }

    private void loadInterstitialAd() {
        interstitialAd = new InterstitialAd(getContext());
        interstitialAd.setAdUnitId("ca-app-pub-6129418659811438/4232270150");
        AdRequest adRequest2 = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest2);
    }


    private void updateUI() {

        SharedPreferences prefs = getActivity().getSharedPreferences("userDetails", MODE_PRIVATE);
        displayName = prefs.getString("displayName", "Stumare user");
        collegeShortName = prefs.getString("collegeShortName", "");
        className = prefs.getString("class", "");
        bio = prefs.getString("bio", "");
        imageUrl = prefs.getString("imageUrl", null);
        headerUrl = prefs.getString("headerUrl", null);
        textViewDisplayName.setText(displayName);
        textViewCollegeName.setText("@ " + collegeShortName + " " + className);
        textViewBio.setText(bio);
        if (imageUrl != null) {
            Glide.with(getActivity())
                    .load(imageUrl)
                    .placeholder(R.drawable.user)
                    .into(profilePicture);
        }
        if (headerUrl != null && !headerUrl.equals("")) {
            addHeader.setVisibility(View.GONE);

            Glide.with(getActivity())
                    .load(headerUrl)
                    .into(header);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        HashMap<String, Object> documentSnapshot = (HashMap<String, Object>) o;
        if (documentSnapshot != null) {
            Log.d(TAG, "getUserDetails: onEvent: documentSnapshot: " + documentSnapshot);
            displayName = (String) documentSnapshot.get("displayName");
            imageUrl = (String) documentSnapshot.get("imageUrl");
            headerUrl = (String) documentSnapshot.get("headerUrl");
            bio = (String) documentSnapshot.get("bio");
            collegeName = (String) documentSnapshot.get("collegeName");
            collegeShortName = (String) documentSnapshot.get("collegeShortName");
            className = (String) documentSnapshot.get("class");
            email = (String) documentSnapshot.get("email");
            phone = (String) documentSnapshot.get("phone");
            uid = (String) documentSnapshot.get("uid");

            clubs = (List<String>) documentSnapshot.get("clubs");
            mates = (List<String>) documentSnapshot.get("mates");
            posts = (List<String>) documentSnapshot.get("Post");
            inks = (List<String>) documentSnapshot.get("inks");
            pending = (List<String>) documentSnapshot.get("pending");
            blocked = (List<String>) documentSnapshot.get("blocked");
            if (loaded && addHeader != null && getActivity() != null) {
                SharedPreferences.Editor editor = Objects.requireNonNull(getActivity()).getSharedPreferences("userDetails", MODE_PRIVATE).edit();
                editor.putString("displayName", displayName);
                editor.putString("imageUrl", imageUrl);
                editor.putString("headerUrl", headerUrl);
                editor.putString("email", email);
                editor.putString("phone", phone);
                editor.putString("uid", uid);
                editor.putString("bio", bio);
                editor.putString("class", className);
                editor.putString("collegeName", collegeName);
                editor.putString("collegeShortName", collegeShortName);
                editor.apply();

                updateUI();
            }
        } else {
            Log.d(TAG, "No such document");
            Toast.makeText(getActivity(), "Could'nt update user details", Toast.LENGTH_SHORT).show();
        }
    }

    public static List<String> getMates() {
        return mates;
    }

    public static List<String> getClubs() {
        return clubs;
    }
}
