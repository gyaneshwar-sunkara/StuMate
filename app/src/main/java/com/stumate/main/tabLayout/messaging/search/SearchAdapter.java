package com.stumate.main.tabLayout.messaging.search;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.stumate.main.tabLayout.messaging.search.tabLayout.CIVILSearchFragment;
import com.stumate.main.tabLayout.messaging.search.tabLayout.CSESearchFragment;
import com.stumate.main.tabLayout.messaging.search.tabLayout.ClubsSearchFragment;
import com.stumate.main.tabLayout.messaging.search.tabLayout.ECESearchFragment;
import com.stumate.main.tabLayout.messaging.search.tabLayout.EEESearchFragment;
import com.stumate.main.tabLayout.messaging.search.tabLayout.MECHSearchFragment;
import com.stumate.main.tabLayout.messaging.search.tabLayout.MyMatesNClubsFragment;

public class SearchAdapter extends FragmentPagerAdapter {
    private int tabs;
    private static final String TAG = "SearchAdapter";

    SearchAdapter(FragmentManager fm, int t, String s, String ss) {
        super(fm);
        this.tabs = t;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new MyMatesNClubsFragment();
            case 1:
                return new ClubsSearchFragment();
            case 2:
                return new CSESearchFragment();
            case 3:
                return new ECESearchFragment();
            case 4:
                return new EEESearchFragment();
            case 5:
                return new MECHSearchFragment();
            case 6:
                return new CIVILSearchFragment();
                // TODO: case 7: for otherSearchFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabs;
    }
}
