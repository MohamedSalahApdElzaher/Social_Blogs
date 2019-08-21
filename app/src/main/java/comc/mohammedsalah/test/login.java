package comc.mohammedsalah.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    private EditText email , pass ;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog login_progress_dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_progress_dialog = new ProgressDialog(this);

        firebaseAuth =  FirebaseAuth.getInstance();

        email = findViewById(R.id.email_login);
        pass = findViewById(R.id.pass_login);


        ConnectionInternet connectionInternet = new ConnectionInternet(getApplicationContext());
        Boolean c = connectionInternet.isConnectToInternt();
        if (!c){
            Toast.makeText(getApplicationContext(),"No Internt Connection\nلا يوجد اتصال بالانترنت",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // check logged user
        FirebaseUser currentuser = firebaseAuth.getCurrentUser();
        if (currentuser != null) // current user already logged
        {
          sendtohome();
        }
    }

    public void sendtohome(){
        Intent intent = new Intent(getApplicationContext() , home.class); // send to home activity
        startActivity(intent);
        finish();
    }

         public void login(View view) {

           String emai_text  = email.getText().toString().trim();
           String password = pass.getText().toString().trim();

        if (!TextUtils.isEmpty(emai_text) && !TextUtils.isEmpty(password)){
              // show progress bar
             login_progress_dialog.setMessage("Login..\nجارى الدخول للحساب");
             login_progress_dialog.create();
             login_progress_dialog.show();

             firebaseAuth.signInWithEmailAndPassword(emai_text , password).
                     addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {

                     if (task.isSuccessful()){

                         sendtohome();

                     }else {
                         String Error = task.getException().getMessage();
                         Toast.makeText(getApplicationContext() , "Error :" +
                                 " " + Error , Toast.LENGTH_SHORT).show();
                     }

                     login_progress_dialog.cancel();
                 }
             });
        }


        }

    public void creat_acc(View view) {
        Intent intent = new Intent(getApplicationContext() , MainActivity.class);
        startActivity(intent);
    }
}
