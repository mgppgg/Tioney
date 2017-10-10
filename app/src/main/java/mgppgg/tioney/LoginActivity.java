package mgppgg.tioney;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Button BtnRegistrarse ;
    private Button BtnLogin ;
    private EditText ETpass;
    private EditText ETuser;
    private String pass;
    private String user;

    DatabaseHelper helper = new DatabaseHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        BtnRegistrarse = (Button)findViewById(R.id.BtnRegistrarse);
        BtnLogin = (Button)findViewById(R.id.BtnLogin);
        ETpass = (EditText)findViewById(R.id.ETpass);
        ETuser = (EditText)findViewById(R.id.ETuser);


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
        });
    }
}
