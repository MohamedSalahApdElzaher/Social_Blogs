package comc.mohammedsalah.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class addpost extends AppCompatActivity {
    private ImageView post_iamge;
    private EditText postTitle , postDesc;
    private static final int PICK_IMAGE = 1;
    private Uri imageuri = null;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    String current_id;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

        Toolbar toolbar = findViewById(R.id.addpost_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Post");
        getSupportActionBar().setSubtitle("اضافه بوست");

        ConnectionInternet connectionInternet = new ConnectionInternet(getApplicationContext());
        Boolean c = connectionInternet.isConnectToInternt();
        if (!c){
            Toast.makeText(getApplicationContext(),"No Internt Connection\nلا يوجد اتصال بالانترنت",Toast.LENGTH_SHORT).show();
        }

        post_iamge = findViewById(R.id.post_image);
        postDesc = findViewById(R.id.post_desc);
        postTitle=findViewById(R.id.post_title);

        firebaseFirestore= FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseAuth  = FirebaseAuth.getInstance();
        current_id = firebaseAuth.getCurrentUser().getUid();

        progressDialog = new ProgressDialog(this);

        post_iamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {  // open a gallery to sellect a photo
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent , "select a photo") , PICK_IMAGE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imageuri=data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver() , imageuri);
                post_iamge.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addpost_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

         if (id==R.id.save_post) {
             try {
                 final String title = postTitle.getText().toString().trim();
                 final String description = postDesc.getText().toString().trim();

                 if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && imageuri != null) {
                     progressDialog.setMessage("Uploading..\nتحميل");
                     progressDialog.create();
                     progressDialog.show();

                     final String Randomname = UUID.randomUUID().toString(); // generate a randome name
                     StorageReference fillpath = storageReference.child("posts_images").child(Randomname + ".jpg");
                     fillpath.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                         @Override
                         public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                             // final Uri downloadUri = task.getResult().getDownloadUrl();
                             if (task.isSuccessful()) {

                                 Uri downloadUri = task.getResult().getDownloadUrl();
                                 Date currentTime = Calendar.getInstance(TimeZone.getTimeZone(String.valueOf(DateFormat.SHORT))).getTime();

                                 // Store posts as an objects in a map
                                 Map<String, String> map = new HashMap<>(); // note that map must be a string !! show error with object
                                 map.put("Image_uri", downloadUri.toString());
                                 map.put("post_title", title);
                                 map.put("post_description", description);
                                 map.put("id", current_id);
                                 map.put("date", currentTime.toString());


                                 firebaseFirestore.collection("posts").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                     @Override
                                     public void onComplete(@NonNull Task<DocumentReference> task) {
                                         if (task.isSuccessful()) {
                                             Toast.makeText(getApplicationContext(), "Posted Successful!\nتم النشر", Toast.LENGTH_SHORT).show();

                                             Intent intent = new Intent(getApplicationContext(), home.class);
                                             startActivity(intent);
                                             finish();

                                         } else {
                                             Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                         }
                                         progressDialog.cancel();
                                     }
                                 });


                             } else {
                                 Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                 progressDialog.cancel();
                             }
                         }
                     });
                 }
             }  catch (Exception e){
                 Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
             }
         }
        return super.onOptionsItemSelected(item);
    }

}
