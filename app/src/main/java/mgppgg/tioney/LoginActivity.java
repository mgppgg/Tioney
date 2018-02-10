package mgppgg.tioney;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.support.annotation.VisibleForTesting;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends BaseActivity {

    private Button BtnRegistrarse ;
    private Button BtnLogin ;
    private EditText ETpass;
    private EditText ETemail;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "EmailPassword";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        BtnRegistrarse = (Button)findViewById(R.id.BtnRegistrarse);
        BtnLogin = (Button)findViewById(R.id.BtnLogin);
        ETpass = (EditText)findViewById(R.id.ETpass);
        ETemail = (EditText)findViewById(R.id.ETuser);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.i(TAG,"Sesión iniciada con usuario:"+user.getEmail());
                } else {
                    Log.i(TAG,"Sesión cerrada");
                }
            }
        };


        BtnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);

            }
        });

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailR = ETemail.getText().toString();
                String passR = ETpass.getText().toString();
                sigin(emailR,passR);

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser!=null){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("login",false);
                startActivity(intent);
        }

    }


    private void sigin(String email,String password){

        if (!validateForm()) {
            return;
        }

        showProgressDialog(this,"Cargando...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("login",true);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            if(!isOnlineNet())Toast.makeText(LoginActivity.this, "Conexión a internet no disponible", Toast.LENGTH_SHORT).show();
                            else Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }


                        hideProgressDialog();
                    }
                });
    }



    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = ETemail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            ETemail.setError("Obligatorio");
            valid = false;
        } else {
            ETemail.setError(null);
        }

        String password = ETpass.getText().toString();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            ETpass.setError("Mínimo 6 caracteres");
            valid = false;
        } else {
            ETpass.setError(null);
        }

        return valid;
    }






}

