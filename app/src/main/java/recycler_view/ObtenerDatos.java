package recycler_view;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mgppgg.tioney.AnunDatabase;
import mgppgg.tioney.BaseActivity;

/**
 * Created by pablich on 25/11/2017.
 */

public class ObtenerDatos extends BaseActivity {

    private List<AnunDatabase> urls;
    private Context context;
    private RecyclerView rv;
    private Anuncio a;
    private DatabaseReference database;
    private RecyclerView.Adapter Adapter;

    public ObtenerDatos(Context context,RecyclerView rv){
        urls = new ArrayList<>();
        this.context = context;
        this.rv = rv;
        database = FirebaseDatabase.getInstance().getReference();
    }


    public void obtener(boolean b){

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Cargando anuncios..");

        if(b) dialog.show();

        urls.clear();

        Query query = database.child("Anuncios1");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Log.d("data123", postSnapshot.getValue().toString());
                    //Log.d("data123", "tam "+ c[0]);
                    AnunDatabase anun = postSnapshot.getValue(AnunDatabase.class);
                    urls.add(anun);
                }

                Adapter = new MyAdapter(urls, context,dialog);
                rv.setAdapter(Adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Error al descargar los anuncios", Toast.LENGTH_SHORT).show();
            }

        });


    }

}
