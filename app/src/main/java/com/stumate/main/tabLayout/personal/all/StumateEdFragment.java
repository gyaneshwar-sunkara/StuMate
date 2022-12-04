package com.stumate.main.tabLayout.personal.all;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stumate.main.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StumateEdFragment extends Fragment {


    public StumateEdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stumate_ed, container, false);
    }

}
