package mostrar_Anuncio;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import mgppgg.tioney.AnunDatabase;
import mgppgg.tioney.Chat;
import mgppgg.tioney.R;


/**
 * Created by pablich on 01/12/2017.
 */

public class MostrarAnun extends AppCompatActivity implements OnMapReadyCallback {

    TextView titulo,descripcion,precio;
    private FirebaseStorage storage;
    Button contactar;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private AnunDatabase anun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar);

        storage = FirebaseStorage.getInstance();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);


        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        anun = new AnunDatabase();


        contactar = (Button)findViewById(R.id.BTNcontactar);
        ImageView transparentImageView = (ImageView) findViewById(R.id.IVtransparente);
        final ScrollView scroll = (ScrollView)findViewById(R.id.SVmostrar);

        anun = (AnunDatabase) getIntent().getSerializableExtra("Anuncio");

        titulo = (TextView) findViewById(R.id.TVtituloMostrar);
        descripcion = (TextView) findViewById(R.id.TVdescripcionMostrar);
        precio = (TextView) findViewById(R.id.TVprecioMostrar);

        titulo.setText(anun.getTitulo());
        descripcion.setText(anun.getDescripcion());
        precio.setText(anun.getPrecio());

        if (user != null && user.getUid().equals(anun.getUID())) contactar.setEnabled(false);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(anun.getUsuario());
        }


        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
        Adaptador_ViewPager adapterView = new Adaptador_ViewPager(this,anun,storage);
        mViewPager.setAdapter(adapterView);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_dots);
        tabLayout.setupWithViewPager(mViewPager, true);


        contactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),Chat.class);
                intent.putExtra("anuncio",anun);
                startActivity(intent);
            }
        });


        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scroll.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scroll.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scroll.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  //boton de atras
        if(item.getItemId()==android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng pos = new LatLng(anun.getLatitud(), anun.getLongitud());
        //mMap.addMarker(new MarkerOptions().position(pos).title(anun.getUsuario())).
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 13));

        mMap.addCircle(new CircleOptions()
                        .center(pos)
                        .radius(800)
                        .strokeColor(Color.RED)
                        .fillColor(0x220000FF)
                        .strokeWidth(5)
        );
    }
}
