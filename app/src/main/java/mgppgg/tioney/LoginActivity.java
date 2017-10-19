package mgppgg.tioney;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button BtnRegistrarse ;
    private Button BtnLogin ;
    private EditText ETpass;
    private EditText ETuser;
    private String pass;
    private String user;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "EmailPassword";

    DatabaseHelper helper = new DatabaseHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        BtnRegistrarse = (Button)findViewById(R.id.BtnRegistrarse);
        BtnLogin = (Button)findViewById(R.id.BtnLogin);
        ETpass = (EditText)findViewById(R.id.ETpass);
        ETuser = (EditText)findViewById(R.id.ETuser);


        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
         @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
         FirebaseUser user = firebaseAuth.getCurrentUser();
         if (user != null) {
                 // User is signed in
         Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
         } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
            }
         // ...
         }
                };




       /* BtnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);

            }
        });*/

        /*BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pass = ETpass.getText().toString();
                user = ETuser.getText().toString();

                String password = helper.searchPass(user);
                if(pass.equals(password)){

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("Username",user);
                    startActivity(intent);

                }

                else{
                    Toast Tpass = Toast.makeText(LoginActivity.this , "Usuario y contraseña incorrectos" , Toast.LENGTH_SHORT);
                    Tpass.show();;
                }

            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
    }

    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            if (!task.isSuccessful()) {
                Toast.makeText(EmailPasswordActivity.this, R.string.auth_failed,
                        Toast.LENGTH_SHORT).show();
            }

            // ...
        }
    });
}

