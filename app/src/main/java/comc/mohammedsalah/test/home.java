package comc.mohammedsalah.test;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class home extends AppCompatActivity {

    private Toolbar home_toolbar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String current_user_id ;
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    NotificationFragment notificationFragment;
    ProfileFragment profileFragment;
    private Toolbar search_toolbar;
    private EditText EditSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        ConnectionInternet connectionInternet = new ConnectionInternet(getApplicationContext());
        Boolean c = connectionInternet.isConnectToInternt();
        if (!c){
            Toast.makeText(getApplicationContext(),"No Internt Connection\nلا يوجد اتصال بالانترنت",Toast.LENGTH_SHORT).show();
        }

        home_toolbar = findViewById(R.id.home_toolbar);
          setSupportActionBar(home_toolbar);
          getSupportActionBar().setTitle("Posts");
          getSupportActionBar().setSubtitle("المنشورات");

          bottomNavigationView=findViewById(R.id.bottomnavigationview);
          homeFragment = new HomeFragment();
          notificationFragment= new NotificationFragment();
          profileFragment=new ProfileFragment();
          EditSearch = findViewById(R.id.Edit_search_bar);

         ReplaceFrafment(homeFragment);

          bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
              @Override
              public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){

                        case R.id.homeId :
                            ReplaceFrafment(homeFragment);
                            return true ;
                        case R.id.notificationId:
                            ReplaceFrafment(notificationFragment);
                            return true;
                        case R.id.PROFILEID:
                           // ReplaceFrafment(profileFragment);
                            Intent intent = new Intent(getApplicationContext(),profileSetting.class);
                            startActivity(intent);
                            return true;
                            default:
                                return false;
                    }
              }
          });

     }

     private void ReplaceFrafment(Fragment fragment){
         FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
         fragmentTransaction.replace(R.id.maincontainer,fragment);
         fragmentTransaction.commit();
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();

       if (id == R.id.logout){
           if (firebaseAuth.getCurrentUser()!=null) {
               try {
                   logout();
                   sendtologin();
               }catch (Exception e){Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();}
           }
       }else if (id==R.id.setting){
           Intent intent = new Intent(getApplicationContext() , profileSetting.class);
           // send to setting activity
           startActivity(intent);

       }else if (id == R.id.add_post){
           Intent intent = new Intent(getApplicationContext() , addpost.class); // send to addpost activity
           startActivity(intent);
       }else if (id == R.id.users){
           Intent intent = new Intent(getApplicationContext() , users.class); // send to addpost activity
           startActivity(intent);
       }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        firebaseAuth.signOut();
    }

    private void sendtologin() {
        Intent intent = new Intent(getApplicationContext() , login.class); // send to login activity
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
            sendtologin();
        }else{
            firebaseFirestore.collection("users").document(current_user_id).get().addOnCompleteListener(
                    new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                  if (!task.getResult().exists()){
                                      Intent intent = new Intent(getApplicationContext(),profileSetting.class);
                                      startActivity(intent);
                                  }
                             }else {
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
            );
        }

    }


}
