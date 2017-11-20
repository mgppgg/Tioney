package mgppgg.tioney;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pablich on 16/11/2017.
 */

public class content_main extends AppCompatActivity{

    private RecyclerView RecyclerView;
    private RecyclerView.Adapter Adapter;
    List<Anuncio> list;
    private RecyclerView.LayoutManager mLayoutManager;
    private StorageReference storageRef;
    private StorageReference filepathTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        RecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        storageRef = FirebaseStorage.getInstance().getReference();
        filepathTitulo = storageRef.child("Anuncios/" + "Titulo");
        list = new ArrayList<>();


        filepathTitulo.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @TargetApi(24)
            public void onSuccess(byte[] bytes) {
                // Use the bytes to display the image
                String str = new String(bytes, StandardCharsets.UTF_8);
                list.add(new Anuncio(str));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(content_main.this, "Error al descargar anuncios", Toast.LENGTH_SHORT).show();
            }
        });


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        RecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        RecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        Adapter = new MyAdapter(list,getApplicationContext());
        RecyclerView.setAdapter(Adapter);


    }
}

