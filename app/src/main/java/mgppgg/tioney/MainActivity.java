package mgppgg.tioney;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private String busqueda,categoria,ultimaKey;
    private Location local,localb;
    private boolean login = false,loading = false;
    private ProgressDialog dialog;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_CODE_ASK_PERMISSIONS2 = 456;
    private static final int NUMERO_ANUNCIOS = 6;
    private int radio2,radio, numeroCargas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = this;
        urls = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        numeroCargas = 1;
        busqueda = "";
        categoria = "Todas las categorías";
        dialog = new ProgressDialog(context);
        dialog.setMessage("Cargando anuncios..");
        login = getIntent().getExtras().getBoolean("login");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        radio2 = 0;
        radio = 10;
        local = new Location("LocationA");
        localb = new Location("LocationB");
        query = database.child("Anuncios1").orderByKey();

        filtro = (TextView) findViewById(R.id.TVfiltro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        rv = (RecyclerView) findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        LayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(LayoutManager);
        Adapter = new MyAdapter(urls,context,dialog);

        permiso();

        rv.setAdapter(Adapter);
       rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastCompletelyVisibleItemPosition = 0;

                lastCompletelyVisibleItemPosition = LayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastCompletelyVisibleItemPosition == urls.size()-1) {
                    if (!loading) {
                        loading = true;
                        numeroCargas++;
                       // cargarMas();
                       Log.d("cargarrrrrrrr","massssssssssssss");
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

        if (login) {
            String token = FirebaseInstanceId.getInstance().getToken();
            database.child("Usuarios").child(user.getUid()).child("token").setValue(token);
        }


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                               @Override
                                               public void onRefresh() {
                                                   numeroCargas = 1;
                                                   urls.clear();
                                                   query = database.child("Anuncios1").orderByKey();
                                                   busqueda = "";
                                                   Adapter.limpiar();
                                                  // Adapter = new MyAdapter(urls, context,dialog);
                                                   //rv.setAdapter(Adapter);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_misAnuncios) {

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
        query = database.child("Anuncios1").orderByKey().startAt(ultimaKey);
        numeroCargas++;
        cargar(false);
    }


    public void cargar(boolean mostrarDia) {

        if (mostrarDia) dialog.show();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChildren()){
                    Toast.makeText(MainActivity.this, "No hay más anuncios disponibles", Toast.LENGTH_SHORT).show();
                    numeroCargas--;
                    dialog.dismiss();

                }else {
                    for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        AnunDatabase anun = postSnapshot.getValue(AnunDatabase.class);
                        ultimaKey = postSnapshot.getKey();
                        localb.setLatitude(anun.getLatitud());
                        localb.setLongitude(anun.getLongitud());
                        if (anun.getTitulo().toLowerCase().contains(busqueda.toLowerCase()))
                            if (categoria.equals("Todas las categorías") && radio > (local.distanceTo(localb) / 1000)) {
                                urls.add(anun);
                                //Adapter.notifyItemInserted(urls.size()-1);
                            }
                            else if (radio > (local.distanceTo(localb) / 1000) && categoria.equals(anun.getCategoria())){
                                urls.add(anun);
                                //Adapter.notifyItemInserted(urls.size()-1);
                            }

                        if (urls.size() == NUMERO_ANUNCIOS * numeroCargas) break;

                    }
                    Log.d("wwwwwww","main:"+urls.size());
                    Adapter.actualizar(urls);
                    //Adapter.notifyItemRangeInserted(NUMERO_ANUNCIOS * (numeroCargas-1),NUMERO_ANUNCIOS);


                }
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
                cargar(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

    }

    public void filtro() {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.filtro);
        Button BTNaplicar = (Button) dialog.findViewById(R.id.BTNaplicar);
        Button BTNcancelar = (Button) dialog.findViewById(R.id.BTNcancelar);
        final TextView TVdistancia = (TextView) dialog.findViewById(R.id.TV_fil_numdis);
        final TextView mm = (TextView) dialog.findViewById(R.id.TV_fil_distancia);
        final SeekBar distancia = (SeekBar) dialog.findViewById(R.id.SBdistancia);
        Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);

        distancia.setMax(3);
        distancia.setProgress(2);

        distancia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mm.setText("Distancia:   <");
                switch (progress){
                    case 0:
                        progress = 1;
                        radio2 = 1;
                        break;
                    case 1:
                        progress = 5;
                        radio2 = 5;
                        break;
                    case 2:
                        progress = 10;
                        radio2 = 10;
                        break;
                    case 3:
                        progress = 10;
                        mm.setText("Distancia:   >");
                        radio2 = 1000;
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BTNaplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urls.clear();
                numeroCargas = 1;
                if(radio2!=0)radio = radio2;
                query = database.child("Anuncios1").orderByKey();
                cargar(true);
                dialog.dismiss();

            }
        });

        BTNcancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }


    @TargetApi(24)
    public void permiso() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS2);

        } else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {

            localizacion();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    localizacion();

                } else {

                    Toast.makeText(context, "Para un buen funcionamiento debe aceptar", Toast.LENGTH_SHORT).show();

                }

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
