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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import mgppgg.tioney.BaseActivity;

/**
 * Created by pablich on 25/11/2017.
 */

public class ObtenerDatos extends BaseActivity{

    private List<Anuncio> list;
    private Context context;
    private RecyclerView rv;
    private String titulo,descripcion,precio;
    private Anuncio a;
    private StorageReference filepathTitulo,filepathDescripcion,filepathPrecio;
    private StorageReference storageRef;
    private RecyclerView.Adapter Adapter;
    private String paths[];

    public ObtenerDatos(Context context,RecyclerView rv){
        list = new ArrayList<>();
        this.context = context;
        this.rv = rv;
        a = new Anuncio();
        paths = new String[4];
    }


    public void obtener(){
        showProgressDialog(context);
        storageRef = FirebaseStorage.getInstance().getReference();
        filepathTitulo = storageRef.child("Anuncios/" + "Titulo");
        filepathDescripcion = storageRef.child("Anuncios/" + "Descripcion");
        filepathPrecio = storageRef.child("Anuncios/" + "Precio");

        for(int b =0;b<4;b++)paths[b]= storageRef.child("Anuncios/" + "Foto" + b).toString();

        for(int i =0;i<4;i++) {
            final int finalI = i;
            storageRef.child("Anuncios/" + "Foto" + i).getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    paths[finalI] = null;
                }
            });
        }



        list.clear();

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
                                a.setIma(paths[0],paths[1],paths[2],paths[3]);
                                list.add(a);

                                Adapter = new MyAdapter(list, context);
                                rv.setAdapter(Adapter);

                                hideProgressDialog();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {}
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {}
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "Error al descargar los anuncios", Toast.LENGTH_SHORT).show();
            }
        });









    }
}
