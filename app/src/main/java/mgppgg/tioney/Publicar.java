package mgppgg.tioney;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
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
import java.util.Objects;
import java.util.UUID;

import recycler_view.Anuncio;
import recycler_view.MyAdapter;


public class Publicar extends BaseActivity {

    private Button BtnSubir,BtnBorrar;
    private ImageButton BtnIma1;
    private ImageButton BtnIma2;
    private ImageButton BtnIma3;
    private ImageButton BtnIma4;
    private int i;
    private String key;
    private ArrayList<ArrrayUri> arrayUris;
    private ArrayList<ImageButton> imageButtons;
    private EditText ETdescripcion;
    private EditText ETtitulo;
    private EditText ETprecio;
    private Anuncio anun2;
    private MisAnuncios mis;
    private FirebaseStorage  storage;
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

        anun2 = null;
        anun2 = (Anuncio) getIntent().getSerializableExtra("Anuncio");

        BtnSubir = (Button)findViewById(R.id.BtnSubir);
        BtnBorrar= (Button)findViewById(R.id.BtnBorrarAnun);
        BtnIma1 = (ImageButton)findViewById(R.id.imageButton1);
        BtnIma2 = (ImageButton)findViewById(R.id.imageButton2);
        BtnIma3 = (ImageButton)findViewById(R.id.imageButton3);
        BtnIma4 = (ImageButton)findViewById(R.id.imageButton4);
        ETdescripcion = (EditText)findViewById(R.id.ETdescripcion);
        ETtitulo = (EditText)findViewById(R.id.ETtitulo);
        ETprecio = (EditText)findViewById(R.id.ETprecio);
        i = -1;
        arrayUris = new ArrayList<>();
        mis = new MisAnuncios();

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(anun2!=null){
            imageButtons = new ArrayList<>();
            imageButtons.add(BtnIma1);
            imageButtons.add(BtnIma2);
            imageButtons.add(BtnIma3);
            imageButtons.add(BtnIma4);

            BtnBorrar.setVisibility(View.VISIBLE);
            BtnSubir.setText("Editar");

            ETtitulo.setText(anun2.getTitulo());
            ETdescripcion.setText(anun2.getDescripcion());
            ETprecio.setText(anun2.getPrecio());
            if(anun2.getFotos()>0) {
                for(int n=0;n<anun2.getFotos();n++) {
                    StorageReference filepathFoto = storage.getReferenceFromUrl(anun2.getUrl() + "Foto" + n);
                    Glide.with(this).using(new FirebaseImageLoader()).load(filepathFoto).diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(imageButtons.get(n));
                }
            }

            database.child("Usuarios").child(user.getUid()).child("Anuncios").addListenerForSingleValueEvent(new ValueEventListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        AnunDatabase a = postSnapshot.getValue(AnunDatabase.class);
                        if(Objects.equals(a.getTitulo(), anun2.getTitulo())) key =postSnapshot.getKey();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        BtnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnlineNet()) subir();
                else snackBar("Sin conexión a internet");

            }
        });

        BtnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnlineNet()) {
                    for(int m=0;m<anun2.getFotos();m++) {
                        storage.getReferenceFromUrl(anun2.getUrl() + "Foto" + m).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(Publicar.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    storage.getReferenceFromUrl(anun2.getUrl() + "Descripcion").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Publicar.this, "Borrado. Desliza abajo para comprobar", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(Publicar.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                        }
                    });

                    database.child("Usuarios").child(user.getUid()).child("Anuncios").child(key).removeValue();
                    database.child("Anuncios1").child(key).removeValue();
                    finish();

                }
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
        boolean seguir = true;

        if(resultCode==RESULT_OK){
            ArrrayUri a = new ArrrayUri(i,imageReturnedIntent.getData());

            for(int c=arrayUris.size()-1;c>=0 && seguir;c--){
                if(arrayUris.get(c).getId()==a.getId()){
                    arrayUris.set(c,a);
                    seguir = false;
                }
            }
            if(seguir)arrayUris.add(a);

            switch(i){
                case 0: BtnIma1.setImageURI(imageReturnedIntent.getData());
                        break;
                case 1: BtnIma2.setImageURI(imageReturnedIntent.getData());
                        break;
                case 2: BtnIma3.setImageURI(imageReturnedIntent.getData());
                        break;
                case 3: BtnIma4.setImageURI(imageReturnedIntent.getData());
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

        showProgressDialog(this,"Creando anuncio..");
        final String descripcion, titulo,precio,ID,UID,url,usuario;
        ID = UUID.randomUUID().toString();
        if (validar()) {
            descripcion = ETdescripcion.getText().toString();
            titulo = ETtitulo.getText().toString();
            precio = ETprecio.getText().toString();
            UID = user.getUid();

            if(anun2==null) {

                usuario = user.getDisplayName();
                url =  "gs://tioney-40377.appspot.com/Anuncios/" + ID + "/";
                final InputStream streamDescripcion = new ByteArrayInputStream(descripcion.getBytes());
                final StorageReference filepathDescripcion = storageRef.child("Anuncios/" + ID + "/" + "Descripcion");

                String key1 =  database.child("Usuarios").child(user.getUid()).child("Anuncios").push().getKey();
                final Map<String, Object> map = new HashMap<>();
                AnunDatabase anun = new AnunDatabase(titulo,precio,url,UID,usuario,arrayUris.size());
                map.put(key1,anun);

                if(i>-1) {

                    for (int b = 0; b < arrayUris.size(); b++) {

                        arrayUris.get(b).setId(b);

                        StorageReference filepathFotos = storageRef.child("Anuncios/" + ID + "/" + "Foto" + b);

                        filepathFotos.putFile(arrayUris.get(b).getUri()).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(Publicar.this, "Error al subir fotos", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Publicar.this, "Error al subir anuncio", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        database.child("Usuarios").child(user.getUid()).child("Anuncios").updateChildren(map);
                        database.child("Anuncios1").updateChildren(map);

                        hideProgressDialog();

                        Toast.makeText(Publicar.this, "Publicación subida correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Publicar.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

            } else{
                Log.d("a",key);
                int cont = 0;
                final StorageReference filepathDescripcion = storage.getReferenceFromUrl(anun2.getUrl()+"Descripcion");
                final InputStream streamDescripcion = new ByteArrayInputStream(descripcion.getBytes());

                for(int c=0;c<arrayUris.size();c++){
                    if(arrayUris.get(c).getId()>anun2.getFotos()-1)cont++;
                }
                final int finalCont = cont;

                if(i > -1){

                    for (int b = 0; b < arrayUris.size(); b++) {

                        StorageReference filepathFotos = storage.getReferenceFromUrl(anun2.getUrl()+"Foto"+arrayUris.get(b).getId());

                        filepathFotos.putFile(arrayUris.get(b).getUri()).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(Publicar.this, "Error al subir fotos", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Publicar.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if(finalCont >0){
                            database.child("Usuarios").child(UID).child("Anuncios").child(key).child("fotos").setValue(anun2.getFotos()+finalCont);
                            database.child("Anuncios1").child(key).child("fotos").setValue(anun2.getFotos()+finalCont);
                        }
                        database.child("Usuarios").child(UID).child("Anuncios").child(key).child("titulo").setValue(titulo);
                        database.child("Usuarios").child(UID).child("Anuncios").child(key).child("precio").setValue(precio);
                        database.child("Anuncios1").child(key).child("titulo").setValue(titulo);
                        database.child("Anuncios1").child(key).child("precio").setValue(precio);

                        hideProgressDialog();
                        Toast.makeText(Publicar.this, "Actualizado. Desliza abajo para comprobar", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });


            }

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

