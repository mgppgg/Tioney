package mgppgg.tioney;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pablich on 25/11/2017.
 */

public class ObtenerDatos {

    private List<Anuncio> list;
    private Context context;
    private RecyclerView rv;
    private String titulo;
    private Anuncio a;
    private StorageReference filepathTitulo;
    private StorageReference filepathTFoto0;
    private StorageReference storageRef;
    private RecyclerView.Adapter Adapter;
    private RecyclerView.LayoutManager LayoutManager;

    public ObtenerDatos(Context context,RecyclerView rv){
        list = new ArrayList<>();
        this.context = context;
        this.rv = rv;
        a = new Anuncio();
    }


    public void obtener(){

        storageRef = FirebaseStorage.getInstance().getReference();
        filepathTitulo = storageRef.child("Anuncios/" + "Titulo");
        filepathTFoto0 = storageRef.child("Anuncios/" + "Foto0");
        list.clear();


        filepathTitulo.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @TargetApi(24)
            public void onSuccess(byte[] bytes) {
                // Use the bytes to display the image
                titulo = new String(bytes, StandardCharsets.UTF_8);
                a.setTitulo(titulo);
                a.setIma(filepathTFoto0,null,null,null);
                list.add(a);

                Adapter = new MyAdapter(list, context);
                rv.setAdapter(Adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "No hay anuncios disponibles", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
