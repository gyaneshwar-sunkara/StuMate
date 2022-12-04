package com.stumate.main.tabLayout.messaging.search;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.tabs.TabLayout;
import com.stumate.main.R;

import java.util.Objects;


public class SearchActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private EditText mEditText;
    private ImageView mImageView;

    private AdView adView;
    private InterstitialAd interstitialAd;

    private static final String TAG = "SearchActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize variables
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mEditText = (EditText) findViewById(R.id.editText);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interstitialAd.isLoaded()) {
                    //interstitialAd.show();
                }
                else {
                    Log.d(TAG, "onClick: InterstitialAd is not loaded");
                }
                finish();
            }
        });

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");  //ToDo: production
        AdRequest adRequest2 = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest2);

        SearchAdapter mTabLayoutAdapter = new SearchAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), getSharedPreferences("userDetails", MODE_PRIVATE).getString("collegeName", "xxx"), getSharedPreferences("userDetails", MODE_PRIVATE).getString("collegeShortName", "NRCM"));
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setText(getSharedPreferences("userDetails", MODE_PRIVATE).getString("collegeShortName", "NRCM") + " CLUBS");  // Stumate symbol
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setAdapter(mTabLayoutAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mEditText.setHint("Search");
                        break;
                    case 1:
                        mEditText.setHint("Search Clubs");
                        break;
                    case 2:
                        mEditText.setHint("Search CSE");
                        break;
                    case 3:
                        mEditText.setHint("Search ECE");
                        break;
                    case 4:
                        mEditText.setHint("Search EEE");
                        break;
                    case 5:
                        mEditText.setHint("Search MECH");
                        break;
                    case 6:
                        mEditText.setHint("Search CIVIL");
                        break;
                    case 7:
                        mEditText.setHint("Search other branches");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
    }

    @Override
    public void onBackPressed() {
        if (interstitialAd.isLoaded()) {
            //interstitialAd.show();
        }
        else {
            Log.d(TAG, "onClick: Interstitial was not loaded");
        }
        finish();
        super.onBackPressed();
    }
}
