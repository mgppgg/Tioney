package mgppgg.tioney;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ObjectInput;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import recycler_view.Anuncio;
import recycler_view.MyAdapter;

public class MisAnuncios extends BaseActivity {

    private DatabaseReference database;
    private StorageReference filepathFoto0,filepathDescripcion;
    private FirebaseStorage storage;
    private ListView listOfAnun;
    private ArrayList<Anuncio> anuncios;
    private FirebaseListAdapter <Anuncio> adapter;
    private FirebaseUser user;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_anuncios);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        listOfAnun= (ListView)findViewById(R.id.list_anun);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_listaAnun);
        anuncios = new ArrayList<>();

        mostrar_anun(database.child("Usuarios").child(user.getUid()).child("Anuncios"));


        listOfAnun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), Publicar.class);
                intent.putExtra("Anuncio",anuncios.get(position));
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
        adapter = new FirebaseListAdapter<Anuncio>(this, Anuncio.class, R.layout.card, ref) {
            @Override
            protected void populateView(View v, final Anuncio anun, final int position) {

                anuncios.add(anun);
                final TextView titulo = (TextView)v.findViewById(R.id.TVtituloCard);
                final TextView descripcion = (TextView)v.findViewById(R.id.TVdescripcionCard);
                final TextView precio = (TextView)v.findViewById(R.id.TVprecioCard);
                ImageView foto =(ImageView)v.findViewById(R.id.IVfotoCard);
                filepathFoto0 = storage.getReferenceFromUrl(anun.getUrl()+"Foto0");
                filepathDescripcion =  storage.getReferenceFromUrl(anun.getUrl() + "Descripcion");

                if(anun.getFotos()>0)Glide.with(getBaseContext()).using(new FirebaseImageLoader()).load(filepathFoto0).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(foto);

                filepathDescripcion.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @TargetApi(24)
                    public void onSuccess(byte[] bytes) {
                        // Use the bytes to display the image
                        String des = new String(bytes, StandardCharsets.UTF_8);

                        titulo.setText(anun.getTitulo());
                        descripcion.setText(des);
                        precio.setText(anun.getPrecio());
                        anun.setDescripcion(des);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getBaseContext(), "Error al descargar los anuncios", Toast.LENGTH_SHORT).show();
                    }
                });




            }
        };

        if(isOnlineNet())listOfAnun.setAdapter(adapter);
        else
            snackBar("Sin conexión a internet");

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  //boton de atras
        if(item.getItemId()==android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }

}
