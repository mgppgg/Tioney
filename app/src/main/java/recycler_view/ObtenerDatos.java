package recycler_view;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;
import java.util.Map;

import mgppgg.tioney.BaseActivity;

/**
 * Created by pablich on 25/11/2017.
 */

public class ObtenerDatos extends BaseActivity{

    private List<Anuncio> list;
    private List<String> urls;
    private Context context;
    private RecyclerView rv;
    private String titulo,descripcion,precio;
    private Anuncio a;
    private StorageReference filepathTitulo,filepathDescripcion,filepathPrecio;
    private StorageReference storageRef;
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
    }


    public void obtener(final boolean p){
        if(p)showProgressDialog(context);
        obtenerUrls();
        list.clear();

        for(int c=0; c<urls.size();c++) {
            storageRef = FirebaseStorage.getInstance().getReference();
            filepathTitulo = storageRef.child(urls.get(c) + "Titulo");
            filepathDescripcion = storageRef.child(urls.get(c) + "Descripcion");
            filepathPrecio = storageRef.child(urls.get(c) + "Precio");

            for (int b = 0; b < 4; b++)
                paths[b] = storageRef.child(urls.get(c) + "Foto" + b).toString();

            for (int i = 0; i < 4; i++) {
                final int finalI = i;
                storageRef.child("Anuncios/" + "Foto" + i).getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        paths[finalI] = null;
                    }
                });
            }

            filepathTitulo.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @TargetApi(24)
                public void onSuccess(byte[] bytes) {
                    // Use the bytes to display the image
                    titulo = new String(bytes, StandardCharsets.UTF_8);
                    a.setTitulo(titulo);

                    filepathDescripcion.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @TargetApi(24)
                        public void onSuccess(byte[] bytes) {
                            // Use the bytes to display the image
                            descripcion = new String(bytes, StandardCharsets.UTF_8);
                            a.setDescripcion(descripcion);


                            filepathPrecio.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @TargetApi(24)
                                public void onSuccess(byte[] bytes) {
                                    // Use the bytes to display the image
                                    precio = new String(bytes, StandardCharsets.UTF_8);
                                    a.setPrecio(precio);
                                    a.setIma(paths[0], paths[1], paths[2], paths[3]);
                                    list.add(a);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(context, "Error al descargar los anuncios", Toast.LENGTH_SHORT).show();
                }
            });


        }

        Adapter = new MyAdapter(list, context);
        rv.setAdapter(Adapter);
        hideProgressDialog();

    }

    public void obtenerUrls(){

        Map<String, Object> map = new HashMap<>();
        Query myTopPostsQuery = database.child("Anuncios1").limitToFirst(10);
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    urls.add(dataSnapshot.child(key).child().getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        });

    }
}
