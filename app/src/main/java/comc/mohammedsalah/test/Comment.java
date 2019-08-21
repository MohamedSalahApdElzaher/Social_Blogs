package comc.mohammedsalah.test;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class Comment extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText editText;
    private ImageView postbtn;
    private Toolbar toolbar;
    private String postid;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    String CurrentUser;
    private List <CommrntsClass> list;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        toolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);


        ConnectionInternet connectionInternet = new ConnectionInternet(getApplicationContext());
        Boolean c = connectionInternet.isConnectToInternt();
        if (!c){
            Toast.makeText(getApplicationContext(),"No Internt Connection\nلا يوجد اتصال بالانترنت",Toast.LENGTH_SHORT).show();
        }

        progressDialog  = new ProgressDialog(this);

        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setSubtitle("تعليقات");
        list = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        CurrentUser=firebaseAuth.getCurrentUser().getUid();

        postid = getIntent().getStringExtra("postId");

        recyclerView = findViewById(R.id.comment_recyclerVeiw);
        adapter= new CommentsAdapter(list);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        editText = findViewById(R.id.Edit_comments);
        postbtn  = findViewById(R.id.post_comments);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 if (editText.getText().toString().trim().isEmpty()){
                     postbtn.setVisibility(View.INVISIBLE);
                 }else {
                     postbtn.setVisibility(View.VISIBLE);
                 }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Reterive a comments
        firebaseFirestore.collection("posts").document(postid).collection("comments").addSnapshotListener(Comment.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent( QuerySnapshot queryDocumentSnapshots,  FirebaseFirestoreException e) {
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        CommrntsClass commrntsClass = documentChange.getDocument().toObject(CommrntsClass.class);
                        list.add(commrntsClass);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {

                Date currentTime = Calendar.getInstance(TimeZone.getTimeZone(String.valueOf(DateFormat.SHORT))).getTime();
                Map<String, String> map = new HashMap<>();
                map.put("comments", editText.getText().toString());
                map.put("userid", CurrentUser);
                map.put("date", currentTime.toString());

                firebaseFirestore.collection("posts").document(postid).collection("comments")
                        .add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Comment Posted Successfully!\n تم النشر بنجاح", Toast.LENGTH_SHORT).show();
                            editText.setText("");
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            editText.setText("");
                        }
                    }
                });
            }

            });
    }
}















