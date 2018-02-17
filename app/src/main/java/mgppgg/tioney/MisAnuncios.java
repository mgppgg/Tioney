package mgppgg.tioney;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class MisAnuncios extends BaseActivity {

    private DatabaseReference database;
    private StorageReference filepathFoto0;
    private FirebaseStorage storage;
    private ListView listOfAnun;
    private ArrayList<AnunDatabase> anuncios;
    private FirebaseListAdapter <AnunDatabase> adapter;
    private FirebaseUser user;
    private TextView noAnun;
    private SwipeRefreshLayout refreshLayout;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_anuncios);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mAdView = (AdView) findViewById(R.id.AVbannerMisAnuncios);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        listOfAnun= (ListView)findViewById(R.id.list_anun);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_listaAnun);
        anuncios = new ArrayList<>();
        noAnun = (TextView)findViewById(R.id.TVnoAnun);

        mostrar_anun(database.child("Usuarios").child(user.getUid()).child("Anuncios"));


        listOfAnun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), Publicar.class);
                intent.putExtra("Anuncio",anuncios.get(position));
                intent.putExtra("key",adapter.getRef(position).getKey());
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                   @Override
                   public void onRefresh() {
                        anuncios.clear();
                        mostrar_anun(database.child("Usuarios").child(user.getUid()).child("Anuncios"));
                        adapter.notifyDataSetChanged();
                       ( new Handler()).postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               refreshLayout.setRefreshing(false);
                           }
                       }, 2500);
                   }
               }
        );

    }


    public void mostrar_anun(DatabaseReference ref){
        adapter = new FirebaseListAdapter<AnunDatabase>(this, AnunDatabase.class, R.layout.card, ref) {
            @Override
            protected void populateView(View v, final AnunDatabase anun, final int position) {
                noAnun.setVisibility(View.GONE);
                anuncios.add(anun);
                final TextView titulo = (TextView)v.findViewById(R.id.TVtituloCard);
                final TextView descripcion = (TextView)v.findViewById(R.id.TVdescripcionCard);
                final TextView precio = (TextView)v.findViewById(R.id.TVprecioCard);
                ImageView foto =(ImageView)v.findViewById(R.id.IVfotoCard);
                filepathFoto0 = storage.getReferenceFromUrl(anun.getUrl()+"Foto0");



                if(anun.getFotos()>0)Glide.with(getBaseContext()).using(new FirebaseImageLoader()).load(filepathFoto0).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(foto);
                else foto.setImageResource(R.drawable.logo_fondonegro);

                titulo.setText(anun.getTitulo());
                descripcion.setText(anun.getDescripcion());
                precio.setText(anun.getPrecio());




            }
        };

        if(isOnlineNet())listOfAnun.setAdapter(adapter);
        else
            snackBar("Sin conexión a internet");

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }

}
