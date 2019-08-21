package comc.mohammedsalah.test;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Blogpost> blogpostList;
    private FirebaseFirestore firebaseFirestore;
    BlogRecyclerAdapter adapter;
    private FirebaseAuth firebaseAuth;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        blogpostList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.post_list);
        adapter = new BlogRecyclerAdapter(blogpostList);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(adapter);
        firebaseAuth = FirebaseAuth.getInstance();

        ConnectionInternet connectionInternet = new ConnectionInternet(getActivity());
        Boolean c = connectionInternet.isConnectToInternt();
        if (!c){
            Toast.makeText(getActivity(),"No Internt Connection\nلا يوجد اتصال بالانترنت",Toast.LENGTH_SHORT).show();
        }

        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();
            Query query = firebaseFirestore.collection("posts").orderBy("date", Query.Direction.DESCENDING); // arrange posts from new to old
            query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            // getting the post's id
                            String post_id = documentChange.getDocument().getId();
                            Blogpost blogpost = documentChange.getDocument().toObject(Blogpost.class).withId(post_id);
                            blogpostList.add(blogpost);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
            return view;
        }

}
