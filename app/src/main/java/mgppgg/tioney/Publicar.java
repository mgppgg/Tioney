package mgppgg.tioney;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



public class Publicar extends BaseActivity {

    private Button BtnSubir;
    private ImageButton BtnIma1;
    private ImageButton BtnIma2;
    private ImageButton BtnIma3;
    private ImageButton BtnIma4;
    private int i;
    private Uri[] uris;
    private EditText ETdescripcion;
    private EditText ETtitulo;
    private EditText ETprecio;
    private StorageReference storageRef;
    private DatabaseReference database;
    private FirebaseUser user;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        BtnSubir = (Button)findViewById(R.id.BtnSubir);
        BtnIma1 = (ImageButton)findViewById(R.id.imageButton1);
        BtnIma2 = (ImageButton)findViewById(R.id.imageButton2);
        BtnIma3 = (ImageButton)findViewById(R.id.imageButton3);
        BtnIma4 = (ImageButton)findViewById(R.id.imageButton4);
        ETdescripcion = (EditText)findViewById(R.id.ETdescripcion);
        ETtitulo = (EditText)findViewById(R.id.ETtitulo);
        ETprecio = (EditText)findViewById(R.id.ETprecio);
        i = 0;
        uris = new Uri[4];
        for(int b=0;b<4;b++)uris[b]=null;

        database = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        BtnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOnlineNet()) subir();
                else snackBar("Sin conexión a internet");

            }
        });

        BtnIma1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 0;
                checkPermission();
            }
        });

        BtnIma2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 1;
                checkPermission();
            }
        });

        BtnIma3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 2;
                checkPermission();
            }
        });

        BtnIma4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 3;
                checkPermission();
            }
        });


    }

    @TargetApi(24)
    public void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione imagen"),1);
    }


    @TargetApi(24)
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(resultCode==RESULT_OK){
            uris[i] = imageReturnedIntent.getData();
            switch(i){
                case 0: BtnIma1.setImageURI(uris[i]);
                        break;
                case 1: BtnIma2.setImageURI(uris[i]);
                        break;
                case 2: BtnIma3.setImageURI(uris[i]);
                        break;
                case 3: BtnIma4.setImageURI(uris[i]);
                        break;
            }
        }

    }



    @TargetApi(24)
    private void checkPermission() {

            int hasWriteContactsPermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);

            }else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED){

                abrirGaleria();

            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    abrirGaleria();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void subir() {

        showProgressDialog(this);
        final String descripcion, titulo,precio,ID,email,url,usuario;
        ID = UUID.randomUUID().toString();
        if (validar()) {
            descripcion = ETdescripcion.getText().toString();
            titulo = ETtitulo.getText().toString();
            precio = ETprecio.getText().toString();
            email = user.getEmail();
            usuario = user.getDisplayName();
            url =  "gs://tioney-40377.appspot.com/Anuncios/" + ID + "/";
            final InputStream streamDescripcion = new ByteArrayInputStream(descripcion.getBytes());
            final StorageReference filepathDescripcion = storageRef.child("Anuncios/" + ID + "/" + "Descripcion");


           /* String key =  database.child("Usuarios").child(user.getUid()).child("Anuncios").push().getKey();
            Map<String, Object> map = new HashMap<>();
            map.put(key, "gs://tioney-40377.appspot.com/Anuncios/" + ID + "/");
            database.child("Usuarios").child(user.getUid()).child("Anuncios").updateChildren(map);
            database.child("Anuncios1").updateChildren(map);*/

            String key =  database.child("Usuarios").child(user.getUid()).child("Anuncios").push().getKey();
            Map<String, Object> map = new HashMap<>();
            AnunDatabase anun = new AnunDatabase(titulo,precio,url,email,usuario);
            map.put(key,anun);
            database.child("Usuarios").child(user.getUid()).child("Anuncios").updateChildren(map);
            database.child("Anuncios1").updateChildren(map);


            for (int b = 0; b < 4; b++) {

                if (uris[b] != null) {
                    StorageReference filepathFotos = storageRef.child("Anuncios/" + ID + "/" + "Foto" + b);

                    filepathFotos.putFile(uris[b]).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(Publicar.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
                }
            }


            filepathDescripcion.putStream(streamDescripcion).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(Publicar.this, "Error al subir descripción", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        hideProgressDialog();

                        Toast.makeText(Publicar.this, "Publicación subida correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Publicar.this, MainActivity.class);
                        startActivity(intent);

                    }
                });

        }


    }


    public boolean validar(){
        boolean v = true;
        if (ETdescripcion.getText().toString().isEmpty()) {
            ETdescripcion.setError("Obligatorio");
            v = false;
        }
        if (ETtitulo.getText().toString().isEmpty()) {
            ETtitulo.setError("Obligatorio");
            v = false;
        }
        if (ETprecio.getText().toString().isEmpty()) {
            ETprecio.setError("Obligatorio");
            v = false;
        }
        if(!v)hideProgressDialog();
        return v;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }

   /* public void comprimir(){
        Bitmap bm2 = createBitmap();
        OutputStream stream = new FileOutputStream(uris[0]);
        //Write bitmap to file using JPEG and 80% quality hint for JPEG.
        bm2.compress(Bitmap.CompressFormat.JPEG, 80, stream);
    }*/
}

