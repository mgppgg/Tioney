package mgppgg.tioney;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;

import recycler_view.MyAdapter;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private RecyclerView rv;
    private TextView filtro;
    private DatabaseReference database;
    private LinearLayoutManager LayoutManager;
    private ArrayList<AnunDatabase> urls;
    private FirebaseUser user;
    private MyAdapter Adapter;
    private SwipeRefreshLayout refreshLayout;
    private Query query;
    private Context context;
    private String busqueda,categoria;
    private Location local,localb;
    private boolean login = false,loading = false;
    private long ultimaFecha;
    private ProgressDialog dialog;
    private ProgressBar PBcargar_mas;
    private TextView noAnun;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_CODE_ASK_PERMISSIONS2 = 456;
    private static final int NUMERO_ANUNCIOS = 15;
    private int radio, numeroCargas,posSpinner,posBar;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this,getString(R.string.banner));
        mAdView = (AdView) findViewById(R.id.AVbanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        context = this;
        urls = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        numeroCargas = 1;
        posSpinner = 0;
        posBar = 3;
        busqueda = "";
        categoria = "Todas las categorías";
        PBcargar_mas = (ProgressBar)findViewById(R.id.PBcargar_mas);
        dialog = new ProgressDialog(context);
        dialog.setMessage("Cargando anuncios..");
        login = getIntent().getExtras().getBoolean("login");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        radio = 1000;
        local = new Location("LocationA");
        localb = new Location("LocationB");
        query = database.child("Anuncios1").orderByChild("fecha");

        filtro = (TextView) findViewById(R.id.TVfiltro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        noAnun = (TextView)findViewById(R.id.TVnoAnun2);

        rv = (RecyclerView) findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        LayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(LayoutManager);
        Adapter = new MyAdapter(context,dialog);

        permiso();

        rv.setAdapter(Adapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastCompletelyVisibleItemPosition = 0;

                lastCompletelyVisibleItemPosition = LayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastCompletelyVisibleItemPosition == Adapter.getUltimo()-1 && dy>0) {
                    if (!loading) {
                        loading = true;
                        PBcargar_mas.setVisibility(View.VISIBLE);
                        cargarMas();
                    }
                }

            }
        });

        if (isOnlineNet()) cargar(true);
        else Snack1();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Publicar.class);
                startActivity(intent);
            }
        });

        filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtro();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView Navusuario = (TextView)header.findViewById(R.id.TVnavUsuario);
        TextView Navemail = (TextView)header.findViewById(R.id.TVnavEmail);
        Navusuario.setText(user.getDisplayName());
        Navemail.setText(user.getEmail());

        if (login) {
            String token = FirebaseInstanceId.getInstance().getToken();
            database.child("Usuarios").child(user.getUid()).child("token").setValue(token);
        }


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                               @Override
                                               public void onRefresh() {
                                                   numeroCargas = 1;
                                                   urls.clear();
                                                   query = database.child("Anuncios1").orderByChild("fecha");
                                                   busqueda = "";
                                                   Adapter.limpiar();
                                                   cargar(false);
                                               }
                                           }
        );


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.buscar).getActionView();
        buscar(searchView);

        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_misAnuncios) {

            Intent intent = new Intent(MainActivity.this, MisAnuncios.class);
            startActivity(intent);

        } else if (id == R.id.nav_chat) {

            Intent intent = new Intent(MainActivity.this, Conversaciones.class);
            startActivity(intent);


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_Sesion) {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void cargarMas(){
        query = database.child("Anuncios1").orderByChild("fecha").startAt(ultimaFecha);
        urls.clear();
        numeroCargas++;
        cargar(false);
    }


    public void cargar(boolean mostrarDia) {

        if (mostrarDia) dialog.show();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    AnunDatabase anun = postSnapshot.getValue(AnunDatabase.class);
                    assert anun != null;
                    ultimaFecha = anun.getFecha();
                    localb.setLatitude(anun.getLatitud());
                    localb.setLongitude(anun.getLongitud());

                    if (anun.getTitulo().toLowerCase().contains(busqueda.toLowerCase()))
                        if (categoria.equals("Todas las categorías") && radio > (local.distanceTo(localb) / 1000)) urls.add(anun);
                        else if (radio > (local.distanceTo(localb) / 1000) && categoria.equals(anun.getCategoria())) urls.add(anun);

                    if (urls.size() == NUMERO_ANUNCIOS * numeroCargas) break;

                }

                if(urls.size()==0)noAnun.setVisibility(View.VISIBLE);
                else noAnun.setVisibility(View.GONE);

                if(loading){
                    urls.remove(0);
                    loading = false;
                    PBcargar_mas.setVisibility(View.GONE);
                }

                Adapter.actualizar(urls);
                refreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Error al descargar los anuncios", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void buscar(SearchView search) {

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                numeroCargas = 1;
                busqueda = text;
                query = database.child("Anuncios1").orderByChild("titulo");
                urls.clear();
                Adapter.limpiar();
                cargar(true);

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

    }

    public void filtro() {

        final Dialog Dfiltro = new Dialog(context);
        Dfiltro.setContentView(R.layout.filtro);
        Button BTNaplicar = (Button) Dfiltro.findViewById(R.id.BTNaplicar);
        Button BTNcancelar = (Button) Dfiltro.findViewById(R.id.BTNcancelar);
        final TextView TVdistancia = (TextView) Dfiltro.findViewById(R.id.TV_fil_numdis);
        final TextView mm = (TextView) Dfiltro.findViewById(R.id.TV_fil_distancia);
        final SeekBar distancia = (SeekBar) Dfiltro.findViewById(R.id.SBdistancia);
        Spinner spinner = (Spinner) Dfiltro.findViewById(R.id.spinner);

        distancia.setMax(3);
        distancia.setProgress(posBar);
        distancia.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);
        if(radio==1000){
            TVdistancia.setText("10");
            mm.setText("Distancia:   >");
        }
        else TVdistancia.setText(""+radio);

        distancia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mm.setText("Distancia:   <");
                posBar= progress;
                switch (progress){
                    case 0:
                        progress = 1;
                        radio = 1;
                        break;
                    case 1:
                        progress = 5;
                        radio = 5;
                        break;
                    case 2:
                        progress = 10;
                        radio = 10;
                        break;
                    case 3:
                        progress = 10;
                        mm.setText("Distancia:   >");
                        radio= 1000;
                        break;
                }
                TVdistancia.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this, R.array.categorias, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);
        spinner.setSelection(posSpinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria = parent.getItemAtPosition(position).toString();
                posSpinner = position;
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BTNaplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numeroCargas = 1;
                query = database.child("Anuncios1").orderByChild("fecha");
                urls.clear();
                Adapter.limpiar();
                cargar(true);
                Dfiltro.dismiss();

            }
        });

        BTNcancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dfiltro.dismiss();
            }
        });

        Dfiltro.show();


    }


    @TargetApi(24)
    public void permiso() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS2);

        } else {

            localizacion();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    localizacion();
                    Toast.makeText(this, "Deslice hacia abajo para actualizar", Toast.LENGTH_SHORT).show();
                }

                else Toast.makeText(context, "Para un buen funcionamiento debe aceptar", Toast.LENGTH_SHORT).show();

            }

        }
    }

    public void localizacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                local = location;
                            }
                        }
                    });
        }
    }


    public void Snack1(){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Sin conexión a internet",  Snackbar.LENGTH_INDEFINITE).setAction("Action", null);
        View sbView = snackbar.getView();
        snackbar.setActionTextColor(Color.BLACK);
        sbView.setBackgroundColor(Color.RED);
        snackbar.setAction("ACTUALIZAR", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnlineNet())cargar(true);
                else Snack2();
            }
        });

        snackbar.show();
    }

    public void Snack2(){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Sin conexión a internet",  Snackbar.LENGTH_INDEFINITE).setAction("Action", null);
        View sbView = snackbar.getView();
        snackbar.setActionTextColor(Color.BLACK);
        sbView.setBackgroundColor(Color.RED);
        snackbar.setAction("ACTUALIZAR", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnlineNet())cargar(true);
                else Snack1();
            }
        });

        snackbar.show();
    }


}
