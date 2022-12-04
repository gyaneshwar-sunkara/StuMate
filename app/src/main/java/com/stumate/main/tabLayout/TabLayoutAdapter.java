package com.stumate.main.tabLayout;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.stumate.main.eventListeners.UserDataListener;
import com.stumate.main.tabLayout.messaging.MessagingFragment;
import com.stumate.main.tabLayout.personal.PersonalFragment;
import com.stumate.main.tabLayout.posts.PostsFragment;

public class TabLayoutAdapter extends FragmentPagerAdapter {
    private int tabs;
    private MessagingFragment messagingFragment = null;
    private PostsFragment postsFragment = null;
    private PersonalFragment personalFragment = null;

    public TabLayoutAdapter(FragmentManager fm, int t, UserDataListener userDataListener, MessagingFragment mMessagingFragment, PostsFragment mPostsFragment, PersonalFragment mPersonalFragment) {
        super(fm);
        // Model/View/CONSTRUCTOR
        this.tabs = t;
        this.messagingFragment = mMessagingFragment;
        this.postsFragment = mPostsFragment;
        this.personalFragment = mPersonalFragment;
        userDataListener.addObserver(messagingFragment);
        userDataListener.addObserver(postsFragment);
        userDataListener.addObserver(personalFragment);
        userDataListener.getUserDetails();
    }

    @Override
    public Fragment getItem(int i) {


        switch (i) {
            case 0:
                return messagingFragment;
            case 1:
                return postsFragment;
            case 2:
                return personalFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabs;
    }
}