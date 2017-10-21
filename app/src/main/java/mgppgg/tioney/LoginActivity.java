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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button BtnRegistrarse ;
    private Button BtnLogin ;
    private EditText ETpass;
    private EditText ETemail;
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
        ETemail = (EditText)findViewById(R.id.ETuser);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Log.i("SESION","Sesión iniciada con usuario:"+user.getEmail());
                }else{
                    Log.i("SESION","Sesión cerrada");
                }

            }
        };


        BtnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailI = ETemail.getText().toString();
                String passI = ETpass.getText().toString();
                iniciarSesion(emailI,passI);

            }
        });

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailR = ETemail.getText().toString();
                String passR = ETpass.getText().toString();
                registrar(emailR,passR);

            }
        });







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

    private void registrar(String email, String pass){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i("SESION","Usuario creado correctamente");
                }else{
                    Log.e("SESION",task.getException().getMessage()+"");
                }
            }
        });
    }

    private void iniciarSesion(String email, String pass){

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener!=null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }



}

