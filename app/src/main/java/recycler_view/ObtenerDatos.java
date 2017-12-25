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

import mgppgg.tioney.BaseActivity;

/**
 * Created by pablich on 25/11/2017.
 */

public class ObtenerDatos extends BaseActivity {

    private List<Anuncio> list;
    private List<String> urls;
    private Context context;
    private ProgressDialog progress;
    private RecyclerView rv;
    private String titulo,descripcion,precio;
    private Anuncio a;
    private StorageReference filepathTitulo,filepathDescripcion,filepathPrecio;
    private FirebaseStorage storage;
    private DatabaseReference database;
    private RecyclerView.Adapter Adapter;
    private String paths[];

    public ObtenerDatos(Context context,RecyclerView rv){
        list = new ArrayList<>();
        urls = new ArrayList<>();
        this.context = context;
        this.rv = rv;
        a = new Anuncio();
        paths = new String[4];
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
    }

        /*
    @Override
    protected Void doInBackground(Void... voids) {

        return null;
    }*/

/*
    @Override
    protected void onPostExecute(Void result) {

        try {
            Log.d("data123", "hola1");
            while(seguir){
                Adapter = new MyAdapter(list, context);
                rv.setAdapter(Adapter);
                progress.dismiss();
            }


        } catch (Exception e) {

            e.printStackTrace();
        }
    }*/

    public void obtener(boolean b){

       if(b) showProgressDialog(context);

        list.clear();
        urls.clear();

        Query query = database.child("Anuncios1");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Log.d("data123", postSnapshot.getValue().toString());
                    //Log.d("data123", "tam "+ c[0]);
                    urls.add(postSnapshot.getValue().toString());
                    Log.d("data123",postSnapshot.getValue().toString());
                }

                Adapter = new MyAdapter(urls, context);
                rv.setAdapter(Adapter);
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Error al descargar los anuncios", Toast.LENGTH_SHORT).show();
            }

        });


    }

}
