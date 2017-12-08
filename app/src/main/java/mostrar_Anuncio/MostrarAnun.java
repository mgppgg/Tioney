package mostrar_Anuncio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import mgppgg.tioney.Chat;
import mgppgg.tioney.MainActivity;
import mgppgg.tioney.Publicar;
import mgppgg.tioney.R;
import mostrar_Anuncio.Adaptador_ViewPager;
import recycler_view.Anuncio;

/**
 * Created by pablich on 01/12/2017.
 */

public class MostrarAnun extends AppCompatActivity {

    TextView titulo,descripcion,precio;
    private FirebaseStorage storage;
    Button contactar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar);

        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        contactar = (Button)findViewById(R.id.BTNcontactar);

        Anuncio anun = (Anuncio) getIntent().getSerializableExtra("Anuncio");

        titulo = (TextView) findViewById(R.id.TVtituloMostrar);
        descripcion = (TextView) findViewById(R.id.TVdescripcionMostrar);
        precio = (TextView) findViewById(R.id.TVprecioMostrar);

        titulo.setText(anun.getTitulo());
        descripcion.setText(anun.getDescripcion());
        precio.setText(anun.getPrecio());


        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
        Adaptador_ViewPager adapterView = new Adaptador_ViewPager(this,anun,storage);
        mViewPager.setAdapter(adapterView);


        contactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),Chat.class);
                intent.putExtra("Id",user.getUid());
                startActivity(intent);
            }
        });



    }

}
