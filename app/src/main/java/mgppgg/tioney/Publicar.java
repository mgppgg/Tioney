package mgppgg.tioney;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;



public class Publicar extends BaseActivity {

    private Button BtnSubir;
    private ImageButton BtnIma1;
    private ImageButton BtnIma2;
    private ImageButton BtnIma3;
    private ImageButton BtnIma4;
    private int btn,i;
    private Uri uri;
    private Uri[] uris;
    private EditText ETdescripcion;
    private StorageReference storageRef;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar);

        BtnSubir = (Button)findViewById(R.id.BtnSubir);
        BtnIma1 = (ImageButton)findViewById(R.id.imageButton1);
        BtnIma2 = (ImageButton)findViewById(R.id.imageButton2);
        BtnIma3 = (ImageButton)findViewById(R.id.imageButton3);
        BtnIma4 = (ImageButton)findViewById(R.id.imageButton4);
        ETdescripcion = (EditText)findViewById(R.id.ETdescripcion);
        btn = 0; i = 0;
        uris = new Uri[4];
        for(int b=0;b<4;b++)uris[b]=null;

        storageRef = FirebaseStorage.getInstance().getReference();

        BtnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOnlineNet()==true) subir();
                else Toast.makeText(Publicar.this, "Conexión a internet no disponible", Toast.LENGTH_SHORT).show();

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
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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

        showProgressDialog();
        String descripcion;
        if (ETdescripcion.getText().toString().isEmpty()) {
            Toast.makeText(Publicar.this, "Obligatorio rellenar descripción", Toast.LENGTH_SHORT).show();
            hideProgressDialog();
        } else {
            descripcion = ETdescripcion.getText().toString();
            InputStream streamDescripcion = new ByteArrayInputStream(descripcion.getBytes());
            StorageReference filepathDescripcion = storageRef.child("Anuncios/" + UUID.randomUUID().toString());


            for (int b = 0; b < 4; b++) {

                if (uris[b] != null) {
                    StorageReference filepathFotos = storageRef.child("Anuncios/" + uris[b].getLastPathSegment());

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
                    hideProgressDialog();
                    Toast.makeText(Publicar.this, "Error al subir tetxo", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hideProgressDialog();
                }
            });

            Toast.makeText(Publicar.this, "Publicación subida correctamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Publicar.this, MainActivity.class);
            startActivity(intent);

        }


    }
}

