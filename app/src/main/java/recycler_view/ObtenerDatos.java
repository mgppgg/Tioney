package recycler_view;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Objects;

import mgppgg.tioney.AnunDatabase;
import mgppgg.tioney.BaseActivity;
import mgppgg.tioney.R;

import static android.support.constraint.R.id.parent;

/**
 * Created by pablich on 25/11/2017.
 */

public class ObtenerDatos extends BaseActivity {

    private List<AnunDatabase> urls;
    private Context context;
    private RecyclerView rv;
    private String uid;
    private RecyclerView.LayoutManager LayoutManager;
    private RecyclerView.Adapter Adapter;
    private FirebaseUser user;

    public ObtenerDatos(Context context,RecyclerView recycler){
        urls = new ArrayList<>();
        this.context = context;
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        rv = recycler;

       // LayoutInflater inflater = LayoutInflater.from(context);
       // View v = inflater.inflate(R.layout.content_main,null);
    }


    public void obtener(boolean dia, Query query){

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Cargando anuncios..");

        if(dia) dialog.show();

        urls.clear();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    AnunDatabase anun = postSnapshot.getValue(AnunDatabase.class);
                    if(!Objects.equals(anun.getUID(), uid))urls.add(anun);

                }

                Adapter = new MyAdapter(urls, context,dialog);
                rv.setAdapter(Adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

}
