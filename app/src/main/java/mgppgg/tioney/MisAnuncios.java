package mgppgg.tioney;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ObjectInput;
import java.util.ArrayList;
import java.util.Objects;

import recycler_view.MyAdapter;
import recycler_view.ObtenerDatos;

public class MisAnuncios extends BaseActivity {

    private DatabaseReference database;
    private FirebaseUser user;
    private RecyclerView rv;
    private RecyclerView.LayoutManager LayoutManager;
    private ObtenerDatos ob;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_anuncios);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        database = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        rv = (RecyclerView)findViewById(R.id.rv_misAnuncios);
        rv.setHasFixedSize(true);
        LayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(LayoutManager);
        ob = new ObtenerDatos(this,rv);
        query = database.child("Usuarios").child(user.getUid()).child("Anuncios");
        ob.obtener(true,query);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  //boton de atras
        if(item.getItemId()==android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }
}
