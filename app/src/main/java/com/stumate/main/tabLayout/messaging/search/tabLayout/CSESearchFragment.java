package com.stumate.main.tabLayout.messaging.search.tabLayout;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.stumate.main.R;
import com.stumate.main.utils.dataTypes.User;
import com.stumate.main.tabLayout.TabLayoutActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class CSESearchFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SearchRecyclerAdapter searchAdapter;
    private List<User> mUsers;
    private EditText mEditText;

    public CSESearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_csesearch, container, false);
        mEditText = Objects.requireNonNull(getActivity()).findViewById(R.id.editText);

//        AdView mAdView = mView.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        mRecyclerView = mView.findViewById(R.id.cseSearchRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = TabLayoutActivity.getCse();

        searchAdapter = new SearchRecyclerAdapter(getContext(), mUsers);
        mRecyclerView.setAdapter(searchAdapter);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return mView;
    }

    private void searchUsers(String s) {
        List<User> sUsers = new ArrayList<>();
        for (User u :
                mUsers) {
            if(u.getDisplayName().toLowerCase().startsWith(s)) {
                sUsers.add(u);
            }
        }
        searchAdapter = new SearchRecyclerAdapter(getContext(), sUsers);
        mRecyclerView.setAdapter(searchAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        String s = mEditText.getText().toString().toLowerCase();
        if(!s.equals("")){
            searchUsers(s);
        }
        else {
            if (mUsers != null) {
                searchAdapter = new SearchRecyclerAdapter(getContext(), mUsers);
                mRecyclerView.setAdapter(searchAdapter);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        String s = mEditText.getText().toString().toLowerCase();
        if(!s.equals("")){
            searchUsers(s);
        }
        else {
            if (mUsers != null) {
                searchAdapter = new SearchRecyclerAdapter(getContext(), mUsers);
                mRecyclerView.setAdapter(searchAdapter);
            }
        }
    }
}