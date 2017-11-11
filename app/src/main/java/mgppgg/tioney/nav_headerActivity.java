package mgppgg.tioney;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by pablich on 31/10/2017.
 */

public class nav_headerActivity extends AppCompatActivity {

    public TextView TVemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);

        TVemail = (TextView)findViewById(R.id.TVemail);

    }

    public void correo(FirebaseUser user){

        TVemail.setText(user.getEmail());
    }
}
