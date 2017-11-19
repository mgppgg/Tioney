package mgppgg.tioney;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private StorageReference filepathTFoto0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        RecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        storageRef = FirebaseStorage.getInstance().getReference();
        filepathTitulo = storageRef.child("Anuncios/" + "Titulo");
        filepathTFoto0 = storageRef.child("Anuncios/" + "Foto0");

        list = new ArrayList<>();

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        RecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        RecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        //mAdapter = new MyAdapter(list,getApplicationContext());
        //RecyclerView.setAdapter(mAdapter);

        storageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);

                    list.add(imageUploadInfo);
                }

                Adapter = new MyAdapter(list,getApplicationContext());

                RecyclerView.setAdapter(Adapter);

                // Hiding the progress dialog.
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
                progressDialog.dismiss();

            }
        });



    }
}

