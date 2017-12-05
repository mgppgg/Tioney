package recycler_view;

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
    private String titulo,descripcion,precio;
    private Anuncio a;
    private StorageReference filepathTitulo,filepathDescripcion,filepathPrecio;
    private StorageReference filepathTFoto0,filepathTFoto1,filepathTFoto2,filepathTFoto3;
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
        filepathDescripcion = storageRef.child("Anuncios/" + "Descripcion");
        filepathPrecio = storageRef.child("Anuncios/" + "Precio");
        filepathTFoto0 = storageRef.child("Anuncios/" + "Foto0");
        filepathTFoto1 = storageRef.child("Anuncios/" + "Foto1");
        filepathTFoto2 = storageRef.child("Anuncios/" + "Foto2");
        filepathTFoto3 = storageRef.child("Anuncios/" + "Foto3");
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
                                a.setIma(filepathTFoto0.toString(),filepathTFoto1.toString(),filepathTFoto2.toString(),filepathTFoto3.toString());
                                Log.i("anun",filepathTFoto0.toString());
                                list.add(a);

                                Adapter = new MyAdapter(list, context);
                                rv.setAdapter(Adapter);

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
