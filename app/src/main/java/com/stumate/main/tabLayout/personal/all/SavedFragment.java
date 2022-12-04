package com.stumate.main.tabLayout.personal.all;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stumate.main.PostsGridLayoutAdapter;
import com.stumate.main.R;
import com.stumate.main.utils.dataTypes.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedFragment extends Fragment {

    private View mView;
    private RecyclerView postsPopulate;
    private TextView textMessage;

    private String collegeName;
    private String postDoc;

    private List<Post> posts = new ArrayList<>();

    private static final String TAG = "SavedFragment";

    public SavedFragment() {
        // Required empty public constructor
    }

    public SavedFragment(String postDoc) {
        this.postDoc = postDoc;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        collegeName = Objects.requireNonNull(getActivity()).getSharedPreferences("userDetails", MODE_PRIVATE).getString("collegeName", "ccc");
        mView = inflater.inflate(R.layout.fragment_saved, container, false);

        postsPopulate = mView.findViewById(R.id.fragment_saved_recycler_view);
        postsPopulate.setHasFixedSize(true);
        postsPopulate.setItemViewCacheSize(20);
        postsPopulate.setLayoutManager(new GridLayoutManager(getContext(), 3));
        textMessage = mView.findViewById(R.id.note);

        if (collegeName != null && postDoc != null) {
            getPots();
        }


        return mView;
    }

    private void getPots() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("institutes")
                .document(collegeName)
                .collection("posts")
                .whereArrayContains("saved", postDoc)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        posts.clear();
                        for (QueryDocumentSnapshot doc :
                                queryDocumentSnapshots) {
                            posts.add(new Post(doc.getId(), Objects.requireNonNull(doc.toObject(Post.class)), (List<String>) doc.get("saved")));
                        }

                        sortPosts(posts);

                        if (posts.size() > 0) {
                            textMessage.setVisibility(View.GONE);
                            postsPopulate.setAdapter(new PostsGridLayoutAdapter(getContext(), posts, "SavedFragment"));
                        }
                        else {
                            textMessage.setVisibility(View.VISIBLE);
                        }

                    }
                });
    }

    private void sortPosts(List<Post> posts) {
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post post, Post t1) {
                return post.getTimestamp().compareTo(t1.getTimestamp());
            }
        });
        Collections.reverse(posts);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (posts.size() > 0) {
            postsPopulate.setAdapter(new PostsGridLayoutAdapter(getContext(), posts, "SavedFragment"));
        }
    }
}
