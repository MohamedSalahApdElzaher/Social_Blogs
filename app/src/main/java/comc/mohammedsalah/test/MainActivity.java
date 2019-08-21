package comc.mohammedsalah.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText email , pass , confpass;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth= FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        email = findViewById(R.id.email_edit);
        pass = findViewById(R.id.pass_edit);
        confpass = findViewById(R.id.confpass_edit);


        ConnectionInternet connectionInternet = new ConnectionInternet(getApplicationContext());
        Boolean c = connectionInternet.isConnectToInternt();
        if (!c){
            Toast.makeText(getApplicationContext(),"No Internt Connection\nلا يوجد اتصال بالانترنت",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // get sign from user
        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentuser != null) // not logged in
        {
            Intent intent = new Intent(getApplicationContext() , home.class); // send to home activity
            startActivity(intent);
            finish();
        }

    }

    public void sign_up(View view) {
        String Email = email.getText().toString().trim();
        String Pass = pass.getText().toString().trim();
        String confPassword = confpass.getText().toString().trim();

        if (!TextUtils.isEmpty(Email) && !TextUtils.isEmpty(confPassword) && !TextUtils.isEmpty(Pass)) {
            if (Pass.equals(confPassword)) {
                progressDialog.setMessage("Loading..\nجارى تسجيل حساب");
                progressDialog.create();
                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), profileSetting.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage()
                                            , Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();
                                }
                            }
                        }
                );
            } else
                Toast.makeText(this, "Password not matching!\nخطا بكلمه السر", Toast.LENGTH_SHORT).show();

           // progressDialog.cancel();
        }
    }

    public void already_have_acc(View view) {

        Intent intent = new Intent(getApplicationContext(), login.class);
        startActivity(intent);
        finish();
    }
}
