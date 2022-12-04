package com.stumate.main.tabLayout.personal.all;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stumate.main.R;
import com.stumate.main.tabLayout.messaging.search.tabLayout.SearchRecyclerAdapter;
import com.stumate.main.utils.dataTypes.User;

import java.util.List;

public class MyMatesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SearchRecyclerAdapter searchAdapter;
    private List<User> mUsers;
    private View mView;

    public MyMatesFragment() {
        // Required empty public constructor
    }

    public MyMatesFragment(List<User> users) {
        this.mUsers = users;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mechsearch, container, false);

        mRecyclerView = mView.findViewById(R.id.mechSearchRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mUsers != null) {
            if (mUsers.size() > 0) {
                mView.findViewById(R.id.note).setVisibility(View.GONE);
                searchAdapter = new SearchRecyclerAdapter(getContext(), mUsers, "no");
                mRecyclerView.setAdapter(searchAdapter);
            }
            else {
                mView.findViewById(R.id.note).setVisibility(View.VISIBLE);
            }
        }
    }
}
