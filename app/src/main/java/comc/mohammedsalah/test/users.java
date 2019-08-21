package comc.mohammedsalah.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class users extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore ;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        ConnectionInternet connectionInternet = new ConnectionInternet(users.this);
        Boolean c = connectionInternet.isConnectToInternt();
        if (!c){
            Toast.makeText(getApplicationContext(),"No Internt Connection\nلا يوجد اتصال بالانترنت",Toast.LENGTH_SHORT).show();
        }

        Toolbar toolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Users");
        getSupportActionBar().setSubtitle("المستخدمين");

        final List <usersclass> list = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.rv_users);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        final usersadapter adapter = new usersadapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (firebaseAuth.getCurrentUser() != null){
             firebaseFirestore = FirebaseFirestore.getInstance();
            Query query = firebaseFirestore.collection("users");
            query.addSnapshotListener(users.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            usersclass users = documentChange.getDocument().toObject(usersclass.class);
                            list.add(users);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }


    }
}
