package mgppgg.tioney;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Pablich on 11/02/2018.
 */

public class OlvidoContra extends  BaseActivity {

    private EditText ETemail;
    private Button BTNenviar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.olvido_contra);

        ETemail = (EditText)findViewById(R.id.ETolvido);
        BTNenviar = (Button)findViewById(R.id.BTNolvido);
        mAuth = FirebaseAuth.getInstance();


        BTNenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = ETemail.getText().toString();

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(OlvidoContra.this, "Correo enviado a: "+ email,Toast.LENGTH_LONG).show();
                            finish();
                        }else Toast.makeText(OlvidoContra.this, "Error al enviar correo",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


    }
}
