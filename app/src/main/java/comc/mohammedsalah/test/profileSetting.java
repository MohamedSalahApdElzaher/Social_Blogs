package comc.mohammedsalah.test;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileSetting extends AppCompatActivity {

    CircleImageView imageView;
    private static final int PICK_IMAGE = 1;
    Uri imageuri = null;
    EditText prof_name, prof_phone_number;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        Toast.makeText(getApplicationContext(),"Choose image\nSet name\nPhone number must be >= 11"
                ,Toast.LENGTH_SHORT).show();

        Toolbar profilesetting_toolbar = findViewById(R.id.prof_setting_toolbar);
        setSupportActionBar(profilesetting_toolbar);


        ConnectionInternet connectionInternet = new ConnectionInternet(getApplicationContext());
        Boolean c = connectionInternet.isConnectToInternt();
        if (!c){
            Toast.makeText(getApplicationContext(),"No Internt Connection\nلا يوجد اتصال بالانترنت",Toast.LENGTH_SHORT).show();
        }
        getSupportActionBar().setTitle("Profile Setting");
        getSupportActionBar().setSubtitle("اعدادات الحساب");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        prof_name= findViewById(R.id.prof_name);
        imageView = findViewById(R.id.profile_image);
        prof_phone_number = findViewById(R.id.prof_number_phone);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ // requeried permision
                          if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                          ) != PackageManager.PERMISSION_GRANTED){
                              ActivityCompat.requestPermissions(profileSetting.this , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                               Toast.makeText(getApplicationContext(),"this app need permission!\nاحتاج الاذن",Toast.LENGTH_SHORT).show();
                          }else{
                            setImage();
                          }
                }else {
                    setImage();
                }
            }
        });

        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  if (task.isSuccessful()){

                      if (task.getResult().exists()){

                          // retrive data from firestore
                          String name = task.getResult().getString("name");
                          String phone_numb = task.getResult().getString("phone");
                          String imag = task.getResult().getString("image");

                          prof_name.setText(name);
                          prof_phone_number.setText(phone_numb);

                          RequestOptions requestOptions = new RequestOptions();
                          requestOptions.placeholder(R.mipmap.ic_profile);

                          Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions).load(imag).into(imageView);

                      }
                  }else {
                      Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                  }
            }
        });
    }

    private void setImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent , "select photo"),PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imageuri=data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver() , imageuri);
                imageView.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }

        }

    }

    public void save(View view) {
        final String user_name = prof_name.getText().toString();
        final String user_phone_number = prof_phone_number.getText().toString();

          if (!TextUtils.isEmpty(user_name) && imageuri!=null ){

              // get id for current user
              user_id = firebaseAuth.getCurrentUser().getUid();

              progressDialog.setMessage("Uploading\nPlease Waite..\nتحميل");
              progressDialog.create();
              progressDialog.show();

            // set path to current image user
              StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");

              image_path.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                       if (task.isSuccessful()){

                          Uri uri_dwonload =  task.getResult().getDownloadUrl();

                           Map <String, String> map = new HashMap<>();
                           map.put("name" , user_name);
                           map.put("image" , uri_dwonload.toString());
                           map.put("phone" , user_phone_number);

                          firebaseFirestore.collection("users").document(user_id).set(map).addOnCompleteListener(
                                  new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                             if (task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(),"Profile Settings are Updated !\n تم التحديث بنجاح",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(),home.class);
                                                startActivity(intent);
                                                finish();
                                             } else{
                                                 Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                             }
                                          progressDialog.cancel();
                                      }
                                  }
                          );

                       }else {
                           Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                           progressDialog.cancel();
                       }

                   }
               });
        }
    }
}
