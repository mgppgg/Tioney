package mgppgg.tioney;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by manug on 04/10/2017.
 */

public class SignUp extends LoginActivity {

    private Button BtnCancelar;
    private Button BtnAceptar;
    private EditText ETpass,ETpass2,ETemail,ETnombre;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference database;
    private static final String TAG = "EmailPasswordReg";


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        ETemail = (EditText)findViewById(R.id.TFemail);
        ETpass = (EditText)findViewById(R.id.TFpass1);
        ETpass2 = (EditText)findViewById(R.id.TFpass2);
        ETnombre = (EditText)findViewById(R.id.ETnombre);
        BtnCancelar = (Button)findViewById(R.id.BCancelar);
        BtnAceptar = (Button)findViewById(R.id.Bsignupbutton);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

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

        BtnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        BtnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailstr = ETemail.getText().toString();
                String pass1str = ETpass.getText().toString();
                String pass2str = ETpass2.getText().toString();
                String nombre = ETnombre.getText().toString();

                if(!isOnlineNet()) Toast.makeText(SignUp.this, "Sin conexión a internet", Toast.LENGTH_SHORT).show();

                else if(!pass1str.equals(pass2str)) {
                    Toast.makeText(SignUp.this, "Las contraseñas no coinciden!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                    createAccount(emailstr,pass1str,nombre);
                    }

            }
        });
    }


    private void createAccount(String email, String password, final String nombre){

        if (!validateForm()) {
            return;
        }

        showProgressDialog(this,"Creando cuenta..");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(nombre).build();
                            user.updateProfile(profileUpdates);

                            String token = FirebaseInstanceId.getInstance().getToken();

                            database.child("Usuarios").child(user.getUid()).setValue(new Usuario(nombre,user.getEmail(),token));


                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                            intent.putExtra("login",false);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Error al registrarse", Toast.LENGTH_SHORT).show();

                        }


                        hideProgressDialog();
                    }
                });

    }



    public boolean validateForm() {
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

        String password2 = ETpass2.getText().toString();
        if (TextUtils.isEmpty(password2) || password2.length() < 6) {
            ETpass2.setError("Mínimo 6 caracteres");
            valid = false;
        } else {
            ETpass2.setError(null);
        }

        String nombre = ETnombre.getText().toString();
        if (TextUtils.isEmpty(nombre)) {
            ETnombre.setError("Obligatorio");
            valid = false;
        } else {
            ETnombre.setError(null);
        }





        return valid;
    }




}
