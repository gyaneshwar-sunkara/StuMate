package com.stumate.main.tabLayout.personal.all;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stumate.main.R;
import com.stumate.main.utils.dataTypes.Ink;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class InksFragment extends Fragment {

    private View mView;
    private RecyclerView inksPopulate;
    private TextView textMessage;

    private String collegeName;
    private String inkDoc;

    private List<Ink> inks = new ArrayList<>();

    private DatabaseReference mDatabaseRef;

    private boolean flag = false;


    private static final String TAG = "InksFragment";

    public InksFragment() {
        // Required empty public constructor
    }

    public InksFragment(String inkDoc) {
        this.inkDoc = inkDoc;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        collegeName = Objects.requireNonNull(getActivity()).getSharedPreferences("userDetails", MODE_PRIVATE).getString("collegeName", "ccc");

        mView = inflater.inflate(R.layout.fragment_inks, container, false);
        inksPopulate = mView.findViewById(R.id.recyclerView);
        inksPopulate.setHasFixedSize(true);
        inksPopulate.setItemViewCacheSize(20);
        inksPopulate.setLayoutManager(new LinearLayoutManager(getContext()));
        textMessage = mView.findViewById(R.id.note);

        if (collegeName != null && inkDoc != null) {
            if (inkDoc.startsWith("#")) {
                inkDoc = inkDoc.substring(2);
            }
            getInks();
        }

        return mView;
    }

    private void getInks() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(collegeName + "/inks/" + inkDoc);
        mDatabaseRef.keepSynced(true);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inks.clear();
                flag = true;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Ink ink = postSnapshot.getValue(Ink.class);
                    inks.add(ink);
                }
                if (inks.size() > 0) {
                    textMessage.setVisibility(View.GONE);
                    inksPopulate.setAdapter(new InksAdapter(getContext(), inks));
                } else {
                    textMessage.setText("No Inks!");
                    textMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseRef.addValueEventListener(valueEventListener);
    }

    @Override
    public void onStart() {
        super.onStart();
            if (flag) {
                if (inks.size() > 0) {
                    inksPopulate.setAdapter(new InksAdapter(getContext(), inks));
                } else {
                    textMessage.setText("No Inks!");
                    textMessage.setVisibility(View.VISIBLE);
                }
            }
            else {
                textMessage.setText("Loading ...");
                textMessage.setVisibility(View.VISIBLE);
            }
    }
}
