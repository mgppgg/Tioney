package mgppgg.tioney;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import recycler_view.ObtenerDatos;

/**
 * Created by pablich on 01/01/2018.
 */

public class Buscar extends BaseActivity {

    private Query query;
    private ObtenerDatos ob;
    private DatabaseReference database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();


       // ob = new ObtenerDatos(this);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String busqueda = intent.getStringExtra(SearchManager.QUERY);
            query = database.child("Anuncios1").orderByChild("titulo").equalTo(busqueda);
            //ob.obtener(true,query);
        }
    }
}
