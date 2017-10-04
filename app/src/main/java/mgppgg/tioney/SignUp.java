package mgppgg.tioney;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by manug on 04/10/2017.
 */

public class SignUp extends Activity {


    DatabaseHelper helper = new DatabaseHelper(this);
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
    }

    public void onSignUpClick(View v){
        if(v.getId() == R.id.Bsignupbutton)
        {
            EditText name = (EditText)findViewById(R.id.TFname);
            EditText email = (EditText)findViewById(R.id.TFemail);
            EditText uname = (EditText)findViewById(R.id.TFuname);
            EditText pass1 = (EditText)findViewById(R.id.TFpass1);
            EditText pass2 = (EditText)findViewById(R.id.TFpass2);

            String namestr = name.getText().toString();
            String emailstr = name.getText().toString();
            String unamestr = name.getText().toString();
            String pass1str = name.getText().toString();
            String pass2str = name.getText().toString();

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
            }

        }
    }

}
