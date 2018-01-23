package mostrar_Anuncio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

import mgppgg.tioney.Chat;
import mgppgg.tioney.R;
import recycler_view.Anuncio;

/**
 * Created by pablich on 01/12/2017.
 */

public class MostrarAnun extends AppCompatActivity {

    TextView titulo,descripcion,precio;
    private FirebaseStorage storage;
    private DatabaseReference database;
    private FirebaseUser user;
    Button contactar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance().getReference();


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        final FirebaseUser user = mAuth.getCurrentUser();


        contactar = (Button)findViewById(R.id.BTNcontactar);

        final Anuncio anun = (Anuncio) getIntent().getSerializableExtra("Anuncio");

        titulo = (TextView) findViewById(R.id.TVtituloMostrar);
        descripcion = (TextView) findViewById(R.id.TVdescripcionMostrar);
        precio = (TextView) findViewById(R.id.TVprecioMostrar);

        titulo.setText(anun.getTitulo());
        descripcion.setText(anun.getDescripcion());
        precio.setText(anun.getPrecio());

        if (user != null && user.getUid().equals(anun.getUID())) contactar.setEnabled(false);


        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
        Adaptador_ViewPager adapterView = new Adaptador_ViewPager(this,anun,storage);
        mViewPager.setAdapter(adapterView);


        contactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),Chat.class);
                intent.putExtra("anuncio",anun);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  //boton de atras
        if(item.getItemId()==android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }
}
