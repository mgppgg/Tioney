package mgppgg.tioney;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by manug on 04/10/2017.
 */

public class SignUp extends AppCompatActivity {

    private Button BtnCancelar;
    private Button BtnAceptar;
    DatabaseHelper helper = new DatabaseHelper(this);


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        BtnCancelar = (Button)findViewById(R.id.BCancelar);
        BtnAceptar = (Button)findViewById(R.id.Bsignupbutton);

        BtnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        BtnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText name = (EditText)findViewById(R.id.TFname);
                EditText email = (EditText)findViewById(R.id.TFemail);
                EditText uname = (EditText)findViewById(R.id.TFuname);
                EditText pass1 = (EditText)findViewById(R.id.TFpass1);
                EditText pass2 = (EditText)findViewById(R.id.TFpass2);

                String namestr = name.getText().toString();
                String emailstr = email.getText().toString();
                String unamestr = uname.getText().toString();
                String pass1str = pass1.getText().toString();
                String pass2str = pass2.getText().toString();

                if(!pass1str.equals(pass2str))
                {
                    Toast pass = Toast.makeText(SignUp.this , "Las contraseñas no coinciden!" , Toast.LENGTH_SHORT);
                }
                else
                {
                    Contact c = new Contact();
                    c.setName(namestr);
                    c.setEmail(emailstr);
                    c.setUname(unamestr);
                    c.setPass(pass1str);

                    helper.insertContact(c);

                    finish();
                }

            }
        });
    }








}
