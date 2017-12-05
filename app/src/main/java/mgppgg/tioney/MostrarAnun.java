package mgppgg.tioney;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import recycler_view.Anuncio;

/**
 * Created by pablich on 01/12/2017.
 */

public class MostrarAnun extends AppCompatActivity {

    ImageView selectedImage;
    TextView titulo,descripcion,precio;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar);

        storage = FirebaseStorage.getInstance();

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



    }

}
