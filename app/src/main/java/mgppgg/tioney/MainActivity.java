package mgppgg.tioney;

import android.*;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
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
import java.util.Objects;

import recycler_view.MyAdapter;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private RecyclerView rv;
    private TextView filtro;
    private DatabaseReference database;
    private RecyclerView.LayoutManager LayoutManager;
    private ArrayList<AnunDatabase> urls;
    private FirebaseUser user;
    private RecyclerView.Adapter Adapter;
    private SwipeRefreshLayout refreshLayout;
    private Query query;
    private Context context;
    private String busqueda;
    private Location local;
    private boolean login = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_CODE_ASK_PERMISSIONS2 = 456;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = this;
        urls = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        query = database.child("Anuncios1");
        busqueda = "";
        login = getIntent().getExtras().getBoolean("login");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        filtro = (TextView) findViewById(R.id.TVfiltro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        rv = (RecyclerView) findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        LayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(LayoutManager);

        if (isOnlineNet()) obtener(true, query);
        else Snack1();

        permiso();

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
                                                   obtener(false, query);
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


    public void obtener(boolean dia, Query query) {

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Cargando anuncios..");

        if (dia) dialog.show();

        urls.clear();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    AnunDatabase anun = postSnapshot.getValue(AnunDatabase.class);
                    if (!Objects.equals(anun.getUID(), user.getUid()) && anun.getTitulo().toLowerCase().contains(busqueda.toLowerCase()))
                        urls.add(anun);

                }

                refreshLayout.setRefreshing(false);
                Adapter = new MyAdapter(urls, context, dialog);
                rv.setAdapter(Adapter);
                busqueda = "";
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
                busqueda = text;
                query = database.child("Anuncios1").orderByChild("titulo");
                obtener(true, query);

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
        int desde, hasta;
        Button BTNaplicar = (Button) dialog.findViewById(R.id.BTNaplicar);
        Button BTNcancelar = (Button) dialog.findViewById(R.id.BTNcancelar);
        final EditText ETdesde = (EditText) dialog.findViewById(R.id.ETdesde);
        final EditText EThasta = (EditText) dialog.findViewById(R.id.EThasta);
        final TextView TVdistancia = (TextView) dialog.findViewById(R.id.TV_fil_numdis);
        final TextView mm = (TextView) dialog.findViewById(R.id.TV_fil_distancia);
        final SeekBar distancia = (SeekBar) dialog.findViewById(R.id.SBdistancia);
        Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);

        distancia.setMax(3);
        distancia.setProgress(1);
        if (!ETdesde.getText().toString().isEmpty())
            desde = Integer.parseInt(ETdesde.getText().toString());
        if (!EThasta.getText().toString().isEmpty())
            hasta = Integer.parseInt(EThasta.getText().toString());

        distancia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mm.setText("Distancia:   <");
                if (progress == 0) progress = 1;
                else if (progress == 1) progress = 5;
                else if (progress == 2) progress = 10;
                else if (progress == 3) {
                    progress = 10;
                    mm.setText("Distancia:   >");
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


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                } else {
                    // Your code to process the selection
                }
            }
        });


        BTNaplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                query = database.child("Anuncios1").orderByChild("titulo");
                obtener(true, query);

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
                                Log.d("local",location.toString());
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
                if(isOnlineNet())obtener(true,query);
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
                if(isOnlineNet())obtener(true,query);
                else Snack1();
            }
        });

        snackbar.show();
    }


}
