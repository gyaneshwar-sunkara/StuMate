package com.stumate.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.stumate.main.tabLayout.personal.all.InksFragment;
import com.stumate.main.tabLayout.personal.all.MyMatesFragment;
import com.stumate.main.tabLayout.personal.all.MyPostsFragment;
import com.stumate.main.utils.dataTypes.User;

import java.util.List;

public class MateProfileActivityAdapter extends FragmentPagerAdapter {

    private int tabs;
    private List<User> mates;
    private List<User> clubs;
    private String uid;

    public MateProfileActivityAdapter(FragmentManager fm, int t, String uid, List<User> mates, List<User> clubs) {
        super(fm);
        tabs = t;
        this.clubs = clubs;
        this.uid = uid;
        this.mates = mates;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new InksFragment(uid);
            case 1:
                return new MyPostsFragment(uid);
            case 2:
                return new MyMatesFragment(clubs);
            case 3:
                return new MyMatesFragment(mates);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabs;
    }
}