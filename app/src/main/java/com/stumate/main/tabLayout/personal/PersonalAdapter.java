package com.stumate.main.tabLayout.personal;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.stumate.main.tabLayout.personal.all.MyPostsFragment;
import com.stumate.main.tabLayout.personal.all.SavedFragment;
import com.stumate.main.tabLayout.personal.all.InksFragment;
import com.stumate.main.tabLayout.personal.all.StumateEdFragment;

public class PersonalAdapter extends FragmentPagerAdapter {

    private int tabs;

    public PersonalAdapter(FragmentManager fm, int t) {
        super(fm);
        tabs = t;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new StumateEdFragment();
            case 1:
                return new InksFragment(FirebaseAuth.getInstance().getCurrentUser().getUid());
            case 2:
                return new MyPostsFragment(FirebaseAuth.getInstance().getCurrentUser().getUid());
            case 3:
                return new SavedFragment(FirebaseAuth.getInstance().getCurrentUser().getUid());
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabs;
    }
}
